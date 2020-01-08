package jevm.nutshell.parser;

import java.util.List;
import java.util.Set;

public interface WordParser {
    public boolean hasNext();
    public String nextLine();
    public List<String> getListOfSentences(String regexSplit);
    public Set<String> getUniqueLines();
}
