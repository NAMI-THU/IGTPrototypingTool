package inputOutput;

import java.util.ArrayList;
import java.util.Vector;

public abstract class Interface {

	private static ArrayList<Tool> toollist = new ArrayList<Tool>();

	public Interface() {

	}

	private static ArrayList update() {

		read();

		return toollist;

	}

	private static void read() {

	}

}
