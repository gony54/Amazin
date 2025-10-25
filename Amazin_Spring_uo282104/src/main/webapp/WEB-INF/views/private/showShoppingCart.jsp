<!DOCTYPE html >
<%@ page contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" language="java"
    import="java.util.*, com.miw.model.Book" errorPage=""%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page isELIgnored="false"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<head>
<title>Amazin - <spring:message code="menu.viewCart"/></title>
<link href="<c:url value="/resources/css/style.css" />" rel="stylesheet">
<script>
    function changeLanguage() {
        var lang = document.getElementById('languageSelector').value;
        window.location.href = window.location.pathname + '?lang=' + lang;
    }
</script>
</head>
<body>
    <header>
        <h1 class="header">Amazin.com</h1>
        <h2 class="centered">
            <spring:message code="welcome"/>
        </h2>
        <!-- Language Selector -->
        <div style="text-align: right; padding: 10px;">
            <label for="languageSelector"><spring:message code="language.select"/>:</label>
            <select id="languageSelector" onchange="changeLanguage()">
                <option value="en" ${pageContext.response.locale.language == 'en' ? 'selected' : ''}>
                    en
                </option>
                <option value="es" ${pageContext.response.locale.language == 'es' ? 'selected' : ''}>
                    es
                </option>
            </select>
        </div>
    </header>
    <nav>
        <ul>
            <li><a href="menu"><spring:message code="start"/></a></li>
            <li><a href="newBook"><spring:message code="books.addNew"/></a></li>
            <li><a href="showReservations"><spring:message code="menu.viewReservations"/></a></li>
            <li><a href="http://miw.uniovi.es"><spring:message code="about"/></a></li>
            <li><a href="mailto:dd@email.com"><spring:message code="contact"/></a></li>
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
                    <p><spring:message code="cart.empty"/></p>
                    <a href="showBooks"><spring:message code="cart.continueShopping"/></a>
                </c:when>
                <c:otherwise>
                    <table>
                        <caption><spring:message code="cart.title"/></caption>
                        <thead>
                            <tr>
                                <th><spring:message code="cart.status"/></th>
                                <th><spring:message code="books.title"/></th>
                                <th><spring:message code="books.author"/></th>
                                <th><spring:message code="books.price"/></th>
                                <th><spring:message code="cart.quantity"/></th>
                                <th><spring:message code="cart.availableStock"/></th>
                                <th><spring:message code="cart.subtotal"/></th>
                                <th><spring:message code="books.actions"/></th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var='cartItem' items="${cartItems}">
                                <tr class="${cartItem.reserved ? 'reserved-row' : ''}">
                                    <td>
                                        <c:if test="${cartItem.reserved}">
                                            <span class="reserved-badge"><spring:message code="cart.reserved"/></span>
                                        </c:if>
                                        <c:if test="${!cartItem.reserved}">
                                            <span><spring:message code="cart.regular"/></span>
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
                                            <br/><small style="color: green;"><spring:message code="cart.reservationNote"/></small>
                                        </c:if>
                                    </td>
                                    <td>
                                        <form action="removeFromCart" method="post" style="display: inline;">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                            <input type="hidden" name="bookId" value="${cartItem.book.id}" />
                                            <input type="submit" value="<spring:message code='cart.remove'/>" />
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <tr>
                                <td colspan="6" style="text-align: right;"><strong><spring:message code="cart.total"/></strong></td>
                                <td colspan="1"><strong><c:out value="${String.format('%.2f', total)}" /> &euro;</strong></td>
                                <td colspan="1"><td/>
                            </tr>
                        </tbody>
                    </table>

                    <br />
                    <form action="checkout" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="submit" value="<spring:message code='cart.checkout'/>"/>
                    </form>
                    <br />
                    <a href="showBooks"><spring:message code="cart.continueShopping"/></a>
                </c:otherwise>
            </c:choose>
        </article>
    </section>
    <footer>
        <strong><spring:message code="footer1"/></strong><br /> 
        <em><spring:message code="footer2"/></em>
    </footer>
</body>
