<!DOCTYPE html >
<%@ page contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" language="java"
    import="java.util.*, com.miw.model.Book,com.miw.presentation.book.*"
    errorPage=""%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page isELIgnored="false"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<head>
<title>Amazin - <spring:message code="books.title"/></title>
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
            <li><a href="showShoppingCart"><spring:message code="menu.viewCart"/></a></li>
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
            
            <table>
                <caption><spring:message code="books.catalog"/></caption>
                <thead>
                    <tr>
                        <th><spring:message code="books.title"/></th>
                        <th><spring:message code="books.author"/></th>
                        <th><spring:message code="books.description"/></th>
                        <th><spring:message code="books.price"/></th>
                        <th><spring:message code="books.stock"/></th>
                        <th><spring:message code="books.actions"/></th>
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
                                        <input type="submit" value="<spring:message code='books.buy'/>" />
                                    </form>
                                    
                                    <form action="reserveBook" method="post" style="display: inline;">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                        <input type="hidden" name="bookId" value="${book.id}" />
                                        <input type="number" name="quantity" value="1" min="1" max="${book.stock}" style="width: 50px;" />
                                        <input type="submit" value="<spring:message code='books.reserve'/>" />
                                    </form>
                                </c:if>
                                <c:if test="${book.stock == 0}">
                                    <span style="color: red;"><spring:message code="books.outOfStock"/></span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </article>
    </section>
    <footer>
        <strong><spring:message code="footer1"/></strong><br /> 
        <em><spring:message code="footer2"/></em>
    </footer>
</body>
