package jevm.nutshell.tests;

import jevm.nutshell.engine.ScoredWord;
import jevm.nutshell.engine.StopWordsFileReader;
import jevm.nutshell.engine.TextAnalyzer;
import jevm.nutshell.parser.FileWordParser;
import jevm.nutshell.visualization.NutshellVisualization;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NutshellVisualizationTest {

    @Test
    void testCreateVisualization1() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        TextAnalyzer analyzer = new TextAnalyzer(r, "DEGREE");

        File f2 = new File ("res/old_man.txt");
        FileWordParser fp = new FileWordParser(f2);
        analyzer.addText(fp);

        List<ScoredWord> keywords = analyzer.getKeyWordsSingle(100);

        NutshellVisualization v = new NutshellVisualization();
        v.addDataSet("Test file", keywords);
        v.createWordCloud("nutshell.html");
    }

    @Test
    void testCreateVisualization2() throws FileNotFoundException {

        StopWordsFileReader r1 = new StopWordsFileReader(new File("res/stopwords_EN.txt"));
        TextAnalyzer analyzer = new TextAnalyzer(r1, "WEIGHTED_DEGREE");

        File f2 = new File ("res/lord_rings.txt");
        FileWordParser fp = new FileWordParser(f2);
        analyzer.addText(fp);

        List<ScoredWord> keywords = analyzer.getKeyWordsSingle(100);

        NutshellVisualization v = new NutshellVisualization();
        v.addDataSet("Corpus", keywords);

        StopWordsFileReader r2 = new StopWordsFileReader(new File("res/stopwords_EN.txt"));
        TextAnalyzer analyzer2 = new TextAnalyzer(r2, "WEIGHTED_DEGREE");

        File f3 = new File ("res/lord_rings.txt");
        FileWordParser fp3 = new FileWordParser(f3);
        File f4 = new File ("res/lord_rings_6.txt");
        FileWordParser fp4 = new FileWordParser(f4);
        analyzer2.addCorpus(fp3);
        analyzer2.addText(fp4);

        List<ScoredWord> keywords2 = analyzer2.getKeyWordsSingle(100);

        v.addDataSet("Text", keywords2);
        v.createWordCloud("nutshell.html");
    }

}