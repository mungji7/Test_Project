package svc;

import java.sql.Connection;

import dao.BoardDAO;
import db.JdbcUtil;
import vo.BoardBean;

public class BoardModifyProService {
	
	// 패스워드 일치 여부
	public boolean isBoardWriter(BoardBean board) {
		
		Connection con = JdbcUtil.getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		boolean isboardwriter = false;
		
		isboardwriter = dao.isBoardWriter(board.getBoard_num(), board.getBoard_pass()); // 패스워드 판별 수행
		
		JdbcUtil.close(con);
		
		return isboardwriter;
	}
	
	// 글 수정 작업
	public boolean modifyBoard(BoardBean board) {
		boolean isModifySuccess = false;
	
		Connection con = JdbcUtil.getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		// BoardDAO의 updateBoard() 메서드를 호출하여 글 수정 작업 수행
		// => 파라미터 : BoardBean 객체    리턴타입 : int(updateCount)
		int updateCount = dao.updateBoard(board);
		
		// 글 수정 결과 판별
		if(updateCount > 0) {
			JdbcUtil.commit(con);
			isModifySuccess = true;
		} else {
			JdbcUtil.rollback(con);
		}
		
		JdbcUtil.close(con);
		
		return isModifySuccess;
	}
}
