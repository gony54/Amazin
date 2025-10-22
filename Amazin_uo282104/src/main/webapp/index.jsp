<!DOCTYPE html >
<%@ page contentType="text/html; charset=iso-8859-1"
	pageEncoding="iso-8859-1" language="java"
	import="java.util.*, com.miw.model.Book,com.miw.presentation.book.*"
	errorPage=""%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<head>
<title>Amazin</title>
<link rel="stylesheet" href="css/style.css" />
</head>
<body>
	<header>
		<h1 class="header">Amazin.com</h1>
		<h2 class="centered">
			Welcome to the <em>smallest</em> online shop in the world!!
		</h2>
	</header>
	<nav>
		<ul>
			<li><a href="#">Start</a></li>
			<li><a href="http://miw.uniovi.es">About</a></li>
			<li><a href="mailto:dd@email.com">Contact</a></li>
			<c:choose>
				<c:when test="${not empty sessionScope.user}">
					<li><a href="Controller?action=LogoutAction">Logout
							(<c:out value="${sessionScope.user.username}"/>)</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="login.jsp">Login</a></li>
				</c:otherwise>
			</c:choose>
		</ul>
	</nav>
	<section>
		<article id="a01">
			<label class="mytitle">Choose an option:</label><br /> 
			<a href="Controller?action=ShowBooksAction">Show Catalog</a><br /> 
			<a href="Controller?action=ShowSpecialOfferAction">Show Special Offers!</a> 
		</article>
	</section>
	<footer>
		<strong> Master in Web Engineering (miw.uniovi.es).</strong><br /> <em>University
			of Oviedo </em>
	</footer>
</body>
