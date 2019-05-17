public class Token {
    private String type = "";
    private String token = "";
    private int line_number = -1;
    public Token(String type , String token, int line_number ){
        this.type = type;
        this.token = token;
        this.line_number = line_number;
    }

    public String getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public int getLine_number()
    {
        return line_number;
    }

    public String getString() { return token; }
}
