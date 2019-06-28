public class TerminalEdge extends Edge {
    private Token matchingToken;
    private String routine;
    public TerminalEdge(int resultingNode, Token matchingToken)
    {
        super(resultingNode);
        this.matchingToken = matchingToken;
        // todo : complete attribute
        this.routine = "";
    }

    public Token getToken() {
        return matchingToken;
    }

    public String getRoutine() {
        return routine;
    }
}
