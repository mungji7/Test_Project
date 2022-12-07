package action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardListService;
import vo.ActionForward;
import vo.BoardBean;
import vo.PageInfo;

public class BoardListAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("BoardListAction");
		
		ActionForward forward = null;
		
		// BoardListService 객체를 통해 게시물 목록 조회 후
		// 조회 결과(List 객체)를 request 객체를 통해 qna_board_list.jsp 페이지로 전달
		// --------------------------------------------------------------------
		// 1) 페이징 처리에서 사용되는 게시물 목록 조회를 위한 계산 작업
		
		// 1. 한 페이지에 표시할 게시물 목록 수(listLimit) 설정 
		int listLimit = 10;
		
		// 2. 현재 페이지 번호 설정(pageNum 파라미터 사용)
		int pageNum = 1; // 기본값
		
		// => pageNum 파라미터가 존재하면 해당 값을 저장하고, 아니면 기본 값 1 사용
		if(request.getParameter("pageNum")!=null) {
			pageNum = Integer.parseInt(request.getParameter("pageNum"));
		}
		
		//3. 현재 페이지에 첫 게시물의 행(레코드) 번호(글번호) (startRow) 계산
		int startRow = (pageNum-1) * listLimit; // 조회 시작 행번호 계산
		// 1페이지 : 0~9행 / 2페이지 : 10~19행 / 3페이지 : 20~29행 
		
		// --------------------------------------------------
		// 검색 후 목록 표시 기능과 일반 목록 표시 기능 통합
		// 파라미터로 전달받은 검색어(keyword) 가져와서 변수에 저장
		String keyword = request.getParameter("keyword");

		// 만약, 전달받은 검색어가 null이면 널스트링으로 변경 (일반 목록일 경우 전체 검색 수행)
		if(keyword==null) { // 검색어 칸이 비어있으면 null이 아닌 ""이 들어간 것
			keyword="";
		}
		// -------------------------------------------------------------
		// BoardListService 클래스 인스턴스 생성
		BoardListService service = new BoardListService();
		// BoardListService 객체의 getBoardList() 메서드를 호출하여 게시물 목록 조회
		// => 파라미터 : 검색어, 시작행번호, 목록갯수  리턴타입 : List<BoardBean>(boardList)
		List<BoardBean> boardList = service.getBoardList(keyword, startRow, listLimit);
//		System.out.println(boardList);
		
		// --------------------------------------------------------------
		// 페이징 처리
		
		// 1. 전체 게시물 수 조회(listCount) 
		// BoardListService - selectBoardListCount() 메서드를 호출하여 게시물 목록 갯수 조회
		// => 파라미터 : 검색어   리턴타입 : int(listCount)
	    int listCount = service.getBoardListCount(keyword);
//	    System.out.println("listCount : " + listCount);
		
		// 2. 한 페이지에 표시할 페이지 목록 수 설정(pageListLimit)
		int pageListLimit = 10;
		
		// 3. 전체 페이지 목록 수 계산 (maxPage)
		// 추가로 게시글이 있으면 1페이지 추가
		int maxPage = listCount/listLimit + (listCount%listLimit!=0? 1 : 0);
		
		// 4. 시작 페이지 번호 계산
		int startPage = (pageNum-1) / pageListLimit * pageListLimit + 1;
		
		// 5. 끝 페이지 번호 계산
		int endPage = startPage * pageListLimit - 1;
		
		// 6. 끝 페이지 번호가 전체 페이지 번호보다 클 경우 전체 페이지 번호를 끝 번호로 설정
		if(endPage>maxPage) {
			endPage = maxPage;
		}
		
		// PageInfo 객체 생성 후 페이징 처리 정보 저장
		PageInfo pageInfo = new PageInfo(listCount, pageListLimit, maxPage, startPage, endPage);
		
		// ------------------------------------------------------------
		// 글목록(List 객체)과 페이징 처리 정보를 request 객체에 저장 - setAttribute()
		request.setAttribute("boardList", boardList);
		request.setAttribute("pageInfo", pageInfo);
		
		// ActionForward 객체 생성후 board/qna_board_list.jsp 페이지 포워딩 설정
		// => URL 및 request 객체 유지 : Dispatch 방식
		forward = new ActionForward();
		forward.setPath("board/qna_board_list.jsp");
		forward.setRedirect(false); // 생략 가능
		
		return forward;
	}

}
