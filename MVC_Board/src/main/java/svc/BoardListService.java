package svc;

import java.sql.Connection;
import java.util.List;

import dao.BoardDAO;
import db.JdbcUtil;
import vo.BoardBean;

public class BoardListService {

	// 게시물 목록 조회 - getBoardList()
	// => 파라미터 : 검색어, 시작행번호, 목록갯수  리턴타입 : List<BoardBean>(boardList)
	public List<BoardBean> getBoardList(String keyword, int startRow, int listLimit) {
		List<BoardBean> boardList = null;
		
		// 공통작업-1. Connection 객체 가져오기
		Connection con = JdbcUtil.getConnection();
		
		// 공통작업-2. BoardDAO 객체 가져오기
		BoardDAO dao = BoardDAO.getInstance();
		
		// 공통작업-3. BoardDAO 객체에 Connection 객체 전달하기
		dao.setConnection(con);
		
		// BoardDAO 객체의 selectBoardList() 메서드를 호출하여 글목록 조회 작업 수행
		// => 파라미터 : 검색어, 시작행번호, 목록갯수  리턴타입 : List<BoardBean>(boardList)
		boardList = dao.selectBoardList(keyword, startRow, listLimit);
		
		
		// 공통작업-4. Connection 객체 반환하기
		JdbcUtil.close(con);
		
		// 조회 결과 리턴
		return boardList;
	}

	// 게시물 갯수 조회 - getBoardListCount()
	// => 파라미터 : 검색어    리턴타입 : int(listCount)
	public int getBoardListCount(String keyword) {
		int listCount = 0;
		
		Connection con = JdbcUtil.getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		listCount = dao.selectBoardListCount(keyword);
		
		JdbcUtil.close(con);

		return listCount;
	}
	
}
