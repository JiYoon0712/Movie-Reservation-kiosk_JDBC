package db.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import db.util.DBConn;

public class UserDAO {
	private Connection conn = DBConn.getConnection();
	List<ReservDTO> list = new ArrayList<ReservDTO>();
	
	// 상영중인 영화 리스트
	public List<MovieDTO> onMovieList(String today){
		List<MovieDTO> list = new ArrayList<MovieDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT DISTINCT m.movieNum, movieName, director, genre"
					+ "	FROM movie m JOIN schedule s ON m.movieNum = s.movieNum"
					+ " WHERE onNum IS NOT NULL "
					+ " AND ? >= openDate AND ? <= closeDate"
					+ " ORDER BY m.movieNum ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, today);
			pstmt.setString(2, today);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MovieDTO dto = new MovieDTO();
				
				dto.setMovieNum(rs.getString("movieNum"));
				dto.setMovieName(rs.getString("movieName"));
				dto.setDirector(rs.getString("director"));
				dto.setGenre(rs.getString("genre"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
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
	
	
	// (오늘 기준)해당 영화의 시간리스트
	public List<MovieDTO> onTodayMovieList(String movieNum, String date){
		List<MovieDTO> list = new ArrayList<MovieDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT s.onNum, TO_CHAR(onDate,'YYYY-MM-DD')onDate, startTime, s.therNum, totSeat, NVL(person, 0) person "
					+ "FROM schedule s "
					+ "	JOIN theater t ON s.therNum = t.therNum "
					+ "	LEFT OUTER JOIN ( "
					+ "		SELECT s.onNum, SUM(person) person "
					+ "		FROM schedule s  "
					+ "	JOIN reservation r ON s.onNum = r.onNum "
					+ "	GROUP BY s.onNum "
					+ " ) t ON s.onNum = t.onNum "
	            + " WHERE s.movieNum = ? AND onDate= ? AND TO_CHAR(TO_DATE(startTime,'HH24:MI'),'HH24:MI') >= TO_CHAR(SYSDATE,'HH24:MI') "
	            + " ORDER BY s.onNum";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, movieNum);
			pstmt.setString(2, date);
			rs = pstmt.executeQuery();
			
			
			while(rs.next()) {
				MovieDTO dto = new MovieDTO();
	
				dto.setOnNum(rs.getString("onNum"));
				dto.setStartTime(rs.getString("startTime"));
				dto.setTherNum(rs.getString("therNum"));
				dto.setTotSeat(rs.getInt("totSeat"));
				int remSeat = rs.getInt("totSeat")-rs.getInt("person");
				dto.setRemSeat(remSeat);
				
				list.add(dto);
			}
			
			
		} catch (SQLException e) {
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
	
	
	// 예매 추가
	public int insertReserv(ReservDTO dto) throws SQLException {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq = 0;
		
		try {
			sql = "SELECT resNum_seq.NEXTVAL from dual";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				seq = rs.getInt(1);
			}
			
			rs.close();
			pstmt.close();
			
			rs = null;
			pstmt =null;
			
			dto.setResNum(Integer.toString(seq));
			
			sql = "INSERT INTO reservation(resNum, resDate, resPay, person, onNum) "
					+ "VALUES (?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getResNum());
			pstmt.setString(2, dto.getResDate());
			pstmt.setInt(3, dto.getResPay());
			pstmt.setInt(4, dto.getPerson());
			pstmt.setString(5, dto.getOnNum());
			
			result = pstmt.executeUpdate();
			
		}  catch (SQLIntegrityConstraintViolationException e) {
			if(e.getErrorCode() == 1400) {	// NOT NULL 위반
				System.out.println("필수 입력사항을 입력하지 않았습니다.");
			} else {
				System.out.println(e.toString());
			}
		} catch (SQLDataException e) { 
			if(e.getErrorCode() == 1840 || e.getErrorCode() == 1861) {
				System.out.println("날짜 형식이 올바르지 않습니다.");
			}else {
				System.out.println(e.toString());
			}
			throw e;	
		} catch (SQLException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e2) {
			}
		}
	
		return result;
	}
	
	
	
	// 예매 내역 전체 조회
	public List<ReservDTO> ReservList(){
		List<ReservDTO> list = new ArrayList<ReservDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT resNum, TO_CHAR(resDate,'yyyy-MM-dd') resDate, resPay, person, onNum"
					+ " FROM reservation"
					+ " ORDER BY resNum";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ReservDTO dto = new ReservDTO();
				dto.setResNum(rs.getString("resNum"));
				dto.setResDate(rs.getString("resDate"));
				dto.setResPay(rs.getInt("resPay"));
				dto.setPerson(rs.getInt("person"));
				dto.setOnNum(rs.getString("onNum"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
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
	
	// 해당 예매내역조회
	public ReservDTO findResNum(String resNum){
		ReservDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT resNum, TO_CHAR(resDate,'yyyy-MM-dd') resDate, resPay, person, onNum"
					+ " FROM reservation"
					+ " WHERE resNum = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new ReservDTO();
				dto.setResNum(rs.getString("resNum"));
				dto.setResDate(rs.getString("resDate"));
				dto.setResPay(rs.getInt("resPay"));
				dto.setPerson(rs.getInt("person"));
				dto.setOnNum(rs.getString("onNum"));
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
	
	
	// 사용자가 선택한 상영코드검색					// 영화코드
	public MovieDTO findOnNum(String onNum, String movieNum) {
		MovieDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT TO_CHAR(onDate,'yyyy-mm-dd') onDate, startTime, movieNum, therNum "
					+ " FROM schedule WHERE onNum = ? AND movieNum =? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, onNum);
			pstmt.setString(2, movieNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MovieDTO();
				
				dto.setOnDate(rs.getString("onDate"));
				dto.setStartTime(rs.getString("startTime"));
				dto.setMovieNum(rs.getString("movieNum"));
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
	
	
	// 영화코드 체크
	public MovieDTO findMovieNum(String movieNum) {
		MovieDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT movieNum, movieName, director,TO_CHAR(openDate,'YYYY-MM-DD') openDate, "
					+ " TO_CHAR(closeDate,'YYYY-MM-DD') closeDate, genre "
					+ "	FROM movie "
					+ " WHERE movieNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, movieNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MovieDTO();
				
				dto.setMovieNum(rs.getString("movieNum"));
				dto.setMovieName(rs.getString("movieName"));
				dto.setDirector(rs.getString("director"));
				dto.setOpenDate(rs.getString("openDate"));
				dto.setCloseDate(rs.getString("closeDate"));
				dto.setGenre(rs.getString("genre"));

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
	
	
	// 예매 취소
	public int cancelReserv(String resNum) throws SQLException { 
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM reservation WHERE resNum =?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, resNum);
			
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



