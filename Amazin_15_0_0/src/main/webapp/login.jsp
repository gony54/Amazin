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
			<li><a href="index.html">Start</a></li>
			<li><a href="http://miw.uniovi.es">About</a></li>
			<li><a href="mailto:dd@email.com">Contact</a></li>
		</ul>
	</nav>
	<section>
		<article id="a01" style="text-align: center; padding: 20px;">
			<c:if test="${not empty requestScope.error}">
				<p style="color: red; background-color: white; opacity: 1;">${requestScope.error}</p>
			</c:if>

			<form action="Controller" method="post">
				<input type="hidden" name="action" value="LoginAction"> <label
					for="username" class="mytitle">Username:</label><br /> <input
					type="text" id="username" name="username" required><br />
				<br /> <input type="submit" value="Login">
			</form>
			<br /> <a href="index.html">Cancel</a>
		</article>
	</section>
	<footer>
		<strong> Master in Web Engineering (miw.uniovi.es).</strong><br /> <em>University
			of Oviedo </em>
	</footer>
</body>