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
import kg.attractor.java.server.ResponseCodes;
import kg.attractor.java.util.FileUtil;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lesson44Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();
    private BookLender bookLender = new BookLender();

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/books", this::freemarkerSampleHandler);
        registerGet("/book", this::freemarkerSampleHandler);
        registerGet("/employee", this::freemarkerSampleHandler);
        registerGet("/register", this::loginGet);
        registerPost("/register", this::loginPost);
    }

    private void loginPost(HttpExchange exchange) {

        String cType = exchange.getRequestHeaders()
                .getOrDefault("Content-Type", List.of())
                .get(0);

        String raw = getBody(exchange);

        Map<String, String> parsed = kg.attractor.java.utils.Utils.parseUrlEncoded(raw, "&");
        String fmt = "<p>Необработанные данные: <b>%s</b></p>" +
                "<p>Content-Type: <b>%s</b></p>" +
                "<p>После обработки: <b>%s</b></p>";
        String data = String.format(fmt, raw, cType, parsed);

        try{
            sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data.getBytes());
        } catch (IOException e){
            e.printStackTrace();
        }
        redirect303(exchange, "/");
    }

    private void loginGet(HttpExchange exchange) {
        Path path = makeFilePath("/auth/login.ftlh");
        sendFile(exchange, path, ContentType.TEXT_HTML);

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
        FileUtil.writeToFile(bookLender);
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
        List<Book> books = getExampleBooks();
        bookLender.setBooks(books);
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
}
