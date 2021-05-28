package inputOutput;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OpenIGTImageSourceConnectionTest {

	/**
	 * This method tests instantiating the class OpenIGTImageSourceSource.
	 */
	@Test
	void testObject() {
		OpenIGTImageSource igtSrc = new OpenIGTImageSource(); 
		assertTrue(igtSrc!=null);
	}
	
	/**
	 * This method tests if the connection to OpenIGTLink (with MITK) can be established.
	 * The connection can only work if the instantiating was successful.
	 */
	@Test
	void testOpenConnection() {
		OpenIGTImageSource igtSrc = new OpenIGTImageSource();
		assertTrue(igtSrc.openConnection());
	}
	
	/**
	 * This method tests the closing of an existing connection.
	 * Prior to that it is important to ensure that opening the connection is working.
	 */
	@Test
	void testCloseConnection() {
		OpenIGTImageSource igtSrc = new OpenIGTImageSource();
		igtSrc.openConnection();
		assertTrue(igtSrc.closeConnection());
	}

}
