import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IO {

    char ch;
    ArrayList<Character> input = new ArrayList<>();

    public void readInput(){

        try{

            InputStream in = new FileInputStream("input.txt");
            Reader r = new InputStreamReader(in, "US-ASCII");
            int reader;
            while ((reader = r.read()) != -1) {
                ch = (char) reader;
                input.add(ch);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        input.add('\0');
    }

    public void printTokens(ArrayList<Token> tokens)
    {

        try (Writer writers = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("tokens2.txt"), "utf-8"))) {
            if (tokens.size() > 0)
            {
                int lineNumber = tokens.get(0).getLine_number();
                writers.write(lineNumber + ". ");

                for (Token token : tokens) {
                    if(token.getLine_number() == lineNumber) {
                        writers.write("(" + token.getType() + ", " + token.getToken() + ") " );

                    }
                    else{
                        writers.write("\n");
                        lineNumber = token.getLine_number();
                        writers.write(lineNumber + ". ");
                        writers.write( "(" + token.getType() + ", " + token.getToken() + ") " );

                    }
                }
            }
            writers.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void printErrors(ArrayList<Error> errors)
    {
        try(Writer errorWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("errors.txt"), "utf-8"))){

            if (errors.size() > 0)
            {
                int lineNumber = errors.get(0).getLine_number();
                errorWriter.write(lineNumber + ". ");
                for (Error error : errors) {
                    if(error.getLine_number() == lineNumber){
                        errorWriter.write("(" + error.getString() + ", " + error.getMessage() + ")");

                    }
                    else {
                        errorWriter.write("\n");
                        lineNumber = error.getLine_number();
                        errorWriter.write(lineNumber + ". ");
                        errorWriter.write("(" + error.getString() + ", " + error.getMessage() + ")");
                    }

                }
            }
            errorWriter.close();

        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Node> readTransitionGraph()
    {
       // String line ;
        ArrayList<Node> nodes = new ArrayList<>();

        try{
//            InputStream transitionRead = new FileInputStream("TransitionGraph.txt");
//            Reader read = new InputStreamReader(transitionRead, "US-ASCII");
//            int reader ;
            File file = new File("TransitionGraph.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                Node newNode = new Node();
                ArrayList<Edge> edges = new ArrayList<>();
                newNode.setEdges(edges);

                stringBuffer.append(line);

                String terminalPattern = "\\(T" ;
                String nonTerminalPattern = "\\(NT" ;

                Pattern tPattern = Pattern.compile(terminalPattern);
                Pattern nonTPattern = Pattern.compile(nonTerminalPattern);
                String[] split;
                split = line.split("\\|");
                String[] s = split[0].split("\\(");
               // System.out.print(s[0] + "----- ");
                String[] s1 = s[1].split(", ");
                String[] s2 = s1[1].split("\\)");

                if(s1[0] .equals("0"))
                    newNode.is_starting = false;
                else
                    newNode.is_starting = true;

                if(s2[0] .equals("0"))
                    newNode.is_returning = false;
                else
                    newNode.is_returning = true;



                for (int i = 1 ; i < split.length ; i++) {

                    Matcher tm = tPattern.matcher(split[i]);
                    Matcher nonTM = nonTPattern.matcher(split[i]);
                    TerminalEdge newTerminalEdge ;

                    if(tm.find()){
                        String[] edgeSplit;
                        edgeSplit = split[i].split(", ");

                        if(edgeSplit.length == 4){
                            String[] resultNode = edgeSplit[3].split("\\)");
                            Token newToken = new Token(edgeSplit[1] , edgeSplit[2].substring(1, edgeSplit[2].length() - 1) , 0);
                             newTerminalEdge = new TerminalEdge(Integer.valueOf(resultNode[0]) , newToken);

                        }
                        else {
                            Token newToken = new Token(edgeSplit[1] , edgeSplit[2].substring(1, edgeSplit[2].length() - 1) , 0);
                            newTerminalEdge = new TerminalEdge(Integer.valueOf(edgeSplit[3]) , newToken);
                            if(edgeSplit[4] != null){
                                String[] lastSplit = edgeSplit[4].split("\\)");


                                newTerminalEdge.setRoutine(lastSplit[0].substring(1,lastSplit[0].length() - 1 ));
                            }

                        }

                        newNode.getEdges().add(newTerminalEdge);


                    }

                    else if(nonTM.find()){
                        String[] edgeSplit ;
                        edgeSplit = split[i].split(", ");

                        NonTerminalEdge newNonTerminalEdge ;
                        if(edgeSplit.length  == 4){
                            String[] returnNode = edgeSplit[3].split("\\)");
                             newNonTerminalEdge = new NonTerminalEdge(Integer.valueOf(edgeSplit[2]) , Integer.valueOf(returnNode[0]) , edgeSplit[1] );
                        }

                        else{
                           newNonTerminalEdge = new NonTerminalEdge(Integer.valueOf(edgeSplit[2]) , Integer.valueOf(edgeSplit[3]) , edgeSplit[1] );
                            if(edgeSplit[4] != null ){
                                String[] lastSplit  = edgeSplit[5].split("\\)");

                                newNonTerminalEdge.setRoutineBefore(edgeSplit[4].substring(1, edgeSplit[4].length() - 1));
                                newNonTerminalEdge.setRoutineAfter(lastSplit[0].substring(1 , lastSplit[0].length() - 1));
                            }
                        }



                        newNode.getEdges().add(newNonTerminalEdge);

                    }

                    else{
                        System.out.println("No match found !!!");
                    }



                }




                stringBuffer.append("\n");
                nodes.add(newNode);
            }
            bufferedReader.close();


        }
        catch (Exception e){
            e.printStackTrace();
        }

        return nodes;
    }

    BufferedWriter parseTreeWriter;
    public void openParseTreeFile()
    {
        try
        {
            parseTreeWriter = new BufferedWriter(new FileWriter("parse_tree.txt"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void writeParseTreeNode(int depth, String node_string)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0 ; i < depth; i++)
                sb.append("\t.");
            sb.append(node_string);
            sb.append('\n');
            parseTreeWriter.write(sb.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void closeParseTreeFile()
    {
        try {
            parseTreeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedWriter codeWriter;
    public void openThreeAddressCodeFile()
    {
        try
        {
            codeWriter = new BufferedWriter(new FileWriter("output.txt"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void printThreeAddressCode(ThreeAddressCode[] PB)
    {
        try {
            for (int i = 0; i < 1000; i++) {
                if (PB[i] != null) {
                    codeWriter.write(i + "\t");
                    codeWriter.write(String.valueOf(PB[i].getCode()));
                    codeWriter.write("\n");
                    System.out.println("wrote line " + i);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void closeThreeAddressCodeFile()
    {
        try {
            codeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
