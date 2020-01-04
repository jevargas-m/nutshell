package jevm.nutshell.engine;

import java.util.*;

public class WordsGraph {
    protected Map<String, WordData> adjacencySets = new HashMap<>();
    protected int numWords = 0;
    protected int numEdges = 0;

    protected class WordData {
        Set<Edge> setOfEdges = new HashSet<>();
        int frequency = 0;
        int inDegree = 0;
        int outDegree = 0;
    }

    protected class Edge {
        public String source;
        public String destination;
        public int frequency;

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

    public void addWord(String from, String to) {
        if (from == null) return;
        WordData fromData, toData;

        fromData = adjacencySets.getOrDefault(from, new WordData());
        toData = adjacencySets.getOrDefault(to, new WordData());

        /* update word frequencies */
        fromData.frequency++;
        toData.frequency++;
        Edge newEdge = new Edge(from, to);
        if (fromData.setOfEdges.contains(newEdge)) {
            newEdge.frequency++;  // update edge weight
        } else {
            // if edge is new, update degrees and add to set
            fromData.outDegree++;
            toData.inDegree++;
            fromData.setOfEdges.remove(newEdge); // eliminate old edge
        }

        if (to != null) {
            adjacencySets.put(to, toData);
            numWords++;
            numEdges++;
            fromData.setOfEdges.add(newEdge); //add edge to set
        }
        adjacencySets.put(from, fromData);
        numWords++;
    }

    public Map<String, WordData> getAdjacencySets() {
        return adjacencySets;
    }

    public int getNumWords() {
        return numWords;
    }

    public int getNumEdges() {
        return numEdges;
    }


}
