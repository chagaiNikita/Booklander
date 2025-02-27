package kg.attractor.java.models;

import java.util.List;

public class BookLender {
    List<User> users;
    List<Book> books;

    public List<Book> getBooks() {
        return books;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
