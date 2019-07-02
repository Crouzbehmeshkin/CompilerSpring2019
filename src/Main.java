import java.util.ArrayList;


public class Main {



    public static void main(String[] args) {
        IO io = new IO();
        ArrayList<Token> list = new ArrayList<>();
        LexicalAnalyzer lexer = new LexicalAnalyzer(io);
        SymbolTabelManager symbolTabelManager = new SymbolTabelManager();
        CodeGenerator codeGenerator = new CodeGenerator(symbolTabelManager);
        Parser parser = new Parser(io, lexer, codeGenerator);
        Token token = null;
        io.readInput();

        System.out.println(io.input.size());

        parser.parseCode();

        io.openThreeAddressCodeFile();
        io.printThreeAddressCode(codeGenerator.getPB());
        io.closeParseTreeFile();

        //io.printTokens(tokens);
        io.printErrors(ErrorManager.errors);

    }
}
