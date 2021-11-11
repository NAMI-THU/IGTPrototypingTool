package inputOutput;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FilestreamSourceConnectionTest {

    /**
     * This method tests instantiating the class FilestreamSource.
     */
    @Test
    void testObject() {
        FilestreamSource filestream = new FilestreamSource("src/test/resources/US-Video1.wmv");
        assertNotNull(filestream);
    }

    /**
     * This method tests if the connection to the device can be established.
     * The connection can only work if the instantiating was successful.
     */
    @Test
    void testOpenConnection() {
        FilestreamSource filestream = new FilestreamSource("src/test/resources/US-Video1.wmv");
        assertTrue(filestream.openConnection());
    }

    /**
     * This method tests the closing of an existing connection.
     * Prior to that it is important to ensure that opening the connection is working.
     */
    @Test
    void testCloseConnection() {
        FilestreamSource filestream = new FilestreamSource("src/test/resources/US-Video1.wmv");
        filestream.openConnection();
        assertTrue(filestream.closeConnection());
        }
}
