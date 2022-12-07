package svc;

import java.sql.Connection;

import dao.BoardDAO;
import db.JdbcUtil;

public class BoardDeleteProService {

	// 글 삭제 가능 여부(= 패스워드 일치 여부) 판별 요청 수행할 isBoardWriter() 메서드 정의
	// => 파라미터 : 글번호, 패스워드    리턴타입 : boolean(isBoardWriter)
	public boolean isBoardWriter(int board_num, String board_pass) {
		System.out.println("BoardDeleteProService - getDelete()");

		Connection con = JdbcUtil.getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		// BoardDAO 의 isBoardWriter() 메서드를 호출하여 패스워드 확인 작업 수행
		// => 파라미터 : 글번호, 패스워드    리턴타입 : boolean(isBoardWriter)
		boolean isboardwriter = dao.isBoardWriter(board_num, board_pass);
		
		JdbcUtil.close(con);
		
		return isboardwriter;
	}

	// 글번호(board_num)에 해당하는 게시물 삭제 작업 수행하는 removeBoard() 메서드
	//  => 파라미터 : 글번호(board_num)    리턴타입 : int(deleteCount)
	public boolean removeBoard(int board_num) {
		System.out.println("BoardDeleteProService - isBoardWriter()");

		Connection con = JdbcUtil.getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		boolean isDeleteSuccess = false;
		
		// BoardDAO 의 deleteBoard() 메서드를 호출하여 글 삭제 작업 수행
		// => 파라미터 : 글번호    리턴타입 : int(deleteCount)
		int deleteCount = dao.deleteBoard(board_num); // 글 삭제 작업 수행
		
		if(deleteCount > 0) { // 삭제 작업 성공하면
			JdbcUtil.commit(con);
			isDeleteSuccess = true;
		} else { // 실패하면
			JdbcUtil.rollback(con);
		}
		
		JdbcUtil.close(con);
		
		return isDeleteSuccess;
	}

}
