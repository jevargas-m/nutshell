package jevm.nutshell.tests;

import jevm.nutshell.parser.FileWordParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;

class FileParserTest {

    @Test
    void testFileRead() throws FileNotFoundException {
        File f = new File ("res/text_test1.txt");
        FileWordParser fp = new FileWordParser(f);

        String s = fp.nextLine();
        assertEquals("alice looked at the jury box", s.toLowerCase());

        s = fp.nextLine();
        assertEquals("and saw that", s.toLowerCase());

        s = fp.nextLine();
        assertEquals("in her haste", s.toLowerCase());

        s = fp.nextLine();
        assertEquals("she had put the lizard in", s.toLowerCase());

        s = fp.nextLine();
        assertEquals("head downwards", s.toLowerCase());

        s = fp.nextLine();
        assertEquals("and the poor little thing was waving its tail about in a melancholy way", s.toLowerCase());
    }

}