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
        assertEquals("alice looked at the jury box", s);

        s = fp.nextLine();
        assertEquals("and saw that", s);

        s = fp.nextLine();
        assertEquals("in her haste", s);

        s = fp.nextLine();
        assertEquals("she had put the lizard in", s);

        s = fp.nextLine();
        assertEquals("head downwards", s);

        s = fp.nextLine();
        assertEquals("and the poor little thing was waving its tail about in a melancholy way", s);
    }

}