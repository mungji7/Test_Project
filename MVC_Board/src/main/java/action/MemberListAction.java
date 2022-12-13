package action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import svc.MemberListService;
import vo.ActionForward;
import vo.MemberBean;

public class MemberListAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = null;
		
		try {
			// 세션 아이디가 null이거나 "admin"이 아닐 경우 자바스크립트를 사용하여 돌려보내기
			HttpSession session = request.getSession();
			// 1. 내 정보 조회 -> 세션에 아이디 저장해서 가져오기
			// 2. 회원 목록 조회 -> 세션 아이디가 관리자일때만
			if(session.getAttribute("sId") == null || !session.getAttribute("sId").equals("admin") ) {
				response.setContentType("text/html; charset=UTF-8"); // setContentType을 설정해야 HTML 문서로 인식됨
				
				PrintWriter out = response.getWriter();
				
				out.println("<script>");
				out.println("alert('잘못된 접근 입니다!!')");
				out.println("history.back()"); 
				out.println("</script>");
			} else { // 관리자일 경우
				// MemberListService - getMemberList()
				// => 파라미터 : 없음   리턴타입 : List<MemberBean>(memberList)
				MemberListService service = new MemberListService();
				List<MemberBean> memberList = service.getMemberList();
				
				request.setAttribute("memberList", memberList); // List 객체 저장해서 뷰로 넘겨주기
			
				forward = new ActionForward();
				forward.setPath("member/member_list.jsp");
				forward.setRedirect(false); // request와 session 객체를 가지고 이동해야함 => Dispatch 방식
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return forward;
	}

}
