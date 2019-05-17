import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Node> nodes = new ArrayList<>();
    private IO io;
    private LexicalAnalyzer lexer;

    public Parser(IO io, LexicalAnalyzer lexer)
    {
        this.io = io;
        this.lexer = lexer;
        init_nodes();
        addToHashMaps();
        parseCode();
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
        follow2.clear();
        follow2.add("(");
        followSets.put("params" , follow2);
        followSets.put("paramsB" , follow2);
        followSets.put("param-listB" , follow2);
        follow2.add(",");
        followSets.put("param" , follow2);
        followSets.put("paramB" , follow2);


        follow.remove("EOF"); follow.add("else");
        follow.add("default"); follow.add("case");
        followSets.put("compound-stmt" , follow);
        followSets.put("statement" , follow);
        followSets.put("expression-stmt" , follow);
        followSets.put("selection-stmt" , follow);
        followSets.put("iteration-stmt" , follow);
        followSets.put("return-stmt" , follow);
        followSets.put("return-stmtB" , follow);
        followSets.put("switch-stmt" , follow);

        ArrayList<String> follow3= new ArrayList<>();
        follow3.add("default"); follow3.add(",");
        followSets.put("case-stmts" , follow3);
        followSets.put("case-stmtsB" , follow3);
        follow3.add("case");
        followSets.put("case-stmt" , follow3);

        follow3.clear();
        follow3.add("}");
        followSets.put("default-stmt" , follow3);

        follow3.clear();
        follow3.add(";") ; follow3.add(")") ; follow3.add("]"); follow3.add(",");
        followSets.put("expression" , follow3);
        followSets.put("expressionB" , follow3);
        followSets.put("expressionC" , follow3);
        followSets.put("simple-expressionB" , follow3);
        followSets.put("addictive-expression" , follow3);

        follow3.clear();
        follow3.add("="); follow3.add("*"); follow3.add("+") ; follow3.add("-");
        follow3.add("<"); follow3.add("==") ; follow3.add(";"); follow3.add(")");
        follow3.add("]"); follow3.add(",");
        followSets.put("varB" , follow3);

        ArrayList<String> follow4= new ArrayList<>();
        follow4.add("ID"); follow4.add("+") ; follow4.add("-") ; follow4.add("(");
        follow4.add("NUM");
        followSets.put("relop" , follow4);

        follow4.clear();
        follow4.add("<"); follow4.add("==") ; follow4.add(";") ; follow4.add(")");
        follow4.add("]"); follow4.add(",");
        followSets.put("addictive-expressionB" , follow4);

        follow4.clear();
        follow4.add("ID"); follow4.add("+"); follow4.add("-"); follow4.add("(");
        follow4.add("NUM");
        followSets.put("addop" , follow4);

        follow4.clear();
        follow4.add("+"); follow4.add("-") ; follow4.add("<");
        follow4.add("=="); follow4.add(";") ; follow4.add(")"); follow4.add("]");
        follow4.add(",");
        followSets.put("term" , follow4);
        followSets.put("termB" , follow4);

        follow4.add("*");

        followSets.put("signed-factor" , follow4);
        followSets.put("signed-factorA" , follow4);
        followSets.put("factor" , follow4);
        followSets.put("factorA" , follow4);
        followSets.put("factorB" , follow4);

        follow1.clear();
        follow1.add(")");
        followSets.put("args" , follow1);
        followSets.put("arg-list" , follow1);
        followSets.put("arg-listB" , follow1);



    }

    public void addToHashMaps(){
        addToFirstSet(firstSets);
        addToFollowSets(followSets);
    }

    private void parseCode()
    {
        int state = 1;
        while (state != 3)
        {
            Token peek  = lexer.getNextToken();
            Node currentNode = nodes.get(state);
            ArrayList<Edge> edges = currentNode.getEdges();
            int nextstate = 0;
            for (int i = 0 ; i < edges.size(); i++)
            {
                Edge edge = edges.get(i);
                if (edge instanceof TerminalEdge)
                {
                    TerminalEdge tedge = (TerminalEdge)edge;

                    if (peek.getType().equals(tedge.getToken().getType()))
                    {

                    }
                }
                else
                {

                }
            }
        }
    }
}
