package jevm.nutshell.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileWordParser implements WordParser {

    public static String SENTENCE_DELIMITERS = "[.,();`\":?!]";
    public static int DEFAULT_MIN_LENGTH = 2;

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
    public List<String> getListOfSentences(String regexSplit) {
        List<String> output = new LinkedList<>();
        while (hasNext()) {
            String line = nextLine();
            line = line.toLowerCase();
            String[] sentences = line.split(regexSplit);
            for (String sentence : sentences) {
                sentence = sentence.trim();
                sentence = sentence.replace('-', ' ');  // split composed words
               // sentence = sentence.replaceAll("^[^a-zA-Z]+|[^a-zA-Z]+$", "");  // trim non char from beginning and end
                if (sentence.length() >= DEFAULT_MIN_LENGTH ) {
                    output.add(sentence);
                }
            }
        }
        return output;
    }

    public Set<String> getUniqueLines() {
        Set<String> output = new HashSet<>();
        while(hasNext()) {
            output.add(nextLine());
        }
        return output;
    }
}
