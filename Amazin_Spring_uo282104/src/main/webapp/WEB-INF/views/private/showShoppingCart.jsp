<!DOCTYPE html >
<%@ page contentType="text/html; charset=iso-8859-1"
	pageEncoding="iso-8859-1" language="java"
	import="java.util.*, com.miw.model.Book" errorPage=""%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<head>
<title>Amazin - Shopping Cart</title>
<link href="<c:url value="/resources/css/style.css" />" rel="stylesheet">
</head>
<body>
	<header>
		<h1 class="header">Amazin.com</h1>
		<h2 class="centered">Your Shopping Cart</h2>
	</header>
	<nav>
		<ul>
			<li><a href="menu">Start</a></li>
			<li><a href="showBooks">Catalog</a></li>
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
			<c:if test="${not empty warning}">
				<p style="color: orange; background-color: #fff3cd; padding: 10px; border: 1px solid orange; border-radius: 5px;">
					<strong><c:out value="${warning}" escapeXml="false" /></strong>
				</p>
			</c:if>

			<c:choose>
				<c:when test="${empty cartItems}">
					<p>Your shopping cart is empty.</p>
					<a href="showBooks">Continue Shopping</a>
				</c:when>
				<c:otherwise>
					<table>
						<caption>Your Shopping Cart:</caption>
						<thead>
							<tr>
								<th>Title</th>
								<th>Author</th>
								<th>Price</th>
								<th>Quantity</th>
								<th>Available Stock</th>
								<th>Subtotal</th>
								<th>Action</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach var='cartItem' items="${cartItems}">
								<tr>
									<td><c:out value="${cartItem.book.title}" /></td>
									<td><c:out value="${cartItem.book.author}" /></td>
									<td><c:out value="${cartItem.book.price}" /> &euro;</td>
									<td><c:out value="${cartItem.quantity}" /></td>
									<td>
										<c:choose>
											<c:when test="${cartItem.book.stock < cartItem.quantity}">
												<span style="color: red; font-weight: bold;">
													<c:out value="${cartItem.book.stock}" /> (WARNING)
												</span>
											</c:when>
											<c:otherwise>
												<c:out value="${cartItem.book.stock}" />
											</c:otherwise>
										</c:choose>
									</td>
									<td><c:out value="${cartItem.subtotal}" /> &euro;</td>
									<td>
										<form action="removeFromCart" method="post" style="display: inline;">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<input type="hidden" name="bookId" value="${cartItem.book.id}" />
											<input type="submit" value="Remove" />
										</form>
									</td>
								</tr>
							</c:forEach>
							<tr>
								<td colspan="5" style="text-align: right;"><strong>Total:</strong></td>
								<td colspan="2"><strong><c:out value="${total}" /> &euro;</strong></td>
							</tr>
						</tbody>
					</table>

					<br />
					<form action="checkout" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<input type="submit" value="Proceed to Checkout"/>
					</form>
					<br />
					<a href="showBooks">Continue Shopping</a>
				</c:otherwise>
			</c:choose>
		</article>
	</section>
	<footer>
		<strong> Master in Web Engineering (miw.uniovi.es).</strong><br /> <em>University
			of Oviedo </em>
	</footer>
</body>