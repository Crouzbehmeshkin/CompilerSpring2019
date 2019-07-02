import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTableManager {
    private ArrayList<HashMap<SymbolTableKey, SymbolTableEntry>> symbolTables;
    private ArrayList<ArrayList<Integer> > breaks;
    private ArrayList<Integer> loopStarts;
    private int address;

    public SymbolTableManager()
    {
        symbolTables = new ArrayList<>();
        HashMap<SymbolTableKey, SymbolTableEntry> globalSymbolTable = new HashMap<>();
        symbolTables.add(globalSymbolTable);

        address = 1004;

        SymbolTableKey key = new SymbolTableKey("function", "output");
        ArrayList<String> params = new ArrayList<>();
        params.add("int0");
        SymbolTableEntry entry = new SymbolTableEntry(address, "int", 0, 2, 0, params);
        address++;
        globalSymbolTable.put(key, entry);

        breaks = new ArrayList<>();
        breaks.add(new ArrayList<>());

        loopStarts = new ArrayList<>();
    }

    public void addScope()
    {
        HashMap<SymbolTableKey, SymbolTableEntry> nSymbolTable = new HashMap<>();
        symbolTables.add(nSymbolTable);
        breaks.add(new ArrayList<>());
    }

    public void removeScope()
    {
        symbolTables.remove(symbolTables.size() - 1);
        breaks.remove(breaks.size() - 1);
    }

    public int getScopeNo()
    {
        return symbolTables.size();
    }

    public void removeLoopScope()
    {
        symbolTables.remove(symbolTables.size() - 1);
        breaks.remove(breaks.size() - 1);
        loopStarts.remove(loopStarts.size() - 1);
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
            entry = new SymbolTableEntry(address, type, dimension, line, 0, new ArrayList<>());
            address += len;
        }
        else
        {
            entry = new SymbolTableEntry(-1, type, dimension, line, -1, new ArrayList<>());
        }
        top.put(key, entry);
    }

    public void insert(String g_type, String var_name, String type, int len, int dimension, int line, int address) {
        HashMap<SymbolTableKey, SymbolTableEntry> top = symbolTables.get(symbolTables.size() - 1);
        SymbolTableKey key = new SymbolTableKey(g_type, var_name);
        if (top.containsKey(key))
        {
            // todo: make error
            return;
        }
        SymbolTableEntry entry;
        if (g_type.equals("var"))
        {
            entry = new SymbolTableEntry(address, type, dimension, line, 0, new ArrayList<>());
        }
        else
        {
            entry = new SymbolTableEntry(address, type, dimension, line, -1, new ArrayList<>());
        }
        top.put(key, entry);
    }

    public SymbolTableEntry lookup(String g_type, String var_name)
    {
        SymbolTableKey key = new SymbolTableKey(g_type, var_name);
        int index = symbolTables.size() - 1;
        while(index >= 0)
        {
            if (symbolTables.get(index).containsKey(key))
                return symbolTables.get(index).get(key);
            index --;
        }
        return null;
    }

    public void insert_break(int code_line)
    {
        breaks.get(breaks.size() - 1).add(code_line);
    }

    public ArrayList<Integer> lookup_scope_breaks()
    {
        return breaks.get(breaks.size() - 1);
    }

    public void insert_loop_start(int code_line)
    {
        loopStarts.add(code_line);
    }

    public int get_last_loop_start()
    {
        if (loopStarts.size() > 0)
            return loopStarts.get(loopStarts.size() - 1);
        return -1;
    }

}
