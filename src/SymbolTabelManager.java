import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTabelManager {
    private static ArrayList<HashMap<SymbolTableKey, SymbolTableEntry>> symbolTables;
    private static int address = 300;

    public SymbolTabelManager()
    {
        HashMap<SymbolTableKey, SymbolTableEntry> globalSymbolTable = new HashMap<>();
        symbolTables.add(globalSymbolTable);
    }

    public void addScope()
    {
        HashMap<SymbolTableKey, SymbolTableEntry> nSymbolTable = new HashMap<>();
        symbolTables.add(nSymbolTable);
    }

    public void removeScope()
    {
        symbolTables.remove(symbolTables.size() - 1);
    }

    public void insert(String g_type, String var_name, String type, int len, int dimension, int line) {
        HashMap<SymbolTableKey, SymbolTableEntry> top = symbolTables.get(symbolTables.size() - 1);
        SymbolTableKey key = new SymbolTableKey(g_type, var_name);
        if (top.containsKey(key)) {
            // todo: make error: duplicate declaration
            return;
        }
        SymbolTableEntry entry;
        if (g_type.equals("var"))
        {
            entry = new SymbolTableEntry(address, type, dimension, line, 0);
            address += len;
        }
        else
        {
            entry = new SymbolTableEntry(-1, type, dimension, line, -1);
        }
    }

    public SymbolTableEntry lookup(String g_type, String var_name)
    {
        SymbolTableKey key = new SymbolTableKey(g_type, var_name);
        int index = symbolTables.size() - 1;
        while(index > 0)
        {
            if (symbolTables.get(index).containsKey(key))
                return symbolTables.get(index).get(key);
            index --;
        }
        return null;
    }

}
