package action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.BoardDeleteProService;
import svc.BoardDetailService;
import vo.ActionForward;
import vo.BoardBean;

public class BoardDeleteProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = null;
		
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		String board_pass = request.getParameter("board_pass");
//		System.out.println(board_num);
//		System.out.println(board_pass);
		
		// BoardDeleteProService 클래스의 인스턴스 생성 및 isBoardWriter() 메서드 호출하여
		// 글 삭제 가능 여부(= 패스워드 일치 여부) 판별 요청
		// => 파라미터 : 글번호, 패스워드    리턴타입 : boolean(isBoardWriter)
		try {
			BoardDeleteProService service = new BoardDeleteProService();
			boolean isboardwriter = service.isBoardWriter(board_num, board_pass);
//			System.out.println("isboardwriter : " + isboardwriter);
			
			// 패스워드가 틀리거나 글 삭제 작업이 실패하면 자바스크립트 태그 출력
			// 패스워드가 맞고 글 삭제 작업이 성공하면 ActionForward 객체 리턴
			if(!isboardwriter) { // 패스워드가 틀릴 경우 (삭제 권한 없음)
				
				response.setContentType("text/html; charset=UTF-8"); // setContentType을 설정해야 HTML 문서로 인식됨
				
				PrintWriter out = response.getWriter();
			
				out.println("<script>");
				out.println("alert('패스워드 불일치!')");
				out.println("history.back()"); 
				out.println("</script>");
				
			} else { // 패스워드가 일치할 경우 (삭제 권한 있음) -> 삭제 작업 요청 수행
				// BoardDetailService 객체의 getBoard() 메서드 호출하여 삭제할 파일명 조회
				// => 파라미터 : 글번호, 조회수 증가 여부(false)  리턴 : Boardbean
				BoardDetailService service2 = new BoardDetailService();
				BoardBean board = service2.getBoard(board_num, false);
				// => 주의! 레코드 삭제 전 정보 조회 먼저 수행해야한다!
				
				// BoardDeleteProService 클래스의 removeBoard() 메서드를 호출하여 글 삭제 작업 수행
				//  => 파라미터 : 글번호(board_num)    리턴타입 : boolean(isDeleteSuccess)
				boolean isDeleteSuccess = service.removeBoard(board_num); // 글 삭제
//				System.out.println("isDeleteSuccess : " + isDeleteSuccess);
				
				// 삭제 결과를 판별하여 실패 시 자바스크립트 오류 메세지 출력 및 이전페이지로 이동하고
				// 성공 시 ActionForward 객체를 통해 "BoardList.bo" 페이지로 포워딩(Redirect)
				// (=> URL 에 페이지 번호를 붙여서 요청)
				if(!isDeleteSuccess) {
					response.setContentType("text/html; charset=UTF-8"); // setContentType을 설정해야 HTML 문서로 인식됨
					
					PrintWriter out = response.getWriter();
				
					out.println("<script>");
					out.println("alert('글삭제 실패!')");
					out.println("history.back()"); 
					out.println("</script>");
				} else {
					String uploadPath = "upload"; // 업로드 가상 디렉토리(이클립스가 관리)
					String realPath = request.getServletContext().getRealPath(uploadPath); // 업로드 실제 디렉토리(톰캣)
					
					// 업로드 된 실제 파일 삭제
					File f = new File(realPath, board.getBoard_real_file());
					
					// 해당 디렉토리 및 파일 존재 여부 판별
					if(f.exists()) { // 존재할 경우
						// File 객체의 delete() 메서드를 호출하여 해당 파일 삭제
						f.delete();
					}
					
					forward = new ActionForward();
					forward.setPath("BoardList.bo?pageNum=" + request.getParameter("pageNum"));
					forward.setRedirect(true); // 글목록으로 이동하므로 Redirect 방식
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return forward;
	}

}
