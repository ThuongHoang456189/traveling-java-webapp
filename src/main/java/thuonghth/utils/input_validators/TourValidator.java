package thuonghth.utils.input_validators;

import java.math.BigDecimal;
import java.util.TreeMap;

import thuonghth.tours.TourDTO;
import thuonghth.utils.DateTimeUtil;
import thuonghth.utils.MyConstants;

public class TourValidator {
	private String tourID;
	private String tourName;
	private String fromPlace;
	private String toPlace;
	private String fromDate;
	private String toDate;
	private String price;
	private TreeMap<String, String> fieldsMsg;

	private final String TOUR_NAME_FIELD = "Tour Name";
	private final String FROM_PLACE_FIELD = "From Place";
	private final String TO_PLACE_FIELD = "To Place";
	private final String FROM_DATE_FIELD = "From Date";
	private final String TO_DATE_FIELD = "To Date";
	private final String PRICE_FIELD = "Price";
	private final String PLACE_FIELD = "Place";
	private final String UP_TO_PRICE_FIELD = "Up to";

	public final int TOUR_NAME_LENGTH = 1000;
	public final int FROM_PLACE_LENGTH = 500;
	public final int TO_PLACE_LENGTH = 500;

	public TourValidator(String tourName, String fromPlace, String toPlace, String fromDate, String toDate,
			String price) {
		this.tourName = tourName.trim();
		this.fromPlace = fromPlace.trim();
		this.toPlace = toPlace.trim();
		this.fromDate = fromDate.trim();
		this.toDate = toDate.trim();
		this.price = price.trim();
		this.fieldsMsg = new TreeMap<>();
	}

	public TourValidator(String place, String fromDate, String toDate, String upToPrice) {
		this.fromPlace = place.trim();
		this.fromDate = fromDate.trim();
		this.toDate = toDate.trim();
		this.price = upToPrice.trim();
		this.fieldsMsg = new TreeMap<>();
	}

	public TourValidator(String tourID, String tourName, String fromDate, String toDate, String price) {
		this.tourID = tourID;
		this.tourName = tourName;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.price = price;
		this.fieldsMsg = new TreeMap<>();
	}

	public String getTourID() {
		return tourID;
	}

	public void setTourID(String tourID) {
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

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String validateRequiredString(String str, String fieldName) {
		return str.isEmpty() ? "The " + fieldName + " field must not be blank. " : "";
	}

	public String validateMaxStringLength(String str, String fieldName, int maxLength) {
		return str.length() > maxLength
				? "The " + fieldName + " is over the maximum length of " + maxLength + " characters. "
				: "";
	}

	public String validateTourName(String tourName) {
		String errorMsg = "";
		errorMsg += validateRequiredString(tourName, TOUR_NAME_FIELD);
		errorMsg += validateMaxStringLength(tourName, TOUR_NAME_FIELD, TOUR_NAME_LENGTH);
		return errorMsg;
	}

	public String validateFromPlace(String fromPlace) {
		String errorMsg = "";
		errorMsg += validateRequiredString(fromPlace, FROM_PLACE_FIELD);
		errorMsg += validateMaxStringLength(fromPlace, FROM_PLACE_FIELD, FROM_PLACE_LENGTH);
		return errorMsg;
	}

	public String validateToPlace(String toPlace) {
		String errorMsg = "";
		errorMsg += validateRequiredString(fromPlace, TO_PLACE_FIELD);
		errorMsg += validateMaxStringLength(fromPlace, TO_PLACE_FIELD, TO_PLACE_LENGTH);
		return errorMsg;
	}

	private String validateDate(String date, String dateFieldName) {
		return DateTimeUtil.isValidDate(date) ? "" : "The " + dateFieldName + " field must be a valid date. ";
	}

	public String validateFromDate(String fromDate) {
		String errorMsg = "";
		errorMsg += validateRequiredString(fromDate, FROM_DATE_FIELD);
		errorMsg += validateDate(fromDate, FROM_DATE_FIELD);
		errorMsg += errorMsg.isBlank() && !DateTimeUtil.isFromToday(fromDate)
				? "The " + FROM_DATE_FIELD + " field must from today " + DateTimeUtil.getCurrentDateStr() + ". "
				: "";
		return errorMsg;
	}

	public String validateToDate(String toDate) {
		String errorMsg = "";
		errorMsg += validateRequiredString(toDate, TO_DATE_FIELD);
		errorMsg += validateDate(toDate, TO_DATE_FIELD);
		if (DateTimeUtil.isValidDate(fromDate)) {
			errorMsg += errorMsg.isBlank() && !DateTimeUtil.isValidDateInterval(fromDate, toDate)
					? "The " + TO_DATE_FIELD + " field must from " + FROM_DATE_FIELD + " field date " + fromDate + ". "
					: "";
		}
		return errorMsg;
	}

	public String validatePrice(String price) {
		String errorMsg = "";
		errorMsg += validateRequiredString(price, PRICE_FIELD);
		try {
			BigDecimal priceValue = new BigDecimal(price);

			if (priceValue.compareTo(BigDecimal.ZERO) < 0)
				errorMsg += "The " + PRICE_FIELD + " field must be a non-negative real number. ";
		} catch (NumberFormatException e) {
			errorMsg += "The " + PRICE_FIELD + " field must be a real number" + ". ";
		}
		return errorMsg;
	}

	public String validateTourID(String tourID) {
		String errorMsg = "";
		errorMsg += validateRequiredString(tourID, TourDTO.TOUR_ID_FIELD);
		try {
			Long tourIDValue = Long.parseLong(tourID);

			if (tourIDValue <= 0)
				errorMsg += "The " + TourDTO.TOUR_ID_FIELD + " field must be a positive long number. ";
		} catch (NumberFormatException e) {
			errorMsg += "The " + TourDTO.TOUR_ID_FIELD + " field must be a long number. ";
		}
		return errorMsg;
	}

	public TreeMap<String, String> validateTour() {
		fieldsMsg.put(TOUR_NAME_FIELD, validateTourName(this.tourName));
		fieldsMsg.put(FROM_PLACE_FIELD, validateFromPlace(this.fromPlace));
		fieldsMsg.put(TO_PLACE_FIELD, validateToPlace(this.toPlace));
		fieldsMsg.put(FROM_DATE_FIELD, validateFromDate(this.fromDate));
		fieldsMsg.put(TO_DATE_FIELD, validateToDate(this.toDate));
		fieldsMsg.put(PRICE_FIELD, validatePrice(this.price));
		return fieldsMsg;
	}

	public boolean isTourValid() {
		boolean isValid = true;
		isValid = isValid && fieldsMsg.get(TOUR_NAME_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(FROM_PLACE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(TO_PLACE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(FROM_DATE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(TO_DATE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(PRICE_FIELD).equals("");
		return isValid;
	}

	public TourDTO getTourDTO(String image) {
		return new TourDTO(tourName, fromPlace, toPlace, DateTimeUtil.getDate(fromDate), DateTimeUtil.getDate(toDate),
				new BigDecimal(price), image, MyConstants.ACTIVE_TOUR_STATUS_ID);
	}

	public String validatePlace(String place) {
		String errorMsg = "";
		errorMsg += validateMaxStringLength(place, PLACE_FIELD, FROM_PLACE_LENGTH);
		return errorMsg;
	}

	public TreeMap<String, String> validateTourSearchingInfo() {
		fieldsMsg.put(PLACE_FIELD, validatePlace(this.fromPlace));
		fieldsMsg.put(FROM_DATE_FIELD, validateFromDate(this.fromDate));
		fieldsMsg.put(TO_DATE_FIELD, validateToDate(this.toDate));
		fieldsMsg.put(UP_TO_PRICE_FIELD, this.price.isBlank() ? "" : validatePrice(this.price));
		return fieldsMsg;
	}

	public boolean isTourSearchingInfoValid() {
		boolean isValid = true;
		isValid = isValid && fieldsMsg.get(PLACE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(FROM_DATE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(TO_DATE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(UP_TO_PRICE_FIELD).equals("");
		return isValid;
	}

	public TourSearchingInfo getTourSearchingInfo() {
		return new TourSearchingInfo(fromPlace, DateTimeUtil.getDate(fromDate), DateTimeUtil.getDate(toDate),
				new BigDecimal(price.isBlank() ? "-1" : price));
	}

	public static TourValidator getDefaultTourSearchingInfo() {
		return new TourValidator(
				"", DateTimeUtil.getCurrentDateStr(), DateTimeUtil.getDateStr(DateTimeUtil
						.getNextPeriodDays(DateTimeUtil.getCurrentDate(), MyConstants.DEFAULT_TOUR_LENGTH_IN_DAYS)),
				"");
	}

	public TreeMap<String, String> validateTourInCart() {
		fieldsMsg.put(TourDTO.TOUR_ID_FIELD, this.tourID == null ? "null" : validateTourID(this.tourID));
		fieldsMsg.put(TourDTO.TOUR_NAME_FIELD, this.tourName == null ? "null" : validateTourName(this.tourName));
		fieldsMsg.put(TourDTO.FROM_DATE_FIELD, this.fromDate == null ? "null" : validateFromDate(this.fromDate));
		fieldsMsg.put(TourDTO.TO_DATE_FIELD, this.toDate == null ? "null" : validateToDate(this.toDate));
		fieldsMsg.put(TourDTO.PRICE_FIELD, this.price == null ? "null" : validatePrice(this.price));
		return fieldsMsg;
	}

	public boolean isTourInCartValid() {
		boolean isValid = true;
		isValid = isValid && fieldsMsg.get(TourDTO.TOUR_ID_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(TourDTO.TOUR_NAME_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(TourDTO.FROM_DATE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(TourDTO.TO_DATE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(TourDTO.PRICE_FIELD).equals("");
		return isValid;
	}

	public TourDTO getTourDTOinCart() {
		return new TourDTO(Integer.parseInt(tourID), tourName, DateTimeUtil.getDate(fromDate),
				DateTimeUtil.getDate(toDate), new BigDecimal(price));
	}

}
