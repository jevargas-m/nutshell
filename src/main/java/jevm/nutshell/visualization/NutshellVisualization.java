package jevm.nutshell.visualization;

import jevm.nutshell.engine.ScoredWord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutshellVisualization {

    public int MAX_VALUE = 100;
    public int MIN_VALUE = 10;
    public String OUTPUT_FILENAME = "gword.js";

    private List<ScoredWord> data;
    private Map<String, Integer> normalizedData;

    public NutshellVisualization(List<ScoredWord> data) {
        this.data = data;
        normalize();
    }

    private void normalize() {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (ScoredWord sw : data) {
            if (sw.score > max) max = sw.score;
            if (sw.score < min) min = sw.score;
        }

        double m = (MAX_VALUE - MIN_VALUE) / (max - min);
        double b = MAX_VALUE - m * max;

        normalizedData = new HashMap<>();
        for (ScoredWord sw : data) {
            int score = (int) ((m * sw.score) + b);
            normalizedData.put(sw.word, score );
        }
    }


    public void createJSONFile() throws FileNotFoundException {
        File f = new File(OUTPUT_FILENAME);
        PrintWriter pw = new PrintWriter(f);

        JSONArray list = new JSONArray();

        for (String word : normalizedData.keySet()) {
            JSONObject obj = new JSONObject();
            Integer score = normalizedData.get(word);
            obj.put("text", word);
            obj.put("size", score);
            list.add(obj);
        }

        pw.print("gword = ");
        pw.print(list.toJSONString());
        pw.close();
    }


}
