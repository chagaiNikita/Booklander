<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="css/freemarker.css" rel="stylesheet">
    <title>Список книг</title>
</head>
<body>
<form action="/logout" method="POST" style="position: absolute; top: 10px; right: 10px;">
    <button type="submit" class="logout-button">Выйти</button>
</form>
<div><h1>Список книг</h1></div>
<div class="table flex flex-col">
    <div class="header flex">
        <div class="column">ID</div>
        <div class="column">Название</div>
        <div class="column">Автор</div>
        <div class="column">Статус</div>
        <div class="column">Дата выдачи</div>
        <div class="column">Кому выдана</div>
        <div class="column">Действие</div>
    </div>
    <#list books as book>
        <#assign userName = "Не указано">
        <#assign issueDate = "Не указано">
        <#assign userId = "Не указано">
        <#list bookHistories as bookHistory>
            <#if bookHistory.bookId == book.id>
                <#list users as user>
                    <#if user.id == bookHistory.userId && !bookHistory.returnDate??>
                        <#assign userId = user.id>
                        <#assign userName = user.fullName>
                        <#assign issueDate = bookHistory.issueDate>
                    </#if>
                <#else>
                    <#assign issueDate = "Не указано">
                    <#assign userName = "Не указано">
                    <#assign userId = "Не указано">
                </#list>
            </#if>
        </#list>
    <div class="row ${book?item_parity} flex">
        <div class="column">${book.id!"-- не указано --"}</div>
        <div class="column"><a href="/book?book-id=${book.id!"#"}">${book.name!"-- не указано --"}</a></div>
        <div class="column">${book.author!"-- не указано --"}</div>
        <div class="column">${book.status!"-- не указано --"}</div>
        <div class="column">${issueDate}</div>
        <div class="column">
            <#if userName == "Не указано">
            <span>${userName}</span>
            <#else>
                <a href="/employee?user-id=${userId}"#">${userName}</a>
            </#if>
        </div>
        <div class="column">
            <form action="/save-book" method="POST">
                <input type="hidden" name="id" value="${book.id!""}">
                <button type="submit" class="save-button">Сохранить</button>
            </form>
            <form action="/return-book" method="POST">
                <input type="hidden" name="id" value="${book.id!""}">
                <button type="submit" class="save-button">Вернуть</button>
            </form>
        </div>
    </div>
</#list>
    <div>
     <#if error?? && error>
        <p class="message error">
        ${message} <br>
            <#if authError?? && authError>
            <a href="/login">Перейдите сюда для авторизации</a>
            </#if>
    </p>
    </#if>
</div>
</div>


</body>
</html>


