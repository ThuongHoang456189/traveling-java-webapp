package thuonghth.tours;

import java.math.BigDecimal;
import java.sql.Date;

public class TourDTO {
	private int tourID;
	private String tourName;
	private String fromPlace;
	private String toPlace;
	private Date fromDate;
	private Date toDate;
	private BigDecimal price;
	private String image;
	private Date dateImport;
	private byte statusID;

	public static final String TOUR_ID_FIELD = "tourID";
	public static final String TOUR_NAME_FIELD = "tourName";
	public static final String FROM_DATE_FIELD = "fromDate";
	public static final String TO_DATE_FIELD = "toDate";
	public static final String PRICE_FIELD = "price";

	public TourDTO(String tourName, String fromPlace, String toPlace, Date fromDate, Date toDate, BigDecimal price,
			String image, byte statusID) {
		this.tourName = tourName;
		this.fromPlace = fromPlace;
		this.toPlace = toPlace;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.price = price;
		this.image = image;
		this.statusID = statusID;
	}

	public TourDTO(int tourID, String tourName, Date fromDate, Date toDate, BigDecimal price, String image) {
		this.tourID = tourID;
		this.tourName = tourName;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.price = price;
		this.image = image;
	}

	public TourDTO(int tourID, String tourName, Date fromDate, Date toDate, BigDecimal price) {
		this.tourID = tourID;
		this.tourName = tourName;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.price = price;
	}

	public int getTourID() {
		return tourID;
	}

	public void setTourID(int tourID) {
		this.tourID = tourID;
	}

	public String getTourName() {
		return tourName;
	}

	public void setTourName(String tourName) {
		this.tourName = tourName;
	}

	public String getFromPlace() {
		return fromPlace;
	}

	public void setFromPlace(String fromPlace) {
		this.fromPlace = fromPlace;
	}

	public String getToPlace() {
		return toPlace;
	}

	public void setToPlace(String toPlace) {
		this.toPlace = toPlace;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Date getDateImport() {
		return dateImport;
	}

	public void setDateImport(Date dateImport) {
		this.dateImport = dateImport;
	}

	public byte getStatusID() {
		return statusID;
	}

	public void setStatusID(byte statusID) {
		this.statusID = statusID;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		boolean isEqual = true;
		TourDTO tour = (TourDTO) obj;
		isEqual = isEqual && this.tourID == tour.tourID;
		isEqual = isEqual && this.tourName.equals(tour.tourName);
		isEqual = isEqual && this.fromDate.compareTo(tour.fromDate) == 0;
		isEqual = isEqual && this.toDate.compareTo(tour.toDate) == 0;
		isEqual = isEqual && this.price.compareTo(tour.price) == 0;
		return isEqual;
	}

}
