package de.axelrindle.simplechatformat;

public class FormatResult {

    private String format;
    private String message;

    public FormatResult(String format, String message) {
        this.format = format;
        this.message = message;
    }

    public String getFormat() {
        return format;
    }

    public String getMessage() {
        return message;
    }
}
