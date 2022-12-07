package svc;

import java.sql.Connection;

import dao.BoardDAO;
import db.JdbcUtil;
import vo.BoardBean;

// Action 클래스로부터 작업 요청을 받아 DAO 클래스와 상호작용을 통해
// 실제 비즈니스 로직(DB 작업)을 수행하는 클래스
// 또한, DB 작업 수행 후 결과 판별을 통해 트랜잭션 처리(commit or rollback)도 수행
public class BoardWriteProService {
	// registBoard() 메서드를 호출하여 글쓰기 작업 요청
	// => 파라미터 : BoardBean 객체   리턴타입 : boolean(isWriteSuccess)
	public boolean registBoard(BoardBean board) {
		System.out.println("BoardWriteProService - registBoard()");
		
		// 1. 글쓰기 작업 요청 처리 결과를 저장할 boolean 타입 변수 선언
		boolean isWriteSuccess = false;
		
		// 2. JdbcUtil 객체로부터 Connection Pool에 저장된 Connection 객체 가져오기
		// => 트랜잭션 처리를 Service 객체에서 수행하므로
		//	  DAO가 아닌 Service가 Connection 객체를 관리함 - 트랜잭션 : DB 작업 단위의 묶음
		Connection con = JdbcUtil.getConnection();

		// 3. BoardDAO 클래스로부터 BoardDAO 객체 가져오기(공통)
		// => 싱글톤 디자인 패턴으로 구현되어 있는 객체는 getInstance() 메서드로 리턴받기
		BoardDAO dao = BoardDAO.getInstance(); 
		
		// 4. BoardDAO 객체의 setConnection() 메서드 호출하여 Connection 객체 전달(공통)
		dao.setConnection(con);
		
		// 5. BoardDAO 객체의 xxx() 메서드를 호출하여 xxx 작업 수행 요청 및 결과 리턴받기
		//    insertBoard() 메서드를 호출하여 글쓰기 작업 요청 및 결과 리턴받기
		// => 파라미터 : BoardBean 객체   리턴타입 : int(insertCount)
		int insertCount = dao.insertBoard(board);
		
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
		
		// 7. Connection Pool로부터 가져온 Connection 자원 반환(공통)
		// => 주의! DAO 객체 내에서 Connection 객체를 반환하지 않도록 해야한다!
		JdbcUtil.close(con);
		
		// 8. 작업 요청 처리 결과 리턴
		return isWriteSuccess; // BoardWriteProAction 으로 리턴
	}
	
}
