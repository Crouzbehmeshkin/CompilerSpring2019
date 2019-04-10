public class Error {
    private int line_number;
    private String string;
    private String message;

    public Error(int line_number, String string, String message) {
        this.line_number = line_number;
        this.string = string;
        this.message = message;
    }

    public int getLine_number() {
        return line_number;
    }

    public String getString() {
        return string;
    }

    public String getMessage() {
        return message;
    }
}
