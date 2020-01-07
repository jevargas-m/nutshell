
package jevm.nutshell.engine;

public class ScoredWord implements Comparable {

    public String word;
    public double score;

    public ScoredWord(String word, double score) {
        this.word = word;
        this.score = score;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ScoredWord)) throw new IllegalArgumentException();
        ScoredWord other = (ScoredWord) o;
        return Double.compare(other.score, this.score);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScoredWord)) return false;
        ScoredWord other = (ScoredWord) obj;
        return this.score == other.score && this.word.equals(other.word);
    }

    @Override
    public String toString() {
        return (word + " " + Double.toString(score));
    }
}
