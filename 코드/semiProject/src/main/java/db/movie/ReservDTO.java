package db.movie;

public class ReservDTO {

	String resNum;     // 예매번호
    String resDate;    // 예매일자
    int resPay;        // 예매금액
    int person;        // 인원수	
    String onNum;      // 상영코드
    
    int remSeat;		  // (잔여좌석)
    
    
    
    public String getResNum() {
		return resNum;
	}

	public void setResNum(String resNum) {
		this.resNum = resNum;
	}

	public String getResDate() {
		return resDate;
	}

	public void setResDate(String resDate) {
		this.resDate = resDate;
	}

	public int getResPay() {
		return resPay;
	}

	public void setResPay(int resPay) {
		this.resPay = resPay;
	}

	public int getPerson() {
		return person;
	}

	public void setPerson(int person) {
		this.person = person;
	}

	public String getOnNum() {
		return onNum;
	}

	public void setOnNum(String onNum) {
		this.onNum = onNum;
	}

	public int getRemSeat() {
		return remSeat;
	}

	public void setRemSeat(int remSeat) {
		this.remSeat = remSeat;
	}

    
    
}
