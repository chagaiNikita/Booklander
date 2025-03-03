package kg.attractor.java.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private transient String cookieCode;
    private String fullName;
    private String login;
    private String email;
    private String password;
    private List<Book> currentBooks = new ArrayList<>();
    private List<Book> pastBooks = new ArrayList<>();
    private String link;

    public void addBookInCurBooks(Book book) {
        System.out.println("Добавление книги в юзере");
        currentBooks.add(book);
        System.out.println("Добавление книги в юзере2");
    }

    public String getCookieCode() {
        return cookieCode;
    }

    public void setCookieCode(String cookieCode) {
        this.cookieCode = cookieCode;
    }

    public String getLink() {
        return link;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public List<Book> getCurrentBooks() {
        return currentBooks;
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

    public List<Book> getPastBooks() {
        return pastBooks;
    }

    public User(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    public User(int id, String fullName, List<Book> currentBooks, List<Book> pastBooks) {
        this.id = id;
        this.fullName = fullName;
        this.currentBooks = currentBooks;
        this.pastBooks = pastBooks;
    }
}
