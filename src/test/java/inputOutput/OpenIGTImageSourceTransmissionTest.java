package inputOutput;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * This JUnit test is for testing if the class OpenIGTImageSource can
 * convey a frame as a matrix from OpenIGTLink (with MITK).
 * Before running this test, a prior test with the "TestOpenIGTImageSourceConnection"
 * is necessary, which reveals if the connection works.
 * @author team3
 *
 */
class JUOpenIGTImageSourceTransmission {

    OpenIGTImageSource igtSrc;

	/**
	 * This method is called before each test. It creates an object of the class
	 * OpenIGTImageSource and establishes a connection to OpenIGTLink (with MITK).
	 */
	@BeforeEach
	void open() {
		igtSrc = new OpenIGTImageSource();
		igtSrc.openConnection();
	}
	
	/**
	 * This method is testing if a matrix is returned after the transmission.
	 */
	@Test
	void TestGetNextMat() {
		assertNotNull(igtSrc.getNextMat());
	}
	
	/**
	 * This method is called after each test. It is for closing the connection.
	 */
	@AfterEach
	void close() {
		igtSrc.closeConnection();
	}

}
