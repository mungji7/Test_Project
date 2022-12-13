package action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import encrypt.MyMessageDigest;
import svc.MemberJoinProService;
import vo.ActionForward;
import vo.MemberBean;

public class MemberJoinProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = null;
		
		try {
			
			request.setCharacterEncoding("UTF-8");
			
			MemberBean member = new MemberBean();
			member.setName(request.getParameter("name"));
			member.setId(request.getParameter("id"));
			// email1, email2 두 개의 파라미터가 전달되므로 결합 필요
			member.setEmail(request.getParameter("email1")+ "@" + request.getParameter("email2"));
			member.setGender(request.getParameter("gender"));
			member.setPasswd(request.getParameter("passwd"));
//			System.out.println(member);
			
			// ------------------------------------------------------
			// 패스워드 암호화(해싱) 기능 추가
			// encrypt.MyMessageDigest 클래스 인스턴스 생성
			MyMessageDigest md = new MyMessageDigest("SHA-256");
			// MyMessageDigest 객체의 hasing() 메서드를 호출하여 암호화 수행
			md.hashing(request.getParameter("passwd"));
			// ------------------------------------------------------
			
			MemberJoinProService service = new MemberJoinProService();
			boolean isJoinSuccess = service.joinMember(member);
			
			if(isJoinSuccess) { // 가입 성공시
				// 컨트롤러에 액션 객체 전달
				forward = new ActionForward();
				forward.setPath("MemberJoinResult.me");
				forward.setRedirect(true);
				
			} else { // 가입 실패시
				response.setContentType("text/html; charset=UTF-8"); // setContentType을 설정해야 HTML 문서로 인식됨
				
				PrintWriter out = response.getWriter();
				
				out.println("<script>");
				out.println("alert('회원 가입 실패!')");
				out.println("history.back()"); 
				out.println("</script>");
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return forward;
	}

}
