package jevm.nutshell.visualization;

import jevm.nutshell.engine.ScoredWord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;

public class NutshellVisualization {

    public static final int DEFAULT_MAX_SIZE = 100;
    public static final int DEFAULT_MIN_SIZE = 20;

    private int maxSize = DEFAULT_MAX_SIZE;
    private int minSize = DEFAULT_MIN_SIZE;

    private Map<String, Map<String, Integer>> normalizedData;

    public NutshellVisualization() {
        normalizedData = new HashMap<>();
    }

    public void addDataSet(String name, List<ScoredWord> data) {
        normalizedData.put(name, normalize(data));
    }

    private Map<String, Integer> normalize(List<ScoredWord> data) {
        Map<String, Integer> output = new HashMap<>();

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (ScoredWord sw : data) {
            if (sw.score > max) max = sw.score;
            if (sw.score < min) min = sw.score;
        }

        double m = (maxSize - minSize) / (max - min);
        double b = maxSize - m * max;

        for (ScoredWord sw : data) {
            int score = (int) ((m * sw.score) + b);
            output.put(sw.word, score );
        }
        return output;
    }

    private String createJSONString(Map<String, Integer> dataMap) {

        JSONArray list = new JSONArray();

        for (String word : dataMap.keySet()) {
            JSONObject obj = new JSONObject();
            Integer score = dataMap.get(word);
            obj.put("text", word);
            obj.put("size", score);
            list.add(obj);
        }

        return list.toJSONString();
    }




    public void createWordCloud(String filename) throws FileNotFoundException {
        File f = new File(filename);
        PrintWriter pw = new PrintWriter(f);

        String html = "<!DOCTYPE html>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.17/d3.js\"></script>\n" +
                "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/d3-cloud/1.2.5/d3.layout.cloud.js\"></script>\n" +
                "<body>\n" +
                "<script>\n" +
                "    var fill = d3.scale.category20();\n" +
                "    function draw(words) {\n" +
                "        d3.select(\"body\").append(\"svg\")\n" +
                "            .attr(\"width\", 1024)\n" +
                "            .attr(\"height\", 800)\n" +
                "            .append(\"g\")\n" +
                "            .attr(\"transform\", \"translate(514, 400)\")\n" +
                "            .selectAll(\"text\")\n" +
                "            .data(words)\n" +
                "            .enter().append(\"text\")\n" +
                "            .style(\"font-size\", function(d) { return d.size + \"px\"; })\n" +
                "            .style(\"font-family\", \"Impact\")\n" +
                "            .style(\"fill\", function(d, i) { return fill(i); })\n" +
                "            .attr(\"text-anchor\", \"middle\")\n" +
                "            .attr(\"transform\", function(d) {\n" +
                "                return \"translate(\" + [d.x, d.y] + \")rotate(\" + d.rotate + \")\";\n" +
                "            })\n" +
                "            .text(function(d) { return d.text; });\n" +
                "    }\n" +
                "</script>\n";
        pw.print(html);

        for (String dataName : normalizedData.keySet()) {
            String jsonData = createJSONString(normalizedData.get(dataName));
            String htmlData =
                    "<html><h2>"  + dataName + "</h2></html>\n" +
                    "<script>\n" +
                    "    d3.layout.cloud().size([1024, 800])\n" +
                    "        .words("   + jsonData +    ")\n" +
                    "        .rotate(function() { return ~~(Math.random() * 2) * 90; })\n" +
                    "        .font(\"Impact\")\n" +
                    "        .fontSize(function(d) { return d.size; })\n" +
                    "        .on(\"end\", draw)\n" +
                    "        .start();\n" +
                    "\n" +
                    "</script>\n";
            pw.print(htmlData);
        }
        pw.close();
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }
}
