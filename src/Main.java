import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import temp.User;

/**
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
 * Calls main() function, which calls drawMainApplication.
 * @author Cailean Bernard
 * @since JDK 22
 * */
public class Main {

	/** 
	 * Constant for Vertical
	 */
	static final boolean VERTICAL = false;
	
	/**
	 * Constant for Horizontal
	 * */
	static final boolean HORIZONTAL = true;
	
	/**
	 * Constant for Visible
	 * */
	static final boolean VISIBLE = true;
	
	/**
	 * Constant for Hidden
	 * */
	static final boolean HIDDEN = false;
	
	/**
	 * Constant for the background pink colour
	 * */
	static final Color BACKGROUND_PINK = new Color(255,241,241);
	
	/**
	 * Constant for the background blue colour
	 * */
	static final Color BACKGROUND_BLUE = new Color(33,65,202);
	
	/**
	 * Constant for the border blue colour
	 * */
	static final Color BORDER_BLUE = new Color(136, 200, 238);

	/**
	 * Calls drawMainApplication(), which draws the GUI.
	 * @param args - An array of strings passed as arguments
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	public static void main(String[] args) {

		drawSplash();
		drawMainApplication();

	}

	/**
	 * Draws the GUI elements. Each GUI element is a JPanel, which holds other
	 * JPanels. There are outer elements, which hold the title bar, console,
	 * player's hand, and the gameplay area. Each of these areas is further 
	 * broken down into several other components.
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	public static void drawMainApplication() {

		/* ---------- MAIN PANELS ---------- */

		// Get font from lib folder
		Font myFont = getMyFont("asset/font/snes-fonts-mario-paint.ttf");

		// Outer panel holds play area and console
		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BorderLayout());

		// Inner holds the game play area
		JPanel gameElements = new JPanel();
		gameElements.setLayout(new BorderLayout());

		/* ------------------------- CONSOLE PANEL ------------------------- */

		// Console holds the chat entry, chat display, and score display
		JPanel console = new JPanel();
		console.setLayout(new BorderLayout());
		console.setBackground(BACKGROUND_PINK);
		console.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.BLACK));

		/* ---------- Score Elements ---------- */

		// ScoreBoxWrapper to glue everything together
		JPanel scoreBoxWrapper = new JPanel();
		scoreBoxWrapper.setLayout(new BorderLayout());
		scoreBoxWrapper.setBackground(null);

		// Scorebox holds the "score" label and the users, with their scores
		JPanel scoreBox = new JPanel();
		scoreBox.setBackground(Color.WHITE);
		scoreBox.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 2));
		scoreBox.setLayout(new BoxLayout(scoreBox, BoxLayout.Y_AXIS));

		// Score label
		JLabel scoreTitle = new JLabel("--- SCORE ---");
		scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoreTitle.setFont(myFont.deriveFont(14f));

		// Adding some temporary users
		User uConsole = new User(0, "Console", 0, myFont);
		User user1 = new User(1, "Coop", 12, myFont);
		User user2 = new User(2, "Laura", 12, myFont);
		User user3 = new User(3, "Hawk", 12, myFont);
		User user4 = new User(4, "Bob", 12, myFont);

		// Adding elements to score box
		scoreBox.add(scoreTitle);
		scoreBox.add(user1);
		scoreBox.add(user2);
		scoreBox.add(user3);
		scoreBox.add(user4);

		// Adding console to wrappers		
		scoreBoxWrapper.add(scoreBox);
		scoreBoxWrapper.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));

		/* ---------- Chat Elements ---------- */

		// Wrapper Panel for chat elements
		JPanel chatBoxWrapper = new JPanel();
		chatBoxWrapper.setLayout(new FlowLayout());
		chatBoxWrapper.setBackground(null);
		chatBoxWrapper.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));

		// Wrapper Panel for chat display elements
		JPanel chatDisplayWrapper = new JPanel();
		chatDisplayWrapper.setLayout(new BorderLayout());
		chatDisplayWrapper.setBackground(null);
		chatDisplayWrapper.setLayout(new BorderLayout());
		chatDisplayWrapper.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));

		// TextArea for chat input
		TextArea chatInput = new TextArea(3, 30);
		chatInput.setBackground(Color.WHITE);
		chatInput.setFont(new Font("Times New Roman", Font.PLAIN, 12));

		// JTextArea for chat display
		JTextPane chatDisplay = new JTextPane();
		chatDisplay.setEditorKit(new StyledEditorKit());
		chatDisplay.setEditable(false);
		chatDisplay.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 2));

		// Scroll pane for chat display
		JScrollPane chatDisplayScroll = new JScrollPane(chatDisplay);

		// Chat "send" button
		JButton chatSend = new JButton("SEND");
		chatSend.setFocusable(false);
		chatSend.setFont(myFont.deriveFont(12f));

		// ChatBox = chatInput + chatSend
		JPanel chatBox = new JPanel();
		chatBox.setBackground(Color.WHITE);
		chatBox.setLayout(new BorderLayout());
		chatBox.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 2));

		// Add chatInput and chatSend button to chatBox panel
		chatBox.add(BorderLayout.NORTH, chatInput);
		chatBox.add(BorderLayout.SOUTH, chatSend);

		// Wrapping the scrolling chat display
		chatDisplayWrapper.add(chatDisplayScroll, BorderLayout.CENTER);

		// Wrapping the chat box
		chatBoxWrapper.add(chatBox);

		// Adding wrapped Chat, Display, and Score panels to the console
		console.add(BorderLayout.SOUTH, chatBoxWrapper);
		console.add(BorderLayout.CENTER, chatDisplayWrapper);
		console.add(BorderLayout.NORTH, scoreBoxWrapper);

		// Temporary messages for the console
		addConsoleMessage(chatDisplay, uConsole, "User " + user1.getName() + " played Ace of Spades!");
		addConsoleMessage(chatDisplay, user1, "brb, grabbing a coffee + pie.");
		addConsoleMessage(chatDisplay, user2, "np");
		addConsoleMessage(chatDisplay, user3, "it's foggy out again");
		addConsoleMessage(chatDisplay, user4, "whose turn is it?");

		/* ------------------------- GAME PANEL ------------------------- */
		// Users are arranged clockwise starting from player 1 at the top
		// User1 = North, User2 = East, user3 = West

		// GridBagLayout for East and West players
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.CENTER;

		/* ---------- NORTH PLAYER ---------- */

		JPanel playerNorth = new JPanel();
		playerNorth.setLayout(new BorderLayout());
		JPanel playerNorthCards = new JPanel();
		playerNorthCards.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		playerNorthCards.setBackground(null);
		JLabel playerNorthName = new JLabel(user1.getName());
		playerNorthName.setFont(myFont);
		playerNorthName.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		playerNorthName.setHorizontalAlignment(SwingConstants.CENTER);
		playerNorth.add(BorderLayout.NORTH, playerNorthName);
		playerNorth.add(BorderLayout.SOUTH, playerNorthCards);
		playerNorth.setBackground(BACKGROUND_PINK);

		/* ---------- EAST PLAYER ---------- */

		// Panel to hold Name and Cards
		JPanel pEast = new JPanel();
		pEast.setBackground(BACKGROUND_PINK);
		pEast.setLayout(new GridBagLayout());

		// Label for the name
		JLabel pEastName = new JLabel(user2.getName());
		pEastName.setFont(myFont);
		pEastName.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		pEast.add(pEastName);

		// Wrapper to hold East Player's elements
		JPanel pEastWrapper = new JPanel();
		pEastWrapper.setBackground(BACKGROUND_PINK);
		pEastWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		pEastWrapper.add(pEast);

		/* ---------- WEST PLAYER ---------- */

		// Panel to hold Name and Cards
		JPanel pWest = new JPanel();
		pWest.setBackground(BACKGROUND_PINK);
		pWest.setLayout(new GridBagLayout());

		// Label for the name
		JLabel pWestName = new JLabel(user3.getName());
		pWestName.setFont(myFont);
		pWestName.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		pWest.add(pWestName);

		// Wrapper to hold East Player's elements
		JPanel pWestWrapper = new JPanel();
		pWestWrapper.setBackground(BACKGROUND_PINK);
		pWestWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		pWestWrapper.add(pWest);

		/* ---------- GAME LOGO ---------- */

		/* Instead of resizing the raw image file, I chose to dynamically resize
		 * it within the program so I could play around with which size I thought
		 * worked best.
		 */
		ImageIcon gameLogoImg = new ImageIcon("asset/img/logo_sm.png");
		JLabel gameLogo = new JLabel(gameLogoImg);
		gameLogo.setVerticalAlignment(SwingConstants.CENTER);
		gameLogo.setHorizontalAlignment(SwingConstants.CENTER);

		/* ---------- PLAYING AREA ---------- */

		/*
		 * // Panel to hold the Library + Played cards and the logo JPanel gameStateArea
		 * = new JPanel(); gameStateArea.setLayout(new BorderLayout());
		 * gameStateArea.setBackground(BACKGROUND_PINK);
		 */

		// Library refers to the pile of face-down cards that have yet to be drawn
		JPanel cardLibrary = new JPanel();
		cardLibrary.setBackground(BACKGROUND_PINK);
		JButton library = new JButton(new ImageIcon("asset/card/back.png"));
		library.setBorder(null);
		cardLibrary.add(library);

		// Played cards have been played by players and are face-up
		JPanel cardsPlayed = new JPanel();
		cardsPlayed.setBackground(BACKGROUND_PINK);
		JButton played = new JButton(new ImageIcon("asset/card/As.png"));
		played.setBorder(null);
		cardsPlayed.add(played);

		// Panel to hold the library and the played cards
		JPanel playingArea = new JPanel();
		playingArea.setBackground(BACKGROUND_PINK);
		playingArea.setLayout(new FlowLayout());
		playingArea.add(cardLibrary);
		playingArea.add(cardsPlayed);

		// Holds the logo panel plus the combined library + played cards panel
		JPanel playingAreaAndLogo = new JPanel();
		playingAreaAndLogo.setLayout(new BorderLayout());
		playingAreaAndLogo.setBackground(BACKGROUND_PINK);
		playingAreaAndLogo.add(BorderLayout.SOUTH, gameLogo);
		playingAreaAndLogo.add(BorderLayout.NORTH, playingArea);

		// Hand for the user interacting with the app
		JPanel mainUserHand = new JPanel();
		mainUserHand.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
		mainUserHand.setBackground(BACKGROUND_PINK);

		// Add all gameplay elements to game area
		//gameStateArea.add(BorderLayout.CENTER, logoPlusCards);
		gameElements.add(BorderLayout.NORTH, playerNorth);
		gameElements.add(BorderLayout.EAST, pEastWrapper);
		gameElements.add(BorderLayout.WEST, pWestWrapper);
		gameElements.add(BorderLayout.SOUTH, mainUserHand);
		gameElements.add(BorderLayout.CENTER, playingAreaAndLogo);
		//gameElements.add(BorderLayout.CENTER, gameStateArea);

		// Adding cards to each players' hand
		addCardsToHand(mainUserHand, HORIZONTAL, VISIBLE, gbc);
		addCardsToHand(playerNorthCards, HORIZONTAL, HIDDEN, gbc);
		addCardsToHand(pEast, VERTICAL, VISIBLE, gbc);
		addCardsToHand(pWest, VERTICAL, HIDDEN, gbc);

		/* -------------------- MENU -------------------- */

		// Menu Bar
		JMenuBar mBar = new JMenuBar();
		mBar.setVisible(true);
		mBar.setLayout(new FlowLayout(FlowLayout.LEADING));

		/* ----- Main menu options ----- */

		JMenu mHostGame = new JMenu("Start Game");
		mHostGame.setVisible(true);
		mHostGame.setEnabled(true);

		JMenu mJoinGame = new JMenu("Join Game");
		mJoinGame.setVisible(true);
		mJoinGame.setEnabled(true);

		JMenu mDisconnect = new JMenu("Disconnect");
		mDisconnect.setVisible(true);
		mDisconnect.setEnabled(false);

		JMenu mOptions = new JMenu("Options");
		mOptions.setVisible(true);
		mOptions.setEnabled(true);

		JMenu mAbout = new JMenu("About");
		mAbout.setVisible(true);
		mAbout.setEnabled(true);

		/* ----- SUBMENU ----- */

		JCheckBoxMenuItem soundToggle = new JCheckBoxMenuItem("Sound effects on/off");
		soundToggle.setSelected(true);

		JCheckBoxMenuItem musicToggle = new JCheckBoxMenuItem("Music on/off");
		musicToggle.setSelected(true);

		JMenuItem mSinglePlayer = new JMenuItem("Single Player");

		JMenuItem mMultiPlayer = new JMenuItem("Multiplayer");

		// Add submenu items
		mOptions.add(soundToggle);
		mOptions.add(musicToggle);
		mHostGame.add(mSinglePlayer);
		mHostGame.add(mMultiPlayer);

		// Add menu items
		mBar.add(mHostGame);
		mBar.add(mJoinGame);
		mBar.add(mDisconnect);
		mBar.add(mOptions);
		mBar.add(mAbout);

		/* ------------------------- MAIN WINDOW ------------------------- */

		// Adding components to the outer frame
		outerPanel.add(BorderLayout.EAST, console);
		outerPanel.add(BorderLayout.CENTER, gameElements);

		// Building the main window
		ImageIcon icon = new ImageIcon("asset/img/icon.png");
		JFrame gui = new JFrame();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setResizable(true);
		gui.setTitle("Crazy Eights");
		gui.setIconImage(icon.getImage());
		gui.getContentPane().add(outerPanel);
		gui.setVisible(true);
		gui.setJMenuBar(mBar);
		gui.pack();
		gui.setLocationRelativeTo(null);

	}

	/**
	 * Draws a splash screen for 3 seconds.
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	public static void drawSplash() {

		// Dynamically resizing the image instead of resizing raw asset
		ImageIcon splashImage = new ImageIcon("asset/img/splash.png");
		JLabel splashImageLabel = new JLabel(splashImage);
		JPanel splashElements = new JPanel();
		splashElements.setLayout(new BorderLayout());
		splashElements.add(BorderLayout.CENTER, splashImageLabel);
		
		JProgressBar loading = new JProgressBar();
		loading.setMinimum(0);
		loading.setMaximum(100);
		loading.setStringPainted(VISIBLE);
		
		splashElements.add(BorderLayout.SOUTH, loading);

		JWindow splashFrame = new JWindow();
		splashFrame.add(splashElements);
		splashFrame.setVisible(true);
		splashFrame.pack();
		splashFrame.setLocationRelativeTo(null);

		// Fake loading
		for (int i = 0; i <= 100; i+=5) {
			try {
				Thread.sleep(100);
				loading.setValue(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		splashFrame.dispose();
	}

	/**
	 * Method to add cards to hand.
	 * @param area - The JPanel to add the cards to
	 * @param horizontal - If the JPanel is displaying horizontally or not.
	 * If true, panel is horizontal, if false, panel is vertical. This changes
	 * which card slices we are displaying for that user's hand. 
	 * @param visible - Changes if the card displayed is a card back (hidden) or
	 * a card face (visible). The only time we display card faces in this method
	 * is for when we are rendering cards in the user's hand.
	 * @param gbc - The GridBagConstraints needed to add the cards to vertical hands.
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	static void addCardsToHand(JPanel area, boolean horizontal, boolean visible, GridBagConstraints gbc) {

		// If the game area is horizontal, render the cards horizontally
		if (horizontal) {
			JButton cardSlice;
			/* method should handle adding names as well as buttons.*/
			for (int i = 0; i < 11; i++) {

				// If the card is visible, render a card face. Else, render a card back
				if (visible) {
					cardSlice = new JButton(new ImageIcon("asset/card/l1c.png"));
				} else {
					cardSlice = new JButton(new ImageIcon("asset/card/lback.png"));
				}
				cardSlice.setBorder(null);
				area.add(cardSlice);
			}

			// Render the last (full) card in hand (either visible or hidden)
			if (visible) {
				cardSlice = new JButton(new ImageIcon("asset/card/Qs.png"));
				cardSlice.setBorder(null);
				area.add(cardSlice);
			} else {
				cardSlice = new JButton(new ImageIcon("asset/card/back.png"));
				cardSlice.setBorder(null);
				area.add(cardSlice);
			}

			// Else, render them vertically
		} else {
			JButton cardSlice;
			for (int i = 0; i < 11; i++) {
				cardSlice = new JButton(new ImageIcon("asset/card/tback.png"));
				cardSlice.setBorder(null);
				cardSlice.setAlignmentX(Component.CENTER_ALIGNMENT);
				area.add(cardSlice, gbc);
			}
			cardSlice = new JButton(new ImageIcon("asset/card/back.png"));
			cardSlice.setBorder(null);
			area.add(cardSlice, gbc);
		}
	}

	/**
	 * Temporary method (will be refactored) to add a message to the chat display.
	 * If the user is "console" (id: 0) then their text is displayed in red. Else,
	 * all messages are in black. Writes a string passed to it to the StyledDocument
	 * of the passed JTextPane, and displays the passed User's name.
	 * @param textBox - The JTextBox to write to
	 * @param user - The user writing to the text box
	 * @param message - The String being written
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	static void addConsoleMessage(JTextPane textBox, User user, String message) {
		StyledDocument doc = textBox.getStyledDocument();
		Style style = textBox.addStyle("Style", null);
		String name = user.getName();

		// If the user is the Console, draw the message in RED
		if (user.getId() == 0) {
			StyleConstants.setForeground(style, Color.RED);
		} else {
			StyleConstants.setForeground(style, Color.BLACK);
		}

		try {
			doc.insertString(doc.getLength(), name + ": " + message + "\n", style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetches the custom font specified at "path" and returns the font to be used
	 * in setFont() functions.
	 * @return Font - The Font returned from the specified path, after it has
	 * been located.
	 * @param path - The path to the font.
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	static Font getMyFont(String path) {
		File fontFile = new File(path);
		Font myFont = new Font("Arial", Font.PLAIN, 12);

		try {
			myFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(12f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}

		return myFont;
	}

}
