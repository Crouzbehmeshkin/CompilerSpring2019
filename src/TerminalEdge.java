public class TerminalEdge extends Edge {
    private Token matchingToken;
    public TerminalEdge(int resultingNode, Token matchingToken)
    {
        super(resultingNode);
        this.matchingToken = matchingToken;
    }

    public Token getToken() {
        return matchingToken;
    }
}
