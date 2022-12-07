package svc;

import java.sql.Connection;

import dao.BoardDAO;
import db.JdbcUtil;
import vo.BoardBean;

public class BoardDetailService {
	
	// 글 상세정보 조회
	// => 단, 글번호와 함께 조회수 증가 여부를 파라미터로 전달
	public BoardBean getBoard(int board_num, boolean isUpdateReadcount) {
		System.out.println("BoardDetailService - getBoard()");
		BoardBean board = null;
		
		Connection con = JdbcUtil.getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		// BoardDAO의 selectBoard() 메서드 호출하여 게시글 상세정보 조회 작업을 수행
		board = dao.selectBoard(board_num);
//		System.out.println(board);
		
//		만약, BoardBean 객체가 null이 아니고, isUpdateReadcount가 true일 경우
//		BoardDAO 클래스의 updateReadcount() 메서드 호출하여 게시물 조회수 증가 작업 수행하고,
//		작업이 성공했을 경우 commit 작업을 수행 및 BoardBean 객체의 조회수 값 1 증가
		// => 파라미터 : 글번호, isUpdateReadcount   리턴타입 : int(updateCount)
		if(board != null && isUpdateReadcount) {
			int updateCount = dao.updateReadcount(board_num);
			
			if(updateCount > 0) { // 작업 성공시
				JdbcUtil.commit(con);
				
				// 만약, 조회수 증가 전 조회 작업을 먼저 수행했을 경우
				// 수동으로 BoardBean 객체의 조회수를 1만큼 증가시켜야함
				board.setBoard_readcount(board.getBoard_readcount() + 1);
			}
		} 
//		else { // BoardBean 객체가 비어있을 경우 rollback 작업 수행 (안해도됨)
//			JdbcUtil.rollback(con);
//		}
		
		JdbcUtil.close(con);
		
		return board;
	}
		
}
