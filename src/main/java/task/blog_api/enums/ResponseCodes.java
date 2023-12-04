package task.blog_api.enums;

public enum ResponseCodes {
    SUCCESSFUL_OPERATION("00"),
    BAD_OPERATION("500"),
    NOT_FOUND("404"),
    BAD_REQUEST("400");

    private final String value;

    ResponseCodes(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
