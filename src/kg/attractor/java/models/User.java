package kg.attractor.java.models;

import java.util.List;

public class User {
    private int id;
    private String fullName;
    private List<Book> currentBooks;
    private List<Book> pastBooks;
    private String link;

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

    public List<Book> getPastBooks() {
        return pastBooks;
    }

    public User(int id, String fullName, List<Book> currentBooks, List<Book> pastBooks) {
        this.id = id;
        this.fullName = fullName;
        this.currentBooks = currentBooks;
        this.pastBooks = pastBooks;
    }
}
