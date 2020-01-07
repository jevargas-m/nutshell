package jevm.nutshell.tests;

import jevm.nutshell.engine.StopWordsFileReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StopWordsFileReaderTest {

    @Test
    void getStopWords() throws FileNotFoundException {
        File f = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f);
        List<String> l = r.getStopWords();
        assertEquals(175, l.size());
        for(String w : l) {
            System.out.println(w);
        }
    }
}