package testInputOutput;

import static org.junit.Assert.*;

import java.util.ArrayList;

import inputOutput.*;

import org.medcare.igtl.messages.*;
import org.junit.Test;

import userinterface.Start_Stop_IGTLink;

public class Networkconnection_test_app {

	static ArrayList<Tool> testlist = new ArrayList<Tool>();
	private static boolean count = false;

	public static void main(String[] args) {

		Start_Stop_IGTLink.setTestappValue();
		OpenIGTLinkConnection.setTestappValue();
		Start_Stop_IGTLink.startIGTWindow();

	}

	public static void setTestlist(ArrayList<Tool> test) {

		if (count == true) {
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
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			System.out
					.println("#############################################################");

		} else {
			count = true;

		}
		// Fix Problem after Start again most times the first toollist is not
		// correct.
	}

	public static void setCount() {
		count = false;
	}

}
