package kg.attractor.java.enums;

public enum BookStatuses {
    ISSUED("Выдана"),
    ONTHESPOT("На месте");


    private String title;

    BookStatuses(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
