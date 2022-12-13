package action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import svc.BoardModifyProService;
import vo.ActionForward;
import vo.BoardBean;

public class BoardModifyProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = null;
		
		String realPath = "";
		// 수정 작업 결과에 따라 삭제할 파일이 달라지므로 파일명을 저장할 변수 선언
		String deleteFileName = "";
		
		try {
			String uploadPath = "upload"; // 업로드 가상 디렉토리(이클립스가 관리)
			realPath = request.getServletContext().getRealPath(uploadPath); // 업로드 실제 디렉토리(톰캣)
			
			// 만약, 해당 디렉토리가 존재하지 않을 경우 디렉토리 생성
			// => java.io.File 클래스 인스턴스 생성(파라미터로 해당 디렉토리 전달)
			File f = new File(realPath);
			// => 단, File 객체가 생성되더라도 해당 디렉토리 또는 파일을 직접 생성 X
			 // 실제 경로에 대상 존재 여부 판별
			if(!f.exists()) { // 해당 경로가 존재하지 않을 경우
				// File 객체의 mkdir() 메서드를 호출하여 경로 생성
				f.mkdir();
			}
			
			int fileSize = 1024 * 1024 * 10; // 10MB
			
			// 파일 업로드 처리(enctype="multipart/form-data")를 위해
			// MultipartRequest 객체 생성 => cos.jar 라이브러리 추가
			MultipartRequest multi = new MultipartRequest(request, realPath, fileSize, "UTF-8", new DefaultFileRenamePolicy());
		
			// 전달받을 파라미터 데이터를 BoardBean 클래스 인스턴스 생성 후 저장
			BoardBean board = new BoardBean();
			board.setBoard_num(Integer.parseInt(multi.getParameter("board_num")));
			board.setBoard_name(multi.getParameter("board_name"));
			board.setBoard_pass(multi.getParameter("board_pass"));
			board.setBoard_subject(multi.getParameter("board_subject"));
			board.setBoard_content(multi.getParameter("board_content"));
			
			// 파일명은 getParameter()로 단순 처리 불가능
			// => 원본 파일명 : getOriginalFileName()
			// 	  중복 처리된(실제 업로드 되는) 파일명 : getFilesystemName()
			board.setBoard_file(multi.getOriginalFileName("board_file")); // board_file은 수정폼에서 선택한 파일
			board.setBoard_real_file(multi.getFilesystemName("board_file"));
//			System.out.println(board);
			// => 만약, 수정할 파일을 선택하지 않았을 경우 파일명은 null 값이 저장됨
			// => NOT NULL 제약조건이 걸려있기 때문에 DB 구문에서 오류 발생!
			// => 따라서 null 값이 저장하는게 아닌 기존 파일을 유지해야함
			
			
			// BoardModifyProService - isBoardWriter() 호출하여 패스워드 일치 여부 확인
			// => 파라미터 : BoardBean 객체   리턴타입 : boolean(isBoardWriter)
			BoardModifyProService service = new BoardModifyProService();
			boolean isboardwriter = service.isBoardWriter(board);
			
			if(!isboardwriter) { // 패스워드 불일치시 수정 권한 없음
				response.setContentType("text/html; charset=UTF-8"); // setContentType을 설정해야 HTML 문서로 인식됨
				
				PrintWriter out = response.getWriter();
			
				out.println("<script>");
				out.println("alert('수정 권한이 없습니다!')");
				out.println("history.back()"); 
				out.println("</script>");
				
				// 삭제할 파일명을 새 파일명의 실제 파일명으로 지정
				deleteFileName = board.getBoard_real_file();
				
			} else { // 패스워드 일치시 
				
				// BoardModifyProService - modifyBoard() 호출하여 글 수정 작업 요청
				// => 파라미터 : BoardBean 객체   리턴타입 : boolean(isModifySuccess)
				boolean isModifySuccess = service.modifyBoard(board);
				
				if(isModifySuccess) { // 글 수정 성공시 
					forward = new ActionForward();
					forward.setPath("BoardDetail.bo?board_num=" + board.getBoard_num() + "&pageNum=" + multi.getParameter("pageNum"));
			 		forward.setRedirect(true); // Redirect 방식 (수정폼에서 글 내용조회 페이지로 이동) 
					
			 		// 삭제할 파일 명을 기존 파일의 실제 파일명으로 지정
			 		// => hidden 속성으로 전달받은 기존 파일명에 대한 파라미터 사용
			 		// => 단, 수정할 새 파일을 선택했을 경우에만 파일명 지정
			 		// => 파일 수정을 하지 않으면 기존 파일이 그대로 유지되야함
			 		if(board.getBoard_file() != null) {			 			
			 			deleteFileName = multi.getParameter("board_real_file"); // 서버에 실제 업로드 되어있던 기존 파일
			 		}
			 		
			 		
				} else { // 글 수정 실패시
					response.setContentType("text/html; charset=UTF-8"); // setContentType을 설정해야 HTML 문서로 인식됨
					
					PrintWriter out = response.getWriter();
				
					out.println("<script>");
					out.println("alert('글 수정 실패!')");
					out.println("history.back()"); 
					out.println("</script>");
					
					// 삭제할 파일명을 새 파일명의 실제 파일명으로 지정
					deleteFileName = board.getBoard_real_file();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 예외가 발생하더라도 파일 삭제는 무조건 수행하도록 finally 블록 작성
			// 1. File 객체 생성(파라미터로 디렉토리명, 파일명 전달)
			File f = new File(realPath, deleteFileName);
			
			// 해당 디렉토리 및 파일 존재 여부 판별
			if(f.exists()) { // 존재할 경우
				// File 객체의 delete() 메서드를 호출하여 해당 파일 삭제
				f.delete();
			}
		}
			
		return forward; 
	}

}
