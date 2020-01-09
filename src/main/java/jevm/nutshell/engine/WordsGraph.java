package jevm.nutshell.engine;

import java.util.*;

/**
 * Weighted directed graph of words for text analysis, each word maintains
 * its frequency, and each edge also maintain its frequency
 */
public class WordsGraph {
    public static final String DEFAULT_WORD_DELIMITER = "\\s";

    protected Map<String, WordData> adjacencySets;
    protected int numEdges = 0;
    protected int numWeightedEdges = 0;

    public WordsGraph() {
        adjacencySets = new HashMap<>();
    }

    /**
     * Add a collection of strings to the graph, each string may contain several
     * words in which case each it is split as per the supplied regex
     * @param collection
     * @param wordDelimiter regex to use for split words
     */
    public void addAll(Collection<String> collection, String wordDelimiter) {
        for(String sentence : collection) {
            addString(sentence, wordDelimiter);
        }
    }

    /**
     * Add a collection of strings to the graph, each string may contain several
     * words in which case each it is split as per  default value
     * WordsGraph.DEFAULT_WORD_DELIMITER
     * @param collection
     */
    public void addAll(Collection<String> collection) {
        addAll(collection, DEFAULT_WORD_DELIMITER);
    }

    /**
     * Data kept for each node word
     */
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

    /**
     * A a singhle word to the graph
     * @param word
     */
    public void addWord(String word) {
        if (word == null || word.equals("")) return;
        WordData data = adjacencySets.getOrDefault(word, new WordData());;
        data.frequency++;
        adjacencySets.put(word, data);
    }

    /**
     * Add a new edge to the graph, if either word is not in the graph it
     * will be added first.  Only the frequency of the first word is updated
     * so addWord(to) needs to be called afterwards.
      * @param from
     * @param to
     */
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

    /**
     * Add a new string which may contain several words in which case each it is split
     * as per default value WordsGraph.DEFAULT_WORD_DELIMITER
     * @param s
     */
    public void addString(String s) {
        addString(s, DEFAULT_WORD_DELIMITER);
    }

    /**
     * Add a new string which may contain several words in which case each it is split
     * as per the supplied regex
     * @param s
     * @param delim regex used for split
     */
    public void addString(String s, String delim) {
        String[] words = s.split(delim);
        if (words.length == 0) return;

        for (int i = 0; i < words.length - 1; i++) {
            addEdge(words[i], words[i + 1]);
        }
        addWord(words[words.length - 1]);
    }

    /**
     * Get a sorted list of all words in the graph as map entries with their corresponding data
     * @param comparator
     * @return
     */
    public List<Map.Entry<String, WordData>> getSortedList(Comparator<Map.Entry<String, WordData>> comparator) {
        List<Map.Entry<String, WordData>> entries = new ArrayList<>(adjacencySets.entrySet());

        entries.sort(comparator);
        return entries;
    }

    /**
     * Get frequency of a word.
     * If word is not in the graph returns -1
     * @param s
     * @return
     */
    public int getWordFreq(String s) {
        if (!adjacencySets.containsKey(s)) {
            return -1;
        } else {
            return adjacencySets.get(s).frequency;
        }
    }

    /**
     * Get word out-degree of unique edges going out
     * If word is not in the graph returns -1
     * @param s
     * @return
     */
    public int getWordOutDegree(String s) {
        if (!adjacencySets.containsKey(s)) {
            return -1;
        } else {
            return adjacencySets.get(s).outDegree;
        }
    }

    /**
     * Get word in-degree of unique edges going out
     * If word is not in the graph returns -1
     * @param s
     * @return
     */
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

    /**
     * Get the frequency this edge has been added to the graph
     * If edge is not in the graph returns -1
     * @param from
     * @param to
     * @return
     */
    public Integer getEdgeWeight(String from, String to) {
        Edge e = new Edge(from, to);
        if (!adjacencySets.containsKey(from) || !adjacencySets.containsKey(to)) return -1;

        Map<Edge, Integer> edges = adjacencySets.get(from).weightedEdges;
        return (edges.getOrDefault(e, -1));
    }

    /**
     * Total unique node words in the graph
     * @return
     */
    public int getNumWords() {
        return adjacencySets.size();
    }

    /**
     * total unique edges in the graph
     * @return
     */
    public int getNumEdges() {
        return numEdges;
    }

    /**
     * List of all entries in the graph
     * @return
     */
    public List<Map.Entry<String, WordData>> getAllEntries() {
        return new ArrayList<>(adjacencySets.entrySet());
    }

    /**
     * Get all edges in the graph with their corresponding weight
     * @return
     */
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

    /**
     * Calculate relative frequency of all words in the graph
     * relative frequency = node word frequency / sum of all node word frequencies
     * @return
     */
    public Map<String, Double> getWordRelativeFreqs() {
        Map<String, Double> output = new HashMap<>();

        int total = 0;
        for (Map.Entry<String, WordData> e : adjacencySets.entrySet()) {
            total += e.getValue().frequency;
        }

        for (Map.Entry<String, WordData> e : adjacencySets.entrySet()) {
            Double relFreq = (double) e.getValue().frequency / total;
            output.put(e.getKey(), relFreq);
        }

        return output;
    }


}
