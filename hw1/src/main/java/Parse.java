import cs132.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Parse {
    private static final String SUCCESS_MESSAGE = "Program parsed successfully";
    private static final String FAILURE_MESSAGE = "Parse error";

    public static void main(String[] args) throws Exception {
        List<Tokenizer.Token> tokens = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        int lineNumber = 1;
        while ((input = bufferedReader.readLine()) != null) {
            if (!Tokenizer.tokenize(input, lineNumber, tokens)) {
                System.out.println(FAILURE_MESSAGE);
                return;
            }
            lineNumber++;
        }
        bufferedReader.close();
        /*for (int i = 0; i < tokens.size(); i++) {
            System.out.print(i + " ");
            final Tokenizer.Token token = tokens.get(i);
            System.out.println(token.tokenString + " line: " + token.lineNumber + " offset: " + token.offset);
        }*/
        Parser parser = new Parser(tokens);
        if (parser.isValid()) {
            System.out.println(SUCCESS_MESSAGE);
        } else {
            System.out.println(FAILURE_MESSAGE);
        }
    }
}