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
        //todo
    }

    public void printErrors(ArrayList<Error> errors)
    {
        //todo
    }
}
