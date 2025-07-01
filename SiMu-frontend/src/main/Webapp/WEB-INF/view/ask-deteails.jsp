<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>

<body>

<h2> det</h2>>

<br>
<br>

<form:form action="musics" modelAttribute="music">

    Name <form:input path="name"/>
    <br><br>
    Surname <form:input path="artist"/>

    <input type="submit" value="OK">

</form:form>


</body>

</html>
