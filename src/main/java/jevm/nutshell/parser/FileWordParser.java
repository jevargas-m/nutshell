package jevm.nutshell.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileWordParser implements WordParser {

    public static String SENTENCE_DELIMITERS = "[.,();`\":?!]";

    private Scanner scanner;

    public FileWordParser(File file) throws FileNotFoundException {
        scanner = new Scanner(file);
        scanner.useDelimiter(SENTENCE_DELIMITERS);
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNext();
    }

    @Override
    public String nextLine() {
        String s = scanner.next();
        s = s.trim();
        s = s.replaceAll("^[^a-zA-Z]+|[^a-zA-Z]+$", "");
        s = s.replace('\n', ' '); //eliminate intermediate newline
       // s = s.toLowerCase();
        if (s.length() == 0 && scanner.hasNext()) {
            return nextLine();
        } else {
            return s;
        }
    }

    @Override
    public List<String> getLines() {
        List<String> output = new LinkedList<>();
        while(hasNext()) {
            output.add(nextLine());
        }
        return output;
    }
}
