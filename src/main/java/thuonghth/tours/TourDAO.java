package thuonghth.tours;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.naming.NamingException;

import thuonghth.utils.DBHelper;
import thuonghth.utils.DateTimeUtil;
import thuonghth.utils.ImageUtil;
import thuonghth.utils.MyConstants;
import thuonghth.utils.input_validators.TourSearchingInfo;
import thuonghth.utils.input_validators.WrapperInputObject;

public class TourDAO {
	private int numberOfAvailableTours = 0;

	public long insertNewTour(TourDTO tour) throws SQLException, NamingException {
		long insertedID = -1;
		Connection con = null;
		try {
			con = DBHelper.getConnect();
			CallableStatement cstmt = con.prepareCall("{? = call dbo.uspInsertTour(?,?,?,?,?,?,?,?)}");
			cstmt.registerOutParameter(1, java.sql.Types.BIGINT);
			cstmt.setString(2, tour.getTourName());
			cstmt.setString(3, tour.getFromPlace());
			cstmt.setString(4, tour.getToPlace());
			cstmt.setDate(5, tour.getFromDate());
			cstmt.setDate(6, tour.getToDate());
			cstmt.setBigDecimal(7, tour.getPrice());
			cstmt.setString(8, tour.getImage());
			cstmt.setByte(9, tour.getStatusID());
			cstmt.execute();
			insertedID = cstmt.getInt(1);
			cstmt.close();
		} finally {
			if (con != null)
				con.close();
		}
		return insertedID;
	}

	private int calculateOffset(int page) {
		return (page - 1) * MyConstants.TOTAL_ITEM_IN_PAGE;
	}

	public LinkedList<TourDTO> getToursList(String contextPath, String imagesDirPath, TourSearchingInfo info, int page)
			throws NamingException, SQLException {
		ImageUtil imageUtil = new ImageUtil();
		LinkedList<TourDTO> toursList = new LinkedList<>();
		Connection con = null;
		try {
			con = DBHelper.getConnect();
			CallableStatement cstmt = con.prepareCall("{? = call dbo.uspSearchTour(?,?,?,?,?,?)}");
			cstmt.registerOutParameter(1, java.sql.Types.BIGINT);
			cstmt.setString(2, info.getPlace());
			cstmt.setDate(3, info.getFromDate());
			cstmt.setDate(4, info.getToDate());
			cstmt.setBigDecimal(5, info.getUpToPrice());
			cstmt.setInt(6, calculateOffset(page));
			cstmt.setInt(7, MyConstants.TOTAL_ITEM_IN_PAGE);
			ResultSet rs = cstmt.executeQuery();
			while (rs.next()) {
				String imagePath = imageUtil.getDisplayableImage(contextPath, imagesDirPath, rs.getString("image"));
				TourDTO tour = new TourDTO(rs.getInt("tourID"), rs.getString("tourName"), rs.getDate("fromDate"),
						rs.getDate("toDate"), rs.getBigDecimal("price"), imagePath);
				toursList.add(tour);
			}
			numberOfAvailableTours = cstmt.getInt(1);
		} finally {
			if (con != null)
				con.close();
		}
		return toursList;
	}

	public int getNumberOfAvailableTours() {
		return numberOfAvailableTours;
	}

	public int getNumOfPages() {
		return (int) (numberOfAvailableTours + MyConstants.TOTAL_ITEM_IN_PAGE - 1) / MyConstants.TOTAL_ITEM_IN_PAGE;
	}

	public TourDTO getTour(int tourID, Connection con) throws NamingException, SQLException {
		TourDTO foundTour = null;
		boolean isPrivateConnection = con == null;
		try {
			if (isPrivateConnection)
				con = DBHelper.getConnect();
			CallableStatement cstmt = con.prepareCall("{call dbo.uspGetTour(?)}");
			cstmt.setInt(1, tourID);
			ResultSet rs = cstmt.executeQuery();
			if (rs.next()) {
				foundTour = new TourDTO(tourID, rs.getString("tourName"), rs.getDate("fromDate"), rs.getDate("toDate"),
						rs.getBigDecimal("price"));
			}
		} finally {
			if (isPrivateConnection && con != null)
				con.close();
		}
		return foundTour;
	}

	private void updateCartRecord(boolean isNotMatched, TourDTO tourInCart, TourDTO tourFromDB,
			TreeMap<String, String> fieldsMsg, String fieldName, String msg) {
		if (isNotMatched) {
			fieldsMsg.put(fieldName, msg);
			switch (fieldName) {
			case TourDTO.TOUR_NAME_FIELD:
				tourInCart.setTourName(tourFromDB == null ? "N/A" : tourFromDB.getTourName());
				break;
			case TourDTO.FROM_DATE_FIELD:
				tourInCart.setFromDate(tourFromDB == null ? null : tourFromDB.getFromDate());
				break;
			case TourDTO.TO_DATE_FIELD:
				tourInCart.setToDate(tourFromDB == null ? null : tourFromDB.getToDate());
				break;
			case TourDTO.PRICE_FIELD:
				tourInCart.setPrice(tourFromDB == null ? BigDecimal.ZERO : tourFromDB.getPrice());
				break;
			default:
				break;
			}
		}
	}

	public boolean validateTour(WrapperInputObject<TourDTO> tourRecord, Connection con)
			throws NamingException, SQLException {
		boolean isMatched = false;
		TourDTO tour = tourRecord.getInputObject();
		int tourID = tour.getTourID();
		TourDTO tourFromDB = getTour(tourID, con);
		TreeMap<String, String> fieldsMsg = tourRecord.getFieldsMsg();
		if (!tour.equals(tourFromDB)) {
			tourRecord.setValid(false);
			if (fieldsMsg == null) {
				fieldsMsg = new TreeMap<>();
				tourRecord.setFieldsMsg(fieldsMsg);
			}

			updateCartRecord(tourFromDB == null, tour, tourFromDB, fieldsMsg, TourDTO.TOUR_NAME_FIELD,
					"Tour " + tour.getTourName() + " is not available! ");
			updateCartRecord(tourFromDB == null, tour, tourFromDB, fieldsMsg, TourDTO.FROM_DATE_FIELD,
					"Schedule is not available! ");
			updateCartRecord(tourFromDB == null, tour, tourFromDB, fieldsMsg, TourDTO.TO_DATE_FIELD,
					"Schedule is not available! ");
			updateCartRecord(tourFromDB == null, tour, tourFromDB, fieldsMsg, TourDTO.PRICE_FIELD,
					"Price is not available! ");

			updateCartRecord(tourFromDB != null && !tour.getTourName().equalsIgnoreCase(tourFromDB.getTourName()), tour,
					tourFromDB, fieldsMsg, TourDTO.TOUR_NAME_FIELD, "Replaced " + tour.getTourName());
			updateCartRecord(tourFromDB != null && tour.getFromDate().compareTo(tourFromDB.getFromDate()) != 0, tour,
					tourFromDB, fieldsMsg, TourDTO.FROM_DATE_FIELD, "Replaced " + tour.getFromDate().toString());
			updateCartRecord(tourFromDB != null && tour.getFromDate().compareTo(DateTimeUtil.getCurrentDate()) <= 0,
					tour, tourFromDB, fieldsMsg, TourDTO.FROM_DATE_FIELD, "Schedule is not available! ");
			updateCartRecord(tourFromDB != null && tour.getToDate().compareTo(tourFromDB.getToDate()) != 0, tour,
					tourFromDB, fieldsMsg, TourDTO.TO_DATE_FIELD, "Replaced " + tour.getToDate().toString());
			updateCartRecord(tourFromDB != null && tour.getPrice().compareTo(tourFromDB.getPrice()) != 0, tour,
					tourFromDB, fieldsMsg, TourDTO.PRICE_FIELD,
					"Replaced " + tour.getPrice().setScale(3, RoundingMode.CEILING).toString());
		} else
			isMatched = true;
		tourRecord.setValid(isMatched);
		tourRecord.setFieldsMsg(fieldsMsg);
		return isMatched;
	}
}
