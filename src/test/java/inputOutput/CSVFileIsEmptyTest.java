package inputOutput;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class CSVFileIsEmptyTest {

    @Test
    public void updateTest() throws IOException {
        CSVFileReader myReader = new CSVFileReader(new BufferedReader(new StringReader("")));
        ArrayList<Tool> result = myReader.update();
        Assert.assertTrue(result.isEmpty());
    }
}
