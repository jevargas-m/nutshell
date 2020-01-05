package jevm.nutshell.engine;

import java.util.*;

public class WordsGraph {
    protected Map<String, WordData> adjacencySets = new HashMap<>();
    protected int numEdges = 0;

    protected class WordData {
        Map<Edge, Integer> weightedEdges = new HashMap<>(); // Weighted Edge
        int frequency = 0;
        int inDegree = 0;
        int outDegree = 0;
    }

    protected static class Edge {
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
    }

    public void addWord(String word) {
        if (word == null) return;
        WordData data = adjacencySets.getOrDefault(word, new WordData());;
        data.frequency++;
        adjacencySets.put(word, data);
    }

    public void addWord(String from, String to) {
        if (from == null) return;
        WordData fromData, toData;

        fromData = adjacencySets.getOrDefault(from, new WordData());
        toData = adjacencySets.getOrDefault(to, new WordData());

        fromData.frequency++;
        Edge newEdge = new Edge(from, to);

        if (fromData.weightedEdges.containsKey(newEdge)) {
            Integer frequency = fromData.weightedEdges.get(newEdge);
            frequency++;  // update edge weight
            fromData.weightedEdges.put(newEdge, frequency);
        } else {
            // if edge is new, update degrees and add to set
            fromData.outDegree++;
            toData.inDegree++;
            numEdges++;
            fromData.weightedEdges.put(newEdge, 1); //add edge to set
        }

        if (to != null) {
            adjacencySets.put(to, toData);
        }
        
        adjacencySets.put(from, fromData);
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


}
