package action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.cj.Session;

import svc.MemberLoginProService;
import vo.ActionForward;
import vo.MemberBean;

public class MemberLogoutAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = null;
		
		// 로그인 된 회원들 로그아웃 시키기. (세션 초기화)
		// 로그인 된 넘들한테만 로그아웃 버튼이 보이니까 별 다른 디비작업 필요X
		HttpSession session = request.getSession();
		session.invalidate();
		
		forward = new ActionForward();
		forward.setPath("./");
		forward.setRedirect(true);
	
		return forward;
	}

}
