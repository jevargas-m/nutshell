package jevm.nutshell.engine;

import jevm.nutshell.parser.Parser;

import java.util.*;

public class RakeAnalyzer implements TextAnalyzer {

    public static int DEFAULT_MIN_LENGTH = 2;
    public static final String DEFAULT_WORD_DELIMITER = "\\s";

    private List<String> candidates;
    private List<String> stopWords;
    private WordsGraph contentWordsGraph;
    private Map<String, Double> contentWordScores;
    private String regexSplit;

    public RakeAnalyzer(StopWordsGenerator stopWordsGenerator) {
        candidates = new LinkedList<>();
        stopWords = stopWordsGenerator.getStopWords();
        contentWordsGraph = new WordsGraph();
        contentWordScores = new HashMap<>();
        buildRegexSplit();
    }

    public void addText(Parser parser) {
        while (parser.hasNext()) {
            String line = parser.nextLine();
            String[] sentences = line.split(regexSplit);
            for (String sentence : sentences) {
                sentence = sentence.trim();
                sentence = sentence.replace('-', ' ');  // split composed words
                sentence = sentence.replaceAll("^[^a-zA-Z]+|[^a-zA-Z]+$", "");  // trim non char from beginning and end
                if (sentence.length() >= DEFAULT_MIN_LENGTH ) {
                    candidates.add(sentence);
                }
            }
        }
        contentWordsGraph.addAll(candidates);
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

    private void calcScoresByRelativeDegree(String strategy) {
        List<Map.Entry<String, WordsGraph.WordData>> all = contentWordsGraph.getAllEntries();


        Map<String, Double> relFreqs = contentWordsGraph.getWordRelativeFreqs();

        for(Map.Entry<String, WordsGraph.WordData> e : all) {
            String word = e.getKey();
            WordsGraph.WordData data = e.getValue();
            double score = 0.0;
            switch ( strategy ) {
                case "RELATIVE_DEGREE" :
                    score = (double) (data.weightedInDegree + data.weightedOutDegree) / relFreqs.get(word);
                    break;

                case "WEIGHTED_DEGREE" :
                    score = (double) data.weightedInDegree + data.weightedOutDegree;
                    break;

                case "DEGREE" :
                    score = (double) data.inDegree + data.outDegree;
                    break;

                case "FREQUENCY" :
                    score = relFreqs.get(word);
                    break;

                case "ENTROPY" :
                    score = relFreqs.get(word);
                    score = - score * Math.log(score);
                    break;
            }
            contentWordScores.put(word, score);
        }
    }

    @Override
    public Map<String, Double> getKeywords(int n, String strategy) {

        calcScoresByRelativeDegree(strategy);
        Map<String, Double> scoredCandidates = new HashMap<>();
        Set<String> setOfCandidates = new HashSet<>(candidates); // eliminate duplicates
        for(String candidate : setOfCandidates) {
            Double candidateScore = 0.0;
            for (String word : candidate.split(DEFAULT_WORD_DELIMITER)) {
                candidateScore += contentWordScores.getOrDefault(word, 0.0);
            }
            scoredCandidates.put(candidate, candidateScore / candidate.split(" ").length);
        }

        List<Map.Entry<String, Double>> allScores = new ArrayList<>(scoredCandidates.entrySet());
        allScores.sort(compareByScore());

        Map<String, Double> output = new HashMap<>();
        for(int i = 0; i < n; i++) {
            output.put(allScores.get(i).getKey(), allScores.get(i).getValue());
        }

        return output;
    }

    public Map<String, Double> getKeyWordsSingle(int n, String strategy) {
        Map<String, Double> output = new HashMap<>();
        Map<String, Double> scoredCandidates = new HashMap<>();
        calcScoresByRelativeDegree(strategy);
        for (Map.Entry<String, WordsGraph.WordData> e : contentWordsGraph.getAllEntries()) {
            String word = e.getKey();
            scoredCandidates.put(word, contentWordScores.get(word));
        }

        List<Map.Entry<String, Double>> allScores = new ArrayList<>(scoredCandidates.entrySet());
        allScores.sort(compareByScore());

        for(int i = 0; i < n; i++) {
            output.put(allScores.get(i).getKey(), allScores.get(i).getValue());
        }

        return output;
    }


    public static Comparator<Map.Entry<String, Double>> compareByScore () {
        return new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> w1, Map.Entry<String, Double> w2) {
                double diff = (w2.getValue() - w1.getValue()) * 1000000;
                return (int) diff;
            }
        };
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
