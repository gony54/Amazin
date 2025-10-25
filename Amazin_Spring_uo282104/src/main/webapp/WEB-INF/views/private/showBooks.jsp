<!DOCTYPE html >
<%@ page contentType="text/html; charset=iso-8859-1"
	pageEncoding="iso-8859-1" language="java"
	import="java.util.*, com.miw.model.Book,com.miw.presentation.book.*"
	errorPage=""%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<head>
<title>Amazin - Books</title>
<link href="<c:url value="/resources/css/style.css" />" rel="stylesheet">
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
			<li><a href="menu">Start</a></li>
			<li><a href="newBook">Add New</a></li>
			<li><a href="showShoppingCart">Shopping Cart</a></li>
			<li><a href="showReservations">Reservations</a></li>
			<li><a href="http://miw.uniovi.es">About</a></li>
			<li><a href="mailto:dd@email.com">Contact</a></li>
		</ul>
	</nav>
	<section>
		<article>
			<c:if test="${not empty message}">
				<p style="color: green;">
					<c:out value="${message}" />
				</p>
			</c:if>
			<c:if test="${not empty error}">
				<p style="color: red;">
					<c:out value="${error}" />
				</p>
			</c:if>
			
			<table>
				<caption>Our catalog:</caption>
				<thead>
					<tr>
						<th>Title</th>
						<th>Author</th>
						<th>Description</th>
						<th>Price</th>
						<th>Stock</th>
						<th>Actions</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var='book' items="${booklist}">
						<tr>
							<td><c:out value="${book.title}" /></td>
							<td><c:out value="${book.author}" /></td>
							<td><c:out value="${book.description}" /></td>
							<td><c:out value="${book.price}" /> &euro;</td>
							<td><c:out value="${book.stock}" /></td>
							<td>
								<c:if test="${book.stock > 0}">
									<form action="shoppingCart" method="post" style="display: inline; margin-right: 5px;">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<input type="hidden" name="bookId" value="${book.id}" />
										<input type="number" name="quantity" value="1" min="1" max="${book.stock}" style="width: 50px;" />
										<input type="submit" value="Buy" />
									</form>
									
									<form action="reserveBook" method="post" style="display: inline;">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<input type="hidden" name="bookId" value="${book.id}" />
										<input type="number" name="quantity" value="1" min="1" max="${book.stock}" style="width: 50px;" />
										<input type="submit" value="Reserve" />
									</form>
								</c:if>
								<c:if test="${book.stock == 0}">
									<span style="color: red;">Out of Stock</span>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</article>
	</section>
	<footer>
		<strong> Master in Web Engineering (miw.uniovi.es).</strong><br /> <em>University
			of Oviedo </em>
	</footer>
</body>
