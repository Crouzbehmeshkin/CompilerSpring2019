import java.util.ArrayList;


public class Main {



    public static void main(String[] args) {
        IO io = new IO();
        ArrayList<Token> list = new ArrayList<>();
        LexicalAnalyzer lexer = new LexicalAnalyzer(io);
        Token token = null;
        io.readInput();

        ArrayList<Token> tokens = new ArrayList<>();

        while(true)
        {
            token = lexer.getNextToken();
            if (token == null)
                break;

            tokens.add(token);
            if (token.getType().equals("EOF"))
                break;
        }

        io.printTokens(tokens);
        io.printErrors(lexer.getErrors());

    }
}
