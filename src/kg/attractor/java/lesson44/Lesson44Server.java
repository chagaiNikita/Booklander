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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            // путь к каталогу в котором у нас хранятся шаблоны
            // это может быть совершенно другой путь, чем тот, откуда сервер берёт файлы
            // которые отправляет пользователю
            cfg.setDirectoryForTemplateLoading(new File("data"));

            // прочие стандартные настройки о них читать тут
            // https://freemarker.apache.org/docs/pgui_quickstart_createconfiguration.html
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
        Object model = getBookList();
        if (exchange.getRequestURI().getPath().equals("/book")) model = getBookInfo();
//        if (exchange.getRequestURI().getPath().equals("/books")) model = getBookList();
        renderTemplate(exchange, getFileNameHTML(exchange.getRequestURI().getPath()), model);
        FileUtil.writeTasksToFile(bookLender);
    }

    private String getFileNameHTML(String uri) {
        return uri.substring(uri.indexOf("/")) + ".html";
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            // Загружаем шаблон из файла по имени.
            // Шаблон должен находится по пути, указанном в конфигурации
            Template temp = freemarker.getTemplate(templateFile);

            // freemarker записывает преобразованный шаблон в объект класса writer
            // а наш сервер отправляет клиенту массивы байт
            // по этому нам надо сделать "мост" между этими двумя системами

            // создаём поток, который сохраняет всё, что в него будет записано в байтовый массив
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // создаём объект, который умеет писать в поток и который подходит для freemarker
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {

                // обрабатываем шаблон заполняя его данными из модели
                // и записываем результат в объект "записи"
                temp.process(dataModel, writer);
                writer.flush();

                // получаем байтовый поток
                var data = stream.toByteArray();

                // отправляем результат клиенту
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private Map<String, List<Book>> getBookList() {
        List<Book> books = getExampleBooks();
        bookLender.setBooks(books);
//        FileUtil.writeTasksToFile(bookLender);
        Map<String, List<Book>> f = new HashMap<>();
        f.put("books", books);

        return f;
    }

    private Map<String, Book> getBookInfo() {
        Map<String, Book> f = new HashMap<>();
        f.put("book", getExampleBooks().getFirst());

        return f;
    }

    private List<Book> getExampleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "Война и мир", "images/0002-min-png.png", "Лев Толстой", "Выдана", "book",
                "Война и мир» — огромная сага, с равной глубиной рассказывающая о событиях различного масштаба: \n" +
                        "от частной жизни нескольких семей и конкретных сражений 1812 года до движения народов и истории вообще. \n" +
                        "Благодаря масштабу замысла, точности психологических наблюдений и жанровой универсальности эпопея Толстого \n" +
                        "остаётся в культурной памяти главным русским романом", LocalDate.of(2020, 12, 11), "Michael"));
        books.add(new Book(2, "Колобок", "images/0002-min-png.png" , "Пушкин", "На месте", "book", "Колобок описание", null, null));
        books.add(new Book(3, "Игра престолов", "images/0002-min-png.png", "Джордж Мартин", "Выдана", "book", "Игра престолов» (англ. A Game of Thrones) — роман в жанре фэнтези", LocalDate.of(2023, 5, 1), "Nikita"));
        bookLender.setBooks(books);

        return books;

    }

    private List<User> getExampleUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "Michael", getExampleBooks(), getExampleBooks()));
        bookLender.setUsers(users);
        return users;
    }
}
