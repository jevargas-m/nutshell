package jevm.nutshell.engine;

import java.util.List;
import java.util.Map;

public interface  TextAnalyzer {

    public Map<String, Double> getKeywords(int n, String strategy) ;
    public int scoreSentence(String sentence);
    public int scoreWord(String word);


}
