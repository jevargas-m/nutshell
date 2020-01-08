package jevm.nutshell;

import jevm.nutshell.engine.ScoredWord;
import jevm.nutshell.engine.StopWordsFileReader;
import jevm.nutshell.engine.TextAnalyzer;
import jevm.nutshell.parser.FileWordParser;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
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
        FileWordParser wordParser = new FileWordParser(new File(filename));
        TextAnalyzer analyzer = new TextAnalyzer(stopReader, scoring);
        analyzer.addText(wordParser);

        List<ScoredWord> keywords1, keywords2;
        if (isCorpus) {

        } else {

            if (analysisKind.equals("single")) {
                keywords1 = analyzer.getKeyWordsSingle(n);
            } else if (analysisKind.equals("multi")) {
                keywords1 = analyzer.getKeyWordsSingle(n);
            } else {
                FileWordParser allText = new FileWordParser(new File(filename));
                for(String line : allText.getUniqueLines()) {

                }

            }
        }







    }
}
