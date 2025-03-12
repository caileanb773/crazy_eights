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
		
		// TODO:
		// get rid of magic numbers (some exist in controller, when ai player decides turn)
		// go through and see if patch notes can be made smaller by conforming to original plan
		// remove redundant method calls when possible

		GameModel m = new GameModel(Const.SINGLE_PLAYER);
		GameView v = new GameView();
		new GameController(m, v);
		
	}

}
