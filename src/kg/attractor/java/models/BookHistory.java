package kg.attractor.java.models;

import java.time.LocalDate;

public class BookHistory {
    private int bookId;
    private int userId;
    private LocalDate issueDate;
    private LocalDate returnDate;

    public BookHistory(int bookId, int userId, LocalDate issueDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.returnDate = null;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public int getBookId() {
        return bookId;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }
}
