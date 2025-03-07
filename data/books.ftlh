<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link href="css/freemarker.css" rel="stylesheet">
    <title>Список книг</title>
</head>
<body>
<form action="/logout" method="POST" style="position: absolute; top: 10px; right: 10px;">
    <button type="submit" class="logout-button">Выйти</button>
</form>
<nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">BookLander</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="/books">Books</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/profile">Profile</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/register">Register</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/login">Login</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<div class = container>
    <div><h1>Список книг</h1></div>
    <div class="my-table my-flex my-flex-col">
        <div class="my-header my-flex">
            <div class="my-column">ID</div>
            <div class="my-column">Название</div>
            <div class="my-column">Автор</div>
            <div class="my-column">Статус</div>
            <div class="my-column">Дата выдачи</div>
            <div class="my-column">Кому выдана</div>
            <div class="my-column">Действие</div>
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
            <div class="my-row ${book?item_parity} my-flex">
                <div class="my-column">${book.id!"-- не указано --"}</div>
                <div class="my-column"><a href="/book?book-id=${book.id!"#"}">${book.name!"-- не указано --"}</a></div>
                <div class="my-column">${book.author!"-- не указано --"}</div>
                <div class="my-column">${book.status!"-- не указано --"}</div>
                <div class="my-column">${issueDate}</div>
                <div class="my-column">
                    <#if userName == "Не указано">
                        <span>${userName}</span>
                    <#else>
                        <a href="/employee?user-id=${userId}"#">${userName}</a>
                    </#if>
                </div>
                <div class="my-column">
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
</div>


<script src = https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js></script>
</body>
</html>


