import java.util.ArrayList;

public class CodeGenerator {
    private SymbolTabelManager symbolTabelManager;
    private ThreeAddressCode[] PB = new ThreeAddressCode[1000];
    private int codePointer = 0, stackPointer = 1000, tempMem = 4000;
    private int outputFunction = 0;
    private ArrayList<String> functionParams, functionParamNames;
    private String param, paramType;
    private int paramDimension, paramCnt;
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
        PB[codePointer] = new ThreeAddressCode("ASSIGN", "#5000", "1000", "");
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
            case "start_function":
                functionParams = new ArrayList<>();
                functionParamNames = new ArrayList<>();
                symbolTabelManager.insert("function", idName, declarationType, -1, 0, line);
                symbolTabelManager.addScope();
                param = "";
                paramType = "";
                paramDimension = 0;
                paramCnt = 0;
                break;
            case "zero_param":
                SymbolTableEntry entry = symbolTabelManager.lookup("function", idName);
                entry.setParams(functionParams);
                break;
            case "add_param_type":
                paramType = peek.getString();
                break;
            case "add_param_name":
                param = peek.getString();
                break;
            case "set_param_dimension":
                paramDimension = 1;
                break;
            case "add_param_exc":
                paramType = "void";
                param = peek.getString();
                break;
            case "add_param":
                String gType;
                if (paramType.equals("void"))
                    gType = "function";
                else
                    gType = "var";
                // setting relative address to the top of the stack later
                symbolTabelManager.insert(gType, param, paramType, 1, paramDimension, line, 0);
                functionParams.add(paramType+paramDimension);
                functionParamNames.add(param);
                param = "";
                paramType = "";
                paramDimension = 0;
                paramCnt++;
                break;
            case "add_function_params":
                entry = symbolTabelManager.lookup("function", idName);
                entry.setParams(functionParams);
                int paramNo = functionParams.size();
                for(int i = 0; i < paramNo; i++)
                {
                    String paramName = functionParamNames.get(i);
                    String paramType = functionParams.get(i).substring(0, functionParams.get(i).length() - 1);
                    if (paramType.equals("int"))
                        gType = "var";
                    else
                        gType = "function";
                    entry = symbolTabelManager.lookup(gType, paramName);
                    entry.setAddress(paramNo - entry.getAddress());
                }
                functionParams = new ArrayList<>();
                paramCnt = 0;
                break;

        }
    }
}
