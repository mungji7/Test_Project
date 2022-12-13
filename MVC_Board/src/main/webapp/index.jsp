<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<!-- 외부 CSS 가져오기 -->
<link href="css/default.css" rel="stylesheet" type="text/css">
<style type="text/css">
#img1 {
	text-align: center;
}

body {
	background-color: #FBEFF2;
}
</style>
</head>
<body>
	<header>
		<!-- Login, Join 링크 표시 영역 -->
		<jsp:include page="inc/top.jsp"></jsp:include>
	</header>
	
	<article>
		<!-- 본문 표시 영역 -->
		<h1> ค^•ﻌ•^ค MVC 게시판  ค^•ﻌ•^ค</h1>	
		<h3><a href="BoardWriteForm.bo">글쓰기</a></h3>
		<h3><a href="BoardList.bo">글목록</a></h3>
	</article>
	<section id=img1>
		<img alt="rabbit" src="images/독기.gif" align="middle">
	</section>
</body>
</html>