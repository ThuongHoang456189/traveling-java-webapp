package thuonghth.utils.input_validators;

import java.math.BigDecimal;
import java.sql.Date;

public class TourSearchingInfo {
	private String place;
	private Date fromDate;
	private Date toDate;
	private BigDecimal upToPrice;

	public TourSearchingInfo(String place, Date fromDate, Date toDate, BigDecimal upToPrice) {
		this.place = place;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.upToPrice = upToPrice;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
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

	public BigDecimal getUpToPrice() {
		return upToPrice;
	}

	public void setUpToPrice(BigDecimal upToPrice) {
		this.upToPrice = upToPrice;
	}

}
