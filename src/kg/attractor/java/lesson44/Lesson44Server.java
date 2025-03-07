package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kg.attractor.java.enums.BookStatuses;
import kg.attractor.java.models.Book;
import kg.attractor.java.models.BookHistory;
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
import java.util.*;

public class Lesson44Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();
    private BookLender bookLender;
    private boolean registerSuccess;
    private boolean fieldIsBlank;

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
        requestRegister();
        bookLender = FileUtil.readFromFile();
    }

    private void requestRegister() {
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
        registerPost("/logout", this::logoutHandler);
        registerSuccess = true;
        fieldIsBlank = false;
    }

    private void logoutHandler(HttpExchange exchange) {
        Map<String, String> cookies = Cookie.parse(getCookie(exchange));
        String cookieVal = cookies.getOrDefault("cookieCode", null);
        User user = getUserByCookieCode(cookieVal);
        if (user != null) {
            user.setCookieCode(null);
            Cookie userCode = Cookie.make("cookieCode", "");
            userCode.setMaxAge(0);
            userCode.setHttpOnly(true);
            setCookie(exchange, userCode);
            redirect303(exchange, "/login");
        } else {
            booksError(exchange, "Невозможно совершить данное действие без авторизации", true);
        }
    }

    private void returnBook(HttpExchange exchange) {
        Map<String, String> cookies = Cookie.parse(getCookie(exchange));
        String cookieVal = cookies.getOrDefault("cookieCode", null);
        User user = getUserByCookieCode(cookieVal);
        if (user != null) {
            Book bookForReturn = getBookById(exchange);
            if (user.getCurrentBooks(bookLender).contains(bookForReturn)) {
                resetBookParameter(bookForReturn);
                BookHistory history = bookLender.getHistory().stream()
                        .filter(h -> h.getBookId() == bookForReturn.getId() && h.getUserId() == user.getId() && h.getReturnDate() == null)
                        .findFirst()
                        .orElse(null);
                history.setReturnDate(LocalDate.now());
                FileUtil.writeToFile(bookLender);
                renderTemplate(exchange, "/books.ftlh", getBookList());
            } else {
                booksError(exchange, "Невозможно вернуть книгу т.к она не находится у вас", false);
            }
        } else {
            notAuthErrorHandler(exchange, bookLender.getBooks());
        }
    }

    private void booksError(HttpExchange exchange, String errorMessage, Boolean authError) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(getBookList());
        map.put("error", true);
        map.put("message", errorMessage);
        map.put("authError", authError);
        renderTemplate(exchange, "/books.ftlh", map);
    }

    private void resetBookParameter(Book book) {
        book.setStatus(BookStatuses.ONTHESPOT.getTitle());
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
            if (user.getCurrentBooks(bookLender).size() == bookLender.getBookLimitOnEmployee()) {
                Map<String, Object> map = new HashMap<>();
                map.putAll(getBookList());
                map.put("error", true);
                map.put("message", "Нельзя взять больше " + bookLender.getBookLimitOnEmployee() + " книг!");
                renderTemplate(exchange, "/books.ftlh", map);
            } else {
                addBookInCurrentBookList(exchange, book, user);

            }

        } else {
            notAuthErrorHandler(exchange, bookList);
        }


    }

    private void addBookInCurrentBookList(HttpExchange exchange, Book book, User user) {
        if (book.getStatus().equals(BookStatuses.ISSUED.getTitle())) {
            bookIsBusyError(exchange, bookLender.getBooks());
        } else {
            book.setStatus(BookStatuses.ISSUED.getTitle());
            BookHistory history = new BookHistory(book.getId(), user.getId(),LocalDate.now());
            bookLender.addHistory(history);
            FileUtil.writeToFile(bookLender);
            renderTemplate(exchange, "/books.ftlh", getBookList());
        }

    }

    private void bookIsBusyError(HttpExchange exchange, List<Book> bookList) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(getBookList());
        map.put("error", true);
        map.put("message", "Невозможно получить книгу т.к она занята");
        renderTemplate(exchange, "books.ftlh", map);
    }

    private void notAuthErrorHandler(HttpExchange exchange, List<Book> bookList) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(getBookList());
        map.put("error", true);
        map.put("message", "Невозможно совершить действие без авторизации!");
        map.put("authError", true);
        renderTemplate(exchange, "/books.ftlh", map);
    }

    private void profileGetForLink(HttpExchange exchange) {
        Map<String, String> cookies = Cookie.parse(getCookie(exchange));
        String cookieVal = cookies.getOrDefault("cookieCode", null);
        User user = getUserByCookieCode(cookieVal);
        Map<String, Object> model = new HashMap<>();
        if (user != null) {
            model.put("user", user);
            if (user.getCurrentBooks(bookLender) != null) {
                model.put("currentBooks", user.getCurrentBooks(bookLender));
                model.put("haveCurBooks", true);
            }
            if (user.getPastBooks(bookLender) != null) {
                model.put("havePastBooks", true);
                model.put("pastBooks", user.getPastBooks(bookLender));
            }
            renderTemplate(exchange, "/profile.ftlh", model);
        } else {
            String errorMessage = "Вы не были авторизованы";
            model.put("message", "error");
            model.put("class", "error");
            model.put("link", "/login");
            model.put("linkMessage", errorMessage);
            renderTemplate(exchange, "auth/registerResult.ftlh", model);
        }


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
        User user = new User(getUniqueId(), parsed.get("login"), parsed.get("email"), parsed.get("user-password"), getRandomName());
        System.out.println("start 61");
        registerNewUser(user);
        setCookieCodeForUser(user);
        Cookie userCode = Cookie.make("cookieCode", user.getCookieCode());
        userCode.setMaxAge(600);
        userCode.setHttpOnly(true);
        setCookie(exchange, userCode);
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

        renderTemplate(exchange, "auth/registerResult.ftlh", map);
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
            invalidLogin.put("postLink", "/login");
            invalidLogin.put("invalidLoginError", true);
            renderTemplate(exchange, "/auth/login.ftlh", invalidLogin);
        } else {
            Map<String, Object> model = new HashMap<>();
            model.put("user", user);
            if (user.getCurrentBooks(bookLender) != null) {
                model.put("currentBooks", user.getCurrentBooks(bookLender));
                model.put("haveCurBooks", true);
            }
            if (user.getPastBooks(bookLender) != null) {
                model.put("havePastBooks", true);
                model.put("pastBooks", user.getPastBooks(bookLender));
            }
            Cookie userCode = Cookie.make("cookieCode", user.getCookieCode());
            userCode.setMaxAge(600);
            userCode.setHttpOnly(true);
            setCookie(exchange, userCode);
            renderTemplate(exchange, "/profile.ftlh", model);
        }
    }

    private void setCookieCodeForUser(User u) {
        u.setCookieCode(CodeGenerator.makeCode(u.getEmail() + u.getLogin()));
    }

    private User checkLogin(String login, String password) {
        for (User u : bookLender.getUsers()) {
            if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
                setCookieCodeForUser(u);
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
        Map<String, Object> model = getModel(exchange);
        String mapKey = model.keySet().iterator().next();
        if (model == null || model.get(mapKey) == null) {
            respond404(exchange);
        } else {
            renderTemplate(exchange, getFileNameHTML(exchange.getRequestURI().getPath()), model);
        }

    }
    private Map<String, Object> getModel(HttpExchange exchange) {
        if (exchange.getRequestURI().getPath().equals("/book")) return getBookInfo(exchange);
        if (exchange.getRequestURI().getPath().equals("/employee")) return getEmployeeInfo(exchange);
        else return getBookList();
    }

    private String getFileNameHTML(String uri) {
        return uri.substring(uri.indexOf("/")) + ".ftlh";
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

    private Map<String, Object> getBookList() {
        Map<String, Object> f = new HashMap<>();
        f.put("books", bookLender.getBooks());
        f.put("bookHistories", bookLender.getHistory());
        f.put("users", bookLender.getUsers());

        return f;
    }

    private Map<String, Object> getEmployeeInfo(HttpExchange exchange) {
        Map<String, Object> employee = new HashMap<>();
        String query = getQueryParams(exchange);
        Map<String, String> params = Utils.parseUrlEncoded(query, "&");
        User user = null;
        for (User u : bookLender.getUsers()) {
            if (u.getId() == Integer.parseInt(params.get("user-id"))) {
                user = u;
                break;
            }
        }
        if (user == null) {
            employee.put("user", null);
            return employee;
        }

        employee.put("user", user);
        employee.put("currentBooks", user.getCurrentBooks(bookLender));
        employee.put("pastBooks", user.getPastBooks(bookLender));
        return employee;
    }

    private Map<String, Object> getBookInfo(HttpExchange exchange) {
        String query = getQueryParams(exchange);
        Map<String, String> params = Utils.parseUrlEncoded(query, "&");
        Book book = null;
        for (Book b : bookLender.getBooks()) {
            if (b.getId() == Integer.parseInt(params.get("book-id"))) {
                book = b;
                break;
            }
        }
        Map<String, Object> f = new HashMap<>();
        f.put("book", book);

        return f;
    }

    private int getUniqueId() {
        if (bookLender.getUsers() == null || bookLender.getUsers().isEmpty()) {
            return 1;
        }
        return bookLender.getUsers().getLast().getId() + 1;
    }

    private String getRandomName() {
        Random rnd = new Random();
        List<String> names = new ArrayList<>();
        names.add("Nikita");
        names.add("Alikhan");
        names.add("Sergey");
        names.add("Some name");
        names.add("Some name 2");

        return names.get(rnd.nextInt(names.size()));
    }


}
