package db.movie;

public class MovieDTO {
	
	String movieNum;      // 영화코드
	String movieName;   // 영화제목
	String director;  	      // 감독
	String openDate; 		  // 개봉일자
	String closeDate;        // 종영일자
	String genre;              // 장르명
	
	String onNum;          // 상영코드
	String onDate;           // 상영날짜
	String startTime;        // (상영시작시간)
	String therNum;         // 상영관번호
	int totSeat;            // 총좌석
    int remSeat;		  // (잔여좌석)

	
    
    
	public String getMovieNum() {
		return movieNum;
	}
	public void setMovieNum(String movieNum) {
		this.movieNum = movieNum;
	}
	public String getMovieName() {
		return movieName;
	}
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	public String getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getOnNum() {
		return onNum;
	}
	public void setOnNum(String onNum) {
		this.onNum = onNum;
	}
	public String getOnDate() {
		return onDate;
	}
	public void setOnDate(String onDate) {
		this.onDate = onDate;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getTherNum() {
		return therNum;
	}
	public void setTherNum(String therNum) {
		this.therNum = therNum;
	}
	public int getTotSeat() {
		return totSeat;
	}
	public void setTotSeat(int totSeat) {
		this.totSeat = totSeat;
	}
	public int getRemSeat() {
		return remSeat;
	}
	public void setRemSeat(int remSeat) {
		this.remSeat = remSeat;
	}


}
