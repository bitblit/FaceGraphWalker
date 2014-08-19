<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <title><spring:message code="mvc.normal.title"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/bootstrap-theme.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/vendor/jquery-2.0.3.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/vendor/bootstrap.js"></script>
    <sitemesh:write property="head"/>
</head>
<body>
<div class="page">
    <div class="navbar navbar-default" role="navigation">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                <span class="sr-only"><spring:message code="label.navigation.toggle.navigation.button"/></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <span class="navbar-brand">Facebook Graph Walker</span>
        </div>
        <div class="collapse navbar-collapse navbar-ex1-collapse">
            <ul class="nav navbar-nav navbar-left">
                <sec:authorize access="isAuthenticated()">
                    <li><a href="/"><spring:message code="label.navigation.home.link"/></a></li>
                </sec:authorize>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <sec:authorize access="isAnonymous()">
                    <li><a href="/user/register"><spring:message code="label.navigation.registration.link"/></a></li>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <li>
                        <form action="/logout" method="POST">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="btn btn-default navbar-btn">
                                <spring:message code="label.navigation.logout.link"/>
                            </button>
                        </form>
                    </li>
                </sec:authorize>
            </ul>
            <sec:authorize access="isAuthenticated()">
                <p class="nav navbar-nav navbar-right navbar-text sign-in-text">
                    <sec:authentication property="principal.socialSignInProvider" var="signInProvider"/>
                    <c:if test="${signInProvider == 'FACEBOOK'}">
                        <i class="icon-facebook-sign"></i>
                    </c:if>
                    <c:if test="${signInProvider == 'TWITTER'}">
                        <i class="icon-twitter-sign"></i>
                    </c:if>
                    <c:if test="${empty signInProvider}">
                        <spring:message code="label.navigation.signed.in.as.text"/>
                    </c:if>
                    <sec:authentication property="principal.username"/>
                </p>
            </sec:authorize>
        </div><!-- /.navbar-collapse -->
    </div>
    <div class="content">
        <div id="view-holder">
            <div class="page-header">
                <h1><spring:message code="label.user.login.page.title"/></h1>
            </div>
            <sec:authorize access="isAnonymous()">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <h2><spring:message code="label.social.sign.in.title"/></h2>
                        <div class="row social-button-row">
                            <div class="col-lg-4">
                                <a href="<c:url value="/auth/facebook"/>"><button class="btn btn-facebook"><i class="icon-facebook"></i> | <spring:message code="label.facebook.sign.in.button"/></button></a>
                            </div>
                        </div>
                    </div>
                </div>
            </sec:authorize>
            <sec:authorize access="isAuthenticated()">
                <p><spring:message code="text.login.page.authenticated.user.help"/></p>
            </sec:authorize>        </div>
    </div>
</div>
</body>
</html>