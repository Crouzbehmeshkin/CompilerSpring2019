import java.util.ArrayList;

public class CodeGenerator {
    private SymbolTableManager symbolTableManager;
    private ThreeAddressCode[] PB = new ThreeAddressCode[1000];
    private int codePointer = 0, stackPointer = 1000, rA = 1003, tempAddress = 4000;
    private int outputFunction = 0;

    private ArrayList<String> idName;

    private SymbolTableEntry entry;
    private ArrayList<String> functionParams, functionParamNames;
    private String param, paramType;
    private int paramDimension, paramCnt;
    private ArrayList<Integer> SS, breaks;
    private String signedFactorName;
    private int idAddress;
    private String addressString, addressString2;
    private int tempMem;
    private int addop, relop;
    private int offset;
    private int switchOrLoops;
    private ArrayList<String> typeStack;
    private String type1, type2;

    public boolean ok;

    private ArrayList<Error> errors;

    //1000: stack pointer
    //1001: frame pointer
    //1002: temporary
    //1003: return address

    //0-1000: code segment
    //1000-5000: static data
    //4000-5000: temporaries
    //5000+: stack
    //10000-: heap


    public CodeGenerator(SymbolTableManager symbolTableManager)
    {
        this.symbolTableManager = symbolTableManager;
        this.ok = true;
        SS = new ArrayList<>();
        signedFactorName = "";
        idAddress = -1;
        idName = new ArrayList<>();
        addop = 0;
        relop = 0;
        offset = 0;
        switchOrLoops = 0;
        typeStack = new ArrayList<>();

        errors = ErrorManager.errors;
        // initializing stack pointer
        PB[codePointer] = new ThreeAddressCode("ASSIGN", "#5000", "1000", "");
        codePointer++;

        // code for output function
        codePointer++;
        PB[codePointer] = new ThreeAddressCode("SUB", String.valueOf(stackPointer), "#1", String.valueOf(stackPointer));
        codePointer++;
        tempMem = getTemporary();
        PB[codePointer] = new ThreeAddressCode("ASSIGN", "@"+stackPointer, String.valueOf(tempMem), "");
        codePointer++;
        PB[codePointer] = new ThreeAddressCode("PRINT", String.valueOf(tempMem), "", "");
        codePointer++;
        PB[codePointer] = new ThreeAddressCode("SUB", String.valueOf(stackPointer), "#1", String.valueOf(stackPointer));
        codePointer++;
        tempMem = getTemporary();
        PB[codePointer] = new ThreeAddressCode("ASSIGN", "@"+stackPointer, String.valueOf(tempMem), "");
        codePointer++;
        PB[codePointer] = new ThreeAddressCode("JP", "@"+tempMem, "", "");
        codePointer++;

    }

    private String declarationType;
    private int dimension, line, var_len;

    private int getTemporary()
    {
        return tempAddress++;
    }

    public void runSemanticRoutine(String routineName, Token peek, int line_no)
    {
        if (!ok)
            return;
        if (!routineName.equals(""))
            System.out.println(routineName);
        switch(routineName)
        {
            case "type_specifier1":
                declarationType = "int";
                break;
            case "type_specifier2":
                declarationType = "void";
                break;
            case "save_id_name":
                idName.add(peek.getString());
                break;
            case "save_dimension":
                dimension = 1;
                var_len = 4 * Integer.valueOf(peek.getString());
                if (declarationType.equals("void"))
                    makeError(line_no, "Illegal type of void.");
                break;
            case "simple_var":
                dimension = 0;
                var_len = 4;
                if (declarationType.equals("void"))
                    makeError(line_no, "Illegal type of void.");
                break;
            case "finalize_declaration1":
                symbolTableManager.insert("var", idName.get(idName.size() - 1), declarationType, var_len, dimension, 0);
                declarationType = "";
                idName.remove(idName.size() - 1);
                dimension = line = -1;
                var_len = 0;
                break;
            case "start_function":
                functionParams = new ArrayList<>();
                functionParamNames = new ArrayList<>();
                symbolTableManager.insert("function", idName.get(idName.size() - 1), declarationType, -1, 0, codePointer);
                if (idName.get(idName.size() - 1).equals("main") && symbolTableManager.getScopeNo() == 1)
                {
                    PB[1] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                    codePointer--;
                }
                symbolTableManager.addScope();
                param = "";
                paramType = "";
                paramDimension = 0;
                paramCnt = 0;
                SS.add(codePointer);
                codePointer++;
                break;
            case "zero_param":
                entry = symbolTableManager.lookup("function", idName.get(idName.size() - 1));
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
                symbolTableManager.insert(gType, param, paramType, 1, paramDimension, line, 0);
                functionParams.add(paramType+paramDimension);
                functionParamNames.add(param);
                param = "";
                paramType = "";
                paramDimension = 0;
                paramCnt++;
                break;
            case "add_function_params":
                entry = symbolTableManager.lookup("function", idName.get(idName.size() - 1));
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
                    entry = symbolTableManager.lookup(gType, paramName);
                    entry.setAddress( -paramNo + i);
                }
                functionParams = new ArrayList<>();
                paramCnt = 0;
                break;
            case "start_compound_statement":
                symbolTableManager.addScope();
                break;
            case "end_compound_statement":
                breaks = symbolTableManager.lookup_scope_breaks();
                for (int i = 0; i < breaks.size(); i++)
                    PB[breaks.get(i)] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                symbolTableManager.removeScope();
                break;
            case "fix_continue":
                int tmp_line = symbolTableManager.get_last_loop_start();
                if (tmp_line == -1)
                    makeError(line_no, "No \'while\' found for \'continue\'");
                PB[codePointer] = new ThreeAddressCode("JP",  "#"+tmp_line, "", "");
                codePointer++;
                break;
            case "add_break":
                if (switchOrLoops == 0)
                    makeError(line_no, "No \'while\' or \'switch\' found for \'break\'");
                symbolTableManager.insert_break(codePointer);
                codePointer++;
                break;
            case "pop_result":
                SS.remove(SS.size() - 1);
                typeStack.remove(typeStack.size() - 1);
                break;
            case "label_if1":
                SS.add(codePointer);
                codePointer++;
                symbolTableManager.addScope();
                break;
            case "label_if2":
                SS.add(codePointer);
                codePointer++;
                symbolTableManager.removeScope();
                break;
            case "fill_if1":
                int label = SS.get(SS.size() - 2);
                int expressionResult = SS.get(SS.size() - 3);
                PB[label] = new ThreeAddressCode("JPF", String.valueOf(expressionResult),String.valueOf(codePointer), "");
                symbolTableManager.addScope();
                break;
            case "fill_if2":
                label = SS.get(SS.size()-1);
                PB[label] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                symbolTableManager.removeScope();
                for (int i = 0; i < 3; i++)
                    SS.remove(SS.size() - 1);
                break;
            case "label_while":
                SS.add(codePointer);
                symbolTableManager.insert_loop_start(codePointer);
                switchOrLoops++;
                break;
            case "save_while":
                SS.add(codePointer);
                codePointer++;
                symbolTableManager.addScope();
                break;
            case "fill_while":
                label = SS.get(SS.size() - 1);
                expressionResult = SS.get(SS.size() - 2);
                PB[label] = new ThreeAddressCode("JPF", String.valueOf(expressionResult), String.valueOf(codePointer+1), "");
                label = SS.get(SS.size() - 3);
                PB[codePointer] = new ThreeAddressCode("JP", String.valueOf(label), "", "");
                codePointer++;
                // pop the stack
                for (int i = 0 ; i < 3; i++)
                    SS.remove(SS.size() - 1);

                //filling breaks
                breaks = symbolTableManager.lookup_scope_breaks();
                for (int i = 0 ; i < breaks.size(); i++)
                    PB[breaks.get(i)] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");

                symbolTableManager.removeLoopScope();
                switchOrLoops--;
                break;
            case "return1":
                entry = symbolTableManager.lookup("function", idName.get(idName.size() - 1));
                offset = entry.getParams().size() + 1;
                PB[codePointer] = new ThreeAddressCode("SUB", String.valueOf(stackPointer), "#"+offset, String.valueOf(stackPointer));
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("ASSIGN", String.valueOf(stackPointer), String.valueOf(rA),"");
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("JP", String.valueOf(rA), "", "");
                codePointer++;
                break;
            case "return2":
                entry = symbolTableManager.lookup("function", idName.get(idName.size() - 1));
                offset = entry.getParams().size() + 1;
                PB[codePointer] = new ThreeAddressCode("SUB", String.valueOf(stackPointer), "#"+offset, String.valueOf(stackPointer));
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("ASSIGN", String.valueOf(stackPointer), String.valueOf(rA),"");
                codePointer++;
                addressString = getAddressString(SS.get(SS.size() - 1));
                PB[codePointer] = new ThreeAddressCode("ASSIGN", addressString, "@"+stackPointer, "");
                PB[codePointer] = new ThreeAddressCode("JP", String.valueOf(rA), "", "");
                codePointer++;
                SS.remove(SS.size()-1);
                typeStack.remove(typeStack.size() - 1);
                break;
            case "return3":
                if (idName.get(idName.size() - 1).equals("main"))
                {
                    SS.remove(SS.size() - 1);
                    idName.remove(idName.size() - 1);
                    breaks = symbolTableManager.lookup_scope_breaks();
                    for (int i = 0; i < breaks.size(); i++)
                        PB[breaks.get(i)] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                    symbolTableManager.removeScope();
                    break;
                }
                entry = symbolTableManager.lookup("function", idName.get(idName.size() - 1));
                offset = entry.getParams().size() + 1;
                PB[codePointer] = new ThreeAddressCode("SUB", String.valueOf(stackPointer), "#"+offset, String.valueOf(stackPointer));
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("ASSIGN", "@"+stackPointer, String.valueOf(rA),"");
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("JP", "@"+rA, "", "");
                codePointer++;

                idName.remove(idName.size() - 1);
                PB[SS.get(SS.size() - 1)] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                SS.remove(SS.size() - 1);

                breaks = symbolTableManager.lookup_scope_breaks();
                for (int i = 0; i < breaks.size(); i++)
                    PB[breaks.get(i)] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                symbolTableManager.removeScope();
                break;
            case "add_switch_scope":
                symbolTableManager.addScope();
                switchOrLoops++;
                break;
            case "remove_switch_scope":
                breaks = symbolTableManager.lookup_scope_breaks();
                for (int i = 0 ; i < breaks.size(); i++)
                    PB[breaks.get(i)] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                SS.remove(SS.size() - 1);
                symbolTableManager.removeScope();
                switchOrLoops--;
                break;
            case "save_case":
                SS.add(codePointer);
                SS.add(Integer.valueOf(peek.getString()));
                codePointer+=2;
                break;
            case "fill_case":
                label = SS.get(SS.size() - 2);
                int num = SS.get(SS.size() - 1);
                expressionResult = SS.get(SS.size() - 3);
                tempMem = getTemporary();
                PB[label] = new ThreeAddressCode("EQ", String.valueOf(expressionResult), "#" + num, String.valueOf(tempMem) );
                PB[label + 1] = new ThreeAddressCode("JPF", String.valueOf(tempMem), String.valueOf(codePointer), "");
                for (int i = 0 ; i < 2; i++)
                    SS.remove(SS.size() - 1);
                break;
            case "save_signed_factor_name":
                signedFactorName = peek.getString();
                break;
            case "pid":
                idName.add(peek.getString());
                break;
            case "pid2":
                entry = symbolTableManager.lookup("var", idName.get(idName.size() - 1));
                if (entry == null)
                {
                    makeError(line_no, idName.get(idName.size() - 1) + " is not defined");
                    return;
                }
                idName.remove(idName.size() - 1);
                SS.add(entry.getAddress());
                typeStack.add(entry.getType() + entry.getDimension());
                break;
            case "find_array_address":
                if (typeStack.get(typeStack.size() - 1).equals("void0") || typeStack.get(typeStack.size() - 1).equals("void1"))
                {
                    makeError(line_no, "array index is void");
                    return;
                }

                tempMem = getTemporary();
                addressString = getAddressString(SS.get(SS.size() - 1));
                PB[codePointer] = new ThreeAddressCode("MULT", addressString, "#4", String.valueOf(tempMem));
                codePointer++;
                SS.remove(SS.size() - 1);
                SS.add(tempMem);

                tempMem = getTemporary();
                addressString = getAddressString(SS.get(SS.size() - 2));
                addressString2 = getAddressString(SS.get(SS.size() - 1));
                PB[codePointer] = new ThreeAddressCode("ADD", addressString, addressString2, String.valueOf(tempMem));
                codePointer++;

                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.add(-tempMem);
                typeStack.remove(typeStack.size() - 1);
                type1 = typeStack.get(typeStack.size() - 1);
                type1 = type1.substring(0, type1.length()-1) + "0";
                typeStack.remove(typeStack.size() - 1);
                typeStack.add(type1);
                break;
            case "allocate_num":
                tempMem = getTemporary();
                PB[codePointer] = new ThreeAddressCode("ASSIGN", "#"+peek.getString(), String.valueOf(tempMem), "");
                codePointer++;
                SS.add(tempMem);
                typeStack.add("int0");
                break;
            case "minus_factor":
                tempMem = getTemporary();
                addressString = getAddressString(SS.get(SS.size() - 1));
                PB[codePointer] = new ThreeAddressCode("SUB", "#0", addressString, String.valueOf(tempMem));
                codePointer++;
                SS.remove(SS.size() - 1);
                SS.add(tempMem);
                break;
            case "mult_termB":
                type1 = typeStack.get(typeStack.size() - 2);
                type2 = typeStack.get(typeStack.size() - 1);
                if (!type1.equals(type2) || type1.equals("void") || type1.equals("int1"))
                {
                    makeError(line_no, "Type mismatch in operands");
                    return;
                }
                tempMem = getTemporary();
                addressString = getAddressString(SS.get(SS.size() - 2));
                addressString2 = getAddressString(SS.get(SS.size() - 1));
                PB[codePointer] = new ThreeAddressCode("MULT", addressString, addressString2, String.valueOf(tempMem));
                codePointer++;
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.add(tempMem);
                typeStack.remove(typeStack.size() - 1);
                break;
            case "addop1":
                addop = 1;
                break;
            case "addop2":
                addop = 2;
                break;
            case "add_exp":
                type1 = typeStack.get(typeStack.size() - 2);
                type2 = typeStack.get(typeStack.size() - 1);
                if (!type1.equals(type2) || type1.equals("void") || type1.equals("int1"))
                {
                    makeError(line_no, "Type mismatch in operands");
                    return;
                }
                addressString = getAddressString(SS.get(SS.size() - 2));
                addressString2 = getAddressString(SS.get(SS.size() - 1));
                tempMem = getTemporary();
                if (addop == 1)
                    PB[codePointer] = new ThreeAddressCode("ADD", addressString, addressString2, String.valueOf(tempMem));
                else
                    PB[codePointer] = new ThreeAddressCode("SUB", addressString, addressString2, String.valueOf(tempMem));
                codePointer++;
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.add(tempMem);
                addop = 0;
                typeStack.remove(typeStack.size() - 1);
                break;
            case "relop1":
                relop = 1;
                break;
            case "relop2":
                relop = 2;
                break;
            case "compare_exp":
                type1 = typeStack.get(typeStack.size() - 2);
                type2 = typeStack.get(typeStack.size() - 1);
                if (!type1.equals(type2) || type1.equals("void") || type1.equals("int1"))
                {
                    makeError(line_no, "Type mismatch in operands");
                    return;
                }
                addressString = getAddressString(SS.get(SS.size() - 2));
                addressString2 = getAddressString(SS.get(SS.size() - 1));
                tempMem = getTemporary();
                if (relop == 1)
                    PB[codePointer] = new ThreeAddressCode("LT", addressString, addressString2, String.valueOf(tempMem));
                else
                    PB[codePointer] = new ThreeAddressCode("EQ", addressString, addressString2, String.valueOf(tempMem));
                codePointer++;
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.add(tempMem);
                relop = 0;
                typeStack.remove(typeStack.size() - 1);
                break;
            case "assignment":
                type1 = typeStack.get(typeStack.size() - 2);
                type2 = typeStack.get(typeStack.size() - 1);
                if (!type1.equals(type2) || type1.equals("void"))
                {
                    makeError(line_no, "Type mismatch in operands");
                    return;
                }
                addressString = getAddressString(SS.get(SS.size() - 2));
                addressString2 = getAddressString(SS.get(SS.size() - 1));
                PB[codePointer] = new ThreeAddressCode("ASSIGN", addressString2, addressString, "");
                codePointer++;
                offset = SS.get(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.add(offset);
                typeStack.remove(typeStack.size() - 1);
                break;
            case "pre_call":
                entry = symbolTableManager.lookup("function", idName.get(idName.size() - 1));
                if (entry == null)
                    makeError(line_no, idName.get(idName.size() - 1) + " is not defined");
                typeStack.add(entry.getType()+"0");
                SS.add(0);
                SS.add(entry.getLine());
                SS.add(entry.getParams().size());
                PB[codePointer] = new ThreeAddressCode("ADD", String.valueOf(stackPointer), "#1", String.valueOf(stackPointer));
                codePointer++;
                break;
            case "add_to_stack":
                entry = symbolTableManager.lookup("function", idName.get(idName.size() - 1));
                type1 = entry.getParams().get(SS.get(SS.size()-4));
                type2 = typeStack.get(typeStack.size() - 1);
                if (!type1.equals(type2))
                {
                    makeError(line_no, "Mismatch in argument types");
                    return;
                }
                typeStack.remove(typeStack.size() - 1);
                addressString = getAddressString(SS.get(SS.size() - 1));
                PB[codePointer] = new ThreeAddressCode("ASSIGN",addressString, "@"+stackPointer, "" );
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("ADD", String.valueOf(stackPointer), "#1", String.valueOf(stackPointer));
                codePointer++;
                SS.remove(SS.size() - 1);
                SS.set(SS.size() - 3, SS.get(SS.size() - 3) + 1);
                break;
            case "call":
                if (SS.get(SS.size()- 3) != SS.get(SS.size() - 1))
                {
                    makeError(line_no, "Mismatch in number of arguments of " + idName.get(idName.size() - 1));
                    return;
                }
                idName.remove(idName.size() - 1);

                offset = SS.get(SS.size() - 1) + 1;
                tempMem = getTemporary();
                PB[codePointer] = new ThreeAddressCode("SUB", String.valueOf(stackPointer), "#"+offset, String.valueOf(tempMem));
                codePointer++;
                //reusing offset as a tmp variable
                offset = codePointer + 3;
                PB[codePointer] = new ThreeAddressCode("ASSIGN", "#"+offset, "@"+tempMem, "");
                codePointer++;
                addressString = "#" + SS.get(SS.size() - 2);
                tempMem = getTemporary();
                PB[codePointer] = new ThreeAddressCode("ASSIGN", addressString, String.valueOf(tempMem), "");
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("JP", "@"+tempMem,"","");
                codePointer++;
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                tempMem = getTemporary();
                PB[codePointer] = new ThreeAddressCode("ASSIGN", "@"+stackPointer, String.valueOf(tempMem), "");
                codePointer++;
                SS.add(tempMem);
                break;
            default:

                break;
        }
    }

    private String getAddressString(int address)
    {
        String ret = String.valueOf(address);
        if (address <= -4000)
            return "@" + ret.substring(1);
        if (address < 0)
        {
            address = -address;
            tempMem = getTemporary();
            PB[codePointer] = new ThreeAddressCode("SUB", "#"+address, String.valueOf(stackPointer), String.valueOf(tempMem));
            codePointer++;
            return "@"+tempMem;
        }
        return ret;
    }

    private void makeError(int line, String message)
    {
        Error error = new Error(line, "SEMANTIC ERROR!", message);
        ErrorManager.errors.add(error);
        ok = false;
    }

    public ThreeAddressCode[] getPB()
    {
        return PB;
    }
}
