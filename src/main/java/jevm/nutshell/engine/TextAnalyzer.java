package jevm.nutshell.engine;


import jevm.nutshell.data.ScoredWord;

import java.util.*;

/**
 * Text analyzer using different scoring methods
 */
public class TextAnalyzer {

    public static final String[] scoringOptions = {"DEGREE", "WEIGHTED_DEGREE", "ENTROPY", "RELATIVE_DEGREE", "FREQUENCY"};

    public static final String DEFAULT_WORD_DELIMITER = "\\s";
    private static final int DEFAULT_MIN_LENGTH = 3;
    public static double UNKNOWN_SCORE_FACTOR = 1.3;

    private List<String> textLines = new LinkedList<>();
    private List<String> corpusLines = new LinkedList<>();
    private List<String> candidates = new LinkedList<>();
    private List<String> stopWords;
    private WordsGraph textGraph, corpusGraph;
    private Map<String, Double> textWordScores, corpusWordScores;
    private String regexSplit, scoringMethod;

    /**
     * Create a new analyzer, if corpus is added analysis is differencial of text vs corpus
     * if is not added analysis is of isolated file
     * @param stopWords list of stopwords, ignored for defining context
     * @param scoring  scoring method, available options in TextAnalyzer.scoringOptions
     */
    public TextAnalyzer(List<String> stopWords, String scoring ) {
        this.stopWords = stopWords;
        textGraph = new WordsGraph();
        corpusGraph = null; // null until one is add to flag analysis without corpus
        this.scoringMethod = scoring ;
        buildRegexSplit();
    }

    /**
     * Add a new list of raw strings to use as corpus, may be called several times in
     * which corpus keeps growing
     * @param lines
     */
    public void addCorpus(List<String> lines) {
        if (corpusGraph == null) {
            corpusGraph = new WordsGraph();
        }
        corpusLines.addAll(lines);
        corpusGraph.addAll(getKeywordCandidates(lines));
    }

    /**
     * Add a new list of raw strings to use as text under analysis, may be called
     * several times in which text keeps growing
     * @param lines
     */
    public void addText(List<String> lines) {
        textLines.addAll(lines);
        candidates = getKeywordCandidates(lines);
        textGraph.addAll(candidates);
    }

    /**
     * Delete text under analysis maintaining corpus
     */
    public void resetText() {
        candidates = new LinkedList<>();
        textGraph = new WordsGraph();
    }

    /**
     * Score text words
     */
    public void analize() {
        buidTextWordScores();
    }

    /**
     * Create a sting of regex with all the stop words to use on a split method
     * TODO: improve with a full developed parser, if list is too large may fail and/or be inefficient
     */
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
                    double thisWordScore = calcScore(scoringMethod, data, relFreqs.get(word), true);
                    if (!scoringMethod.equals("ENTROPY")) {
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
            normalizeUnknowns(unknownWords, UNKNOWN_SCORE_FACTOR);
        }
    }

    /**
     * give a normalized score to all unknown words in the list and add to
     * textWordScores
     * @param unknownWords
     */
    private void normalizeUnknowns(List<String> unknownWords, double factor) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for(Double score : textWordScores.values()) {
            if (score > max) max = score;
            if (score < min) min = score;
        }
        double scoreUnkown = min + (max - min) * factor;
        for (String w : unknownWords) {
            textWordScores.put(w, scoreUnkown * textGraph.getWordFreq(w));
        }
    }

    private Map<String, Double> calculateWordScores(WordsGraph graph) {
        Map<String, Double> output = new HashMap<>();
        Map<String, Double> relFreqs = graph.getWordRelativeFreqs();
        for(Map.Entry<String, WordsGraph.WordData> e : graph.getAllEntries()) {
            String word = e.getKey();
            WordsGraph.WordData data = e.getValue();
            output.put(word, calcScore(scoringMethod, data, relFreqs.get(word), false));
        }
        return output;
    }

    private double calcScore(String strategy, WordsGraph.WordData data, double relFreq, boolean isCorpus) {
        double score = 0.0;
        switch ( strategy ) {

            case "RELATIVE_DEGREE" :
                score = (double) (data.weightedInDegree + data.weightedOutDegree) / data.frequency;
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

    /**
     * Get a list of keywords sorted descending on score, keywords may be multi-word
     * depending on scoring results.
     * @param n number of keywords to generate
     * @return
     */
    public List<ScoredWord> getKeywords(int n) {

        buidTextWordScores();

        /* additive scoring to account candidate frequency */
        Map <String, Double> scoredCandidates = new HashMap<>();
        for(String candidate : candidates) {
            double score = scoredCandidates.getOrDefault(candidate, 0.0) + scoreString(candidate);
            scoredCandidates.put(candidate, score);
        }

        /* use a head to sort from max to min candidate */
        PriorityQueue<ScoredWord> sortedCandidates = new PriorityQueue<>();
        for (String candidate : scoredCandidates.keySet()) {
            sortedCandidates.add(new ScoredWord(candidate, scoredCandidates.get(candidate)));
        }

        /* only output top n candidates */
        List<ScoredWord> output = new ArrayList<>(n);
        for(int i = 0; i < n; i++) {
            if (!sortedCandidates.isEmpty()) {
                output.add(sortedCandidates.remove());
            }
        }

        return output;
    }

    /**
     * Get a list of keywords sorted descending on score, keywords are single word
     * @param n number of keywords to generate
     * @return
     */
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

    /**
     * Score a string of text according to word scores
     * @param s
     * @return
     */
    public double scoreString(String s) {
        if (textWordScores == null) {
            buidTextWordScores();
        }
        s = s.toLowerCase();
        double score = 0.0;
        String [] words = s.split(DEFAULT_WORD_DELIMITER);
        for (String word : words) {
            score += textWordScores.getOrDefault(word, 0.0);
        }
        return score;
    }

    /**
     * Get a list of raw lines (including stop words) delimited only by punctuation
     * sorted by descending score
     * @param n
     * @return
     */
    public List<ScoredWord> getAbstract(int n) {
        List<ScoredWord> output = new ArrayList<>();

        Set<String> setOfLines = new HashSet<>(textLines); //eliminate duplicates

        PriorityQueue<ScoredWord> scoredLines = new PriorityQueue<>();

        for(String line : setOfLines) {
            if (line.length() < DEFAULT_MIN_LENGTH) continue;
            double score = scoreString(line);

            ScoredWord sc = new ScoredWord(line, score);
            scoredLines.add(sc);
        }

        for(int i = 0; i < n; i++) {
            if (scoredLines.isEmpty()) {
                break;
            }

           output.add(scoredLines.remove());
        }
        return output;
    }

    public String getTextAbstract(int n) {
        List<ScoredWord> keyScoredPhrases = getAbstract(n);

        List<String> keyPhrases = new ArrayList<>(n);
        for (ScoredWord sw : keyScoredPhrases) {
            keyPhrases.add(sw.word);
        }

        StringBuilder sb = new StringBuilder();

        for (String line : textLines) {

            if (keyPhrases.contains(line)) {
                String capitalizedLine = line.substring(0, 1).toUpperCase() + line.substring(1);
                sb.append(capitalizedLine);
                sb.append(". ");
            }
        }

        return sb.toString();

    }

    /**
     * Get a list of content candidates split by stopwords and punctuation
     * (works using RAKE methodology)
     * @param lines
     * @return
     */
    public List<String> getKeywordCandidates(List<String> lines) {
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
