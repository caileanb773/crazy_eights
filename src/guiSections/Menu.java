package guiSections;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

public class Menu extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel menuBar;
	
	public Menu(){
		this.menuBar = new JPanel();
		this.menuBar.setLayout(new FlowLayout());
		this.setBackground(Color.pink);
		this.setPreferredSize(new Dimension(0,50));
        menuBar.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		JButton host = new JButton("HOST GAME");
		host.setFont(new Font("SNES Fonts: Mario Paint Regular",Font.PLAIN,12));
		host.setFocusable(false);
		this.menuBar.add(host);
		
		JButton join = new JButton("JOIN GAME");
		join.setFont(new Font("SNES Fonts: Mario Paint Regular",Font.PLAIN,12));
		join.setFocusable(false);
		this.menuBar.add(join);
		
		JButton disc = new JButton("DISCONNECT");
		disc.setFont(new Font("SNES Fonts: Mario Paint Regular",Font.PLAIN,12));
		disc.setFocusable(false);
		disc.setEnabled(false);
		this.menuBar.add(disc);
		
		JButton options = new JButton("OPTIONS");
		options.setFont(new Font("SNES Fonts: Mario Paint Regular",Font.PLAIN,12));
		options.setFocusable(false);
		this.menuBar.add(BorderLayout.WEST, options);
		
		this.add(BorderLayout.WEST, menuBar);
	}

}
