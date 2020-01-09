package jevm.nutshell;

import jevm.nutshell.data.ScoredWord;
import jevm.nutshell.parser.StopWordsFileReader;
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
        String stopWordsFilename = "stopwords_EN.txt";  // default value may be changed in args

        /* parse command line arguments */
        String usage  = "nutshell -f <source.txt> -om|-os|-oa <n> (-c <directory>) (-v) (-sc <option>)";
        Options options = new Options();
        options.addOption("h", false, "Show this help");
        options.addOption("f", true, "Source .txt file");
        options.addOption("om", true, "Muti-word keyword output <n>");
        options.addOption("os", true, "Single-word keyword output <n>");
        options.addOption("oa", true, "Abstract output <n>");
        options.addOption("c", true, "Optional: Corpus differential analysis vs all .txt files in the supplied dir");
        options.addOption("v", false, "Optional: Create Visualization nutshell.html file");
        options.addOption("sc", true, "Optional: Scoring options:" + Arrays.toString(scoringOptions));
        options.addOption("stop", true, "Optional: Stopwords file (default is " + stopWordsFilename + ")");


        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        File corpusDirFile;

        try {

            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                helpFormatter.printHelp(usage, options);
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

            if (cmd.hasOption("os")) {
                analysisKind = "single";
                n = Integer.parseInt(cmd.getOptionValue("os"));
            } else if (cmd.hasOption("om")) {
                analysisKind = "multi";
                n = Integer.parseInt(cmd.getOptionValue("om"));
            } else if (cmd.hasOption("oa")) {
                analysisKind = "abstract";
                n = Integer.parseInt(cmd.getOptionValue("oa"));
            } else {
                throw new IllegalArgumentException();
            }

            if (cmd.hasOption("stop")) {
                stopWordsFilename = cmd.getOptionValue("stop");
            }

            List<ScoredWord> keywords1 = null;
            List<ScoredWord> keywords2 = null;

            /* program controller */

            StopWordsFileReader stopReader = new StopWordsFileReader(new File(stopWordsFilename));
            List<String> stopWords = stopReader.getStopWords();

            FileWordParser wordParser = new FileWordParser(new File(filename));
            TextAnalyzer analyzer = new TextAnalyzer(stopWords, scoring);

            List<String> lines = wordParser.getLines();

            analyzer.addText(lines);
            TextAnalyzer fullCorpusAnalyzer = new TextAnalyzer(stopWords, scoring);

            if(isCorpus) {
                processCorpus(corpusDir, analyzer, fullCorpusAnalyzer);
            }

            if(analysisKind.equals("single")) {
                keywords1 = analyzer.getKeyWordsSingle(n);
                if (isCorpus) keywords2 = fullCorpusAnalyzer.getKeyWordsSingle(n);

            } else if(analysisKind.equals("multi")) {
                keywords1 = analyzer.getKeywords(n);
                if (isCorpus) keywords2 = fullCorpusAnalyzer.getKeywords(n);

            } else {
                keywords1 = analyzer.getAbstract(n);
                if (isCorpus) keywords2 = fullCorpusAnalyzer.getAbstract(n);
            }

            if(hasVisualization) {
                CloudVisualization v = new CloudVisualization();
                v.addDataSet(filename, keywords1);
                if (isCorpus) v.addDataSet("Full Corpus", keywords2);
                v.createWordCloud(visualizationFilename);
            }

            /* print output to console */
            for (ScoredWord sw : keywords1) {
                System.out.println(sw);
            }

        } catch (ParseException | IllegalArgumentException e) {
            System.out.println("Invalid arguments");
            helpFormatter.printHelp(usage, options);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    /**
     * Process all txt files in the supplied directory
     * @param corpusDir
     * @param analyzer
     * @param fullCorpusAnalyzer used for showing vs corpus in visualization
     * @throws FileNotFoundException
     */
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
