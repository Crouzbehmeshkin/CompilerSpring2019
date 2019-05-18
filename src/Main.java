import java.util.ArrayList;


public class Main {



    public static void main(String[] args) {
        IO io = new IO();
        ArrayList<Token> list = new ArrayList<>();
        LexicalAnalyzer lexer = new LexicalAnalyzer(io);
        Parser parser = new Parser(io, lexer);
        Token token = null;
        io.readInput();

        System.out.println(io.input.size());

        parser.parseCode();

        //io.printTokens(tokens);
        io.printErrors(ErrorManager.errors);

    }
}
