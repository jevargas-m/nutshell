package jevm.nutshell.engine;

import jevm.nutshell.parser.FileParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WordsGraphTest {

    @Test
    void testGraphConstruction1() throws FileNotFoundException {
        File f = new File ("res/text_test2.txt");
        FileParser fp = new FileParser(f);
        WordsGraph graph = new WordsGraph();

        while (fp.hasNext()) {
            String sentence = fp.nextSentence();
            String[] words = sentence.split(" ");

            for (int i = 0; i < words.length - 1; i++) {
                graph.addWord(words[i], words[i + 1]);
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
}