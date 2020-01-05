package jevm.nutshell.engine;

import jevm.nutshell.parser.FileParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordsGraphTest {

    @Test
    void testGraphConstruction1() throws FileNotFoundException {
        File f = new File ("res/text_test2.txt");
        FileParser fp = new FileParser(f);
        WordsGraph graph = new WordsGraph();

        while (fp.hasNext()) {
            String sentence = fp.nextLine();
            String[] words = sentence.split(" ");

            for (int i = 0; i < words.length - 1; i++) {
                graph.addEdge(words[i], words[i + 1]);
            }
            graph.addWord(words[words.length - 1]);
        }

        for(String word : graph.adjacencySets.keySet()) {
            System.out.print(word + " freq = ");
            WordsGraph.WordData data = graph.adjacencySets.get(word);
            System.out.println(data.frequency + " in = " + data.inDegree + " out = " + data.outDegree);

        }

        System.out.println();
        for(String word : graph.adjacencySets.keySet()) {
            System.out.print(word + " {");
            WordsGraph.WordData data = graph.adjacencySets.get(word);
            for(WordsGraph.Edge e : data.weightedEdges.keySet()) {
                System.out.print("," + e.source + "-" + e.destination + "(" + data.weightedEdges.get(e) + ")");
            }
            System.out.println(" }");
        }
    }

    @Test
    void testGraphConstruction2() throws FileNotFoundException {
        File f = new File ("res/alice.txt");
        FileParser fp = new FileParser(f);
        WordsGraph graph = new WordsGraph();

        while (fp.hasNext()) {
            graph.addSentence(fp.nextLine(), " ");
        }

        System.out.println("Alice word count = " + graph.getNumWords());
        System.out.println("Alice edge count = " + graph.getNumEdges());
    }

    @Test
    void testAddSentence() {
        String sentence = "Mary has a little lamp, Mary has a big lamp, Mary is a happy girl.";
        WordsGraph g = new WordsGraph();
        for(String s : sentence.split(",.")) {
            g.addSentence(s, " ");
        }

        assertEquals(9, g.getNumWords());
        assertEquals(10, g.numEdges);
        assertEquals(3, g.getWordFreq("Mary"));
        assertEquals(2, g.getWordOutDegree("Mary"));
        assertEquals(0, g.getWordInDegree("Mary"));
    }

    @Test
    void testGetEdgeWeight() {
        String sentence = "Mary has a little lamp, Mary has a big lamp, Mary is a happy girl.";
        WordsGraph g = new WordsGraph();
        for(String s : sentence.split(",.")) {
            g.addSentence(s, " ");
        }
        assertEquals(2, g.getEdgeWeight("Mary", "has"));
        assertEquals(1, g.getEdgeWeight("a", "big"));
        assertEquals(-1, g.getEdgeWeight("girl", "lamb"));
        assertEquals(-1, g.getEdgeWeight("Mary", "bananas"));
        assertEquals(-1, g.getEdgeWeight("bananas", "has"));
    }

    @Test
    void testSortFreq() {
        String sentence = "Mary has a little lamp, Mary has a big lamp, Mary is a happy girl.";
        WordsGraph g = new WordsGraph();
        for(String s : sentence.split(",.")) {
            g.addSentence(s, " ");
        }
        List<Map.Entry<String, WordsGraph.WordData>> l = g.getSortedList(WordsGraph.compareByWordFrequency());
        for (Map.Entry<String, WordsGraph.WordData> e : l){
            System.out.println(e.getKey() + " " + e.getValue().frequency);
        }
    }


    @Test
    void testAliceTopFreqs() throws FileNotFoundException {
        File f = new File ("res/alice.txt");
        WordsGraph graph = new WordsGraph(new FileParser(f));

        List<Map.Entry<String, WordsGraph.WordData>> l = graph.getSortedList(WordsGraph.compareByWordFrequency());
        for (int i = 0; i < 100; i++){
            System.out.println(l.get(i).getKey() + " " + l.get(i).getValue().frequency);
        }
    }

    @Test
    void testAllEdges() {
        String sentence = "Mary has a little lamp, Mary has a big lamp, Mary is a happy girl.";
        WordsGraph g = new WordsGraph();
        for(String s : sentence.split("[,.]")) {
            g.addSentence(s);
        }
        Map<WordsGraph.Edge, Integer> map = g.getAllWeightedEdges();
        for (WordsGraph.Edge e : map.keySet()) {
            System.out.println(e + "(" + map.get(e) + ")");
        }
    }
}