package db.movie;
// (영화 수정 ) 
// (영화 리스트)

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import db.util.DBConn;

public class AdminDAO {
	private Connection conn = DBConn.getConnection();

	// 영화 추가
	public int insertMovie(MovieDTO dto) throws SQLException{
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq = 0;
		
		try {
			sql = "SELECT movieNum_seq.NEXTVAL from dual";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				seq = rs.getInt(1);
			}
			
			rs.close();
			pstmt.close();
			
			rs = null;
			pstmt =null;
			
			dto.setMovieNum(Integer.toString(seq));
			
			sql = "INSERT INTO movie(movieNum, movieName, director, openDate, closeDate, genre) "
					+ "VALUES(?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMovieNum());
			pstmt.setString(2, dto.getMovieName());
			pstmt.setString(3, dto.getDirector());
			pstmt.setString(4, dto.getOpenDate());
			pstmt.setString(5, dto.getCloseDate());
			pstmt.setString(6, dto.getGenre());
			
			result = pstmt.executeUpdate();
			
		} catch(SQLIntegrityConstraintViolationException e){
			if(e.getErrorCode() == 1) {
				System.out.println("영화코드 중복입니다.");
			} else if(e.getErrorCode() == 1400) {
				System.out.println("필수 사항을 입력하지 않았습니다.");
			} else if(e.getErrorCode() == 2291){
				System.out.println("해당 영화코드는 존재하지 않습니다.");
			} else {
				System.out.println(e.toString());
			}
			throw e;
		} catch (SQLDataException e) {
			if(e.getErrorCode() == 1840 || e.getErrorCode() == 1861 || e.getErrorCode() == 1841) {
				System.out.println("날짜입력 형식 오류입니다.");
			} else {
				System.out.println(e.toString());
			}
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		return result;
	}
	
	// 영화 수정
	 public int updateMovie(MovieDTO dto) throws SQLException {
		 PreparedStatement pstmt = null;
		 int result = 0;
		 String sql;
		 
		 try {
			 sql = "UPDATE movie SET movieName=?, director=?, openDate=?, closeDate=?, genre=?"
			 		+ " WHERE movieNum = ?";
			 
			 pstmt = conn.prepareStatement(sql);
			 
			 pstmt.setString(1, dto.getMovieName());
			 pstmt.setString(2, dto.getDirector());
			 pstmt.setString(3, dto.getOpenDate());
			 pstmt.setString(4, dto.getCloseDate());
			 pstmt.setString(5, dto.getGenre());
			 pstmt.setString(6, dto.getMovieNum());
			 
			 result = pstmt.executeUpdate();
			 
		} catch (SQLIntegrityConstraintViolationException e) {
			if(e.getErrorCode() == 1400) {	// NOT NULL 위반
				System.out.println("필수 입력사항을 입력하지 않았습니다.");
			} else {
				System.out.println(e.toString());
			}
		} catch (SQLException e) {
			if(e.getErrorCode() == 1840 || e.getErrorCode() == 1861 || e.getErrorCode() == 1841) {
				System.out.println("날짜 형식이 올바르지 않습니다.");
			}else {
				System.out.println(e.toString());
			}
		
			throw e;
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e2) {
			}
		}
		 
		 return result;
	 }
	
	

	// 영화 스케줄 추가
	// 상영코드, 상영날짜, 상영시작시간, *영화코드, *상영관번호
	public int insertSchedule(MovieDTO dto) throws SQLException {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq = 0;
		
		try {
			sql = "SELECT onNum_seq.NEXTVAL from dual";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				seq = rs.getInt(1);
			}
			
			rs.close();
			pstmt.close();
			
			rs = null;
			pstmt =null;
			
			dto.setOnNum(Integer.toString(seq));
			
			sql = "INSERT INTO schedule(onNum, onDate, startTime, movieNum, therNum) "
					+ "VALUES (?,?,TO_CHAR(TO_DATE(?,'HH24:MI'),'HH24:MI'),?,?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getOnNum());
			pstmt.setString(2, dto.getOnDate());
			pstmt.setString(3, dto.getStartTime());	// - 상영시간
			pstmt.setString(4, dto.getMovieNum());
			pstmt.setString(5, dto.getTherNum());
			
			result = pstmt.executeUpdate();
			
		} catch(SQLIntegrityConstraintViolationException e){
			if(e.getErrorCode() == 1) {
				System.out.println("상영코드 중복입니다.");
			} else if(e.getErrorCode() == 1400) {
				System.out.println("필수 사항을 입력하지 않았습니다.");
			} else {
				System.out.println(e.toString());
			}
			throw e;
		} catch (SQLDataException e) {
			if(e.getErrorCode() == 1840 || e.getErrorCode() == 1861) {
				System.out.println("날짜입력 형식 오류입니다.");
			} else {
				System.out.println(e.toString());
			}
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		
		return result;
	}
	
	
	// 영화 스케줄 수정
		public int updateSchedule(MovieDTO dto) throws SQLException {
			int result = 0;
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "UPDATE schedule SET  onDate = ?, startTime = TO_CHAR(TO_DATE(?,'HH24:MI'),'HH24:MI'), movieNum = ?, therNum = ?"
						+ " WHERE onNum = ?";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, dto.getOnDate());
				pstmt.setString(2, dto.getStartTime());
				pstmt.setString(3, dto.getMovieNum());
				pstmt.setString(4, dto.getTherNum());
				pstmt.setString(5, dto.getOnNum());
				
				result = pstmt.executeUpdate();
				
			} catch (SQLIntegrityConstraintViolationException e) {
				if(e.getErrorCode() == 1400) {
					System.out.println("필수 입력사항을 입력하지 않았습니다.");
				} else if(e.getErrorCode() == 2291){
					System.out.println("영화코드가 존재하지 않습니다.");
				} else {
					System.out.println(e.toString());
				}
			} catch (SQLException e) {
				if(e.getErrorCode() == 1840 || e.getErrorCode() == 1861) {
					System.out.println("날짜 형식이 올바르지 않습니다.");
				}else {
					System.out.println(e.toString());
				}
			
				throw e;
			} finally {
				try {
					if(pstmt != null) {
						pstmt.close();
					}
				} catch (Exception e2) {
				}
			}
			
			return result;
		}
		
		
	// 영화 스케줄 삭제 
	public int deleteSchedule(String onNum) throws SQLException {
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM schedule WHERE onNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, onNum);
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("예매가 완료되어 상영스케줄을 삭제할 수 없습니다.");
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return result;
	}
	
	
    // 상영코드 검색
	public MovieDTO findOnNum(String onNum) {
		MovieDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT TO_CHAR(onDate,'yyyy-mm-dd') onDate, startTime, movieNum, therNum "
					+ " FROM schedule WHERE onNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, onNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MovieDTO();
				
				dto.setOnDate(rs.getString("onDate"));
				dto.setStartTime(rs.getString("startTime"));
				dto.setOnNum(rs.getString("movieNum"));
				dto.setTherNum(rs.getString("therNum"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return dto;
	}
	
	// 영화코드 검색
	public MovieDTO findMovieNum(String movieNum) {
		MovieDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT movieNum "
					+ " FROM movie WHERE movieNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, movieNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MovieDTO();
				
				dto.setMovieNum(rs.getString("movieNum"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return dto;
	}

	// 영화 전체 리스트
	public List<MovieDTO> onMovieList() {
		List<MovieDTO> list = new ArrayList<MovieDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT movieNum, movieName, director,TO_CHAR(openDate,'YYYY-MM-DD') openDate,"
					+ "TO_CHAR(closeDate,'YYYY-MM-DD') closeDate, genre "
					+ "FROM movie "
					+ "ORDER BY movieNum";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MovieDTO dto = new MovieDTO();
				dto.setMovieNum(rs.getString("movieNum"));
				dto.setMovieName(rs.getString("movieName"));
				dto.setDirector(rs.getString("director"));
				dto.setOpenDate(rs.getString("openDate"));
				dto.setCloseDate(rs.getString("closeDate"));
				dto.setGenre(rs.getString("genre"));
				
				list.add(dto);
			}
		} catch (SQLException e) {
			if(e.getErrorCode() == 1840 || e.getErrorCode() == 1861 ||e.getErrorCode()==1843) {
				System.out.println("날짜 형식이 올바르지 않습니다.");
			}else {
				System.out.println(e.toString());
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return list;
	}
	
	// 상영 스케줄 전체 리스트
	public List<MovieDTO> onSceduleList(){
		List<MovieDTO> list = new ArrayList<MovieDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;	
		
		try {
			sql = "SELECT onNum, TO_CHAR(onDate,'yyyy-mm-dd') onDate, startTime, m.movieNum, therNum, movieName"
					+ " FROM schedule s JOIN movie m ON s.movieNum = m.movieNum"
					+ " ORDER BY m.movieNum";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MovieDTO dto = new MovieDTO();
				
				dto.setOnNum(rs.getString("onNum"));
				dto.setOnDate(rs.getString("onDate"));
				dto.setStartTime(rs.getString("startTime"));
				dto.setMovieNum(rs.getString("movieNum"));
				dto.setTherNum(rs.getString("therNum"));
				dto.setMovieName(rs.getString("movieName"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return list;
	}
	
	// 해당 상영코드의 상영스케줄
	MovieDTO ScheduleOnNumList (String onNum){
		MovieDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;	
		
		try {
			sql = "SELECT onNum, TO_CHAR(onDate,'yyyy-mm-dd') onDate, startTime, m.movieNum, therNum, movieName"
					+ " FROM schedule s JOIN movie m ON s.movieNum = m.movieNum"
					+ " WHERE onNum = ? "
					+ " ORDER BY m.movieNum ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, onNum);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				dto = new MovieDTO();
				
				dto.setOnNum(rs.getString("onNum"));
				dto.setOnDate(rs.getString("onDate"));
				dto.setStartTime(rs.getString("startTime"));
				dto.setMovieNum(rs.getString("movieNum"));
				dto.setTherNum(rs.getString("therNum"));
				dto.setMovieName(rs.getString("movieName"));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return dto;
	}
	
	//해당 영화코드의 상영스케줄
	List<MovieDTO> movieScheduleList (String movieNum){
		List<MovieDTO> list = new ArrayList<MovieDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;	
		
		try {
			sql = "SELECT onNum, TO_CHAR(onDate,'yyyy-mm-dd') onDate, startTime, m.movieNum, therNum, movieName"
					+ " FROM schedule s JOIN movie m ON s.movieNum = m.movieNum"
					+ " WHERE m.movieNum = ?"
					+ " ORDER BY m.movieNum";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, movieNum);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MovieDTO dto = new MovieDTO();
				
				dto.setOnNum(rs.getString("onNum"));
				dto.setOnDate(rs.getString("onDate"));
				dto.setStartTime(rs.getString("startTime"));
				dto.setMovieNum(rs.getString("movieNum"));
				dto.setTherNum(rs.getString("therNum"));
				dto.setMovieName(rs.getString("movieName"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return list;
	}
	

	// 영화코드의 개봉일자 <= 상영날짜 <= 종영일자  체크
	public MovieDTO checkonDate(String movieNum) {
		MovieDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;	
		
		try {
			 sql = " SELECT TO_CHAR(openDate,'YYYY-MM-DD')openDate, TO_CHAR(closeDate,'YYYY-MM-DD')closeDate "
			 		+ " FROM movie "
		            + " WHERE movieNum = ? ";

			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, movieNum);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MovieDTO();
				dto.setOpenDate(rs.getString("openDate"));
				dto.setCloseDate(rs.getString("closeDate"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return dto;
	}
	

		// 같은 영화코드, 상영날짜, 상영시작시간, 상영관 번호인 테이블 구하기
		public MovieDTO checkExistSchedule(String onDate, String startTime, String therNum) {
			MovieDTO dto = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT movieNum, onDate, startTime, therNum "
						+ "FROM schedule "
						+ "WHERE onDate = ? AND startTime = ? AND therNum = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, onDate);
				pstmt.setString(2, startTime);
				pstmt.setString(3, therNum);
				
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					dto = new MovieDTO();
					
					dto.setOnDate(rs.getString("onDate"));
					dto.setStartTime(rs.getString("startTime"));
					dto.setTherNum(rs.getString("therNum"));
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e2) {
					}
				}
				
				if(rs != null) {
					try {
						rs.close();
					} catch (Exception e2) {
					}
				}
			}
			
			return dto;
		}
		
		// 상영관 검색
		public List<MovieDTO> checkTheater() {
			List<MovieDTO> list = new ArrayList<MovieDTO>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT therNum FROM theater";
				pstmt = conn.prepareStatement(sql);
				
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					MovieDTO dto = new MovieDTO();
					dto.setTherNum(rs.getString("therNum"));
					
					list.add(dto);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(rs != null) {
					try {
						rs.close();
					} catch (Exception e2) {
					}
				}
				
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e2) {
					}
				}
			}
			
			return list;
		}
		
		// 테스트 용도 -  영화 스케줄 전체 삭제 
		public int deleteAllSchedule() throws SQLException {
			int result =0;
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "DELETE FROM schedule";
				pstmt = conn.prepareStatement(sql);
				result = pstmt.executeUpdate();
				
			} catch (SQLException e) {
				throw e;
			} finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e2) {
					}
				}
			}
			return result;
		}
		
	// 테스트 용도 -  영화 전체 삭제 
		public int deleteAllMovie() throws SQLException {
			int result =0;
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "DELETE FROM movie";
				pstmt = conn.prepareStatement(sql);
				result = pstmt.executeUpdate();
				
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e2) {
					}
				}
			}
			return result;
		}	
		
		// 테스트 용도 -  예매 전체 삭제 
		public int deleteAllReserve() throws SQLException {
			int result =0;
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "DELETE FROM reservation";
				pstmt = conn.prepareStatement(sql);
				result = pstmt.executeUpdate();
				
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e2) {
					}
				}
			}
			return result;
		}
		
		
}
