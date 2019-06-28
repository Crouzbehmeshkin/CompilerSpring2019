public class SymbolTableEntry {
    private String type, dimension;
    private int line, address, pointer;

    public SymbolTableEntry(int address, String type, String dimension, int line, int pointer) {
        this.address = address;
        this.type = type;
        this.dimension = dimension;
        this.line = line;
        this.pointer = pointer;
    }

    public int getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public String getDimension() {
        return dimension;
    }

    public int getLine() {
        return line;
    }

    public int getPointer()
    {
        return pointer;
    }
}
