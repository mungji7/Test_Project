<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
function confirm_logout() {
	let result = confirm("로그아웃 하시겠습니까?");
	
	if(result) {
		location.href="MemberLogout.me";
	}
}
</script>

    
<% String sId = (String)session.getAttribute("sId"); %>

<div id="member_area">
	<a href="./">Home</a>
	<c:choose>
		<c:when test="${empty sessionScope.sId}">
			| <a href="MemberLoginForm.me">Login</a> | <a href="MemberJoinForm.me">Join</a>
		</c:when>
		<%-- 로그인 상태일 경우 아이디 표시, Logout 링크 표시 --%>
		<c:otherwise>
			| <a href="MemberLoginForm.me">${sessionScope.sId }님</a> | <a href="javascript:confirm_logout()">Logout</a>
			<%-- 로그인 된 세션 아이디가 admin일 경우 관리자페이지 링크(MemberList.me) 표시 --%>
			<c:if test='${sessionScope.sId eq "admin" }'>
				| <a href="MemberList.me">관리자 페이지</a>
			</c:if>
		</c:otherwise>		
	</c:choose>
</div>