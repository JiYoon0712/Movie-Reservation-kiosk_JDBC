package db.movie;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class AdminUI {
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private AdminDAO dao = new AdminDAO();
	private UserUI dao1 = new UserUI();
	
	public void adminManage() {
		int ch = 0;
		
		while(true) {
			System.out.println("\n[관리자]");
			
			try {
				System.out.print("1.영화 추가\t2.영화 수정\t3.상영스케줄 추가\t4.상영스케줄 수정"
								+ "\n5.상영스케줄 삭제\t6.전체 영화\t7.전체 상영스케줄\t8.예매내역조회"
								+ "\n9.메인\t( 10.상영스케줄 모두삭제\t11.영화 모두삭제\t12.예매 모두삭제 ) = > ");
				ch = Integer.parseInt(br.readLine());
			
				if(ch == 9) return;
				
				switch(ch) {
				case 1: insertMovie(); break;
				case 2: updateMovie(); break;
				case 3: insertSchedule(); break;
				case 4: updateSchedule(); break;
				case 5: deleteSchedule(); break;
				case 6: listMovie(); break;
				case 7: listSchedule(); break;
				case 8: dao1.ReservList(); break;
				case 10: deleteAllSchedule();; break;		// 테스트용도
				case 11: deleteAllMovie(); break;			// 테스트용도
				case 12: deleteAllReserve(); break;			// 테스트용도
				}
			} catch (Exception e) {
			}
		}
		
	}


	// -- 12 테스트 용도
	private void deleteAllReserve() {
		try {
			int result = dao.deleteAllReserve();
			
			if(result != 0) {
				System.out.println("모든 예매 삭제 완료.");
			} else {
				System.out.println("이미 삭제했음.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	// -- 11 테스트 용도
	private void deleteAllMovie() {
		try {
			int result = dao.deleteAllMovie();
			
			if(result != 0) {
				System.out.println("모든 영화 삭제 완료.");
			} else {
				System.out.println("이미 삭제했음.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		
	}

}
	// -- 10 테스트 용도
	private void deleteAllSchedule() {
		try {
			int result = dao.deleteAllSchedule();
			
			if(result != 0) {
				System.out.println("모든 스케줄 삭제 완료.");
			} else {
				System.out.println("이미 삭제했음.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	// 1. 영화 추가
	// 영화코드, 영화제목, 감독, 개봉일자, 종영일자, 장르명
	public void insertMovie() {
		System.out.println("\n======================== [ 영화 추가 ] ========================");
		MovieDTO dto = new MovieDTO();
		
		try {
			System.out.print("영화제목 : ");
			dto.setMovieName(br.readLine());
			
			System.out.print("감독 : ");
			dto.setDirector(br.readLine());
			
			System.out.print("개봉일자(yyyy-MM-dd) : ");
			String openDate = br.readLine();
			
			System.out.print("종영일자(yyyy-MM-dd) : ");
			String closeDate = br.readLine();
			
			// 상영일자 < 종영일자 입력 체크
			if(checkOpneClose(openDate, closeDate)) {
				System.out.println("종영일자가 개봉일자 전입니다.");
				return;
			}
			
			dto.setOpenDate(openDate);
			dto.setCloseDate(closeDate);
			
			System.out.print("장르명 : ");
			dto.setGenre(br.readLine());
			
			dao.insertMovie(dto);
			
			System.out.println("영화가 추가되었습니다.");
		} catch (Exception e) {
			System.out.println("영화 추가 실패");
		}
		System.out.println();
	}
	
	// 2. 영화 수정
	private void updateMovie() throws ParseException {
		System.out.println("\n======================== [ 영화 수정 ] ========================");
		MovieDTO dto = new MovieDTO();
		try {
			listMovie();
			System.out.print("영화코드 : ");
			String movieNum = br.readLine();
			
			// 영화코드가 존재하는지 체크
			if(checkMovieNum(movieNum)) {	
				System.out.println("영화코드가 존재하지 않습니다.");
				return;
			}
			dto.setMovieNum(movieNum);
			
			System.out.print("영화제목 : ");
			dto.setMovieName(br.readLine());
			
			System.out.print("감독 : ");
			dto.setDirector(br.readLine());
			
			System.out.print("개봉일자(yyyy-MM-dd) : ");
			String openDate = br.readLine();
			
			System.out.print("종영일자(yyyy-MM-dd) : ");
			String closeDate = br.readLine();

			// 상영일자 < 종영일자 입력 체크
			if(checkOpneClose(openDate, closeDate)) {
				System.out.println("종영일자가 개봉일자 전입니다.");
				return;
			}
			
			dto.setOpenDate(openDate);
			dto.setCloseDate(closeDate);
			
			System.out.print("장르명 : ");
			dto.setGenre(br.readLine());
			
			dao.updateMovie(dto);

			System.out.println(dto.getMovieNum() + "를 수정하였습니다.");
			System.out.println();
			
		} catch (Exception e) {
			System.out.println("데이터 수정이 실패했습니다.");
		}
	}

	
	// 3.상영 스케줄 추가  
	// 상영코드, 상영날짜, 상영시작시간, *영화코드, *상영관번호
	public void insertSchedule() {
		System.out.println("\n======================== [ 상영스케줄 추가 ] ========================");
		MovieDTO dto = new MovieDTO();
		
		try {
			listMovie();
			System.out.print("영화코드 : ");
			String movieNum = br.readLine();
			
			// 영화코드가 존재하는지 체크
			if(checkMovieNum(movieNum)) {	
				System.out.println("영화코드가 존재하지 않습니다.");
				return;
			}			
			
			// 해당 영화코드의 상영스케줄 리스트
			movieScheduleList(movieNum);
			
			dto.setMovieNum(movieNum);

			System.out.print("상영날짜(yyyy-MM-dd) : ");
			String onDate = br.readLine();
			
			// 개봉날짜 <= 상영날짜 <= 종영날짜 체크 
			if(checkonDate(movieNum, onDate)) {
				System.out.println("상영날짜가 개봉 ~ 종영 날짜 사이가 아닙니다.");
				return;
			}
			
			dto.setMovieNum(movieNum);
			dto.setOnDate(onDate);

			System.out.print("상영시작시간 : ");
			String startTime = br.readLine();
			dto.setStartTime(startTime);
			
			System.out.print("상영관번호 : ");
			String therNum = br.readLine();
			
			// 상영관 존재하는지 체크
			int checkTheater = checkTheater(therNum);
			if(checkTheater == 0){
				System.out.println("상영관이 존재하지 않습니다.");
				return;
			}
			dto.setTherNum(therNum);
			
			// 같은 상영날짜, 상영시작시간, 상영관 번호인 경우 체크
			if(checkExistSchedule(onDate,  startTime,  therNum)) {
				System.out.println("같은 상영날짜, 시작시간, 상영관 번호가 이미 존재합니다.");
				return;
			}
			
			dao.insertSchedule(dto);
			
			System.out.println("상영스케줄이 추가되었습니다.");
		} catch (ParseException e) {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		
	
	
	// 4.상영 스케줄 수정
	// //onNum, onDate, startTime, movieNum, therNum
	public void updateSchedule() {
		System.out.println("\n======================== [ 상영스케줄 수정 ] ========================");
		
		MovieDTO dto = new MovieDTO();
		
		try {
			listSchedule();
			
			System.out.print("상영코드 : ");
			String onNum = br.readLine();
			
			// 상영코드가 존재하는지 체크
			if(checkOnNum(onNum)) {
				System.out.println("상영코드가 존재하지 않습니다.");
				return;
			}
			dto.setOnNum(onNum);
			
			// 해당 상영코드의 상영스케줄 리스트
			ScheduleOnNumList(onNum);
			
			System.out.print("영화코드 : ");
			String movieNum = br.readLine();
			
			dto.setMovieNum(movieNum);

			System.out.print("상영날짜 : ");
			String onDate = br.readLine();
			
			System.out.print("상영시작시간 : ");
			String startTime = br.readLine();
			
			System.out.print("상영관번호 : ");
			String therNum = br.readLine();
			
			// 상영관 존재하는지 체크
			int checkTheater = checkTheater(therNum);
			if(checkTheater == 0){
				System.out.println("상영관이 존재하지 않습니다.");
				return;
			}
			dto.setTherNum(therNum);
			
			// 동일 날짜, 시간, 상영관 번호 체크
			if(checkExistSchedule(onDate,  startTime,  therNum)) {
				System.out.println("같은 상영날짜, 시작시간, 상영관 번호가 이미 존재합니다.");
				return;
			}
			
			dto.setOnDate(onDate);
			dto.setStartTime(startTime);
			dto.setTherNum(therNum);
			
			dao.updateSchedule(dto);
			
			System.out.println(dto.getOnNum() + " 상영코드 자료를 수정했습니다. ");
		} catch (Exception e) {
			System.out.println("데이터 수정이 실패했습니다.");
		}
	}

	
	// 5.상영 스케줄 삭제  
	public void deleteSchedule() {
		System.out.println("\n======================== [ 상영스케줄 삭제 ] ========================");
		String onNum;
		
		try {
			listSchedule();
			
			System.out.print("삭제할 상영번호 : ");
			onNum = br.readLine();
			
			int result = dao.deleteSchedule(onNum);
			
			if(result == 0) {
				System.out.println("등록된 자료가 아닙니다.");
				return;
			}
			
			System.out.println(onNum + "상영번호를 삭제했습니다.");
			
		} catch (Exception e) {
		}
	}

	
	// 6.전체 영화
	public void listMovie() {
		System.out.println("\n======================== [ 영화 리스트 ] ========================");
		List<MovieDTO> list = dao.onMovieList();
		System.out.print("\n영화코드\t영화제목\t\t감독\t개봉일자\t\t종영일자\t\t장르명\n");
		System.out.println("---------------------------------------------------------------------");
		
		for(MovieDTO dto : list) {
			System.out.print(dto.getMovieNum()+"\t");
			System.out.print(dto.getMovieName()+"\t\t");
			System.out.print(dto.getDirector()+"\t");
			System.out.print(dto.getOpenDate()+"\t");
			System.out.print(dto.getCloseDate()+"\t");
			System.out.println(dto.getGenre());
		}
		System.out.println();
	}

	// 7.전체 상영스케줄
	public void listSchedule() {
		System.out.println("\n======================== [ 상영스케줄 리스트 ] ========================");
		System.out.print("\n상영코드\t영화코드\t영화이름\t\t상영날짜\t\t상영시작시간\t상영관번호\n");
		System.out.println("----------------------------------------------------------------------");
		List<MovieDTO> list = dao.onSceduleList();
		
		for(MovieDTO dto : list) {
			System.out.print(dto.getOnNum() + "\t");
			System.out.print(dto.getMovieNum() + "\t");
			System.out.print(dto.getMovieName() + "\t\t");
			System.out.print(dto.getOnDate() + "\t");
			System.out.print(dto.getStartTime() + "\t\t");
			System.out.println(dto.getTherNum());
		}
		System.out.println();
	}

	// 해당 상영코드의 상영스케줄 리스트
	public void ScheduleOnNumList (String onNum) {
		MovieDTO dto  = dao.ScheduleOnNumList(onNum);
		System.out.println("\n======================== [ 리스트 ] ========================");
		System.out.print("\n상영코드\t영화코드\t영화이름\t\t상영날짜\t\t상영시작시간\t상영관번호\n");
		System.out.println("----------------------------------------------------------------------");
		
		System.out.print(dto.getOnNum() + "\t");
		System.out.print(dto.getMovieNum() + "\t");
		System.out.print(dto.getMovieName() + "\t\t");
		System.out.print(dto.getOnDate() + "\t");
		System.out.print(dto.getStartTime() + "\t\t");
		System.out.println(dto.getTherNum());
		System.out.println();
	
	}
	
	// 해당 영화코드의 상영스케줄 리스트
	public void movieScheduleList(String movieNum) {
		System.out.println("\n======================== [ 상영 리스트 ] ========================");
		System.out.print("\n상영코드\t영화코드\t영화이름\t\t상영날짜\t\t상영시작시간\t상영관번호\n");
		System.out.println("----------------------------------------------------------------------");
		List<MovieDTO> list = dao.movieScheduleList(movieNum);
		
		for(MovieDTO dto : list) {
			System.out.print(dto.getOnNum() + "\t");
			System.out.print(dto.getMovieNum() + "\t");
			System.out.print(dto.getMovieName() + "\t\t");
			System.out.print(dto.getOnDate() + "\t");
			System.out.print(dto.getStartTime() + "\t\t");
			System.out.println(dto.getTherNum());
		}
		System.out.println();
	}
	
// *****************************************************************************
	// 상영날짜는 영화코드의 개봉일자와 종영일자 사이인지 체크
	boolean checkonDate(String movieNum, String onDate) throws Exception{
		
		// 영화 코드의 개봉일자와 종영일자 사이가 존재하는지
		try {
			MovieDTO dto = new MovieDTO();
			dto = dao.checkonDate(movieNum);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			Date date = sdf.parse(onDate);
			Date openDate = sdf.parse(dto.getOpenDate());
			Date closeDate = sdf.parse(dto.getCloseDate());
			
			int result = date.compareTo(openDate);
			int result2 = date.compareTo(closeDate);
			
			if(result < 0 || result2 > 0) {
				return true;
			}
			
		} catch(ParseException e) {
			System.out.println("날짜형식[yyyy-MM-dd]을 맞춰주세요.");
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	// 영화코드가 존재하는지 체크
	boolean checkMovieNum (String movieNum) {
		MovieDTO dto = dao.findMovieNum(movieNum);
		if(dto == null) {
			return true;
		}
		
		return false;
	}
	
	
	// 같은 상영날짜, 상영시작시간, 상영관 번호인 경우 체크
	boolean checkExistSchedule(String onDate, String startTime, String therNum) {
		// 같은 상영날짜, 상영시작시간, 상영관 번호 가 있으면 true 반환
		MovieDTO dto = null;
		
		try {
			dto = dao.checkExistSchedule(onDate, startTime, therNum);
			if(dto != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	// 상영코드가 존재하는지 체크
	boolean checkOnNum(String onNum) {
		MovieDTO dto = dao.findOnNum(onNum);
		
		if(dto == null) {
			return true;
		}
		return false;
	}
	
	// 상영관번호 체크
	int checkTheater(String therNum) {
		int TheaterExist = 0;
		List<MovieDTO> list = dao.checkTheater();
		for(MovieDTO dto : list) {
			if (therNum.equals(dto.getTherNum())) {
				++TheaterExist;
			}
		}
		
		return TheaterExist;
	}
	
	// 상영일자 < 종영일자 입력 체크
	boolean checkOpneClose(String openDate, String closeDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date openDate1 = sdf.parse(openDate);
			Date closeDate1 = sdf.parse(closeDate);
			int result = openDate1.compareTo(closeDate1);
			
			if(result > 0) {
				return true;
			}
		} catch (ParseException e) {
			System.out.println("날짜 형식이 잘못되었습니다.");
			throw e;
		}
		return false;
	}
	

}
