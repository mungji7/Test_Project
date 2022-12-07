package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardDetailService;
import vo.ActionForward;
import vo.BoardBean;

public class BoardReplyFormAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = null;
		
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		
		BoardDetailService service = new BoardDetailService();
		BoardBean board = service.getBoard(board_num, false);
		
		request.setAttribute("board", board);
		
		forward = new ActionForward();
		forward.setPath("board/qna_board_reply.jsp");
		forward.setRedirect(false);
		
		return forward;
	}

}
