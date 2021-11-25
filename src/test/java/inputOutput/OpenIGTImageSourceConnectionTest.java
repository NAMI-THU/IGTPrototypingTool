package inputOutput;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OpenIGTImageSourceConnectionTest {

    /**
     * This method tests instantiating the class OpenIGTImageSourceSource.
     */
    @Test
    void testObject() {
        OIGTImageSource igtSrc = new OIGTImageSource();
        assertNotNull(igtSrc);
    }

    /**
     * This method tests if the connection to OpenIGTLink (with MITK) can be established.
     * The connection can only work if the instantiating was successful.
     */
    @Test
    void testOpenConnection() {
        OIGTImageSource igtSrc = new OIGTImageSource();
        assertTrue(igtSrc.openConnection());
    }

    /**
     * This method tests the closing of an existing connection.
     * Prior to that it is important to ensure that opening the connection is working.
     */
    @Test
    void testCloseConnection() {
        OIGTImageSource igtSrc = new OIGTImageSource();
        igtSrc.openConnection();
        assertTrue(igtSrc.closeConnection());
    }

}
