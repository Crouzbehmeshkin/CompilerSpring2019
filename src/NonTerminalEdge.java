public class NonTerminalEdge extends Edge{
    private int returningNode;
    private String string;
    private String routineBefore, routineAfter;
    public NonTerminalEdge(int resultingNode, int returningNode, String string)
    {
        super(resultingNode);
        this.returningNode = returningNode;
        this.string = string;
        // todo: complete attribute
        this.routineBefore = "";
        this.routineAfter = "";
    }

    public int getReturningNode() {
        return returningNode;
    }

    public String getString() {
        return string;
    }

    public String getRoutineBefore() {
        return routineBefore;
    }

    public String getRoutineAfter() {
        return routineAfter;
    }
}
