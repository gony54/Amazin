<!DOCTYPE html >
<%@ page contentType="text/html; charset=iso-8859-1"
	pageEncoding="iso-8859-1" language="java"
	import="java.util.*, com.miw.model.Book" errorPage=""%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<head>
<title>Amazin - Reservations</title>
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
			<li><a href="showBooks">Catalog</a></li>
			<li><a href="showShoppingCart">Shopping Cart</a></li>
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

			<c:choose>
				<c:when test="${empty reservedItems}">
					<p>You have no active reservations.</p>
					<a href="showBooks">Browse Catalog</a>
				</c:when>
				<c:otherwise>
					<table>
						<caption>Reservations</caption>
						<thead>
							<tr>
								<th>Title</th>
								<th>Author</th>
								<th>Full Price</th>
								<th>Quantity</th>
								<th>Paid (5%)</th>
								<th>Remaining (95%)</th>
								<th>Actions</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach var='cartItem' items="${reservedItems}">
								<tr>
									<td><c:out value="${cartItem.book.title}" /></td>
									<td><c:out value="${cartItem.book.author}" /></td>
									<td><c:out value="${cartItem.book.price}" /> &euro;</td>
									<td><c:out value="${cartItem.quantity}" /></td>
									<td><c:out value="${String.format('%.2f', cartItem.subtotal)}" /> &euro;</td>
									<td><c:out value="${String.format('%.2f', cartItem.remainingAmount)}" /> &euro;</td>
									<td>
										<form action="buyReservedBook" method="post" style="display: inline; margin-right: 5px;">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<input type="hidden" name="bookId" value="${cartItem.book.id}" />
											<input type="submit" value="Complete Purchase" />
										</form>

										<form action="cancelReservation" method="post" style="display: inline;">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<input type="hidden" name="bookId" value="${cartItem.book.id}" />
											<input type="submit" value="Cancel" />
										</form>
									</td>
								</tr>
							</c:forEach>
							<tr>
								<td colspan="4" style="text-align: right;"><strong>Total Paid (5%):</strong></td>
								<td><strong style="color: green;"><c:out value="${String.format('%.2f', totalPaid)}" /> &euro;</strong></td>
								<td colspan="2"><td/>
							</tr>
						</tbody>
					</table>

					<br />
					<a href="showBooks">Continue Browsing</a>
				</c:otherwise>
			</c:choose>
		</article>
	</section>
	<footer>
		<strong> Master in Web Engineering (miw.uniovi.es).</strong><br /> <em>University
			of Oviedo </em>
	</footer>
</body>
