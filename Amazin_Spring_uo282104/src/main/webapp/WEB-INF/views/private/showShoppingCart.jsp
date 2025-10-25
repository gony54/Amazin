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
		<h2 class="centered">
			Welcome to the <em>smallest</em> online shop in the world!!
		</h2>
	</header>
	<nav>
		<ul>
			<li><a href="menu">Start</a></li>
			<li><a href="newBook">Add New</a></li>
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
			<c:if test="${not empty warning}">
				<p style="color: orange;">
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
								<th>Status</th>
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
								<tr class="${cartItem.reserved ? 'reserved-row' : ''}">
									<td>
										<c:if test="${cartItem.reserved}">
											<span class="reserved-badge">RESERVED</span>
										</c:if>
										<c:if test="${!cartItem.reserved}">
											<span>Regular</span>
										</c:if>
									</td>
									<td><c:out value="${cartItem.book.title}" /></td>
									<td><c:out value="${cartItem.book.author}" /></td>
									<td><c:out value="${cartItem.book.price}" /> &euro;</td>
									<td><c:out value="${cartItem.quantity}" /></td>
									<td><c:out value="${cartItem.book.stock}" /></td>
									<td>
										<c:out value="${String.format('%.2f', cartItem.subtotal)}" /> &euro;
										<c:if test="${cartItem.reserved}">
											<br/><small style="color: green;">(5% to reserve)</small>
										</c:if>
									</td>
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
								<td colspan="6" style="text-align: right;"><strong>Total:</strong></td>
								<td colspan="1"><strong><c:out value="${String.format('%.2f', total)}" /> &euro;</strong></td>
								<td colspan="1"><td/>
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
