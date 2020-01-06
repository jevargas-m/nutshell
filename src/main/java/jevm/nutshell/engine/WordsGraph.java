package jevm.nutshell.engine;

import jevm.nutshell.parser.Parser;

import java.util.*;

public class WordsGraph {
    public static final String DEFAULT_WORD_DELIMITER = "\\s";

    protected Map<String, WordData> adjacencySets;
    protected int numEdges = 0;
    protected int numWeightedEdges = 0;

    public WordsGraph() {
        adjacencySets = new HashMap<>();
    }

    public WordsGraph(Parser parser) {
        this(parser, DEFAULT_WORD_DELIMITER);
    }

    public WordsGraph(Parser parser, String wordDelimiter) {
        this();
        addAll(parser, wordDelimiter);
    }

    public void addAll(Parser parser, String wordDelimiter) {
        while (parser.hasNext()) {
            addSentence(parser.nextLine(), wordDelimiter);
        }
    }

    public void addAll(Collection<String> collection, String delim) {
        for(String sentence : collection) {
            addSentence(sentence, delim);
        }
    }

    public void addAll(Collection<String> collection) {
        addAll(collection, DEFAULT_WORD_DELIMITER);
    }


    public class WordData {
        Map<Edge, Integer> weightedEdges = new HashMap<>(); // Weighted Edge
        int frequency = 0;
        int inDegree = 0;
        int outDegree = 0;
        int weightedInDegree = 0;
        int weightedOutDegree = 0;
    }

    public class Edge {
        public String source;
        public String destination;

        public Edge(String source, String destination) {
            this.source = source;
            this.destination = destination;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Edge)) return false;
            Edge other = (Edge) obj;

            return this.source.equals(other.source) &&
                    this.destination.equals(other.destination);
        }

        @Override
        public int hashCode() {
            return source.hashCode() + destination.hashCode();
        }

        @Override
        public String toString() {
            return source + "->" + destination;
        }
    }

    public void addWord(String word) {
        if (word == null || word.equals("")) return;
        WordData data = adjacencySets.getOrDefault(word, new WordData());;
        data.frequency++;
        adjacencySets.put(word, data);
    }

    // Only from frequency is updated
    public void addEdge(String from, String to) {
        if (from == null || from.equals("")) return;

        WordData fromData = adjacencySets.getOrDefault(from, new WordData());
        WordData toData = adjacencySets.getOrDefault(to, new WordData());

        Edge newEdge = new Edge(from, to);

        if (fromData.weightedEdges.containsKey(newEdge)) {
            Integer frequency = fromData.weightedEdges.get(newEdge);
            frequency++;  // update edge weight
            fromData.weightedEdges.put(newEdge, frequency);
            fromData.weightedInDegree++;
            toData.weightedOutDegree++;
            numWeightedEdges++;
            adjacencySets.put(to, toData);

        } else if (to != null && !to.equals("")){
            // if edge is new, update degrees and add to set
            fromData.weightedInDegree++;
            toData.weightedOutDegree++;
            fromData.outDegree++;
            toData.inDegree++;
            numEdges++;
            numWeightedEdges++;
            fromData.weightedEdges.put(newEdge, 1); //add new edge to set
            adjacencySets.put(to, toData);
        }

        fromData.frequency++; // freq only updated in from to avoid double counting
        adjacencySets.put(from, fromData);
    }

    public void addSentence(String sentence) {
        addSentence(sentence, DEFAULT_WORD_DELIMITER);
    }

    public void addSentence(String sentence, String delim) {
        String[] words = sentence.split(delim);
        if (words.length == 0) return;

        for (int i = 0; i < words.length - 1; i++) {
            addEdge(words[i], words[i + 1]);
        }
        addWord(words[words.length - 1]);
    }

    public List<Map.Entry<String, WordData>> getSortedList(Comparator<Map.Entry<String, WordData>> comparator) {
        List<Map.Entry<String, WordData>> entries = new ArrayList<>(adjacencySets.entrySet());

        entries.sort(comparator);
        return entries;
    }

    public int getWordFreq(String s) {
        if (!adjacencySets.containsKey(s)) {
            return -1;
        } else {
            return adjacencySets.get(s).frequency;
        }
    }

    public int getWordOutDegree(String s) {
        if (!adjacencySets.containsKey(s)) {
            return -1;
        } else {
            return adjacencySets.get(s).outDegree;
        }
    }

    public int getWordInDegree(String s) {
        if (!adjacencySets.containsKey(s)) {
            return -1;
        } else {
            return adjacencySets.get(s).inDegree;
        }
    }

    public static Comparator<Map.Entry<String, WordData>> compareByWordFrequency () {
        return new Comparator<Map.Entry<String, WordData>>() {
            @Override
            public int compare(Map.Entry<String, WordData> w1, Map.Entry<String, WordData> w2) {
                int diff = w2.getValue().frequency - w1.getValue().frequency;
                if (diff != 0) {
                    return diff;
                } else {
                    return w1.getKey().compareTo(w2.getKey());
                }

            }
        };
    }

    public Integer getEdgeWeight(String from, String to) {
        Edge e = new Edge(from, to);
        if (!adjacencySets.containsKey(from) || !adjacencySets.containsKey(to)) return -1;

        Map<Edge, Integer> edges = adjacencySets.get(from).weightedEdges;
        return (edges.getOrDefault(e, -1));
    }

    public Map<String, WordData> getAdjacencySets() {
        return adjacencySets;
    }

    public int getNumWords() {
        return adjacencySets.size();
    }

    public int getNumEdges() {
        return numEdges;
    }

    public List<Map.Entry<String, WordData>> getAllEntries() {
        return new ArrayList<>(adjacencySets.entrySet());
    }

    public Map<Edge, Integer> getAllWeightedEdges() {
        Map<Edge, Integer> output = new HashMap<>();
        for(Map.Entry<String, WordData> entry : adjacencySets.entrySet()) {
            Map<Edge, Integer> entryEdges = entry.getValue().weightedEdges;

            for (Edge edge : entryEdges.keySet()) {
                output.put(edge, entryEdges.get(edge));
            }
        }
        return output;
    }


}
