package jevm.nutshell.engine;

import jevm.nutshell.parser.WordParser;

import java.util.*;

public class RakeAnalyzer implements TextAnalyzer {


    public static final String DEFAULT_WORD_DELIMITER = "\\s";

    private List<String> candidates;
    private List<String> stopWords;
    private WordsGraph textGraph, corpusGraph;
    private Map<String, Double> contentWordScores;
    private String regexSplit;

    public RakeAnalyzer(StopWordsGenerator stopWordsGenerator) {
        candidates = new LinkedList<>();
        stopWords = stopWordsGenerator.getStopWords();
        textGraph = new WordsGraph();
        corpusGraph = null;
        contentWordScores = new HashMap<>();
        buildRegexSplit();
    }

    public void addCorpus(WordParser parser) {
        if (corpusGraph == null) {
            corpusGraph = new WordsGraph();
        }
        corpusGraph.addAll(parser.getListOfSentences(regexSplit));
    }

    public void addText(WordParser wordParser) {
        candidates = wordParser.getListOfSentences(regexSplit);
        textGraph.addAll(candidates);
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

    private void calculateWordScores(String strategy) {
        List<Map.Entry<String, WordsGraph.WordData>> all = textGraph.getAllEntries();


        Map<String, Double> relFreqs = textGraph.getWordRelativeFreqs();

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
                    score = - score * Math.log(score) * data.frequency;
                    break;
            }
            contentWordScores.put(word, score);
        }
    }

    @Override
    public Map<String, Double> getKeywords(int n, String strategy) {

        calculateWordScores(strategy);
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
        calculateWordScores(strategy);
        for (Map.Entry<String, WordsGraph.WordData> e : textGraph.getAllEntries()) {
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
