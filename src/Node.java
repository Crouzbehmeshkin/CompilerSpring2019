import java.util.ArrayList;

public class Node {
    private ArrayList<Edge> edges = new ArrayList<>();
    public boolean is_starting;
    public boolean is_returning;

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}
