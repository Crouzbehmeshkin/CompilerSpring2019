import java.util.ArrayList;
import java.util.Stack;

public class CodeGenerator {
    private SymbolTabelManager symbolTabelManager;
    private ThreeAddressCode[] PB = new ThreeAddressCode[1000];
    private int codePointer = 0, stackPointer = 1000, rA = 1003, tempMem = 4000;
    private int outputFunction = 0;

    private ArrayList<String> idName;

    private SymbolTableEntry entry;
    private ArrayList<String> functionParams, functionParamNames;
    private String param, paramType;
    private int paramDimension, paramCnt;
    private ArrayList<Integer> SS;
    private String signedFactorName;
    private int idAddress;
    private String addressString, addressString2;
    private int tmp_num;
    private int addop, relop;
    private int offset;

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
        SS = new ArrayList<>();
        signedFactorName = "";
        idAddress = -1;
        idName = new ArrayList<>();
        addop = 0;
        relop = 0;
        offset = 0;

        // code for output function
        PB[codePointer] = new ThreeAddressCode("PRINT", "@"+ stackPointer, "", "");
        codePointer++;
        PB[codePointer] = new ThreeAddressCode("JP", "@1003", "", "");
        codePointer++;

        // initializing stack pointer
        PB[codePointer] = new ThreeAddressCode("ASSIGN", "#5000", "1000", "");
        codePointer++;
    }

    private String declarationType;
    private int dimension, line, var_len;

    private int getTemporary()
    {
        return tempMem++;
    }

    public void runSemanticRoutine(String routineName, Token peek)
    {
        switch(routineName)
        {
            case "typeSpecifier1":
                declarationType = "int";
                break;
            case "typeSpecifier2":
                declarationType = "void";
                break;
            case "save_id_name":
                idName.add(peek.getString());
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
                symbolTabelManager.insert("var", idName.get(idName.size() - 1), declarationType, var_len, dimension, 0);
                declarationType = "";
                idName.remove(idName.size() - 1);
                dimension = line = -1;
                var_len = 0;
                break;
            case "start_function":
                functionParams = new ArrayList<>();
                functionParamNames = new ArrayList<>();
                symbolTabelManager.insert("function", idName.get(idName.size() - 1), declarationType, -1, 0, codePointer);
                symbolTabelManager.addScope();
                param = "";
                paramType = "";
                paramDimension = 0;
                paramCnt = 0;
                SS.add(codePointer);
                codePointer++;
                break;
            case "zero_param":
                entry = symbolTabelManager.lookup("function", idName.get(idName.size() - 1));
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
                entry = symbolTabelManager.lookup("function", idName.get(idName.size() - 1));
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
                    entry.setAddress( -paramNo + i);
                }
                functionParams = new ArrayList<>();
                paramCnt = 0;
                break;
            case "end_compound_statement":
                ArrayList<Integer> breaks = symbolTabelManager.lookup_scope_breaks();
                for (int i = 0; i < breaks.size(); i++)
                {
                    PB[breaks.get(i)] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                }
                symbolTabelManager.removeScope();
                break;
            case "fix_continue":
                int tmp_line = symbolTabelManager.get_last_loop_start();
                PB[codePointer] = new ThreeAddressCode("JP",  String.valueOf(tmp_line), "", "");
                codePointer++;
                break;
            case "add_break":
                symbolTabelManager.insert_break(codePointer);
                codePointer++;
                break;
            case "label_if1":
                SS.add(codePointer);
                codePointer++;
                symbolTabelManager.addScope();
                break;
            case "label_if2":
                SS.add(codePointer);
                codePointer++;
                symbolTabelManager.removeScope();
                break;
            case "fill_if1":
                int label = SS.get(SS.size() - 2);
                int expressionResult = SS.get(SS.size() - 3);
                PB[label] = new ThreeAddressCode("JPF", String.valueOf(expressionResult),String.valueOf(codePointer), "");
                symbolTabelManager.addScope();
                break;
            case "fill_if2":
                label = SS.get(SS.size()-1);
                PB[label] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                symbolTabelManager.removeScope();
                for (int i = 0; i < 3; i++)
                    SS.remove(SS.size() - 1);
                break;
            case "label_while":
                SS.add(codePointer);
                symbolTabelManager.insert_loop_start(codePointer);
                break;
            case "save_while":
                SS.add(codePointer);
                codePointer++;
                symbolTabelManager.addScope();
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
                breaks = symbolTabelManager.lookup_scope_breaks();
                for (int i = 0 ; i < breaks.size(); i++)
                    PB[breaks.get(i)] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");

                symbolTabelManager.removeScope();
                break;
            case "return1":
                entry = symbolTabelManager.lookup("function", idName.get(idName.size() - 1));
                offset = entry.getParams().size() + 1;
                PB[codePointer] = new ThreeAddressCode("SUB", "#"+offset, String.valueOf(stackPointer), String.valueOf(stackPointer));
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("ASSIGN", String.valueOf(stackPointer), String.valueOf(rA),"");
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("JP", String.valueOf(rA), "", "");
                codePointer++;
                break;
            case "return2":
                entry = symbolTabelManager.lookup("function", idName.get(idName.size() - 1));
                offset = entry.getParams().size() + 1;
                PB[codePointer] = new ThreeAddressCode("SUB", "#"+offset, String.valueOf(stackPointer), String.valueOf(stackPointer));
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("ASSIGN", String.valueOf(stackPointer), String.valueOf(rA),"");
                codePointer++;
                PB[codePointer] = new ThreeAddressCode("JP", String.valueOf(rA), "", "");
                codePointer++;

                idName.remove(idName.size() - 1);
                PB[SS.get(SS.size() - 2)] = new ThreeAddressCode("JP", "#"+codePointer, "", "");
                offset = SS.get(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.add(offset);
                break;
            case "add_switch_scope":
                symbolTabelManager.addScope();
                break;
            case "remove_switch_scope":
                breaks = symbolTabelManager.lookup_scope_breaks();
                for (int i = 0 ; i < breaks.size(); i++)
                    PB[breaks.get(i)] = new ThreeAddressCode("JP", String.valueOf(codePointer), "", "");
                symbolTabelManager.removeScope();
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
                int t = getTemporary();
                PB[label] = new ThreeAddressCode("EQ", String.valueOf(expressionResult), "#" + num, String.valueOf(t) );
                PB[label + 1] = new ThreeAddressCode("JPF", String.valueOf(t), String.valueOf(codePointer), "");
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
                entry = symbolTabelManager.lookup("var", idName.get(idName.size() - 1));
                idName.remove(idName.size() - 1);
                SS.add(entry.getAddress());
                break;
            case "find_array_address":
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
                break;
            case "allocate_num":
                tempMem = getTemporary();
                PB[codePointer] = new ThreeAddressCode("ASSIGN", "#"+peek.getString(), String.valueOf(tempMem), "");
                codePointer++;
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
                tempMem = getTemporary();
                addressString = getAddressString(SS.get(SS.size() - 2));
                addressString2 = getAddressString(SS.get(SS.size() - 1));
                PB[codePointer] = new ThreeAddressCode("MULT", addressString, addressString2, String.valueOf(tempMem));
                codePointer++;
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.add(tempMem);
                break;
            case "addop1":
                addop = 1;
                break;
            case "addop2":
                addop = 2;
                break;
            case "add_exp":
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
                break;
            case "relop1":
                relop = 1;
                break;
            case "relop2":
                relop = 2;
                break;
            case "compare_exp":
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
                break;
            case "assignment":
                addressString = getAddressString(SS.get(SS.size() - 2));
                addressString2 = getAddressString(SS.get(SS.size() - 1));
                PB[codePointer] = new ThreeAddressCode("ASSIGN", addressString, addressString2, "");
                codePointer++;
                break;
            case "pre_call":
                entry = symbolTabelManager.lookup("function", idName.get(idName.size() - 1));
                SS.add(0);
                SS.add(entry.getLine());
                SS.add(entry.getParams().size());
                PB[codePointer] = new ThreeAddressCode("ADD", String.valueOf(stackPointer), "#1", String.valueOf(stackPointer));
                codePointer++;
                break;
            case "add_to_stack":
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
                    //semantic error
                    // Mismatch in number of arguments of idName.get(idName.size() - 1)
                }
                idName.remove(idName.size() - 1);

                offset = SS.get(SS.size() - 1) + 1;
                tempMem = getTemporary();
                PB[codePointer] = new ThreeAddressCode("SUB", String.valueOf(stackPointer), "#"+offset, String.valueOf(tempMem));
                codePointer++;
                //reusing offset as a tmp variable
                offset = codePointer + 2;
                PB[codePointer] = new ThreeAddressCode("ASSIGN", "#"+offset, String.valueOf(tempMem), "");
                codePointer++;
                addressString = String.valueOf(SS.get(SS.size() - 2));
                PB[codePointer] = new ThreeAddressCode("JP", addressString,"","");
                codePointer++;
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
                SS.remove(SS.size() - 1);
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
}
