package guiSections;

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

public class UserScore extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int scoreInt;
	private int userNumInt;
	JLabel scoreLabel;
	JLabel userNum;
	
	public UserScore(int userNo) {
		this.userNumInt = userNo;
		this.scoreLabel = new JLabel("User " + userNo + " score = " + scoreInt);
		this.scoreLabel.setFont(new Font("SNES Fonts: Mario Paint Regular", Font.PLAIN, 14));
		this.scoreLabel.setOpaque(false);
		this.setOpaque(false);
		this.add(scoreLabel);
	}
	
	public void incrementScore() {
		this.scoreInt++;
		this.scoreLabel.setText("User " + this.userNumInt + " score = " + scoreInt);
		this.revalidate();
		this.repaint();

	}

}
