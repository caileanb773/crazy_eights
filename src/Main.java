import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
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
		JPanel outer = new JPanel();
		outer.setLayout(new BorderLayout());
		
		// Inner holds the gameplay area and the hand display
		JPanel inner = new JPanel();
		inner.setLayout(new BorderLayout());
		
		// Set up menu bar
		Menu menuBar = new Menu();
        menuBar.setBorder(BorderFactory.createLineBorder(Color.black, 2));

		// Set up console
		JPanel console = new JPanel();
		console.setLayout(new BorderLayout());
		console.setBackground(Color.cyan);
		console.setPreferredSize(new Dimension(300,0));
        console.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		// Score
		JPanel score = new JPanel();
		score.setBackground(Color.lightGray);
		score.setPreferredSize(new Dimension(0,100));
        score.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		// Chat input
		JPanel chat = new JPanel();
		chat.setBackground(Color.green);
		chat.setPreferredSize(new Dimension(0,150));
        chat.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		// Add chat section to console
		console.add(BorderLayout.SOUTH, chat);
		console.add(BorderLayout.NORTH, score);
		
		// Game area
		JPanel game = new JPanel();
		game.setBackground(Color.orange);
		
		// Hand
		JPanel hand = new JPanel();
		hand.setPreferredSize(new Dimension(0,150));
		hand.setBackground(Color.darkGray);
		
		inner.add(BorderLayout.SOUTH, hand);
		inner.add(BorderLayout.CENTER, game);
		
		// Adding components to the outer frame
		outer.add(BorderLayout.NORTH, menuBar);
		outer.add(BorderLayout.EAST, console);
		outer.add(BorderLayout.CENTER, inner);
		
		JFrame gui = new JFrame();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
		gui.setSize(1280, 720);
		gui.setResizable(false);
		gui.setTitle("Crazy Eights");
		gui.add(outer);
		
	}

}
