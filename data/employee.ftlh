<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${user.fullName}</title>
</head>
<body>
<form action="/logout" method="POST" style="position: absolute; top: 10px; right: 10px;">
    <button type="submit" class="logout-button">Выйти</button>
</form>
    <div><h1>Имя сотрудника: ${user.fullName}</h1></div>
    <div>
        <h2>Текущие книги</h2>
        <ul>
            <#list currentBooks as book>
            <li class="${book?item_parity}">
                ${book.name} ${book.author}
            </li>
        </#list>
        </ul>
    </div>
    <div>
        <h2>Прошлые книги</h2>
        <ul>
            <#list pastBooks as book>
            <li class="${book?item_parity}">
                ${book.name} ${book.author}
            </li>
        </#list>
        </ul>
    </div>

</body>
</html>