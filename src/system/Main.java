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

		// TODO: go through and see if patch notes can be made smaller by conforming to original plan
		// TODO: translation
		// TODO: javadoc
		// TODO: console messages
		// TODO: change back max points constant and dealCards(1) to max hand size
		// TODO: batch file
		// TODO: check that game conforms to all rules laid out in a11
		// TODO: check the invisible border on the east/west handpanels, could remove the pack check and just trim the bottom
		// TODO: should the active player's name be in some other font to make it clear who's turn it is?

		//GameModel m = new GameModel();
		//GameView v = new GameView();
		new GameController(new GameModel(), new GameView());

	}

}
