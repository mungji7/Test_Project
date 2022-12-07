package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardDetailService;
import vo.ActionForward;
import vo.BoardBean;

public class BoardModifyFormAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = null;
		
		int board_num = Integer.parseInt(request.getParameter("board_num"));
//		System.out.println(board_num);
		
		// BoardDetailService 클래스의 인스턴스 생성 및 getBoard() 메서드를 호출하여
		// 글 상세정보 조회 작업 요청
		// => 주의! BoardModifyFormService 클래스는 생성하지 않는다! (중복이므로 불필요)
		// => 파라미터 : 글번호, 조회수 증가 여부(false)  리턴타입 : BoardBean(board)
		BoardDetailService service = new BoardDetailService();
		BoardBean board = service.getBoard(board_num, false);
		System.out.println(board);
		
		// 뷰페이지로 데이터 전달을 위해 request 객체에 저장
		request.setAttribute("board", board);
		
		// ActionForward 객체를 통해 qna_board_modify.jsp 페이지 포워딩 설정
		// => URL 유지 및 request 객체 유지를 위해 Dispatch 방식 포워딩
		forward = new ActionForward();
		forward.setPath("board/qna_board_modify.jsp");
		forward.setRedirect(false);
		
		return forward;
	}

}
