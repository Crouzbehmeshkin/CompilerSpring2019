import java.io.FileWriter;
import java.util.ArrayList;


public class Main {



    public static void main(String[] args) {
        IO io = new IO();
        ArrayList<Token> list = new ArrayList<>();
        LexicalAnalyzer lexer = new LexicalAnalyzer(io);
        Token token = null;

        io.readInput();

        while(true)
        {
            token = lexer.getNextToken();
            if (token == null)
                break;

            lexer.tokens.add(token);
            System.out.println("(" + token.getType() + ", " +  token.getToken() + ")");

            if (token.getType().equals("EOF"))
                break;
        }


//        try{
//
//            FileWriter tokenWriter = new FileWriter("tokens.txt");
//            lexer.printTokens(tokenWriter);
//
//            FileWriter errorWriter = new FileWriter("errors.txt");
//            lexer.printErrors(errorWriter , lexer.errors);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }


    }
}
