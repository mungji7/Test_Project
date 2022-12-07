package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import action.BoardDeleteProAction;
import action.BoardDetailAction;
import action.BoardListAction;
import action.BoardModifyFormAction;
import action.BoardModifyProAction;
import action.BoardReplyFormAction;
import action.BoardReplyProAction;
import action.BoardWriteProAction;
import vo.ActionForward;

@WebServlet("*.bo")
public class BoardFrontController extends HttpServlet {
	// GET or POST 방식 요청을 공통으로 처리할 doProcess() 메서드 정의
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("BoardFrontController");
		
		// POST 방식 요청에 대한 한글 인코딩 처리
		request.setCharacterEncoding("UTF-8");
		
		// 서블릿 주소 추출
		String command = request.getServletPath();
		System.out.println("command : " + command);
		
		// 공통으로 사용할 변수 선언
		Action action = null; // XXXAction 클래스를 공통으로 관리할 Action 타입 인터페이스 선언
		ActionForward forward = null; // 포워딩 정보를 저장할 ActionForward 타입 변수 선언
		
		// 추출 된 서블릿 주소(command)를 if문을 통해 문자열 비교를 수행하고
		// 각 주소에 따른 액션(작업) 요청
		if(command.equals("/BoardWriteForm.bo")) {
//			System.out.println("글쓰기 폼!");
			// 글쓰기 폼을 출력하는 뷰페이지(board/qna_board_write.jsp)로 이동
			// => 비즈니스 로직(= DB 작업) 불필요하므로 뷰페이지로 바로 이동
			// => Dispatch 방식 포워딩
			// 1. ActionForward 객체 생성
			forward = new ActionForward(); // Action 클래스를 갈 일이 없으니 직접 객체 생성
			// 2. ActionForward 객체의 setPath() 메서드 호출하여 이동할 페이지 지정
			forward.setPath("board/qna_board_write.jsp");
			// 3. ActionForward 객체의 setRedirect() 메서드를 호출하여 포워딩 방식 지정
			// => Dispatch 방식으로 포워딩하므로 false 값 전달
			forward.setRedirect(false); // 생략 가능
			
		} else if(command.equals("/BoardWritePro.bo")) {
//			System.out.println("글쓰기 작업!");
			// 글쓰기 비즈니스 로직 요청
			// 비즈니스 로직을 처리할 Action 클래스의 인스턴스 생성 후 execute() 메서드 호출
			// 파라미터 : HttpServletRequest(request), HttpServletRespons(response) 객체
			// 리턴타입 : ActionForward
			// 1. BoardWirteProAction 클래스 인스턴스 생성
			action = new BoardWriteProAction();
			// 2. BoardWirteProAction 인스턴스의 execute() 메서드 호출
			forward = action.execute(request, response);
			
		} else if(command.equals("/BoardList.bo")) {
//			System.out.println("글목록 작업!");
			// 글목록 비즈니스 작업 요청
			// BoardListAction의 execute() 메서드 호출
			action = new BoardListAction();
			forward = action.execute(request, response);
		} else if(command.equals("/BoardDetail.bo")) {
			// 글 상세정보 조회 비즈니스 작업 요청
			// BoardDetailAction의 execute() 메서드 호출
			action = new BoardDetailAction();
			forward = action.execute(request, response);
		} else if(command.equals("/BoardDeleteForm.bo")) {
			// 글삭제 폼 페이지로 이동 -> 비즈니스 로직X
			forward = new ActionForward(); // Action 클래스를 갈 일이 없으니 직접 객체 생성
			forward.setPath("board/qna_board_delete.jsp");
			forward.setRedirect(false); // Dispatch 방식일경우 생략 가능
		} else if(command.equals("/BoardDeletePro.bo")) {
			action = new BoardDeleteProAction();
			forward = action.execute(request, response);
		} else if(command.equals("/BoardModifyForm.bo")) {
			// 글 수정 폼 비즈니스 작업 요청
			action = new BoardModifyFormAction();
			forward = action.execute(request, response);
		} else if(command.equals("/BoardModifyPro.bo")) {
			// 글 수정 작업 비즈니스 작업 요청
			action = new BoardModifyProAction();
			forward = action.execute(request, response);
		} else if(command.equals("/BoardReplyForm.bo")) {
			// 답글 작성 폼 비즈니스 작업 요청
			action = new BoardReplyFormAction();
			forward = action.execute(request, response);
		} else if(command.equals("/BoardReplyPro.bo")) {
			// 답글 작성 작업 비즈니스 작업 요청
			action = new BoardReplyProAction();
			forward = action.execute(request, response);
		}
		
		// -------------------------------------------------------
		// ActionForward 객체 내용에 따라 각각 다른 방식의 포워딩 작업 수행(공통)
		// 1. ActionForward 객체가 null이 아닐 경우 판별
		if(forward != null) { // ActionForward 객체가 있을때만 동작
			// 2. ActionForward 객체에 저장된 포워딩 방식 판별
			if(forward.isRedirect()) { // Redirect 방식
				// Redirect 방식의 포워딩 작업 수행
				// => 포워딩 경로는 ActionForward 객체의 getPath() 메서드 활용
				response.sendRedirect(forward.getPath());
			} else { // Dispatch 방식
				// Dispatch 방식의 포워딩 작업 수행
				RequestDispatcher dispatcher = request.getRequestDispatcher(forward.getPath());
				dispatcher.forward(request, response);
			}
		}
		
	} // doProcess() 메서드 끝(응답데이터 전송) 
	// response 안에 응답데이터가 들어있음 -> 클라이언트에게 자바스크립트 태그 전송
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

}
