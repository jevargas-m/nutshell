package jevm.nutshell.engine;


import java.util.*;

public class TextAnalyzer {


    public static final String DEFAULT_WORD_DELIMITER = "\\s";
    private static final int DEFAULT_MIN_LENGTH = 2;
    public static double UNKNOWN_SCORE_FACTOR = 1.3;

    private List<String> textLines = new LinkedList<>();
    private List<String> corpusLines = new LinkedList<>();
    private List<String> candidates;
    private List<String> stopWords;
    private WordsGraph textGraph, corpusGraph;
    private Map<String, Double> textWordScores, corpusWordScores;
    private String regexSplit, strategy;

    public TextAnalyzer(List<String> stopWords, String strategy) {
        candidates = new LinkedList<>();
        this.stopWords = stopWords;
        textGraph = new WordsGraph();
        corpusGraph = null;
        this.strategy = strategy;
        buildRegexSplit();
    }

    public void addCorpus(List<String> lines) {
        if (corpusGraph == null) {
            corpusGraph = new WordsGraph();
        }
        corpusLines.addAll(lines);
        corpusGraph.addAll(getListOfSentences(lines));
    }

    public void addText(List<String> lines) {
        textLines.addAll(lines);
        candidates = getListOfSentences(lines);
        textGraph.addAll(candidates);
    }

    public void resetText() {
        candidates = new LinkedList<>();
        textGraph = new WordsGraph();
    }

    public void analize() {
        buidTextWordScores();
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

    private void buidCorpusWordScores() {
        corpusWordScores = calculateWordScores(corpusGraph);
    }

    private void buidTextWordScores() {
        if (corpusGraph == null) {
            /* if corpus is not existent score as single text, otherwise is a relative scoring */
            textWordScores = calculateWordScores(textGraph);
        } else {
            buidCorpusWordScores();
            textWordScores = new HashMap<>();
            /* relative scoring vs corpus */
            List<String> unknownWords = new LinkedList<>();
            Map<String, Double> relFreqs = textGraph.getWordRelativeFreqs();
            for(Map.Entry<String, WordsGraph.WordData> e : textGraph.getAllEntries()) {
                String word = e.getKey();
                WordsGraph.WordData data = e.getValue();
                if (corpusWordScores.containsKey(word) && corpusWordScores.get(word) != 0.0) {
                    double thisWordScore = calcScore(strategy, data, relFreqs.get(word), true);
                    if (!strategy.equals("ENTROPY")) {
                        double corpusScore = corpusWordScores.get(word);
                        if (corpusScore > Double.MIN_VALUE) {
                            textWordScores.put(word, thisWordScore / corpusScore);
                        }

                    } else {
                        /* entropy is additive */
                        double currentEntropy = textWordScores.getOrDefault(word, 0.0);
                        currentEntropy += thisWordScore;
                        textWordScores.put(word, currentEntropy);
                    }
                } else {
                    unknownWords.add(word);
                }
            }

            /* normalize unknown words */
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for(Double score : textWordScores.values()) {
                if (score > max) max = score;
                if (score < min) min = score;
            }
            double scoreUnkown = min + (max - min) * UNKNOWN_SCORE_FACTOR;
            for (String w : unknownWords) {
                textWordScores.put(w, scoreUnkown * textGraph.getWordFreq(w));
            }
        }
    }

    private Map<String, Double> calculateWordScores(WordsGraph graph) {
        Map<String, Double> output = new HashMap<>();
        Map<String, Double> relFreqs = graph.getWordRelativeFreqs();
        for(Map.Entry<String, WordsGraph.WordData> e : graph.getAllEntries()) {
            String word = e.getKey();
            WordsGraph.WordData data = e.getValue();
            output.put(word, calcScore(strategy, data, relFreqs.get(word), false));
        }
        return output;
    }

    private double calcScore(String strategy, WordsGraph.WordData data, double relFreq, boolean isCorpus) {
        double score = 0.0;
        switch ( strategy ) {

            case "RELATIVE_DEGREE" :
                score = (double) (data.weightedInDegree + data.weightedOutDegree) / relFreq;
                break;

            case "WEIGHTED_DEGREE" :
                score = (double) data.weightedInDegree + data.weightedOutDegree;
                break;

            case "DEGREE" :
                score = (double) data.inDegree + data.outDegree;
                break;

            case "FREQUENCY" :
                score = relFreq;
                break;

            case "ENTROPY" :
                score = relFreq;
                score = - score * Math.log(score);
                if (!isCorpus) score *= data.frequency;
                break;
        }
        return score;
    }

    public List<ScoredWord> getKeywords(int n) {

        buidTextWordScores();
        Queue<ScoredWord> scoredCandidates = new PriorityQueue<>();

        Set<String> setOfCandidates = new HashSet<>(candidates); // eliminate duplicates
        for(String candidate : setOfCandidates) {
            double candidateScore = scoreSentence(candidate);
            scoredCandidates.add(new ScoredWord(candidate, candidateScore));
        }

        List<ScoredWord> output = new ArrayList<>(n);
        for(int i = 0; i < n; i++) {
            if (!scoredCandidates.isEmpty()) {
                output.add(scoredCandidates.remove());
            }
        }

        return output;
    }

    public List<ScoredWord> getKeyWordsSingle(int n) {

        Queue<ScoredWord> scoredCandidates = new PriorityQueue<>();
        buidTextWordScores();
        for (Map.Entry<String, WordsGraph.WordData> e : textGraph.getAllEntries()) {
            String word = e.getKey();
            scoredCandidates.add(new ScoredWord(word, textWordScores.get(word)));
        }

        List<ScoredWord> output = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            if (scoredCandidates.isEmpty()) break;
            output.add(scoredCandidates.remove());
        }

        return output;
    }

    public double scoreSentence(String sentence) {
        if (textWordScores == null) {
            buidTextWordScores();
        }
        sentence = sentence.toLowerCase();
        double score = 0.0;
        String [] words = sentence.split(DEFAULT_WORD_DELIMITER);
        for (String word : words) {
            score += textWordScores.getOrDefault(word, 0.0);
        }
        return score;
    }

    public List<ScoredWord> getAbstract(int n) {
        List<ScoredWord> output = new ArrayList<>();

        Set<String> setOfLines = new HashSet<>(textLines);

        PriorityQueue<ScoredWord> scoredLines = new PriorityQueue<>();

        for(String line : setOfLines) {
            if (line.length() < DEFAULT_MIN_LENGTH) continue;
            double score = scoreSentence(line);
            String capitalizedLine = line.substring(0, 1).toUpperCase() + line.substring(1);
            ScoredWord sc = new ScoredWord(capitalizedLine, score);
            scoredLines.add(sc);
        }

        for(int i = 0; i < 20; i++) {
            if (scoredLines.isEmpty()) {
                break;
            }

           output.add(scoredLines.remove());
        }
        return output;
    }

    public List<String> getListOfSentences(List<String> lines) {
        List<String> output = new LinkedList<>();

        for (String line : lines) {
            line = line.toLowerCase();
            String[] sentences = line.split(regexSplit);
            for (String sentence : sentences) {
                sentence = sentence.trim();
                sentence = sentence.replace('-', ' ');  // split composed words
                if (sentence.length() >= DEFAULT_MIN_LENGTH ) {
                    output.add(sentence);
                }
            }
        }

        return output;
    }
}
