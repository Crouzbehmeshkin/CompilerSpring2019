import java.util.ArrayList;

public class SymbolTableEntry {
    private String type;
    private int line, address, pointer, dimension;
    private ArrayList<String> params;

    public SymbolTableEntry(int address, String type, int dimension, int line, int pointer, ArrayList<String> params) {
        this.address = address;
        this.type = type;
        this.dimension = dimension;
        this.line = line;
        this.pointer = pointer;
        this.params = params;
    }

    public int getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public int getDimension() {
        return dimension;
    }

    public int getLine() {
        return line;
    }

    public int getPointer()
    {
        return pointer;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public void setAddress(int address)
    {
        this.address = address;
    }
}
