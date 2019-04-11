import java.util.ArrayList;

public class LexicalAnalyzer {
    private IO io;
    private int index;
    private int line_number;
    private ArrayList<Error> errors ;
    private int saved_line_number;

    public LexicalAnalyzer(IO io)
    {
        this.io = io;
        this.index = 0;
        line_number = 1;
        errors = new ArrayList<>();
        saved_line_number = 1;
    }

    public ArrayList<Error> getErrors()
    {
        return errors;
    }

    public Token getNextToken(){
        Token ret = null;
        StringBuilder string = new StringBuilder();
        boolean found = false;
        while (!found)
        {
            char state = 'A';

            while (state != '_') {
                char peek = io.input.get(index);
                switch (state) {
                    case 'A':
                        saved_line_number = line_number;
                        string = new StringBuilder();
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
                            return new Token("EOF", "End Of File", saved_line_number);
                        } else {
                            state = 'A';
                            if (isValid(peek))
                                makeError(string.append(peek).toString(), "Empty Table");
                            else
                                makeError(string.append(peek).toString(), "Invalid Input");
                        }
                        break;
                    case 'B':
                        if (isDigit(peek))
                            state = 'C';
                        else {
                            if (isValid(peek)) {
                                ret = new Token("NUM", string.toString(), saved_line_number);
                                state = '_';
                            } else {
                                makeError(string.append(peek).toString(), "Invalid Input");
                                state = 'A';
                            }
                        }
                        break;
                    case 'C':
                        if (isDigit(peek))
                            state = 'C';
                        else {
                            if (isValid(peek)) {
                                ret = new Token("NUM", string.toString(), saved_line_number);
                                state = '_';
                            } else {
                                makeError(string.append(peek).toString(), "invalid input");
                                state = 'A';
                            }
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
                                    ret = new Token("KEYWORD", string.toString(), saved_line_number);
                                else
                                    ret = new Token("ID", string.toString(), saved_line_number);
                                state = '_';
                            } else {
                                makeError(string.append(peek).toString(), "Invalid Input");
                                state = 'A';
                            }
                        }
                        break;
                    case 'G':
                        if (peek == '=')
                            state = 'H';
                        else {
                            if (isValid(peek)) {
                                ret = new Token("SYMBOL", string.toString(), saved_line_number);
                                state = '_';
                            } else {
                                makeError(string.append(peek).toString(), "Invalid Input");
                                state = 'A';
                            }
                        }
                        break;
                    case 'H':
                        if (isValid(peek)) {
                            ret = new Token("SYMBOL", string.toString(), saved_line_number);
                            state = '_';
                        } else {
                            makeError(string.append(peek).toString(), "Invalid Input");
                            state = 'A';
                        }
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
                            return new Token("_UC", "Unterminated Comment", saved_line_number);
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
                            return new Token("_UC", "Unterminated Comment", saved_line_number);
                        }
                        break;
                    case 'M':
                        if (peek == '*')
                            state = 'L';
                        else if (peek != '\0')
                            state = 'M';
                        else {
                            makeError(string.toString(), "Unterminated Comment");
                            return new Token("_UC", "Unterminated Comment", saved_line_number);
                        }
                        break;
                    case 'N':
                        if (isValid(peek)) {
                            ret = new Token("COMMENT", string.toString(), saved_line_number);
                            state = '_';
                        } else {
                            makeError(string.append(peek).toString(), "Invalid Input");
                            state = 'A';
                        }
                        break;
                    case 'O':
                        if (isWhitespace(peek))
                            state = 'O';
                        else {
                            ret = new Token("WHITESPACE", string.toString(), saved_line_number);
                            state = '_';
                        }
                        break;
                    case 'R':
                        if (peek == '\n')
                            state = 'S';
                        else if (peek != '\0')
                            state = 'R';
                        else {
                            makeError(string.toString(), "Unterminated Comment");
                            return new Token("_UC", "Unterminated Comment", saved_line_number);
                        }
                        break;
                    case 'S':
                        if (isValid(peek))
                        {
                            state = '_';
                            ret = new Token("COMMENT", string.toString(), saved_line_number);
                        }
                        else
                        {
                            state = 'A';
                            makeError(string.toString(), "Invalid Input");
                        }
                        break;
                }
                if (state != '_' )
                {
                    if (peek == '\n')
                        line_number++;
                    string.append(peek);
                    index++;
                }
            }
            if (!ret.getType().equals("WHITESPACE") && !ret.getType().equals("COMMENT"))
                found = true;
        }
        return ret;
    }


    private boolean isWhitespace (char c){
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

    private void makeError(String token, String msg){
        Error error = new Error(saved_line_number , token , msg);
        errors.add(error);
    }

    private boolean isValid(char c)
    {
        return isLetter(c) || isSymbol(c) || isWhitespace(c) || isDigit(c) || (c == '\0') || (c == '*') || (c == '/');
    }
}
