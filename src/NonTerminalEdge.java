public class NonTerminalEdge extends Edge{
    private int returningNode;
    private String string;
    public NonTerminalEdge(int resultingNode, int returningNode, String string)
    {
        super(resultingNode);
        this.returningNode = returningNode;
        this.string = string;
    }

    public int getReturningNode() {
        return returningNode;
    }

    public String getString() {
        return string;
    }
}
