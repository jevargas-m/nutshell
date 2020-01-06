package jevm.nutshell.tests;

import jevm.nutshell.engine.RakeAnalyzer;
import jevm.nutshell.engine.StopWordsFileReader;
import jevm.nutshell.parser.FileParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RakeAnalyzerTest {

    @Test
    void addParser() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        RakeAnalyzer analyzer = new RakeAnalyzer(r);

        File f2 = new File ("res/alice.txt");
        FileParser fp = new FileParser(f2);
        analyzer.addText(fp);
    }

    @Test
    void getKeyWords() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        RakeAnalyzer analyzer = new RakeAnalyzer(r);

        File f2 = new File ("res/mobydick.txt");
        FileParser fp = new FileParser(f2);
        analyzer.addText(fp);

        Map<String, Double> keywords = analyzer.getKeyWords(100);
        for(String keyword : keywords.keySet()) {
            System.out.println(keyword + " " + keywords.get(keyword));
        }
    }
}