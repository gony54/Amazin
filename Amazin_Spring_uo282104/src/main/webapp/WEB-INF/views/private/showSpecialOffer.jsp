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
<title>Amazin - <spring:message code="menu.showSpecialOffers"/></title>
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
            <li><a href="http://miw.uniovi.es"><spring:message code="about"/></a></li>
            <li><a href="mailto:dd@email.com"><spring:message code="contact"/></a></li>
        </ul>
    </nav>
    <section>
        <article>
            <table>
                <caption><spring:message code="specialOffer.title"/></caption>
                <thead>
                    <tr>
                        <th><spring:message code="books.title"/></th>
                        <th><spring:message code="books.author"/></th>
                        <th><spring:message code="books.description"/></th>
                        <th><spring:message code="books.price"/></th>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td><c:out value="${book.title}" /></td>
                        <td><c:out value="${book.author}" /></td>
                        <td><c:out value="${book.description}" /></td>
                        <td><c:out value="${book.price}" /> &euro;</td>
                    </tr>
                </tbody>
            </table>
        </article>
    </section>
    <footer>
        <strong><spring:message code="footer1"/></strong><br /> 
        <em><spring:message code="footer2"/></em>
    </footer>
</body>
