import java.io.*;
import java.util.ArrayList;

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
                new FileOutputStream("tokens.txt"), "utf-8"))) {
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
                stringBuffer.append(line);
                Node node = new Node();

                if(line.startsWith())

                stringBuffer.append("\n");

            }


        }
        catch (Exception e){
            e.printStackTrace();
        }

        return nodes;
    }
}
