package inputOutput;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilestreamSourceConnectionTest {

    String filepath = "src/test/resources/US-Video1.wmv";

    /**
     * Adjust file path, if test is executed on linux.
     */
    @BeforeEach
    void setFilepath() {
        String os = System.getProperty("os.name");
        if(os.contains("Linux")) {
            filepath = "/".concat(filepath);
        }
    }

    /**
     * This method tests instantiating the class FilestreamSource.
     */
    @Test
    void testObject() {
        FilestreamSource filestream = new FilestreamSource(filepath);
        assertTrue(filestream!=null);
    }

    /**
     * This method tests if the connection to the device can be established.
     * The connection can only work if the instantiating was successful.
     */
    @Test
    void testOpenConnection() {
        FilestreamSource filestream = new FilestreamSource(filepath);
        assertTrue(filestream.openConnection());
    }

    /**
     * This method tests the closing of an existing connection.
     * Prior to that it is important to ensure that opening the connection is working.
     */
    @Test
    void testCloseConnection() {
        FilestreamSource filestream = new FilestreamSource(filepath);
        filestream.openConnection();
        assertTrue(filestream.closeConnection());
        }
}
