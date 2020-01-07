package jevm.nutshell.tests;

import jevm.nutshell.engine.ScoredWord;
import jevm.nutshell.engine.StopWordsFileReader;
import jevm.nutshell.engine.TextAnalyzer;
import jevm.nutshell.parser.FileWordParser;
import jevm.nutshell.visualization.NutshellVisualization;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NutshellVisualizationTest {

    @Test
    void testCreateVisualization1() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        TextAnalyzer analyzer = new TextAnalyzer(r, "WEIGHTED_DEGREE");

        File f2 = new File ("res/lord_rings.txt");
        FileWordParser fp = new FileWordParser(f2);
        analyzer.addText(fp);

        List<ScoredWord> keywords = analyzer.getKeyWordsSingle(200);

        NutshellVisualization v = new NutshellVisualization(keywords);
        v.createJSONFile();
    }

    @Test
    void testCreateVisualization2() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        TextAnalyzer analyzer = new TextAnalyzer(r, "WEIGHTED_DEGREE");

        File f2 = new File ("res/lord_rings.txt");
        FileWordParser fp = new FileWordParser(f2);
        analyzer.addText(fp);

        List<ScoredWord> keywords = analyzer.getKeyWordsSingle(200);

        NutshellVisualization v = new NutshellVisualization(keywords);
        v.createJSONFile();
    }

}