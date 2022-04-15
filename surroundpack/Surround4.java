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
		JMenuBar menus;
		JMenu fileMenu;
		JMenuItem quitItem;
		JButton undoButton;

		JFrame frame = new JFrame ("Surround game");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.pack();

		fileMenu = new JMenu("File");
		quitItem = new JMenuItem("Quit");

		fileMenu.add(quitItem);
		fileMenu.add(newGameItem);
		undoButton = new JButton("Undo");

		menus = new JMenuBar();
		menus.add(fileMenu);
		menus.add(undoButton);

		frame.setJMenuBar(menus);

		Surround4Panel panel = new Surround4Panel(quitItem, newGameItem, undoButton);
		frame.add(panel);
		frame.setSize(600, 600);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
}