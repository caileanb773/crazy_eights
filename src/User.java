/*
 * CET - CS Academic Level 4
 * Declaration: I declare that this is my own original work and is free of plagiarism
 * Student Name: Cailean Bernard
 * Student Number: 041143947
 * Section #: 300-302
 * Course: CST8221 - Java Application Programming
 * Professor: Daniel Cormier
 * Contents:
 */

public class User {

	/** User's name */
	private String name;
	
	/** The score of the user */
	private int score;
	
	/** The ID of the user (int) */
	private int userID;
	
	/** Number of cards user has in hand*/
	private int cardsInHand;

	/**
	 * Constructor for User
	 * @param userNo - The user's ID
	 * @param n - The user's name
	 * @param numCards - How many cards in user's hand
	 * @param font - The font with which to display this user
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	public User(int userNo, String n, int numCards, int score) {
		this.name = n.toUpperCase();
		this.userID = userNo;
		this.cardsInHand = numCards;
		this.score = score;
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
		return this.userID;
	}
	
	/**
	 * Renders user's name, score, and cards in hand
	 * @return String - The formatted string.
	 * @author Cailean Bernard
	 * @since JDK 22
	 */
	@Override
	public String toString() {
		return this.name + " SCORE= " + this.score + ", CARDS= " + this.cardsInHand;
	}
	
}
