package kg.attractor.java.models;

import java.time.LocalDate;

public class Book {
    private int id;
    private String name;
    private String author;
    private String photo;
    private String status;
    private String description;
    private String link;
    private LocalDate issueDate;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public String getUserName() {
        return userName;
    }

    public LocalDate getIssueDate() {
        return issueDate;
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

//    public Book(int id, String name, String author, String status, String photo) {
//        this.id = id;
//        this.name = name;
//        this.author = author;
//        this.status = status;
//        this.photo = photo;
//    }

    public String getLink() {
        return link;
    }

    public Book(int id, String name, String photo, String author, String status, String link, String description, LocalDate issueDate, String userName) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.link = link;
        this.status = status;
        this.photo = photo;
        this.description = description;
        this.issueDate = issueDate;
        this.userName = userName;
    }

}
