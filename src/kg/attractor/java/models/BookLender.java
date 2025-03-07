package kg.attractor.java.models;

import java.util.ArrayList;
import java.util.List;

public class BookLender {
    private List<User> users = new ArrayList<>();
    private List<Book> books = new ArrayList<>();
    private List<BookHistory> history = new ArrayList<>();
    private final int bookLimitOnEmployee = 2;

    public void addHistory(BookHistory history) {
        this.history.add(history);
    }

    public List<BookHistory> getHistory() {
        return history;
    }


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

    public int getBookLimitOnEmployee() {
        return bookLimitOnEmployee;
    }


}
