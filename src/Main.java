import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import guiSections.Menu;

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

public class Main {
	
	public static void main(String[] args) {
		
		// Outer panel holds Menu bar and console
		JPanel outerElements = new JPanel();
		outerElements.setLayout(new BorderLayout());
		
		// Inner holds the gameplay area and the hand display
		JPanel gameElements = new JPanel();
		gameElements.setLayout(new BorderLayout());
		
		// Set up menu bar
		Menu menuBar = new Menu();
        menuBar.setBorder(BorderFactory.createLineBorder(Color.black, 2));

		// Set up console
		JPanel console = new JPanel();
		console.setLayout(new BorderLayout());
		console.setBackground(new Color(255,241,241));
		console.setPreferredSize(new Dimension(300,0));
        console.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		// Score
		JPanel score = new JPanel();
		score.setBackground(new Color(255,241,241));
		score.setPreferredSize(new Dimension(0,100));
        score.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		// Chat input
		JPanel chat = new JPanel();
		chat.setBackground(new Color(255,241,241));
		chat.setPreferredSize(new Dimension(0,150));
        chat.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		// Add chat section to console
		console.add(BorderLayout.SOUTH, chat);
		console.add(BorderLayout.NORTH, score);
		
		// Game area
		JPanel gameStateArea = new JPanel();
		gameStateArea.setBackground(new Color(255,241,241));
		
		// Hand
		JPanel handArea = new JPanel();
		handArea.setPreferredSize(new Dimension(0,150));
		handArea.setBackground(new Color(255,241,241));
		handArea.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		gameElements.add(BorderLayout.SOUTH, handArea);
		gameElements.add(BorderLayout.CENTER, gameStateArea);
		
		// Adding components to the outer frame
		outerElements.add(BorderLayout.NORTH, menuBar);
		outerElements.add(BorderLayout.EAST, console);
		outerElements.add(BorderLayout.CENTER, gameElements);
		
		// Icon
		ImageIcon icon = new ImageIcon("resource/img/icon.png");
		
		// Draw the gui
		JFrame gui = new JFrame();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(1280, 720);
		gui.setResizable(false);
		gui.setTitle("Crazy Eights");
		gui.setIconImage(icon.getImage());
		gui.add(outerElements);
		gui.setVisible(true);

		
	}

}
