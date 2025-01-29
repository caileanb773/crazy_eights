import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.TextArea;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import guiSections.Menu;
import guiSections.UserScore;

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

		drawMainApplication();

	}

	public static void drawMainApplication() {
		
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
		console.setBorder(BorderFactory.createLineBorder(Color.black, 2));

		// Score text/counters
		JLabel scoreTitle = new JLabel("---SCORE---");
		scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoreTitle.setFont(new Font("SNES Fonts: Mario Paint Regular", Font.PLAIN, 20));
		UserScore user1 = new UserScore(1);
		UserScore user2 = new UserScore(2);

		// Score
		JPanel scoreBox = new JPanel();
		scoreBox.setBackground(new Color(255,241,241));
		scoreBox.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		scoreBox.setLayout(new BoxLayout(scoreBox, BoxLayout.Y_AXIS));

		// Adding elements to score box
		scoreBox.add(scoreTitle);
		scoreBox.add(user1);
		scoreBox.add(user2);
		
		// ScoreBoxWrapper
		JPanel scoreBoxWrapper = new JPanel();
		scoreBoxWrapper.setLayout(new BorderLayout());

		// -------------------- CHAT STUFF --------------------

		// Add textArea for chat input
		TextArea chatBox = new TextArea();
		chatBox.setBackground(Color.pink);
		
		// Chat input wrapper
		JPanel chatBoxWrapper = new JPanel();
		chatBoxWrapper.setLayout(new FlowLayout());
		
		// Chat Display wrapper panel
		JPanel chatDisplayWrapper = new JPanel();
		chatDisplayWrapper.setLayout(new BorderLayout());
		
		// Add JTextArea for chat display
		JTextPane chatDisplay = new JTextPane();
		chatDisplay.setEditable(false);
		chatDisplay.setBorder(BorderFactory.createLineBorder(Color.black, 2));

		// Chat "send" button
		JButton chatSend = new JButton("SEND");
		chatSend.setFocusable(false);
		chatSend.setFont(new Font("SNES Fonts: Mario Paint Regular",Font.PLAIN,12));

		// Chat input
		JPanel chat = new JPanel();
		chat.setBackground(new Color(255,241,241));
		chat.setLayout(new FlowLayout());
		chat.setBorder(BorderFactory.createLineBorder(Color.black, 2));

		// Add chatBox to the chat section
		chat.add(chatBox);
		chat.add(chatSend);
				
		// Adding components to wrappers		
		scoreBoxWrapper.add(scoreBox);
		scoreBoxWrapper.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));
		
		chatDisplayWrapper.add(chatDisplay);
		chatDisplayWrapper.setLayout(new BorderLayout());
		chatDisplayWrapper.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
		chatDisplayWrapper.add(chatDisplay, BorderLayout.CENTER);
		
		chatBoxWrapper.add(chat);
		chatBoxWrapper.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));

		// Add chat and score sections to console
		console.add(BorderLayout.SOUTH, chatBoxWrapper);
		console.add(BorderLayout.CENTER, chatDisplayWrapper);
		console.add(BorderLayout.NORTH, scoreBoxWrapper);

		// -------------------- GAME AREA STUFF --------------------

		// Game logo
		ImageIcon gameLogoImg = new ImageIcon("resource/img/crazyeightslogo.png");
		JLabel gameLogo = new JLabel();
		gameLogo.setIcon(gameLogoImg);

		// Game area
		JPanel gameStateArea = new JPanel();
		gameStateArea.setBackground(new Color(255,241,241));

		// Add logo to Game Area
		gameStateArea.add(gameLogo);

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
		
		// -------------------- BEGIN TESTING AREA --------------------
		
		JButton inc1 = new JButton("Increment user 1 score");
		inc1.addActionListener(e -> user1.incrementScore());
		handArea.add(inc1);
		
		JButton inc2 = new JButton("Increment user 2 score");
		inc2.addActionListener(e -> user2.incrementScore());
		handArea.add(inc2);
		
		// -------------------- END TESTING AREA --------------------

		// Icon
		ImageIcon icon = new ImageIcon("resource/img/icon.png");

		// Draw the gui
		JFrame gui = new JFrame();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(1280, 720);
		gui.setResizable(true);
		gui.setTitle("Crazy Eights");
		gui.setIconImage(icon.getImage());
		gui.add(outerElements);
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);
		gui.pack();
		
	}

}
