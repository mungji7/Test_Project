package action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.cj.Session;

import encrypt.MyMessageDigest;
import svc.MemberLoginProService;
import vo.ActionForward;
import vo.MemberBean;

public class MemberLoginProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = null;
		
		
		try {
			MemberBean member = new MemberBean();
			member.setId(request.getParameter("id"));
//			member.setPasswd(request.getParameter("passwd"));
			
			// ------------------------------------------------------
			// 패스워드 암호화(해싱) 기능 추가
			// => 로그인 시 입력받은 패스워드를 해싱을 통해 암호화를 수행하고
			//    암호화 된 패스워드끼리 비교 수행해야함
			MyMessageDigest md = new MyMessageDigest("SHA-256");
			member.setPasswd(md.hashing(request.getParameter("passwd")));
			// ------------------------------------------------------
			
			// MemberLoginProService - loginMember()
			// => 파라미터 : MemberBean 객체   리턴타입 : int(loginResult)
			// => 파라미터 : MemberBean 객체   리턴타입 : boolean(loginResult)
			MemberLoginProService service = new MemberLoginProService();
			boolean loginResult = service.loginMember(member);
//			System.out.println(loginmember);
			
			// int 타입으로 리턴받는 경우 : 아이디 틀림(-1), 패스워드 틀림(0), 로그인 성공(1)
//			if(loginResult == -1) { // 아이디 틀림
//				response.setContentType("text/html; charset=UTF-8"); // setContentType을 설정해야 HTML 문서로 인식됨
//				
//				PrintWriter out = response.getWriter();
//				
//				out.println("<script>");
//				out.println("alert('존재하지 않는 아이디!')");
//				out.println("history.back()"); 
//				out.println("</script>");
//				 
//			} else if(loginResult == 0) { // 패스워드 틀림
//				response.setContentType("text/html; charset=UTF-8"); // setContentType을 설정해야 HTML 문서로 인식됨
//				
//				PrintWriter out = response.getWriter();
//				
//				out.println("<script>");
//				out.println("alert('패스워드 틀림!!')");
//				out.println("history.back()"); 
//				out.println("</script>");
//				
//			} else { // else if(loginResult == 1)과 동일 = 로그인 성공
//				// session 아이디 저장
//				HttpSession session = request.getSession();
//				session.setAttribute("sId", member.getId());
//				
//				// 컨트롤러에 액션 객체 전달
//				forward = new ActionForward();
//				forward.setPath("index.jsp");
//				forward.setRedirect(true);
//			}
			
			if(loginResult) { // 로그인 성공시
				// session 객체에 아이디 저장
				// request와 달리 session은 요청없이도 정보를 들고다님
				// => 단, 서블릿 클래스에서 세션 객체에 직접 접근이 불가능함(내장 객체가 없음)
				//	  따라서, requset 객체로부터 세션 객체를 얻어와야 함 = getSession() 메서드
				//    (리턴타입 HttpSession 타입)
				HttpSession session = request.getSession();
				session.setAttribute("sId", member.getId());
				
				// 컨트롤러에 액션 객체 전달
				forward = new ActionForward();
				forward.setPath("index.jsp");
				forward.setRedirect(true);
				
			} else { // 로그인 실패시
				
				response.setContentType("text/html; charset=UTF-8"); // setContentType을 설정해야 HTML 문서로 인식됨
				
				PrintWriter out = response.getWriter();
				
				out.println("<script>");
				out.println("alert('로그인 실패!')");
				out.println("history.back()"); 
				out.println("</script>");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
				
		return forward;
	}

}
