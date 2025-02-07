package temp;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * CET - CS Academic Level 4
 * Declaration: I declare that this is my own original work and is free of plagiarism
 * Student Name: Cailean Bernard
 * Student Number: 041143947
 * Section #: 300-302
 * Course: CST8221 - Java Application Programming
 * Professor: Daniel Cormier
 * Contents:
 */

public class User extends JPanel {

	/** User's name */
	private String name;
	
	/** SerialVersionUID (for multithreading?) */
	private static final long serialVersionUID = 1L;
	
	/** The score of the user */
	private int scoreInt;
	
	/** The ID of the user (int) */
	private int userNumInt;
	
	/** Number of cards user has in hand*/
	private int cardsInHand;
	
	/** Label for adding to the score section of the UI */
	JLabel scoreLabel;

	/**
	 * Constructor for User
	 * @param userNo - The user's ID
	 * @param n - The user's name
	 * @param numCards - How many cards in user's hand
	 * @param font - The font with which to display this user
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	public User(int userNo, String n, int numCards, Font font) {
		scoreLabel = new JLabel();
		this.name = n.toUpperCase();
		this.userNumInt = userNo;
		this.cardsInHand = numCards;
		this.scoreLabel = new JLabel(name + " SCORE " + scoreInt + " CARDS " + cardsInHand);
		this.scoreLabel.setFont(font);
		this.setOpaque(false);
		this.add(scoreLabel);
	}
	
	/**
	 * Getter for name
	 * @return String - The name of the user
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Getter for user ID
	 * @return int - The ID of the user
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	public int getId() {
		return this.userNumInt;
	}

}
