<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


 
<p> Register </p>
<div>


<form action="/signup" method="POST">


<p>First name: <input type="text" name="firstname" /></p>
<p>Last name: <input type="text" name="lastname" /></p>
<p>Email: <input type="text" name="email" /></p>
<p>Location: <input type="text" name="location" /></p>
<p>State: <input type="text" name="state" /></p>
<p>Password: <input type="text" name="password" /></p>
<p>Confirm Password: <input type="text" name="password" /></p>

<input type="submit" value="Register" />

</form>

</div>

 
 
 
<form:form action="${pageContext.request.contextPath}/authenticateTheUser" method="POST">





 <c:if test="${param.error != null }">
  
    <i>Invalid username/password.</i>
 
 </c:if>


<p>
 User name: <input type="text" name="email" />
</p>

<p> 
Password:  <input type="password" name="password" />
</p>

<input type="submit" value="Login" />


</form:form>