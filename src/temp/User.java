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
	
	/**
	 * 
	 */
	private String name;
	private static final long serialVersionUID = 1L;
	private int scoreInt;
	private int userNumInt;
	private int cardsInHand;
	JLabel scoreLabel;
	JLabel userNum;
	
	public User(int userNo, String n, int numCards) {
		this.name = n.toUpperCase();
		this.userNumInt = userNo;
		this.cardsInHand = numCards;
		this.scoreLabel = new JLabel(name + " SCORE " + scoreInt + " CARDS " + cardsInHand);
		this.scoreLabel.setFont(new Font("SNES Fonts: Mario Paint Regular", Font.PLAIN, 11));
		this.scoreLabel.setOpaque(false);
		this.setOpaque(false);
		this.add(scoreLabel);
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getId() {
		return this.userNumInt;
	}

}
