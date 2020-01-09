package jevm.nutshell.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class StopWordsFileReader implements StopWordsGenerator {

    public static int DEFAULT_MIN_LENGTH = 1;

    List<String> stopWords = new LinkedList<>();

    public StopWordsFileReader(File f) throws FileNotFoundException {
        Scanner s = new Scanner(f);
        s.useDelimiter("[,\\s]");
        while (s.hasNext()) {
            String word = s.next();
            if (word.length() >= DEFAULT_MIN_LENGTH) {
                stopWords.add(word);
            }
        }
    }

    @Override
    public List<String> getStopWords() {
        return stopWords;
    }
}
