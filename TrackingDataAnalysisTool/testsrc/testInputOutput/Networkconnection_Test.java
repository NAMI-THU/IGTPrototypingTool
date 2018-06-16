package testInputOutput;

import static org.junit.Assert.*;

import java.util.ArrayList;

import inputOutput.*;

import org.medcare.igtl.messages.*;
import org.junit.Test;

public class Networkconnection_Test {

	static ArrayList<Tool> testlist = new ArrayList<Tool>();

	@Test
	public void test() {

		Start_Stop_IGTLink.startIGTWindow();
		try {
			Thread.sleep(100000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void setTestlist(ArrayList<Tool> test) {
		testlist = test;

		int testsize = testlist.size();

		for (int i = 0; i < testsize; i++) {
			System.out.println("\n" + testlist.get(i).getName() + "\n"
					+ testlist.get(i).getTimestamp() + "\n"
					+ testlist.get(i).getValid() + "\n"
					+ testlist.get(i).getCoordinat() + "\n"
					+ testlist.get(i).getRotation_r() + "\n"
					+ testlist.get(i).getRotation_x() + "\n"
					+ testlist.get(i).getRotation_y() + "\n"
					+ testlist.get(i).getRotation_z());

			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("#############################################################");
	}

}
