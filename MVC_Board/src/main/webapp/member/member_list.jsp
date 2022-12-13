<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!-- 세션아이디가 null 이거나 "admin" 이 아닐 경우 메인페이지로 쫓아내기 -->
<%-- <c:if test='${empty sessionScope.sId || sessionScope.sId ne "admin"}'> --%>
<%-- 	<% response.sendRedirect("./"); %> 	 --%>
<%-- </c:if> --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<!-- 외부 CSS 가져오기 -->
<link href="css/default.css" rel="stylesheet" type="text/css">
<style type="text/css">
	#listForm {
		width: 1024px;
		max-height: 610px;
		margin: auto;
	}
	
	h1 {
		text-align: center;
	}
	
	h2 {
		text-align: center;
	}
	
	table {
		margin: auto;
		width: 1024px;
	}
	
	#tr_top {
		background: #CECEF6;
		text-align: center;
	}
	
	table td {
		text-align: center;
	}
	
	#subject {
		text-align: left;
		padding-left: 20px;
	}
	
	#pageList {
		margin: auto;
		width: 1024px;
		text-align: center;
	}
	
	#emptyArea {
		margin: auto;
		width: 1024px;
		text-align: center;
	}
	
	#buttonArea {
		margin: auto;
		width: 1024px;
		text-align: right;
		margin-top: 10px;
	}
	
	a {
		text-decoration: none;
	}
</style>
</head>
<body>
	<header>
		<!-- Login, Join 링크 표시 영역 -->
		<jsp:include page="/inc/top.jsp"></jsp:include>
	</header>
	<h1>MemberList</h1>
	<!-- member 테이블의 모든 레코드 조회하여 테이블에 출력 -->
	<script type="text/javascript">
		function confirmDelete(id) {
			// confirm 사용하여 "XXX 회원을 삭제하시겠습니까?" 확인 요청
			// => 결과값이 true 일 경우 delete 페이지로 이동
			let result = confirm(id + " 회원을 삭제하시겠습니까?");
			
			if(result) {
				location.href = "삭제페이지 매핑" + id;
			}
		}
	</script>

	<h1><marquee behavior="alternate" scrollamount="20">
	ค^•ﻌ•^ค 회원 목록 ค^•ﻌ•^ค
	</marquee>

	</h1>
		<table border="1">
			<tr id=tr_top>
				<th width="100">이름</th>
				<th width="100">아이디</th>
				<th width="200">E-Mail</th>
				<th width="150">연락처</th>
				<th width="100">가입일</th>
				<th width="150"></th>
			</tr>
				
				<c:forEach var="member" items="${memberList }">
			<tr>
				<td>${member.id}</td>
				<td>${member.name}</td>
				<td>${member.email}</td>
				<td>${member.gender}</td>
				<td>${member.date}</td>
				<td>
					<input type="button" value="수정" onclick="location.href='MemberUpdate.me?id=${member.id}'">
					<input type="button" value="삭제" onclick="location.href='MemberDelete.meid=${member.id}'">
				</td>
			</tr>
				</c:forEach>
	</table>
</body>
</html>



