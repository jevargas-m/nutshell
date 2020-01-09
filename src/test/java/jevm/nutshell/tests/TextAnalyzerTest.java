package jevm.nutshell.tests;

import jevm.nutshell.engine.TextAnalyzer;
import jevm.nutshell.data.ScoredWord;
import jevm.nutshell.parser.StopWordsFileReader;
import jevm.nutshell.parser.FileWordParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class TextAnalyzerTest {

    @Test
    void addParser() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        TextAnalyzer analyzer = new TextAnalyzer(r.getStopWords(), "WEIGHTED_DEGREE");

        File f2 = new File ("res/alice.txt");
        FileWordParser fp = new FileWordParser(f2);
        analyzer.addText(fp.getLines());
    }

    @Test
    void getKeyWords() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        TextAnalyzer analyzer = new TextAnalyzer(r.getStopWords(), "WEIGHTED_DEGREE");

        File f2 = new File ("res/lord_rings.txt");
        FileWordParser fp = new FileWordParser(f2);
        analyzer.addText(fp.getLines());

        List<ScoredWord> keywords = analyzer.getKeywords(20);
        for(ScoredWord k : keywords) {
            System.out.println(k);
        }
    }

    @Test
    void testKeywordsSingle() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        TextAnalyzer analyzer = new TextAnalyzer(r.getStopWords(), "WEIGHTED_DEGREE");

        File f2 = new File ("res/lord_rings.txt");
        FileWordParser fp = new FileWordParser(f2);
        analyzer.addText(fp.getLines());

        List<ScoredWord> keywords = analyzer.getKeyWordsSingle(100);
        System.out.println("--------------- ENTROPY -----------------------------------");
        for(ScoredWord keyword : keywords) {
            System.out.println(keyword);
        }
        System.out.println("-----------------------------------------------------------");

        keywords = analyzer.getKeyWordsSingle(10);
        System.out.println("--------------- WEIGHTED_DEGREE -----------------------------------");
        for(ScoredWord keyword : keywords) {
            System.out.println(keyword);
        }
        System.out.println("-----------------------------------------------------------");

        keywords = analyzer.getKeyWordsSingle(10);
        System.out.println("--------------- DEGREE -----------------------------------");
        for(ScoredWord keyword : keywords) {
            System.out.println(keyword);
        }
        System.out.println("-----------------------------------------------------------");
    }

    @Test
    void testCorpus1() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        TextAnalyzer analyzer = new TextAnalyzer(r.getStopWords(), "RELATIVE_DEGREE");

        FileWordParser fp1 = new FileWordParser(new File ("res/lord_rings.txt"));
        analyzer.addCorpus(fp1.getLines());

        FileWordParser fp1_1 = new FileWordParser(new File ("res/lord_rings_6.txt"));
        analyzer.addText(fp1_1.getLines());

        List<ScoredWord> keywords = analyzer.getKeywords(500);
        System.out.println("--------------- WEIGHTED_DEGREE -----------------------------------");
        for(ScoredWord keyword : keywords) {
            System.out.println(keyword);
        }
        System.out.println("-----------------------------------------------------------");

        FileWordParser fp1_6 = new FileWordParser(new File ("res/lord_rings_1.txt"));
        analyzer.resetText();
        analyzer.addText(fp1_6.getLines());

        keywords = analyzer.getKeywords(50);
        System.out.println("--------------- WEIGHTED_DEGREE -----------------------------------");
        for(ScoredWord keyword : keywords) {
            System.out.println(keyword);
        }
        System.out.println("-----------------------------------------------------------");



    }

    @Test
    void testCorpus2() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        TextAnalyzer analyzer = new TextAnalyzer(r.getStopWords(), "ENTROPY");

        FileWordParser fp1 = new FileWordParser(new File ("res/lord_rings.txt"));
        analyzer.addCorpus(fp1.getLines());

        FileWordParser fp1_1 = new FileWordParser(new File ("res/lord_rings_6.txt"));
        analyzer.addText(fp1_1.getLines());

        List<ScoredWord> keywords = analyzer.getKeyWordsSingle(20);
        System.out.println("--------------- \"ENTROPY\" -----------------------------------");
        for(ScoredWord keyword : keywords) {
            System.out.println(keyword);
        }
        System.out.println("-----------------------------------------------------------");

        FileWordParser fp1_6 = new FileWordParser(new File ("res/lord_rings_1.txt"));
        analyzer.resetText();
        analyzer.addText(fp1_6.getLines());

        keywords = analyzer.getKeyWordsSingle(20);
        System.out.println("--------------- \"ENTROPY\" -----------------------------------");
        for(ScoredWord keyword : keywords) {
            System.out.println(keyword);
        }
        System.out.println("-----------------------------------------------------------");
    }

    @Test
    void createAbstractTest() throws FileNotFoundException {
        File f1 = new File("res/stopwords_EN.txt");
        StopWordsFileReader r = new StopWordsFileReader(f1);
        TextAnalyzer analyzer = new TextAnalyzer(r.getStopWords(), "WEIGHTED_DEGREE");

        File f2 = new File ("res/mobydick.txt");
        FileWordParser fp = new FileWordParser(f2);
        analyzer.addText(fp.getLines());
        analyzer.analize();

        FileWordParser fp2 = new FileWordParser(f2);
        Set<String> setOfLines = new HashSet<>(fp2.getLines());

        PriorityQueue<ScoredWord> scoredLines = new PriorityQueue<>();

        for(String line : setOfLines) {
            double score = analyzer.scoreString(line);
            ScoredWord sc = new ScoredWord(line, score);
            scoredLines.add(sc);
        }

        for(int i = 0; i < 20; i++) {
            if (scoredLines.isEmpty()) {
                break;
            }

            System.out.println(scoredLines.remove());
        }


    }
}