package surroundpack;

import javax.swing.*;

public class Surround4 {
	/**
	 * @param args
	 */

	private static JMenuItem quitItem = new JMenuItem("QUIT");
	private static JMenuItem newGameItem = new JMenuItem("New  Game");

	public static void main (String[] args)
	{

		/*  The JMenuBar is associated with the frame. The first step
		 *  is to create the menu items, and file menu.
		 */
		

		JFrame frame = new JFrame ("Surround game");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.pack();

		quitItem = new JMenuItem("Quit");

		Surround4Panel panel = new Surround4Panel();
		frame.add(panel);
		frame.setSize(600, 600);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
}