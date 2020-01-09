package jevm.nutshell;

import jevm.nutshell.engine.ScoredWord;
import jevm.nutshell.engine.StopWordsFileReader;
import jevm.nutshell.engine.TextAnalyzer;
import jevm.nutshell.parser.FileWordParser;
import jevm.nutshell.visualization.CloudVisualization;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {


        final String DEFAULT_SCORING = "WEIGHTED_DEGREE";

        String[] scoringOptions = TextAnalyzer.scoringOptions;
        String scoring = DEFAULT_SCORING;
        String filename = "";
        boolean isCorpus = false;
        boolean hasVisualization = false;
        String analysisKind = "";
        String corpusDir = "";
        int n = 0;
        String visualizationFilename = "nutshell.html";
        String stopWordsFilename = "stopwords_EN.txt";

        /* parse command line arguments */
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("f", true, "Source .txt file");
        options.addOption("m", true, "Muti-word <n> keywords generation");
        options.addOption("s", true, "Single-word <n> keywords generation");
        options.addOption("a", true, "Abstract <n> sentences generation");
        options.addOption("c", true, "Corpus differential analysis vs all *.txt files");
        options.addOption("v", false, "Create Visualization html file");
        options.addOption("sc", true, "Scoring options:" + Arrays.toString(scoringOptions));
        options.addOption("stop", true, "Stopwords file (default is " + stopWordsFilename + ")");

        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        File corpusDirFile;

        try {

            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                helpFormatter.printHelp("test", options);
                return;
            }

            if (!cmd.hasOption("f")) {
                throw new IllegalArgumentException();
            } else {
                filename = cmd.getOptionValue("f");
            }

            if (cmd.hasOption("c")) {
                isCorpus = true;
                corpusDir = cmd.getOptionValue("c");

            }

            if (cmd.hasOption("v")) {
                hasVisualization = true;
            }

            if (cmd.hasOption("sc")) {
                scoring = cmd.getOptionValue("sc");
                if (!Arrays.asList(scoringOptions).contains(scoring)) {
                    throw new IllegalArgumentException();
                }
            }

            if (cmd.hasOption("s")) {
                analysisKind = "single";
                n = Integer.parseInt(cmd.getOptionValue("s"));
            } else if (cmd.hasOption("m")) {
                analysisKind = "multi";
                n = Integer.parseInt(cmd.getOptionValue("m"));
            } else if (cmd.hasOption("a")) {
                analysisKind = "abstract";
                n = Integer.parseInt(cmd.getOptionValue("a"));
            } else {
                throw new IllegalArgumentException();
            }

            if (cmd.hasOption("stop")) {
                stopWordsFilename = cmd.getOptionValue("stop");
            }

            StopWordsFileReader stopReader = new StopWordsFileReader(new File(stopWordsFilename));
            List<String> stopWords = stopReader.getStopWords();

            FileWordParser wordParser = new FileWordParser(new File(filename));
            TextAnalyzer analyzer = new TextAnalyzer(stopWords, scoring);

            analyzer.addText(wordParser.getLines());
            TextAnalyzer fullCorpusAnalyzer = new TextAnalyzer(stopWords, scoring);

            if (isCorpus) {
                processCorpus(corpusDir, analyzer, fullCorpusAnalyzer);
            }

            List<ScoredWord> keywords1 = null;
            List<ScoredWord> keywords2 = null;

            if (analysisKind.equals("single")) {
                keywords1 = analyzer.getKeyWordsSingle(n);
                if (isCorpus) keywords2 = fullCorpusAnalyzer.getKeyWordsSingle(n);
            } else if (analysisKind.equals("multi")) {
                keywords1 = analyzer.getKeywords(n);
                if (isCorpus) keywords2 = fullCorpusAnalyzer.getKeywords(n);
            } else {
                keywords1 = analyzer.getAbstract(n);
                if(isCorpus) keywords2 = fullCorpusAnalyzer.getAbstract(n);
            }

            if (hasVisualization) {
                CloudVisualization v = new CloudVisualization();
                v.addDataSet(filename, keywords1);
                if (isCorpus) v.addDataSet("Full Corpus", keywords2);
                v.createWordCloud(visualizationFilename);
                //System.out.println("Visualization in: " + visualizationFilename);
            }

            for (ScoredWord sw : keywords1) {
                System.out.println(sw);
            }

        } catch (ParseException | NumberFormatException e) {
            System.out.println("Invalid arguments");
            helpFormatter.printHelp("test", options);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void processCorpus(String corpusDir, TextAnalyzer analyzer, TextAnalyzer fullCorpusAnalyzer) throws FileNotFoundException {
        File corpusDirFile;
        corpusDirFile = new File(corpusDir);
        try (ProgressBar pb = new ProgressBar("Processing corpus: " + corpusDir, corpusDirFile.listFiles().length * 3)) {
            for(File f : corpusDirFile.listFiles()) {
                if (f.toString().endsWith(".txt")) {
                    FileWordParser parserCorpus = new FileWordParser(f);
                    List<String> lines = parserCorpus.getLines();
                    pb.step();
                    fullCorpusAnalyzer.addText(lines);  // used for reference visualization
                    pb.step();
                    analyzer.addCorpus(lines);
                    pb.step();
                } else {
                    pb.stepBy(3);
                }
            }
        }
    }
}
