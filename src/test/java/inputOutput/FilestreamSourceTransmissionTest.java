package inputOutput;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * This JUnit test is for testing if the class FilestreamSource can convey a frame as a matrix from a selected video file.
 * Before running this test, a prior test with the "TestFilestreamSourceConnection" is necessary, which reveals
 * if the connection works.
 * @author team3
 *
 */
class FilestreamSourceTransmissionTest {
    FilestreamSource filestream;
    String filepath;
    String seperator = File.separator;

    /**
     * This method is called before each test. It creates an object of the class FilestreamSource and establishes a connection to the selected video file.
     */
    @BeforeEach
    void open() {
        String path = System.getProperty("user.dir");
        filepath = path + seperator + "src" + seperator + "test" + seperator + "resources" + seperator + "US-Video1.avi";
        filestream = new FilestreamSource(filepath);
        filestream.openConnection();
    }

    /**
     * This method is testing if a matrix is returned after the transmission.
     */
    @Test
    void TestGetNextMat() {
        assertNotNull(filestream.getNextMat());
    }

    /**
     * This method is called after each test. It is for closing the connection.
     */
    @AfterEach
    void close() {
        filestream.closeConnection();
    }

}
