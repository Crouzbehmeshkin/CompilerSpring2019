public class SymbolTableEntry {
    private String type;
    private int line, address, pointer, dimension;

    public SymbolTableEntry(int address, String type, int dimension, int line, int pointer) {
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
}
