package jevm.nutshell.tests;

import jevm.nutshell.parser.FileParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;

class FileParserTest {

    @Test
    void testFileRead() throws FileNotFoundException {
        File f = new File ("res/text_test1.txt");
        FileParser fp = new FileParser(f);

        String s = fp.nextSentence();
        assertEquals("alice looked at the jury box", s);

        s = fp.nextSentence();
        assertEquals("and saw that", s);

        s = fp.nextSentence();
        assertEquals("in her haste", s);

        s = fp.nextSentence();
        assertEquals("she had put the lizard in", s);

        s = fp.nextSentence();
        assertEquals("head downwards", s);

        s = fp.nextSentence();
        assertEquals("and the poor little thing was waving its tail about in a melancholy way", s);
    }

}