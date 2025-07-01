<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Music List</title>
</head>


<body>
<table>
    <tr>
        <th>Name</th>
        <th>artist</th>
        <th>music</th>
    </tr>

    <%--<c:forEach var="music" items="${listMusics}">
        <tr>
            <td>${music.name}</td>
            <td>${music.artist}</td>
            <td>${music.url}</td>
            <td>
                <audio controls>
                    <source src="${music.url}" type="audio/mpeg">
                    Ваш браузер не поддерживает аудио.
                </audio>
            </td>
        </tr>
    </c:forEach>--%>
</table>
</body>


<input type="button" value="add"
       onClick="window.location.href = 'uploadForm'"
/>

</html>
