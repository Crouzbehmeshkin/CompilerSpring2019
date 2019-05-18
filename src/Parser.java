import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Node> nodes = new ArrayList<>();
    private IO io;
    private LexicalAnalyzer lexer;
    private ArrayList<Error> errors;

    public Parser(IO io, LexicalAnalyzer lexer) {
        this.io = io;
        this.lexer = lexer;
        errors = ErrorManager.errors;
        init_nodes();
        addToHashMaps();
    }

    private void init_nodes()
    {
        nodes = io.readTransitionGraph();
    }

    HashMap<String , ArrayList> firstSets = new HashMap<>();
    HashMap<String , ArrayList> followSets = new HashMap<>();

    public  void addToFirstSet( HashMap<String , ArrayList> firstSets  ){

        ArrayList<String> first = new ArrayList<>();
       first.add("EOF");first.add("int"); first.add("void");
            firstSets.put("program" ,first );

        ArrayList<String> first1 = new ArrayList<>();
        first1.add("epsilon");  first1.add("int");  first1.add("void");
            firstSets.put("declaration-list" , first1);
            firstSets.put("declaration-listB" , first1);

        ArrayList<String> first2 = new ArrayList<>();
        first2.add("int");  first2.add("void");
            firstSets.put("declaration" , first2);

        ArrayList<String> first3 = new ArrayList<>();
        first3.add("(");first3.add(";");first3.add("[");
            firstSets.put("type" , first3);
            firstSets.put("var-declaration" , first2);

        ArrayList<String> first4 = new ArrayList<>();
        first4.add(";");first4.add("[");
            firstSets.put("var-declarationB" , first4);

            firstSets.put("type-specifier" , first2);
            firstSets.put("params" , first2);

        ArrayList<String> first5 = new ArrayList<>();
        first5.add("ID");first5.add("epsilon");
             firstSets.put("paramsB" , first5);
             firstSets.put("param-list" , first2);

        ArrayList<String> first6 = new ArrayList<>();
        first6.add(",");first6.add("epsilon");
        firstSets.put("param-listB" , first6);
        firstSets.put("param" , first2);

        ArrayList<String> first7 = new ArrayList<>();
        first7.add("[");   first7.add("epsilon");
        firstSets.put("paramB" , first7);

        ArrayList<String> first8 = new ArrayList<>();
        first8.add("{");
            firstSets.put("compound-stmt" , first8);

        ArrayList<String> first9 = new ArrayList<>();
        first9.add("{");first9.add("continue");first9.add("break");first9.add(";");first9.add("if"); first9.add("epsilon");
        first9.add("while");first9.add("return");first9.add("switch");first9.add("ID"); first9.add("+");first9.add("-");first9.add("(");first9.add("NUM");
            firstSets.put("statement-list" , first9);
            firstSets.put("statement-listB" , first9);

       ArrayList<String> first10 = new ArrayList<>();
        first10.add("{");first10.add("continue");first10.add("break");first10.add(";");first10.add("if");
        first10.add("while");first10.add("return");first10.add("switch");first10.add("ID"); first10.add("+");first10.add("-");first10.add("(");first10.add("NUM");
            firstSets.put("statement" , first10);

        ArrayList<String> first11 = new ArrayList<>();
        first11.add("continue");first11.add("break"); first11.add(";");first11.add("ID");first11.add("+");first11.add("-");first11.add("(");first11.add("NUM");
             firstSets.put("expression-stmt" , first11);

        ArrayList<String> first12 = new ArrayList<>();
        first12.add("if");
            firstSets.put("selection-stmt" , first12);

        ArrayList<String> first13 = new ArrayList<>();
            first13.add("while");
            firstSets.put("iteration-stmt", first13);

        ArrayList<String> first14 = new ArrayList<>();
        first14.add("return");
        firstSets.put("return-stmt" , first14);

        ArrayList<String> first15 = new ArrayList<>();
        first15.add(";") ; first15.add("ID"); first15.add("+"); first15.add("-"); first15.add("(");first15.add("NUM");
            firstSets.put("return-stmtB" , first15);

        ArrayList<String> first16 = new ArrayList<>();
            first16.add("switch");
            firstSets.put("switch-stmt" , first16);

        ArrayList<String> first17 = new ArrayList<>();
        first17.add("case"); first17.add("epsilon");
            firstSets.put("case-stmt" , first17);
            firstSets.put("case-stmtB" , first17);

        ArrayList<String> first18 = new ArrayList<>();
        first18.add("case");
            firstSets.put("case-stmt" , first18);

        ArrayList<String> first19 = new ArrayList<>();
        first19.add("default") ; first19.add("epsilon");
            firstSets.put("default-stmt" , first19);

        ArrayList<String> first20 = new ArrayList<>();
        first20.add("ID"); first20.add("+");  first20.add("-"); first20.add("("); first20.add("NUM");
            firstSets.put("expression" , first20);

        ArrayList<String> first21 = new ArrayList<>();
            first21 . add("(") ; first21.add("["); first21.add("=") ; first21.add("*");
            first21.add("+"); first21.add("-");first21.add("<"); first21.add("=="); first21.add("epsilon");
        firstSets.put("expressionB" , first21);

        ArrayList<String> first22 = new ArrayList<>();
        first22.add("="); first22.add("*"); first22.add("epsilon"); first22.add("+");
        first22.add("-"); first22.add("<"); first22.add("==");
        firstSets.put("expressionC" , first22);

        firstSets.put("varB" , first7);

        ArrayList<String> first23 = new ArrayList<>();
        first23.add("epsilon"); first23.add("<") ; first23.add("==");
        firstSets.put("simple-expressionB" , first23);

        ArrayList<String> first24 = new ArrayList<>();
        first24.add("<"); first24.add("==");
        firstSets.put("relop" , first24);

        firstSets.put("addictive-expression" , first20);

        ArrayList<String> first25 = new ArrayList<>();
         first25.add("+") ; first25.add("-") ; first25.add("epsilon");
        firstSets.put("addictive-expressionB" , first25);

        ArrayList<String> first26 = new ArrayList<>();
        first26.add("+");   first26.add("-");
        firstSets.put("addop" , first26);

        firstSets.put("term" , first20);

        ArrayList<String> first27 = new ArrayList<>();
        first27.add("*");   first27.add("epsilon");
        firstSets.put("termB" , first27);

        firstSets.put("signed-factor" , first20);

        ArrayList<String> first28 = new ArrayList<>();
        first28.add("+"); first28.add("-"); first28.add("(");   first28.add("NUM");
        firstSets.put("signed-factorA" , first28);

        ArrayList<String> first29 = new ArrayList<>();
        first29.add("ID");  first29.add("(");   first29.add("NUM");
        firstSets.put("factor" , first29);

        ArrayList<String> first30 = new ArrayList<>();
        first30.add("(");   first30.add("NUM");
        firstSets.put("factorA" , first30);

        ArrayList<String> first31 = new ArrayList<>();
        first31.add("[");   first31.add("epsilon");     first31.add("(");
        firstSets.put("factorB" , first31);


        ArrayList<String> first32 = new ArrayList<>();
        first32.add("epsilon"); first32.add("ID");  first32.add("+");   first32.add("-");
        first32.add("(");   first32.add("NUM");
        firstSets.put("args" , first32);

        firstSets.put("arg-list" , first20);

        ArrayList<String> first33 = new ArrayList<>();
        first33.add(","); first33.add("epsilon");
        firstSets.put("arg-listB" , first33);


    }

    public void addToFollowSets(  HashMap<String , ArrayList> followSets ){

        ArrayList<String> follow= new ArrayList<>();
        follow.add("EOF"); follow.add("{"); follow.add("continue");
        follow.add("break"); follow.add(";"); follow.add("if"); follow.add("while");
        follow.add("return"); follow.add("switch"); follow.add("ID"); follow.add("+");
        follow.add("-"); follow.add("("); follow.add("NUM"); follow.add("}");
        followSets.put("declaration-list" , follow);
        followSets.put("declaration-listB" , follow);

        ArrayList<String> follow1= new ArrayList<>();
        follow1.add("int"); follow1.add("void");follow1.add("EOF");
        follow1.add("{"); follow1.add("continue"); follow1.add("break");
        follow1.add(";"); follow1.add("if"); follow1.add("while");follow1.add("return");
        follow1.add("switch"); follow1.add("ID") ; follow1.add("+"); follow1.add("-");
        follow1.add("("); follow1.add("NUM"); follow1.add("}");

        followSets.put("declaration" , follow1);
        followSets.put("type" , follow1);
        followSets.put("var-declarationB" , follow1);

        ArrayList<String> follow2= new ArrayList<>();
        follow2.add("ID");
        followSets.put("type-specifier" , follow2);

        ArrayList<String> follow3= new ArrayList<>();
        follow3.add("(");
        followSets.put("params" , follow3);
        followSets.put("paramsB" , follow3);
        followSets.put("param-listB" , follow3);

        ArrayList<String> follow4= new ArrayList<>();
        follow4.add("("); follow4.add(",");
        followSets.put("param" , follow4);
        followSets.put("paramB" , follow4);


        ArrayList<String> follow5= new ArrayList<>();
        follow5.add("int"); follow5.add("void");follow5.add("EOF");
        follow5.add("{"); follow5.add("continue"); follow5.add("break");
        follow5.add(";"); follow5.add("if"); follow5.add("while");follow5.add("return");
        follow5.add("switch"); follow5.add("ID") ; follow5.add("+"); follow5.add("-");
        follow5.add("("); follow5.add("NUM"); follow5.add("}");
        follow5.add("default"); follow5.add("case"); follow5.add("else");
        followSets.put("compound-stmt" , follow5);

        ArrayList<String> follow6= new ArrayList<>();
        follow6.add("}");   follow6.add("case");    follow6.add("default");
        followSets.put("statement-list" , follow6);
        followSets.put("statement-listB" , follow6);


        ArrayList<String> follow7= new ArrayList<>();
        follow7.add("{");   follow7.add("continue");    follow7.add("break"); follow7.add(";");
        follow7.add("if"); follow7.add("while"); follow7.add("return"); follow7.add("switch");
        follow7.add("ID");  follow7.add("+"); follow7.add("-"); follow7.add("(");
        follow7.add("NUM"); follow7.add("}"); follow7.add("else"); follow7.add("case");
        follow7.add("default");
        followSets.put("statement" , follow7);
        followSets.put("expression-stmt" , follow7);
        followSets.put("selection-stmt" , follow7);
        followSets.put("iteration-stmt" , follow7);
        followSets.put("return-stmt" , follow7);
        followSets.put("return-stmtB" , follow7);
        followSets.put("switch-stmt" , follow7);

        ArrayList<String> follow8= new ArrayList<>();
        follow8.add("default"); follow8.add("}");
        followSets.put("case-stmts" , follow8);
        followSets.put("case-stmtsB" , follow8);

        ArrayList<String> follow9= new ArrayList<>();
        follow9.add("default"); follow9.add("}"); follow9.add("case");
        followSets.put("case-stmt" , follow9);


        ArrayList<String> follow10= new ArrayList<>();
        follow10.add("}");
        followSets.put("default-stmt" , follow10);


        ArrayList<String> follow11= new ArrayList<>();
        follow11.add(";") ; follow11.add(")") ; follow11.add("]"); follow11.add(",");
        followSets.put("expression" , follow11);
        followSets.put("expressionB" , follow11);
        followSets.put("expressionC" , follow11);
        followSets.put("simple-expressionB" , follow11);
        followSets.put("addictive-expression" , follow11);



        ArrayList<String> follow12= new ArrayList<>();
        follow12.add("=");   follow12.add("*"); follow12.add("+") ; follow12.add("-");
        follow12.add("<"); follow12.add("==") ; follow12.add(";"); follow12.add(")");
        follow12.add("]"); follow12.add(",");
        followSets.put("varB" , follow12);

        ArrayList<String> follow13= new ArrayList<>();
        follow13.add("ID"); follow13.add("+") ; follow13.add("-") ; follow13.add("(");
        follow13.add("NUM");
        followSets.put("relop" , follow13);

        ArrayList<String> follow14= new ArrayList<>();
        follow14.add("<"); follow14.add("==") ; follow14.add(";") ; follow14.add(")");
        follow14.add("]"); follow14.add(",");
        followSets.put("addictive-expressionB" , follow14);

        ArrayList<String> follow15= new ArrayList<>();
        follow15.add("ID"); follow15.add("+"); follow15.add("-"); follow15.add("(");
        follow15.add("NUM");
        followSets.put("addop" , follow15);

        ArrayList<String> follow16= new ArrayList<>();
        follow16.add("+"); follow16.add("-") ; follow16.add("<");
        follow16.add("=="); follow16.add(";") ; follow16.add(")"); follow16.add("]");
        follow16.add(",");
        followSets.put("term" , follow16);
        followSets.put("termB" , follow16);


        ArrayList<String> follow17= new ArrayList<>();
        follow17.add("+"); follow17.add("-") ; follow17.add("<");
        follow17.add("=="); follow17.add(";") ; follow17.add(")"); follow17.add("]");
        follow17.add(",");  follow17.add("*");

        followSets.put("signed-factor" , follow17);
        followSets.put("signed-factorA" , follow17);
        followSets.put("factor" , follow17);
        followSets.put("factorA" , follow17);
        followSets.put("factorB" , follow17);

        ArrayList<String> follow18= new ArrayList<>();
        follow18.add(")");
        followSets.put("args" , follow18);
        followSets.put("arg-list" , follow18);
        followSets.put("arg-listB" , follow18);



    }

    public void addToHashMaps(){
        addToFirstSet(firstSets);
        addToFollowSets(followSets);
    }

    public void parseCode() {
        int state = 1;
        ArrayList<Integer> stack = new ArrayList<>();
        ArrayList<String> currentNonTerminals = new ArrayList<>();
        currentNonTerminals.add("program");

        io.openParseTreeFile();

        while (state != 3) {
            System.out.println(state);
            Token peek = lexer.getNextToken();
            int line_no = lexer.getSaved_line_number();
            Node currentNode = nodes.get(state);
            ArrayList<Edge> edges = currentNode.getEdges();
            String current_nt = currentNonTerminals.get(currentNonTerminals.size() - 1);
            int nextstate = 0;

            // reaching an ending node. so should return
            if (edges.size() == 0) {
                state = stack.get(stack.size() - 1);
                stack.remove(stack.size() - 1);
                currentNonTerminals.remove(currentNonTerminals.size() - 1);
                io.writeParseTreeNode(stack.size(), current_nt);
                continue;
            }
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                if (edge instanceof TerminalEdge) {
                    // if edge was a terminal
                    TerminalEdge t_edge = (TerminalEdge) edge;

                    if (t_edge.getToken().getType().equals("epsilon"))
                    {
                        // epsilon edge handling
                        ArrayList nt_follow_set = followSets.get(current_nt);
                        if (peek.getType().equals("NUM") || peek.getType().equals("ID") || peek.getType().equals("EOF"))
                        {
                            if (nt_follow_set.contains(peek.getType()))
                            {
                                nextstate = t_edge.getResultingNode();
                                break;
                            }
                        }
                        else
                        {
                            if (nt_follow_set.contains(peek.getString()))
                            {
                                nextstate = t_edge.getResultingNode();
                                break;
                            }
                        }
                    }
                    if (t_edge.getToken().getType().equals(peek.getType()))
                    {
                        // terminal edge. not epsilon
                        if (t_edge.getToken().getString().equals("*") || t_edge.getToken().getType().equals("EOF"))
                        {
                            nextstate = t_edge.getResultingNode();
                            io.writeParseTreeNode(stack.size(), t_edge.getToken().getType());
                            break;
                        }
                        else if (t_edge.getToken().getString().equals(peek.getString()))
                        {
                            nextstate = t_edge.getResultingNode();
                            io.writeParseTreeNode(stack.size(), t_edge.getToken().getString());
                            break;
                        }

                        // error handling for terminals:
                        if (edges.size() == 1 && nextstate == 0)
                        {
                            String terminal_name;
                            if (t_edge.getToken().getType().equals("ID") || t_edge.getToken().getType().equals("NUM"))
                                terminal_name = t_edge.getToken().getType();
                            else
                                terminal_name = t_edge.getToken().getString();

                            if (terminal_name.equals("End Of File"))
                            {
                                makeError(line_no, "Malformed Input");
                                io.closeParseTreeFile();
                                return;
                            }
                            makeError(line_no, "Missing " + terminal_name);
                            nextstate = t_edge.getResultingNode();
                        }
                    }
                }
                else
                {
                    // if edge was nonTerminal. checking its first and follow sets for a match
                    NonTerminalEdge nt_edge = (NonTerminalEdge) edge;
                    String nt_edge_string = nt_edge.getString();

                    ArrayList edge_first_set = firstSets.get(nt_edge_string);
                    nextstate = getNextState(edge_first_set, nt_edge, stack, peek, currentNonTerminals);

                    ArrayList edge_follow_set = followSets.get(nt_edge_string);
                    if (edge_first_set.contains("epsilon"))
                    {
                        nextstate = getNextState(edge_follow_set, nt_edge, stack, peek, currentNonTerminals);
                    }

                    // handling nonTerminal errors
                    if (edges.size() == 1 && nextstate == 0)
                    {
                        String terminal_name;
                        if (peek.getType().equals("ID") || peek.getType().equals("NUM"))
                            terminal_name = peek.getType();
                        else
                            terminal_name = peek.getString();

                        if (terminal_name.equals("End Of File"))
                        {
                            makeError(line_no, "Unexpected EndOfFile");
                            io.closeParseTreeFile();
                            return;
                        }
                        if (edge_follow_set.contains(terminal_name))
                        {
                            nextstate = nt_edge.getReturningNode();
                            makeError(line_no, "Missing " + nt_edge_string);
                        }
                        else
                        {
                            makeError(line_no, "Unexpected " + terminal_name);
                            nextstate = state;
                        }
                    }
                }
            }
            state = nextstate;
        }
        io.closeParseTreeFile();
    }


    private int getNextState(ArrayList set, NonTerminalEdge nt_edge, ArrayList<Integer> stack, Token peek, ArrayList<String> cNT) {
        int nextstate = 0;
        for (int j = 0; j < set.size(); j++) {
            if (set.get(j).equals("NUM") || set.get(j).equals("ID")) {
                if (peek.getType().equals(set.get(j))) {
                    nextstate = nt_edge.getResultingNode();
                    stack.add(nt_edge.getReturningNode());
                    cNT.add(nt_edge.getString());
                    return nextstate;
                }
            } else {
                if (peek.getString().equals(set.get(j))) {
                    nextstate = nt_edge.getResultingNode();
                    stack.add(nt_edge.getReturningNode());
                    cNT.add(nt_edge.getString());
                    return nextstate;
                }
            }
        }
        return nextstate;
    }

    private void makeError(int line_no, String message)
    {
        Error error = new Error(line_no, "SYNTAX ERROR!", message);
        errors.add(error);
    }
}