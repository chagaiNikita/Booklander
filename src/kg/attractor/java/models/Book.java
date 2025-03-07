package kg.attractor.java.models;

import java.time.LocalDate;
import java.util.Objects;

public class Book {
    private int id;
    private String name;
    private String author;
    private String photo;
    private String status;
    private String description;
    private String link;

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getPhoto() {
        return photo;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(name, book.name) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author);
    }

    public String getLink() {
        return link;
    }

    public Book(int id, String name, String photo, String author, String status, String link, String description) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.link = link;
        this.status = status;
        this.photo = photo;
        this.description = description;

    }

}
