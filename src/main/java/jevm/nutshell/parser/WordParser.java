package jevm.nutshell.parser;

import java.util.List;

public interface WordParser {
    public boolean hasNext();
    public String nextLine();
    public List<String> getLines();
}
