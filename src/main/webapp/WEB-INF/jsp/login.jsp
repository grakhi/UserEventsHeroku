<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title> </title>
    </head>
    <h2>User Events </h2>
    <p> Registration</p>
    <p>Login</p>
    
<head>
     <title>Login Form</title>
   
</head>
<body>
<h1> From login.jsp</h1>
<div class="container">
 
 <!--    
<div th:if="${param.error}">
    <label style="color:red">Invalid username and password.</label>
</div>
<div th:if="${param.logout}">
    <label>
        You have been logged out.
    </label>
</div>
 -->
 
<form action="/rest" method="get">
 
    <table >
        <tr>
            <td><label> User Name : <input type="text" name="username"/> </label></td>
        </tr>
        <tr>
            <td><label> Password : <input type="password" name="password"/> </label></td>
        </tr>
        <tr>
            <td> <button type="submit" >Sign In</button></td>
        </tr>
    </table>
 
</form>
</div>
</body>
</html>
</html>