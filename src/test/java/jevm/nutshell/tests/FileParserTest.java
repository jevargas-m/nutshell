package jevm.nutshell.tests;

import jevm.nutshell.parser.FileParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

class FileParserTest {

    @Test
    void testFileRead() throws FileNotFoundException {
        File f = new File ("res/text_test1.txt");
        FileParser fp = new FileParser(f);

        for (int i = 0; i < 10; i++) {
            String s1 = fp.nextSentence();
            System.out.println(i + " " + s1);
        }







    }

}