<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>

<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>
<input type="button" value="exit"
       onClick="window.location.href = 'musics'">
<br>

<form:form action="upload" method="post" modelAttribute="music" enctype="multipart/form-data">

    Name <form:input path="name"/>
    <br><br>
    Surname <form:input path="artist"/>
    <body>
    <h2>Загрузить файл</h2>
    <% if (request.getAttribute("message") != null) { %>
    <p style="color: green;"><%= request.getAttribute("message") %>
    </p>
    <% } %>
    <input type="file" name="file"/>
    </body>
    <br>
    <input type="submit" value="OK">

</form:form>


</body>

</html>
