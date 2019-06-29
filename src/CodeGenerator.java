public class CodeGenerator {
    private SymbolTabelManager symbolTabelManager;
    private ThreeAddressCode[] PB = new ThreeAddressCode[300];
    private int codePointer = 0, stackPointer = 2000;

    public CodeGenerator(SymbolTabelManager symbolTabelManager)
    {
        this.symbolTabelManager = symbolTabelManager;
        PB[codePointer] = new ThreeAddressCode("PRINT", String.valueOf(stackPointer), "", "");
    }

    private String declarationType, idName;
    private int dimension, line, var_len;

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
