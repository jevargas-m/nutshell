package jevm.nutshell.engine;

import jevm.nutshell.parser.Parser;

import java.util.*;

public class RakeAnalyzer implements TextAnalyzer {

    public static int DEFAULT_MIN_LENGTH = 2;

    private Set<String> candidates;
    private List<String> stopWords;
    private WordsGraph contentWords;
    private String regexSplit;

    public RakeAnalyzer(StopWordsGenerator stopWordsGenerator) {
        candidates = new HashSet<>();
        stopWords = stopWordsGenerator.getStopWords();
        buildRegexSplit();
    }

    public void addText(Parser parser) {
        while (parser.hasNext()) {
            String line = parser.nextLine();
            String[] sentences = line.split(regexSplit);
            for (String sentence : sentences) {
                sentence = sentence.trim();
                if (sentence.length() >= DEFAULT_MIN_LENGTH) {
                    candidates.add(sentence);
                }
            }
        }
    }

    private void buildRegexSplit() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String stopWord : stopWords) {
            if (first) {
                first = false;
                sb.append("\\b(");
            } else {
                sb.append("|");
            }
            sb.append(stopWord);
        }
        sb.append(")+\\b");
        regexSplit = sb.toString();
    }



    @Override
    public Map<String, Integer> getKeyWords(int n) {
        return null;
    }

    @Override
    public int scoreSentence(String sentence) {
        return 0;
    }

    @Override
    public int scoreWord(String word) {
        return 0;
    }
}
