package system;

/*
 * CET - CS Academic Level 4
 * Declaration: I declare that this is my own original work and is free of plagiarism
 * Student Name: Cailean Bernard
 * Student Number: 041143947
 * Section #: 300-302
 * Course: CST8221 - Java Application Programming
 * Professor: Daniel Cormier
 * Contents: Main class. Contains main method and methods to draw the GUI
 */

/**
 * Calls main() function, which calls creates the GameController.
 * 
 * @author Cailean Bernard
 * @since 23
 */
public class Main {

	/**
	 * Creates the controller, which is made of a model and a view.
	 * 
	 * @param args - An array of strings passed as arguments
	 * @author Cailean Bernard
	 * @since 23
	 */
	public static void main(String[] args) {

		GameModel m = new GameModel(1);
		GameView v = new GameView();
		new GameController(m, v);
		
	}

}
