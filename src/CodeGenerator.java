public class CodeGenerator {
    private SymbolTabelManager symbolTabelManager;
    private ThreeAddressCode[] PB = new ThreeAddressCode[1000];
    private int codePointer = 0, stackPointer = 5000, tempMem = 4000;
    private int outputFunction = 0;
    //1000: stack pointer
    //1001: frame pointer
    //1002: temporary
    //1003: return address

    //0-1000: code segment
    //1000-5000: static data
    //4000-5000: temporaries
    //5000+: stack
    //10000-: heap


    public CodeGenerator(SymbolTabelManager symbolTabelManager)
    {
        this.symbolTabelManager = symbolTabelManager;

        // code for output function
        PB[codePointer] = new ThreeAddressCode("PRINT", "@"+ stackPointer, "", "");
        codePointer++;
        PB[codePointer] = new ThreeAddressCode("JP", "@1003", "", "");
        codePointer++;

        // initializing stack pointer
        PB[codePointer] = new ThreeAddressCode("ASSIGN", "#4000", "1000", "");
        codePointer++;
    }

    private String declarationType, idName;
    private int dimension, line, var_len;

    private int getTemporary()
    {
        return tempMem++;
    }

    public void runSemanticRoutine(String routineName, Token peek, int line_no)
    {
        switch(routineName)
        {
            case "typeSpecifier1":
                declarationType = "int";
                line = line_no;
                break;
            case "typeSpecifier2":
                declarationType = "void";
                line = line_no;
                break;
            case "save_id_name":
                idName = peek.getString();
                break;
            case "save_dimension":
                dimension = 1;
                var_len = 4 * Integer.valueOf(peek.getString());
                break;
            case "simple_var":
                dimension = 0;
                var_len = 4;
                break;
            case "finalize_declaration1":
                symbolTabelManager.insert("var", idName, declarationType, var_len, dimension, line);
                declarationType = "";
                idName = "";
                dimension = line = -1;
                var_len = 0;
                break;

        }
    }
}
