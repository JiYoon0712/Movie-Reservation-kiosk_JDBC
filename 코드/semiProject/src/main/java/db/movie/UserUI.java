package db.movie;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class UserUI {
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private UserDAO dao = new UserDAO();
	// 메뉴
	public void userManage() {
		int ch = 0 ;
		
		while(true) {
			System.out.println("\n[고객]");
			
			try {
				System.out.print("1.영화 예매  2.예매 취소  3.메인 => ");
				ch = Integer.parseInt(br.readLine());
				
				if(ch == 3) return;
				
				switch(ch) {
				case 1: reservation(); break;
				case 2: cancel(); break;
				}
				
			} catch (Exception e) {
			}
		}
	}
	
// =================================================================================================
	// 1.영화 예매  
	public void reservation() {
		ReservDTO dto = new ReservDTO();
		MovieDTO dto1 = new MovieDTO();
		
		try {
			
			System.out.println("\n영화 예매 ..");
			int todayMovie = onMovieList();	// - 상영하는 영화 리스트 보여주기
			if(todayMovie == 0) {
				System.out.println(" * 현재 상영예정인 영화가 존재하지 않습니다. * ");;
				return;
			} 
			
			System.out.print(">> 원하는 영화 코드 : ");
			String movieNum = br.readLine();
			
				
			if(checkMovieNum(movieNum)) {	// 영화코드 체크
				dto1.setMovieNum(movieNum);
			} else {
				System.out.println("해당 영화코드는 존재하지 않습니다.");
				return;
			}
			
			// -- 상영코드 --
			int todayList = onTodayMovieList(movieNum);	// - 해당하는 영화의 *오늘 상영 리스트* 보여주기
			if(todayList == 0) {
				System.out.println(" * 해당 영화는 오늘 상영 스케줄이 없습니다. * ");
				return;
			} 
			
			System.out.print(">> 원하는 상영 코드 : ");
			
			String onNum = br.readLine();
			if(checkTodayonNum(onNum, movieNum)) {	// 오늘 상영 리스트 이외의 상영코드 입력 체크
				System.out.print("올바른 상영코드를 입력하세요.\n");
				return;
			}
			
			if(! checkRemSeat(movieNum)) {	 // 인원 초과 체크
				System.out.println(" 인원이 마감되어 예매가 불가능합니다. \n");
				return;
			}
				
			if(checkOnNum(onNum, movieNum)) { 	// 상영코드 체크
				dto.setOnNum(onNum);
			} else {
				System.out.println("해당 상영코드는 존재하지 않습니다.");
				return;
			}
			
			// -- 인원수 -- 
			System.out.print(">> 인원수 : ");
			int person = Integer.parseInt(br.readLine());
			if (checkRemSeat2(movieNum, person)) {	// 인원 초과 체크2
				System.out.println("잔여좌석을 확인해주세요.");
				return;
			} else {
				dto.setPerson(person);
			}
			
			System.out.println();
			
			// -- 예매금액 -- 
			int resPay = person * 10000;
			dto.setResPay(resPay);
			
			// -- 예매일자 -- 
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String resDate = sdf.format(date);
			dto.setResDate(resDate);
			
			while(true) {
				System.out.print("예매하시겠습니까 ? (1:네, 2:아니요) >> ");
				String req = br.readLine();
				
				if(req.equals("1")) {
					dao.insertReserv(dto);	// 예매 추가
					System.out.println("예매가 완료되었습니다.");
					ReservShow(dto.getResNum());
					break;
				} else if(req.equals("2")) {
					System.out.println("예매가 취소되었습니다.");
					return;
				} else {
					continue;
				}
			}
			
			System.out.println();
		}  catch (NumberFormatException e) {
			System.out.println("숫자만 입력 가능합니다.");
		} catch (Exception e) {
			System.out.println("데이터 추가가 실패했습니다.");
		}
		System.out.println();
	}
	
// ==============================================================================================
	
	// 1-1. 상영중인 영화 리스트
	public int onMovieList() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(date);
		
		int todayMovie = 0;
		List<MovieDTO> list =  dao.onMovieList(today);

		System.out.println("\n========[ 현재 상영중인 영화 리스트 ]=========");
		System.out.print("\n영화코드\t영화제목\t\t감독\t장르\n");
		System.out.println("---------------------------------------");
		
		for(MovieDTO dto : list) {
			System.out.print(dto.getMovieNum()+"\t");
			System.out.print(dto.getMovieName()+"\t\t");
			System.out.print(dto.getDirector()+"\t");
			System.out.println(dto.getGenre());
			
			++todayMovie;
		}
		System.out.println();
		return todayMovie;
	}
	
	// 1-2. (오늘 기준)해당 영화의 시간리스트
	public int onTodayMovieList(String movieNum) {
		int todaylist = 0;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(date);
		
		List<MovieDTO> list = dao.onTodayMovieList(movieNum, today);
		
		System.out.println("\n============[ 오늘 상영 리스트 ]=============");
		System.out.print("\n상영코드\t상영시작시간\t상영관번호\t전체/잔여좌석\n");
		System.out.println("-----------------------------------------");
		
		for(MovieDTO dto : list) {
			System.out.print(dto.getOnNum()+"\t");
			System.out.print(dto.getStartTime()+"\t\t");
			System.out.print(dto.getTherNum() + "\t");
			System.out.print(dto.getTotSeat() + "/");
			System.out.println(dto.getRemSeat() + "석\t");
			
			++todaylist;
		}
		System.out.println();
		return todaylist;
	}
	

	// 1-3. 예매내역 보여주기
	public void ReservShow(String resNum) {
		ReservDTO dto = dao.findResNum(resNum);
		
		System.out.print("| 예매번호 : "+dto.getResNum()+" | ");
		System.out.print("예매일자 : "+dto.getResDate()+" | ");
		System.out.print("예매금액 : "+dto.getResPay()+" | ");
		System.out.print("인원수 : "+dto.getPerson()+" | ");
		System.out.print("상영코드 : "+dto.getOnNum()+" | ");
	}
	
	// 1-4. 예매 내역 전체
	public void ReservList() {
		System.out.println("\n======================== [ 예매전체내역 ] ========================");
		System.out.print("\n예매번호\t예매일자\t\t예매금액\t\t인원수\t상영코드\n");
		System.out.println("---------------------------------------------------------------");
		List<ReservDTO> list = dao.ReservList();
		
		for(ReservDTO dto : list) {
			System.out.print(dto.getResNum() + "\t");
			System.out.print(dto.getResDate() + "\t");
			System.out.print(dto.getResPay() + "\t\t");
			System.out.print(dto.getPerson() + "\t");
			System.out.println(dto.getOnNum());
		}
		System.out.println();
	}
	
	
// CHECK *************************************************************************************
	
	// -- 영화코드 체크
	public boolean checkMovieNum(String movieNum) {
		MovieDTO dto = dao.findMovieNum(movieNum);
		
		if(dto != null) {
			return true;
		}
		return false;
	}

	
	// -- 상영코드 체크
	public boolean checkOnNum(String onNum, String movieNum) {
		MovieDTO dto = dao.findOnNum(onNum, movieNum);
		
		if(dto != null) {
			return true;
		}
		return false;
	}
	
	
	// -- 예매좌석 초과 체크 ( 0명일때 예매 불가 )
	public boolean checkRemSeat(String movieNum) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(date);
		List<MovieDTO> list = dao.onTodayMovieList(movieNum, today);
		
		for(MovieDTO dto : list) {
			if(dto.getRemSeat() != 0) {
				return true;
			}
		}
		return false;
	}
	
	// -- 예매좌석 초과 체크2 ( 예매좌석 수 < 인원수 )
	public boolean checkRemSeat2(String movieNum, int person) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(date);
		List<MovieDTO> list = dao.onTodayMovieList(movieNum, today);
		
		for(MovieDTO dto : list) {
			if(dto.getRemSeat() < person ) {
				return true;
			}
		}
		return false;
	}	
	
	// -- 원하는 상영코드 입력시, 오늘 상영리스트의 상영코드만 입력하도록 체크
	public boolean checkTodayonNum(String onNum, String movieNum) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(date);
		List<MovieDTO> list = dao.onTodayMovieList(movieNum, today);
		
		for(MovieDTO dto : list) {
				// dto.getOnNum() 와 onNum이 다르다면
			if(! dto.getOnNum().equals(onNum)) return true;
			}
		
			return false;
		}
	

	
// =============================================================================================
	
	
	// 2.예매 취소
	public void cancel() {
		System.out.println("\n예매 취소 ..");
		
		try {
			System.out.print("예매번호 : ");
			String resNum = br.readLine();
			
			
			
			while(true) {
				System.out.print("예매를 취소하시겠습니까 ? (1:네, 2:아니요) >> ");
				String req = br.readLine();
				
				if(req.equals("1")) {
					int result = dao.cancelReserv(resNum);
					if(result == 0) {
						System.out.println("예매 내역이 조회되지 않습니다.");
						return;
					} 
					
					System.out.println(resNum + " >> 예매가 취소되었습니다.");
					
					break;
				} else if(req.equals("2")) {
					System.out.println("예매가 취소되었습니다.");
					return;
				} else {
					continue;
				}
			}
			
			
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}

