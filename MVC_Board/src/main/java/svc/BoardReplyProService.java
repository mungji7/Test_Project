package svc;

import java.sql.Connection;

import dao.BoardDAO;
import db.JdbcUtil;
import vo.BoardBean;

public class BoardReplyProService {

	// registBoard() 메서드를 호출하여 글쓰기 작업 요청
	// => 파라미터 : BoardBean 객체   리턴타입 : boolean(isWriteSuccess)
	public boolean registBoard(BoardBean board) {
		System.out.println("BoardReplyProService - registBoard()");
		
		// 1. 글쓰기 작업 요청 처리 결과를 저장할 boolean 타입 변수 선언
		boolean isWriteSuccess = false;
		
		Connection con = JdbcUtil.getConnection();
		BoardDAO dao = BoardDAO.getInstance(); 
		dao.setConnection(con);
		
		// 5. BoardDAO 객체의 xxx() 메서드를 호출하여 xxx 작업 수행 요청 및 결과 리턴받기
		//    insertReplyBoard() 메서드를 호출하여 글쓰기 작업 요청 및 결과 리턴받기
		// => 파라미터 : BoardBean 객체   리턴타입 : int(insertCount)
		int insertCount = dao.insertReplyBoard(board);
		
		// 6. 작업 처리 결과에 따른 트랙잭션 처리
		if(insertCount > 0) { // 성공시
			// INSERT 작업 성공했을 경우의 트랜잭션 처리(commit) 을 위해
			// JdbcUtil 클래스의 commit() 메서드를 호출하여 commit 작업 수행
			// => 파라미터 : Connection 객체
			JdbcUtil.commit(con); // 작업 처리 결과를 성공으로 표시하여 리턴하기 위해 isWriteSuccess를 true로 변경
			isWriteSuccess = true;
		} else { // 실패시
			// INSERT 작업 실패했을 경우의 트랜잭션 처리(rollback) 을 위해
			// JdbcUtil 클래스의 rollback() 메서드를 호출하여 rollback 작업 수행
			JdbcUtil.rollback(con);
			// isWriteSuccess 기본값이 false 이므로 변경 생략
		}
		
		JdbcUtil.close(con);
		
		return isWriteSuccess; // BoardReplyProAction 으로 리턴
	}
	
}
