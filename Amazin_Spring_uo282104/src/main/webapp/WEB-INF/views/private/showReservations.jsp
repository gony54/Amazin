<!DOCTYPE html >
<%@ page contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" language="java"
    import="java.util.*, com.miw.model.Book" errorPage=""%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page isELIgnored="false"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<head>
<title>Amazin - <spring:message code="menu.viewReservations"/></title>
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
            <li><a href="showBooks"><spring:message code="menu.showCatalog"/></a></li>
            <li><a href="showShoppingCart"><spring:message code="menu.viewCart"/></a></li>
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

            <c:choose>
                <c:when test="${empty reservedItems}">
                    <p><spring:message code="reservations.empty"/></p>
                    <a href="showBooks"><spring:message code="reservations.browseCatalog"/></a>
                </c:when>
                <c:otherwise>
                    <table>
                        <caption><spring:message code="reservations.title"/></caption>
                        <thead>
                            <tr>
                                <th><spring:message code="books.title"/></th>
                                <th><spring:message code="books.author"/></th>
                                <th><spring:message code="reservations.fullPrice"/></th>
                                <th><spring:message code="cart.quantity"/></th>
                                <th><spring:message code="reservations.paid"/></th>
                                <th><spring:message code="reservations.remaining"/></th>
                                <th><spring:message code="books.actions"/></th>
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
                                            <input type="submit" value="<spring:message code='reservations.completePurchase'/>" />
                                        </form>

                                        <form action="cancelReservation" method="post" style="display: inline;">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                            <input type="hidden" name="bookId" value="${cartItem.book.id}" />
                                            <input type="submit" value="<spring:message code='reservations.cancel'/>" />
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <tr>
                                <td colspan="4" style="text-align: right;"><strong><spring:message code="reservations.totalPaid"/></strong></td>
                                <td><strong style="color: green;"><c:out value="${String.format('%.2f', totalPaid)}" /> &euro;</strong></td>
                                <td colspan="2"><td/>
                            </tr>
                        </tbody>
                    </table>

                    <br />
                    <a href="showBooks"><spring:message code="reservations.continueBrowsing"/></a>
                </c:otherwise>
            </c:choose>
        </article>
    </section>
    <footer>
        <strong><spring:message code="footer1"/></strong><br /> 
        <em><spring:message code="footer2"/></em>
    </footer>
</body>
