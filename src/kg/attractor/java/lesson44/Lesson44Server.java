package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kg.attractor.java.models.Book;
import kg.attractor.java.models.BookLender;
import kg.attractor.java.models.User;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.Cookie;
import kg.attractor.java.server.ResponseCodes;
import kg.attractor.java.util.CodeGenerator;
import kg.attractor.java.util.FileUtil;
import kg.attractor.java.util.Utils;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lesson44Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();
    private BookLender bookLender;
    private boolean registerSuccess;
    private boolean fieldIsBlank;

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/books", this::freemarkerSampleHandler);
        registerGet("/book", this::freemarkerSampleHandler);
        registerGet("/employee", this::freemarkerSampleHandler);
        registerGet("/register", this::registerGet);
        registerPost("/register", this::registerPost);
        registerGet("/registerResult", this::sendRegResult);
        registerGet("/login", this::loginGet);
        registerPost("/login", this::loginPost);
        registerGet("/profile", this::profileGetForLink);
        registerPost("/save-book", this::saveBook);
        registerPost("/return-book", this::returnBook);
        bookLender = FileUtil.readFromFile();
        registerSuccess = true;
        fieldIsBlank = false;
    }

    private void returnBook(HttpExchange exchange) {
        Map<String, String> cookies = Cookie.parse(getCookie(exchange));
        String cookieVal = cookies.getOrDefault("cookieCode", null);
        User user = getUserByCookieCode(cookieVal);
        if (user != null) {
            Book bookForReturn = getBookById(exchange);
            user.removeBookFromCurBooks(bookForReturn);
            resetBookParameter(bookForReturn);
            renderTemplate(exchange, "/books.html", getBookList());
        } else {
            notAuthErrorHandler(exchange, bookLender.getBooks());
        }
    }

    private void resetBookParameter(Book book) {
        book.setUserName(null);
        book.setIssueDate(null);
        book.setStatus("На месте");
    }

    private void saveBook(HttpExchange exchange) {
        List<Book> bookList = bookLender.getBooks();
        String cookie = getCookie(exchange);
        Map<String, String> cookies = Cookie.parse(cookie);
        String cookieVal = cookies.getOrDefault("cookieCode", null);
        User user = getUserByCookieCode(cookieVal);
        if (user != null) {
            Book book = getBookById(exchange);
            System.out.println("Начата добавка книги");
            if (user.getCurrentBooks().size() == bookLender.getBookLimitOnEmployee()) {
                Map<String, Object> map = new HashMap<>();
                map.put("books", bookList);
                map.put("error", true);
                map.put("message", "Нельзя взять больше " + bookLender.getBookLimitOnEmployee() + " книг!");
                renderTemplate(exchange, "/books.html", map);
            } else {
                addBookInCurrentBookList(exchange, book, user);
            }

        } else {
            notAuthErrorHandler(exchange, bookList);
        }


    }

    private void addBookInCurrentBookList(HttpExchange exchange, Book book, User user) {
        user.addBookInCurBooks(book);
        book.setUserName(user.getLogin());
        book.setStatus("Выдана");
        book.setIssueDate(LocalDate.now());
        System.out.println("Закончилась");
        FileUtil.writeToFile(bookLender);
        renderTemplate(exchange, "/books.html", getBookList());
    }

    private void notAuthErrorHandler(HttpExchange exchange, List<Book> bookList) {
        Map<String, Object> map = new HashMap<>();
        map.put("books", bookList);
        map.put("error", true);
        map.put("message", "Невозможно совершить действие без авторизации!");
        map.put("authError", true);
        renderTemplate(exchange, "/books.html", map);
    }

    private void profileGetForLink(HttpExchange exchange) {
        renderTemplate(exchange, "/profile.html", null);

    }

    private Book getBookById(HttpExchange exchange) {
        String book = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(book, "&");
        int bookId = Integer.parseInt(parsed.get("id"));
        List<Book> books = bookLender.getBooks();
        for (Book b : books) {
            if (b.getId() == bookId) {
                return b;
            }
        }
        return null;
    }

    private void registerPost(HttpExchange exchange) {
        registerSuccess = true;
        System.out.println("register Post");

        String raw = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");
        System.out.println("start 58");
        User user = new User(parsed.get("login"), parsed.get("email"), parsed.get("user-password"));
        System.out.println("start 61");
        registerNewUser(user);
        sendRegResult(exchange);

        redirect303(exchange, "/registerResult");
    }

    private void registerNewUser(User user) {
        if (user.getEmail() == null || user.getLogin() == null || user.getPassword() == null
        || user.getEmail().isBlank() || user.getLogin().isBlank() || user.getPassword().isBlank()) {
            registerSuccess = false;
            fieldIsBlank = true;
            return;
        }
        if (bookLender.getUsers() != null) {
            for (User u : bookLender.getUsers()) {
                if (u.getEmail().equals(user.getEmail()) || u.getLogin().equals(user.getLogin())) {
                    System.out.println("Пользователь с такими id уже существует");
                    registerSuccess = false;
                    return;
                }
            }
        }
        bookLender.addUser(user);
        FileUtil.writeToFile(bookLender);
        System.out.println("Регистрация прошла успешно");

    }


    private void sendRegResult(HttpExchange exchange) {
        Map<String, String> map = new HashMap<>();
        String errorMessage;
        if (fieldIsBlank) {
            errorMessage = "Поля не должны быть пустыми! Нажмите сюда и попробуйте еще раз";

        } else {
            errorMessage = "Пользователь с такой почтой или логином уже существует. Нажмите сюда чтобы попробовать еще раз";
        }
        if (registerSuccess) {
            map.put("message", "success");
            map.put("class", "success");
            map.put("link", "/books");
            map.put("linkMessage", "Регистрация прошла успешно! Нажмите сюда чтобы перейти к списку книг");
        } else {
            map.put("message", "error");
            map.put("class", "error");
            map.put("link", "/register");
            map.put("linkMessage", errorMessage);
        }

        renderTemplate(exchange, "auth/registerResult.html", map);
    }

    private void registerGet(HttpExchange exchange) {
        Map<String, Object> buttonText = new HashMap<>();
        buttonText.put("class", "");
        buttonText.put("buttonText", "Зарегистрироваться");
        buttonText.put("showEmailField", true);
        buttonText.put("postLink", "/register");
        renderTemplate(exchange, "/auth/login.ftlh", buttonText);
    }

    private void loginPost(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");
        System.out.println(parsed);
        User user = checkLogin(parsed.get("login"), parsed.get("user-password"));
        if (user == null) {
            Map<String, Object> invalidLogin = new HashMap<>();
            invalidLogin.put("buttonText", "Войти");
//          invalidLogin.put("class", "email");
            invalidLogin.put("postLink", "/login");
            invalidLogin.put("invalidLoginError", true);
            renderTemplate(exchange, "/auth/login.ftlh", invalidLogin);
        } else {
            Map<String, Object> model = new HashMap<>();
            model.put("user", user);
            Cookie userCode = Cookie.make("cookieCode", user.getCookieCode());
            userCode.setMaxAge(600);
            userCode.setHttpOnly(true);
            setCookie(exchange, userCode);
            renderTemplate(exchange, "/profile.html", model);
        }
    }

    private User checkLogin(String login, String password) {
        for (User u : bookLender.getUsers()) {
            if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
                u.setCookieCode(CodeGenerator.makeCode(u.getEmail() + u.getLogin()));
                return u;
            }

        }
        return null;
    }

    private User getUserByCookieCode(String cookieCode) {
        List<User> users = bookLender.getUsers();
        for (User u  : users) {
            if (u.getCookieCode() != null && u.getCookieCode().equals(cookieCode)) return u;
        }
        return null;
    }

    private void loginGet(HttpExchange exchange) {
        Map<String, String> buttonText = new HashMap<>();
        buttonText.put("buttonText", "Войти");
//        buttonText.put("class", "email");
        buttonText.put("postLink", "/login");
        renderTemplate(exchange, "/auth/login.ftlh", buttonText);
    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void freemarkerSampleHandler(HttpExchange exchange) {
        Object model = getModel(exchange);
        renderTemplate(exchange, getFileNameHTML(exchange.getRequestURI().getPath()), model);
    }
    private Object getModel(HttpExchange exchange) {
        if (exchange.getRequestURI().getPath().equals("/book")) return getBookInfo();
        if (exchange.getRequestURI().getPath().equals("/employee")) return getEmployeeInfo();
        else return getBookList();
    }

    private String getFileNameHTML(String uri) {
        return uri.substring(uri.indexOf("/")) + ".html";
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {


                temp.process(dataModel, writer);
                writer.flush();

                var data = stream.toByteArray();

                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private Map<String, List<Book>> getBookList() {
        List<Book> books = bookLender.getBooks();
//        bookLender.setBooks(books);
        Map<String, List<Book>> f = new HashMap<>();
        f.put("books", books);

        return f;
    }

    private Map<String, Book> getBookInfo() {
        Map<String, Book> f = new HashMap<>();
        f.put("book", getExampleBooks().getFirst());

        return f;
    }

    private Map<String, Object> getEmployeeInfo() {
        Map<String, Object> employee = new HashMap<>();
        employee.put("user", getExampleUsers().getFirst());
        employee.put("currentBooks", getExampleUsers().getFirst().getCurrentBooks());
        employee.put("pastBooks", getExampleUsers().getFirst().getPastBooks());
        return employee;
    }

    private List<Book> getExampleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "Война и мир", "images/0002-min-png.png", "Лев Толстой", "Выдана", "book",
                "Война и мир» — огромная сага", LocalDate.of(2020, 12, 11), "Michael"));
        books.add(new Book(2, "Колобок", "images/0002-min-png.png" , "Алексей Толстой", "На месте", "book", "Колобок описание", null, null));
        books.add(new Book(3, "Игра престолов", "images/0002-min-png.png", "Джордж Мартин", "Выдана", "book", "Игра престолов» (англ. A Game of Thrones) — роман в жанре фэнтези", LocalDate.of(2023, 5, 1), "Nikita"));
        bookLender.setBooks(books);

        return books;

    }

    private List<User> getExampleUsers() {
        List<User> users = new ArrayList<>();
        List<Book> books = getExampleBooks();
        List<Book> currentBook = books.stream()
                .filter(b -> b.getUserName() != null)
                .filter(book -> book.getUserName().equals("Michael"))
                        .toList();
        List<Book> pastBooks = new ArrayList<>();
        pastBooks.add(books.get(1));
        pastBooks.add(books.get(2));
        users.add(new User(1, "Michael", currentBook, pastBooks));
        bookLender.setUsers(users);
        return users;
    }

    private List<User> getExampleUsersForCheckRegister() {
        List<User> users = new ArrayList<>();
        users.add(new User("nikita321", "nikita@gmail.com", "321"));
        users.add(new User("pushkin5", "pushkin@gmail.com", "password"));
        users.add(new User("clown", "clown@gmail.com", "superpassword"));
        return users;
    }
}
