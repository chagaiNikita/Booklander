package kg.attractor.java.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class User {
    private int id;
    private transient String cookieCode;
    private String fullName;
    private String login;
    private String email;
    private String password;
//    private List<Book> currentBooks = new ArrayList<>();
//    private List<Book> pastBooks = new ArrayList<>();

//    public void removeBookFromCurBooks(Book book) {
//        currentBooks.remove(book);
//        pastBooks.add(book);
//    }

//    public void addBookInCurBooks(Book book) {
//        System.out.println("Добавление книги в юзере");
//        currentBooks.add(book);
//        System.out.println("Добавление книги в юзере2");
//    }

    public String getCookieCode() {
        return cookieCode;
    }

    public void setCookieCode(String cookieCode) {
        this.cookieCode = cookieCode;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public List<Book> getCurrentBooks(BookLender bookLender) {
        int currentUserId = this.id;
        return bookLender.getBooks().stream()
                .filter(book -> bookLender.getHistory().stream()
                        .anyMatch(history -> history.getUserId() == currentUserId
                                && history.getBookId() == book.getId()
                                && history.getReturnDate() == null))
                .collect(Collectors.toList());

    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Book> getPastBooks(BookLender bookLender) {
        int currentUserId = this.id;
        return bookLender.getBooks().stream()
                .filter(book -> bookLender.getHistory().stream()
                        .anyMatch(history -> history.getUserId() == currentUserId
                                && history.getBookId() == book.getId()
                                && history.getReturnDate() != null))
                .collect(Collectors.toList());
    }

    public User(int id, String login, String email, String password) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.password = password;
    }

//    public User(int id, String fullName, List<Book> currentBooks, List<Book> pastBooks) {
//        this.id = id;
//        this.fullName = fullName;
//        this.currentBooks = currentBooks;
//        this.pastBooks = pastBooks;
//    }
}
