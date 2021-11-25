package inputOutput;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * This JUnit test is for testing the connection with the class LivestreamSource.
 * As a prerequisite a device is needed to establish the connection,
 * i.e. a webcam or an ultrasound device.
 * @author sahin
 *
 */
class LivestreamConnectionTest {

    /**
     * This method tests instantiating the class LivestreamSource.
     */
    @Test
    void testObject() {
        LivestreamSource livestream = new LivestreamSource(0);
        assertNotNull(livestream);
    }

    /**
     * This method tests if the connection to the device can be established.
     * Here the parameter 0 in the constructor means that the first connected device to the computer is selected.
     * (The device manager can help to identify the order of devices.)
     * The connection can only work if the instantiating was successful.
     */
    @Test
    void testOpenConnection() {
        LivestreamSource livestream = new LivestreamSource(0);
        assertTrue(livestream.openConnection());
    }

    /**
     * This method tests the closing of an existing connection.
     * Prior to that it is important to ensure that opening the connection is working.
     */
    @Test
    void testCloseConnection() {
        LivestreamSource livestream = new LivestreamSource(0);
        livestream.openConnection();
        assertTrue(livestream.closeConnection());
    }
}
