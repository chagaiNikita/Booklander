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

    public User(int id, String login, String email, String password, String fullName) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }


}
