<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Профиль пользователя</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/profile.css">
</head>

<body>
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
<form action="/logout" method="POST" style="position: absolute; top: 10px; right: 10px;">
    <button type="submit" class="logout-button">Выйти</button>
</form>
    <div class="profile-container">
        <h2>Профиль пользователя</h2>
        <p><strong>Имя:</strong> ${user.login!"Некий пользователь"}</p>
        <p><strong>Email:</strong> ${user.email!"example@example.com"}</p>
    </div>
    <#if (haveCurBooks?? && haveCurBooks) || (havePastBooks?? && havePastBooks)>
    <div>
        <#if haveCurBooks ?? && haveCurBooks>
        <h3>Текущие книги:</h3>
        <ul>
            <#list currentBooks as book>
            <li class="${book?item_parity}">
                ${book.name} - ${book.author}
            </li>
        </#list>
        </ul>
    </#if>
        <#if havePastBooks ?? && havePastBooks>
        <h3>Предыдущие книги:</h3>
        <ul>
            <#list pastBooks as book>
            <li class="${book?item_parity}">
                ${book.name} - ${book.author}
            </li>
        </#list>
        </ul>
        </#if>
    </div>
</#if>


<script src = https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js></script>
</body>

</html>
