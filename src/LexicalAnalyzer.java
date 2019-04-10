import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class LexicalAnalyzer {
    private IO io;
    private int index;
    private int line_number;
    ArrayList<Error> errors ;
    ArrayList<Token> tokens ;
    public LexicalAnalyzer(IO io)
    {
        this.io = io;
        this.index = 0;
        line_number = 1;
        errors = new ArrayList<>();
        tokens = new ArrayList<>();
    }

    public Token getNextToken(){
        io.readInput();
        Token ret = null;
        StringBuilder string = new StringBuilder();
        boolean found = false;
        while (!found)
        {
//            System.out.println("Start Over");

            char state = 'A';
            while (state != '_') {
                char peek = io.input.get(index);
//                System.out.println(peek + " " + Character.toString(state));
                switch (state) {
                    case 'A':
                        if (isDigit(peek))
                            state = 'B';
                        else if (isLetter(peek))
                            state = 'D';
                        else if (peek == '=')
                            state = 'G';
                        else if (isSymbol(peek))
                            state = 'H';
                        else if (peek == '/')
                            state = 'J';
                        else if (isWhitespace(peek))
                            state = 'O';
                        else if (peek == '\0') {
                            return new Token("EOF", "End Of File", line_number);
                        } else {
                            state = 'A';
                            if (isValid(peek))
                                makeError(string.append(peek).toString(), "Empty Table");
                            else
                                makeError(string.append(peek).toString(), "Invalid Input");
                            string = new StringBuilder();
                        }
                        break;
                    case 'B':
                        if (isDigit(peek))
                            state = 'C';
                        else {
                            if (isValid(peek)) {
                                ret = new Token("NUM", string.toString(), line_number);
                                state = '_';
                            } else {
                                makeError(string.append(peek).toString(), "Invalid Input");
                                state = 'A';
                            }
                            string = new StringBuilder();
                        }
                        break;
                    case 'C':
                        if (isDigit(peek))
                            state = 'C';
                        else {
                            if (isValid(peek)) {
                                ret = new Token("NUM", string.toString(), line_number);
                                state = '_';
                            } else {
                                makeError(string.append(peek).toString(), "invalid input");
                                state = 'A';
                            }
                            string = new StringBuilder();
                        }
                        break;
                    case 'D':
                        if (isDigit(peek))
                            state = 'D';
                        else if (isLetter(peek))
                            state = 'D';
                        else {
                            if (isValid(peek)) {
                                if (isKeyWord(string.toString()))
                                    ret = new Token("KEYWORD", string.toString(), line_number);
                                else
                                    ret = new Token("ID", string.toString(), line_number);
                                state = '_';
                            } else {
                                makeError(string.append(peek).toString(), "Invalid Input");
                                state = 'A';
                            }
                            string = new StringBuilder();
                        }
                        break;
                    case 'G':
                        if (peek == '=')
                            state = 'H';
                        else {
                            if (isValid(peek)) {
                                ret = new Token("SYMBOL", string.toString(), line_number);
                                state = '_';
                            } else {
                                makeError(string.append(peek).toString(), "Invalid Input");
                                state = 'A';
                            }
                            string = new StringBuilder();
                        }
                        break;
                    case 'H':
                        if (isValid(peek)) {
                            ret = new Token("SYMBOL", string.toString(), line_number);
                            state = '_';
                        } else {
                            makeError(string.append(peek).toString(), "Invalid Input");
                            state = 'A';
                        }
                        string = new StringBuilder();
                        break;
                    case 'J':
                        if (peek == '/')
                            state = 'R';
                        else if (peek == '*')
                            state = 'K';
                        else {
                            if (isValid(peek))
                                makeError(string.append(peek).toString(), "Empty Table");
                            else
                                makeError(string.append(peek).toString(), "Invalid Input");
                            string = new StringBuilder();
                            state = 'A';
                        }
                        break;
                    case 'K':
                        if (peek == '*')
                            state = 'L';
                        else if (peek != '\0')
                            state = 'M';
                        else {
                            makeError(string.toString(), "Unterminated Comment");
                            return new Token("_UC", "Unterminated Comment", line_number);
                        }
                        break;
                    case 'L':
                        if (peek == '/')
                            state = 'N';
                        else if (peek == '*')
                            state = 'L';
                        else if (peek != '\0')
                            state = 'M';
                        else {
                            makeError(string.toString(), "Unterminated Comment");
                            return new Token("_UC", "Unterminated Comment", line_number);
                        }
                        break;
                    case 'M':
                        if (peek == '*')
                            state = 'L';
                        else if (peek != '\0')
                            state = 'M';
                        else {
                            makeError(string.toString(), "Unterminated Comment");
                            return new Token("_UC", "Unterminated Comment", line_number);
                        }
                        break;
                    case 'N':
                        if (isValid(peek)) {
                            ret = new Token("COMMENT", string.toString(), line_number);
                            state = '_';
                        } else {
                            makeError(string.append(peek).toString(), "Invalid Input");
                            state = 'A';
                        }
                        string = new StringBuilder();
                        break;
                    case 'O':
                        if (isWhitespace(peek))
                            state = 'O';
                        else {
                            if (isValid(peek)) {
                                ret = new Token("WHITESPACE", string.toString(), line_number);
                                state = '_';
                            } else {
                                makeError(string.append(peek).toString(), "Invalid Input");
                                state = 'A';
                            }
                            string = new StringBuilder();
                        }
                        break;
                    case 'R':
                        if (peek == '\n')
                            state = 'S';
                        else if (peek != '\0')
                            state = 'T';
                        else {
                            makeError(string.toString(), "Unterminated Comment");
                            return new Token("_UC", "Unterminated Comment", line_number);
                        }
                        break;
                    case 'S':
                        if (peek == '\n')
                            state = 'S';
                        else if (peek != '\0')
                            state = 'T';
                        else {
                            makeError(string.toString(), "Unterminated Comment");
                            return new Token("_UC", "Unterminated Comment", line_number);
                        }
                        break;
                    case 'T':
                        if (peek == '\n')
                            state = 'S';
                        else if (peek != '\0')
                            state = 'T';
                        else {
                            makeError(string.toString(), "Unterminated Comment");
                            return new Token("_UC", "Unterminated Comment", line_number);
                        }
                        break;
                }
                if (peek == '\0')
                    return new Token("EOF", "End Of File", line_number);
                if (state != '_')
                {
                    string.append(peek);
                    index++;
                }
//                else
//                {
////                    System.out.println("Token found");
////                    System.out.println(ret.getType());
//                }
            }
            if (!ret.getType().equals("WHITESPACE") && !ret.getType().equals("COMMENT"))
            {
                //System.out.println("BREAK");
                found = true;
            }
            //System.out.println(found);
        }
        return ret;
    }


    private boolean isWhitespace (char c){
        if (c == '\n')
            line_number++;
        return c == '\n' || c == '\t' || c == '\f' || c == ' ' || c == '\r';
    }

    private boolean isKeyWord(String str ){
        if(str.equals("if") || str.equals("else") || str.equals("while") || str.equals("return") || str.equals("void") || str.equals("int") ||
                str.equals("break") || str.equals("continue") || str.equals("switch") || str.equals("default") || str.equals("case"))
            return true;
        else
            return false;
    }

    private boolean isDigit(char c)
    {
        return (c >= '0' && c <='9');
    }

    private boolean isLetter(char c)
    {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c<= 'Z');
    }


    private boolean isSymbol(char c)
    {
        return c == '=' || c == '<' || c == '>' || c == ';' || c == ':' ||
                c == ']' || c == '[' || c == '(' || c == ')' || c == '*' || c == '-' ||
                c == ',' || c == '+' || c == '{' || c == '}';
    }

    public void makeError(String token, String msg){
        Error error = new Error(line_number , token , msg);
        errors.add(error);
    }

    public void printErrors(FileWriter fileWriter , ArrayList<Error> errors){
        PrintWriter printWriter = new PrintWriter(fileWriter);
        try{
            for (Error error : errors) {
                //  printWriter.print( (error.getLine_number() + ".   (" +  error.getString() + "," + error.getMessage() + ")"));

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void printTokens(FileWriter fileWriter)
    {
        PrintWriter printWriter = new PrintWriter(fileWriter);

        try{
        for (Token token : tokens) {
                System.out.println(   (token.getLine_number() + ".   (" +  token.getType()+ "," + token.getToken() + ")"));
                //  printWriter.print( (token.getLine_number() + ".   (" +  token.getType()+ "," + token.getToken() + ")"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isValid(char c)
    {
        return isLetter(c) || isSymbol(c) || isWhitespace(c) || isDigit(c) || (c == '\0') || (c == '*') || (c == '/');
    }
}
