<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Login form</title>
    <link rel="stylesheet" href="/auth/css/forms.css">
</head>

<body>
    <main>
        <form action="${postLink}" method="post">
            <fieldset>
                <div class="legend">
                    <p>Welcome!</p>
                    <img src="../images/1.jpg" alt="coins" style="display:block; width: 200px; height: 200px;">
                </div>
                <#if showEmailField?? && showEmailField>
                    <div class="form-element">
                        <label for="user-email">email</label>
                        <input type="email" name="email" id="user-email" placeholder="your email" required autofocus>
                    </div>
                </#if>

                <div class="form-element">
                    <label for="user-login">login</label>
                    <input type="login" name="login" id="user-login" placeholder="your login" required autofocus>
                </div>
                <div class="form-element">
                    <label for="user-password">password</label>
                    <input type="password" name="user-password" id="user-password" placeholder="your password" required>
                </div>
                <div class="hr-line">
                    <span class="details">one more step to go</span>
                </div>
                <div class="form-element">
                    <button class="register-button" type="submit">${buttonText}</button>
                </div>
            </fieldset>
        </form>
        <#if invalidLoginError?? && invalidLoginError>
            <p class="message error">
                Неверный логин или пароль повторите попытку
            </p>
        </#if>

    </main>
</body>

</html>