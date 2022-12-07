package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;

import db.JdbcUtil;
import vo.BoardBean;

// 실제 비즈니스 로직을 수행하는 BoardDAO 클래스 정의
// => 각 Service 클래스 인스턴스에서 BoardDAO 인스턴스에 접근 시 고유 데이터가 불필요하므로
//	  BoardDAO 인스턴스는 애플리케이션에서 단 하나만 생성하여 공유해도 된다!
// 	  따라서, 싱글톤 디자인 패턴을 적용하여 클래스를 정의하면 메모리 낭비를 막을 수 있다!
public class BoardDAO {
	// ----------------- 싱글톤 디자인 패턴을 활용한 BoardDAO 인스턴스 생성 작업 ------------
	// 1. 외부에서 인스턴스 생성이 불가능하도록 생성자는 private 접근제한자로 선언
	// 2. 자신의 클래스 내에서 직접 인스턴스를 생성하여 멤버변수에 저장
	//	  => 인스턴스 생성없이 클래스가 메모리에 로딩될 때 함께 로딩되도록 static 변수로 선언
	//	  => 외부에서 접근하여 함부로 값을 변경할 수 없도록 private 접근제한자로 선언
	// 3. 생성된 인스턴스를 외부로 리턴하는 Getter 메서드 정의
	//	  => 인스턴스 생성없이 클래스가 메모리에 로딩될 때 함께 로딩되도록 static 메서드로 선언
	// 	  => 누구나 접근 가능하도록 public 접근제한자로 선언 
	private BoardDAO() {};
	
	private static BoardDAO instance = new BoardDAO();

	public static BoardDAO getInstance() {
		return instance;
	}

	// -------------------------------------------------------------------
	// 데이터베이스 접근에 사용할 Connection 객체를 Service 객체로부터 전달받기 위한
	// Connection 타입 멤버변수 선언 및 Setter 메서드 정의
	private Connection con;

	public void setConnection(Connection con) {
		this.con = con;
	}
	// -------------------------------------------------------------------
	// 글쓰기 작업 수행
	// => Service 로부터 전달받은 BoardBean 객체 사용하여 INSERT 작업 수행
	// => 파라미터 : BoardBean 객체   리턴타입 : int(insertCount)
	public int insertBoard(BoardBean board) {
		System.out.println("BoardDAO - insertBoard()");
		
		// INSERT 작업 결과를 리턴받아 저장할 변수 선언
		int insertCount = 0;
		
		// 데이터베이스 작업에 필요한 변수 선언
		PreparedStatement pstmt = null, pstmt2 = null;
		ResultSet rs = null;
		
		try {
			// 새 글 번호 계산을 위해 기본 board 테이블의 모든 번호(board_num) 중 가장 큰 번호 조회
			// => 조회 결과 +1 값을 새 글 번호로 지정하고, 조회 결과가 없으면 기본값 1로 설정
			// => MySQL 구문의 MAX() 함수 사용(SELECT MAX(컬럼명) FROM 테이블명)
			int board_num = 1; // 새 글 번호 , 기본값 1 설정
			
			String sql = "SELECT MAX(board_num) FROM board";
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) { // 조회 결과가 있을 경우(= 기존 게시물이 하나라도 존재할 경우)
				// 만약, 게시물이 존재하지 않을 경우 DB에서 NULL, rs.next()는 false
				board_num = rs.getInt(1) + 1; // 기존 게시물 번호 중 가장 큰 번호(= 조회 결과 + 1
				// rs.getInt(1) -> MAX(idx) 컬럼의 조회 결과 중 첫번째 행
			}
			                                                                          
			System.out.println("새 글 번호 : " + board_num);
			// ----------------------------------------------------------------------
			// 전달받은 데이터(BoardBean 객체)를 사용하여 INSERT 작업 수행
			// => 참조글번호(board_re_ref)는 새 글 번호와 동일한 번호로 지정
			// => 들여쓰기 레벨(board_re_lev)과 순서번호(board_re_seq)는 0으로 지정
			sql = "INSERT INTO board VALUES (?,?,?,?,?,?,?,?,0,0,0,now())";
			pstmt2 = con.prepareStatement(sql);
			pstmt2.setInt(1, board_num); // 글번호
			pstmt2.setString(2, board.getBoard_name()); 
			pstmt2.setString(3, board.getBoard_pass());
			pstmt2.setString(4, board.getBoard_subject());
			pstmt2.setString(5, board.getBoard_content());
			pstmt2.setString(6, board.getBoard_file()); // 원본파일명
			pstmt2.setString(7, board.getBoard_real_file()); // 실제파일명
			pstmt2.setInt(8, board_num); // 참조글번호(글쓰기는 글번호와 동일하게 사용)
//			pstmt2.setInt(9, board_re_lev); // 들여쓰기레벨
//			pstmt2.setInt(10, board_re_seq); // 순서번호
			
			insertCount = pstmt2.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - insertBoard()");
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(pstmt2);
			// 주의! Connection 객체는 Service 클래스가 관리하므로 DAO에서 반환 금지!
			// => 반환하면 Service에서 NullPoinrException 발생!
		}
		
		return insertCount; // Service로 리턴
	}
	
	// 글목록 조회
	public List<BoardBean> selectBoardList(String keyword, int startRow, int listLimit) {
		System.out.println("BoardDAO - selectBoardList()");
		List<BoardBean> boardList = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// board 테이블의 모든 레코드 조회(idx 컬럼 기준 내림차순 정렬)
			// => 제목에 검색어를 포함하는 레코드 조회 (LIKE 사용)
			//	  (단, 쿼리에 직업 '%?%' 형태로 작성 시 ? 문자를 파라미터로 인식하지 못함
			//	  (따라서, setXXX() 메서드에 문자열 결합으로 처리
			// => 시작행번호부터 게시물 목록 수 만큼으로 갯수 제한(LIMIT 시작행번호, 목록수)
			// 	  (단, 시작행번호 첫번째는 0부터 시작)
			//	  (또한, LIMIT 에 파라미터 하나만 사용 시 목록 갯수로 사용됨 -> 최근글 목록에 사용)
			// =>  정렬 : 참조글번호(board_re_ref) 기준 내림차순,
			//			  순서번호(board_re_seq) 기준 오름차순
			String sql = "SELECT * FROM board WHERE board_subject LIKE ? ORDER BY board_re_ref DESC, board_re_seq ASC LIMIT ?,?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "%"+keyword+"%");
			pstmt.setInt(2, startRow);
			pstmt.setInt(3, listLimit);
			
			rs = pstmt.executeQuery();
			
			// 전체 목록 저장할 List 객체 생성
			boardList = new ArrayList<BoardBean>();
			
			// BoardBean 객체(board) 생성 후 조회 데이터 저장
			while(rs.next()) {
				// 조회 결과가 있을 경우
				BoardBean board = new BoardBean();
				board.setBoard_num(rs.getInt("board_num"));
				board.setBoard_name(rs.getString("board_name"));
				board.setBoard_pass(rs.getString("board_pass"));
				board.setBoard_subject(rs.getString("board_subject"));
				board.setBoard_content(rs.getString("board_content"));
				board.setBoard_file(rs.getString("board_file"));
				board.setBoard_real_file(rs.getString("board_real_file"));
				board.setBoard_re_ref(rs.getInt("board_re_ref"));
				board.setBoard_re_lev(rs.getInt("board_re_lev"));
				board.setBoard_re_seq(rs.getInt("board_re_seq"));
				board.setBoard_readcount(rs.getInt("board_readcount"));
				board.setBoard_date(rs.getTimestamp("board_date"));
				
				// 전체 목록 저장하는 List 객체에 게시물 정보가 저장된 BoardBean 객체 추가
				boardList.add(board);
			}
			
		} catch (SQLException e) {
			System.out.println("BoardDAO - selectBoardList()");
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
		
		
		return boardList;
	}
	
	// 게시물 갯수 조회 - selectBoardListCount()
	// => 파라미터 : 검색어   리턴타입 : int(listCount)
	public int selectBoardListCount(String keyword) {
		int listCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
				
		try {
			// board 테이블의 모든 레코드 조회(idx 컬럼 기준 내림차순 정렬)
			// => 제목에 검색어를 포함하는 레코드 조회 (LIKE 사용)
			//	  (단, 쿼리에 직업 '%?%' 형태로 작성 시 ? 문자를 파라미터로 인식하지 못함
			//	  (따라서, setXXX() 메서드에 문자열 결합으로 처리
			// =>  정렬 : 참조글번호(board_re_ref) 기준 내림차순,
			//			  순서번호(board_re_seq) 기준 오름차순
			String sql = "SELECT COUNT(*) FROM board WHERE board_subject LIKE ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "%"+keyword+"%");
			
			rs = pstmt.executeQuery();
			
			// 조회 결과가 있을 경우 listCount 변수에 저장
			if(rs.next()) {
				listCount = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			System.out.println("BoardDAO - selectBoardList()");
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
		
		return listCount;
	}
	
	// 게시글 상세정보 조회
	public BoardBean selectBoard(int board_num) {
		BoardBean board = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT * FROM board WHERE board_num = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				board = new BoardBean();
				board.setBoard_num(rs.getInt("board_num"));
				board.setBoard_name(rs.getString("board_name"));
				board.setBoard_pass(rs.getString("board_pass"));
				board.setBoard_subject(rs.getString("board_subject"));
				board.setBoard_content(rs.getString("board_content"));
				board.setBoard_file(rs.getString("board_file"));
				board.setBoard_real_file(rs.getString("board_real_file"));
				board.setBoard_re_ref(rs.getInt("board_re_ref"));
				board.setBoard_re_lev(rs.getInt("board_re_lev"));
				board.setBoard_re_seq(rs.getInt("board_re_seq"));
				board.setBoard_readcount(rs.getInt("board_readcount"));
				board.setBoard_date(rs.getTimestamp("board_date"));
			}
			
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - selectBoard()");
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
		
		return board;
	}

	// 조회수 증가
	public int updateReadcount(int board_num) {
		int updateCount = 0;
		PreparedStatement pstmt = null;
		
		try {
			String sql = "UPDATE board SET board_readcount = board_readcount+1 WHERE board_num = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			updateCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - updateReadcount()");
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pstmt);
		}
		
		return updateCount;
	}
	
	// 패스워드 일치하면 true 반환
	public boolean isBoardWriter(int board_num, String board_pass) {
		boolean isboardwriter = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT * FROM board WHERE board_num = ? AND board_pass = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			pstmt.setString(2, board_pass);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				board_pass = rs.getString(2);
				isboardwriter = true;
			}
			
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - isBoardWriter()");
			e.printStackTrace();
		}
				
		return isboardwriter;
	}
	
	// 게시글 삭제
	public int deleteBoard(int board_num) {
		int deleteCount = 0;
		
		PreparedStatement pstmt = null;
		
		try {
			String sql = "DELETE FROM board WHERE board_num = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			
			deleteCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - removeBoard()");
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pstmt);
		}
		
		return deleteCount;
	}
	
	// 게시글 수정
	public int updateBoard(BoardBean board) {
		int updateCount = 0;
		
		PreparedStatement pstmt = null;
		
		try {
			// board 테이블의 제목, 내용을 변경(이름은 고정)
			String sql = "UPDATE board SET board_subject = ?, board_content = ?";
					// 단, 파일명이(board_file)이 null이 아닐 경우에만 파일명도 수정
					// => 즉, 파일명을 수정하는 SET 절을 문장에 추가 결합!
					if(board.getBoard_file() != null) {
						sql += ", board_file = ?, board_real_file = ?";
					}
				   sql += " WHERE board_num = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, board.getBoard_subject());
			pstmt.setString(2, board.getBoard_content());
			// 단, 파일명이(board_file)이 null이 아닐 경우에만 
			// 파일명 파라미터를 교체한 setXXX() 메서드 호출
			// => 또한, null이 아닐때는 글 번호의 파리미터번호가 5번, 아니면 3번
			if(board.getBoard_file() != null) {
				pstmt.setString(3, board.getBoard_file());
				pstmt.setString(4, board.getBoard_real_file());
				pstmt.setInt(5, board.getBoard_num());	
			} else {
				pstmt.setInt(3, board.getBoard_num());	
			}
			
			updateCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - updateBoard()");
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pstmt);
		}
		
		return updateCount;
	}
	
	// 답글 쓰기
	public int insertReplyBoard(BoardBean board) {
		
		// INSERT 작업 결과를 리턴받아 저장할 변수 선언
		int insertCount = 0;
		
		// 데이터베이스 작업에 필요한 변수 선언
		PreparedStatement pstmt = null, pstmt2 = null;
		ResultSet rs = null;
		
		try {
			// 새 글 번호 계산을 위해 기본 board 테이블의 모든 번호(board_num) 중 가장 큰 번호 조회
			// => 조회 결과 +1 값을 새 글 번호로 지정하고, 조회 결과가 없으면 기본값 1로 설정
			// => MySQL 구문의 MAX() 함수 사용(SELECT MAX(컬럼명) FROM 테이블명)
			int board_num = 1; // 새 글 번호 , 기본값 1 설정
			
			String sql = "SELECT MAX(board_num) FROM board";
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) { // 조회 결과가 있을 경우(= 기존 게시물이 하나라도 존재할 경우)
				// 만약, 게시물이 존재하지 않을 경우 DB에서 NULL, rs.next()는 false
				board_num = rs.getInt(1) + 1; // 기존 게시물 번호 중 가장 큰 번호(= 조회 결과 + 1
				// rs.getInt(1) -> MAX(idx) 컬럼의 조회 결과 중 첫번째 행
			}
			                                                                          
			System.out.println("새 글 번호 : " + board_num);
			// ------------------------------------------------------------------------------
			
			
		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - insertBoard()");
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(pstmt2);
			// 주의! Connection 객체는 Service 클래스가 관리하므로 DAO에서 반환 금지!
			// => 반환하면 Service에서 NullPoinrException 발생!
		}
		
		return insertCount; // Service로 리턴
	}
}
