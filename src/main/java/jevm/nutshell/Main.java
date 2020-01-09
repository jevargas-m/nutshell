package jevm.nutshell;

import jevm.nutshell.engine.ScoredWord;
import jevm.nutshell.engine.StopWordsFileReader;
import jevm.nutshell.engine.TextAnalyzer;
import jevm.nutshell.parser.FileWordParser;
import jevm.nutshell.visualization.CloudVisualization;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        final String DEFAULT_SCORING = "WEIGHTED_DEGREE";

        String[] scoringOptions = {"DEGREE", "WEIGHTED_DEGREE", "ENTROPY", "RELATIVE_DEGREE", "FREQUENCY"};
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
        options.addOption("v", true, "Create Visualization html file");
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
                visualizationFilename = cmd.getOptionValue("v");
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

        } catch (Exception e) {
            helpFormatter.printHelp("test", options);
        }

        StopWordsFileReader stopReader = new StopWordsFileReader(new File(stopWordsFilename));
        List<String> stopWords = stopReader.getStopWords();

        FileWordParser wordParser = new FileWordParser(new File(filename));
        TextAnalyzer analyzer = new TextAnalyzer(stopWords, scoring);

        analyzer.addText(wordParser.getLines());
        TextAnalyzer fullCorpusAnalyzer = new TextAnalyzer(stopWords, scoring);


        List<ScoredWord> keywords1 = null;
        List<ScoredWord> keywords2 = null;

        if (isCorpus) {
            corpusDirFile = new File(corpusDir);
            for(File f : corpusDirFile.listFiles()) {
                if (f.toString().endsWith(".txt")) {
                    FileWordParser parserFull = new FileWordParser(f);
                    fullCorpusAnalyzer.addText(parserFull.getLines());  // used for reference visualization
                    FileWordParser parserCorpus = new FileWordParser(f);
                    analyzer.addCorpus(parserCorpus.getLines());
                }
            }
        }

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
        }


        for (ScoredWord sw : keywords1) {
            //System.out.print(sw.word + ".  ");
            System.out.println(sw);
        }
    }
}
