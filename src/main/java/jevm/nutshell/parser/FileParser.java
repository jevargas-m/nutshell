package jevm.nutshell.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileParser implements Parser {

    public static String SENTENCE_DELIMITERS = "[.,();`\":?!\\n]";

    private Scanner scanner;

    public FileParser(File file) throws FileNotFoundException {
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
        s = s.replace('\n', ' '); //eliminate intermediate newline
        s = s.toLowerCase();
        if (s.length() == 0 && scanner.hasNext()) {
            return nextLine();
        } else {
            return s;
        }


    }
}
