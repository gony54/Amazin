<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html >
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<head>
<title>Amazin - <spring:message code="start"/></title>
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
            <li><a href="#"><spring:message code="start"/></a></li>
            <li><a href="http://miw.uniovi.es"><spring:message code="about"/></a></li>
            <li><a href="mailto:dd@email.com"><spring:message code="contact"/></a></li>
        </ul>
    </nav>
    <section>
        <article id="a01">
            <label class="mytitle"><spring:message code="menu.choose"/></label><br /> 
            <a href="showBooks"><spring:message code="menu.showCatalog"/></a><br /> 
            <a href="showSpecialOffer"><spring:message code="menu.showSpecialOffers"/></a><br>
            <a href="showShoppingCart"><spring:message code="menu.viewCart"/></a><br>
            <a href="showReservations"><spring:message code="menu.viewReservations"/></a><br>
            <spring:message code="menu.visits"/> <c:out value="${loginCounter.logins}"/> 
        </article>
    </section>
    <footer>
        <strong><spring:message code="footer1"/></strong><br /> 
        <em><spring:message code="footer2"/></em>
    </footer>
</body>
