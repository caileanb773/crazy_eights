import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
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
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
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
	
	static final boolean VERTICAL = false;
	static final boolean HORIZONTAL = true;
	static final boolean VISIBLE = true;
	static final boolean HIDDEN = false;
	static final Color MY_BKGRD_PINK = new Color(255,241,241);
	static final Color MY_BKGRD_BLUE = new Color(33,65,202);
	static final Color MY_BORDER_BLUE = new Color(136, 200, 238);

	/**
	 * Calls drawMainApplication(), which draws the GUI.
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	public static void main(String[] args) {

		drawMainApplication();
		
		// TODO: Bust drawMainApplication() into more methods

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
		
		Font myFont = getMyFont("asset/font/snes-fonts-mario-paint.ttf");
		
		// Outer panel holds Menu bar and console
		JPanel outerElements = new JPanel();
		outerElements.setLayout(new BorderLayout());

		// Inner holds the gameplay area and the hand display
		JPanel gameElements = new JPanel();
		gameElements.setLayout(new BorderLayout());
		
		// Set up custom menu bar
		//Menu menuBar = new Menu();
		//menuBar.setBorder(BorderFactory.createLineBorder(Color.black, 2));

		// Set up console
		JPanel console = new JPanel();
		console.setLayout(new BorderLayout());
		console.setBackground(MY_BKGRD_PINK);
		console.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.BLACK));

		// Score text/counters
		JLabel scoreTitle = new JLabel("--- SCORE ---");
		scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoreTitle.setFont(myFont.deriveFont(14f));
		User uConsole = new User(0, "Console", 0, myFont);
		User user1 = new User(1, "Coop", 12, myFont);
		User user2 = new User(2, "Laura", 12, myFont);
		User user3 = new User(3, "Hawk", 12, myFont);
		User user4 = new User(4, "Bob", 12, myFont);

		// Score
		JPanel scoreBox = new JPanel();
		scoreBox.setBackground(Color.WHITE);
		scoreBox.setBorder(BorderFactory.createLineBorder(MY_BORDER_BLUE, 2));
		scoreBox.setLayout(new BoxLayout(scoreBox, BoxLayout.Y_AXIS));

		// Adding elements to score box
		scoreBox.add(scoreTitle);
		scoreBox.add(user1);
		scoreBox.add(user2);
		scoreBox.add(user3);
		scoreBox.add(user4);
		
		// ScoreBoxWrapper
		JPanel scoreBoxWrapper = new JPanel();
		scoreBoxWrapper.setLayout(new BorderLayout());
		scoreBoxWrapper.setBackground(null);

		// -------------------- CHAT STUFF --------------------

		// Add textArea for chat input
		TextArea chatBox = new TextArea(5,20);
		chatBox.setBackground(Color.WHITE);
		chatBox.setFont(new Font("Arial", Font.PLAIN, 18));
		
		// Chat input wrapper
		JPanel chatBoxWrapper = new JPanel();
		chatBoxWrapper.setLayout(new FlowLayout());
		
		// Chat Display wrapper panel
		JPanel chatDisplayWrapper = new JPanel();
		chatDisplayWrapper.setLayout(new BorderLayout());
		
		// Add JTextArea for chat display
		JTextPane chatDisplay = new JTextPane();
		chatDisplay.setEditorKit(new StyledEditorKit());
		chatDisplay.setEditable(false);
		chatDisplay.setBorder(BorderFactory.createLineBorder(MY_BORDER_BLUE, 2));
		
		// Scroll pane for chat display
		JScrollPane chatDisplayScroll = new JScrollPane(chatDisplay);

		// Chat "send" button
		JButton chatSend = new JButton("SEND");
		chatSend.setFocusable(false);
		//chatSend.setFont(new Font("SNES Fonts: Mario Paint Regular",Font.PLAIN,12));
		chatSend.setFont(myFont.deriveFont(12f));

		// Chat input
		JPanel chat = new JPanel();
		chat.setBackground(Color.WHITE);
		chat.setLayout(new BorderLayout());
		chat.setBorder(BorderFactory.createLineBorder(MY_BORDER_BLUE, 2));

		// Add chatBox to the chat section
		chat.add(BorderLayout.NORTH, chatBox);
		chat.add(BorderLayout.SOUTH, chatSend);
				
		// Adding components to wrappers		
		scoreBoxWrapper.add(scoreBox);
		scoreBoxWrapper.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));
		
		chatDisplayWrapper.add(chatDisplayScroll);
		chatDisplayWrapper.setBackground(null);
		chatDisplayWrapper.setLayout(new BorderLayout());
		chatDisplayWrapper.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
		chatDisplayWrapper.add(chatDisplay, BorderLayout.CENTER);
		
		chatBoxWrapper.add(chat);
		chatBoxWrapper.setBackground(null);
		chatBoxWrapper.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));

		// Add chat and score sections to console
		console.add(BorderLayout.SOUTH, chatBoxWrapper);
		console.add(BorderLayout.CENTER, chatDisplayWrapper);
		console.add(BorderLayout.NORTH, scoreBoxWrapper);

		// -------------------- GAME AREA STUFF --------------------
		
		// Users are arranged clockwise starting from player 1 at the top
		
		// Player 1 Area
		JPanel playerNorth = new JPanel();
		playerNorth.setLayout(new BorderLayout());
		JPanel playerNorthCards = new JPanel();
		playerNorthCards.setBackground(null);
		JLabel playerNorthName = new JLabel(user1.getName());
		playerNorthName.setFont(myFont);
		playerNorthName.setHorizontalAlignment(SwingConstants.CENTER);
		playerNorth.add(BorderLayout.NORTH, playerNorthName);
		playerNorth.add(BorderLayout.SOUTH, playerNorthCards);
		playerNorth.setBackground(MY_BKGRD_PINK);

		// Player 2 Area
		JPanel playerEast = new JPanel();
		JLabel playerEastName = new JLabel(user2.getName());
		playerEastName.setFont(myFont);
		playerEastName.setHorizontalAlignment(SwingConstants.CENTER);
		playerEast.add(playerEastName);
		playerEast.setBackground(MY_BKGRD_PINK);

		// Player 3 Area
		JPanel playerWest = new JPanel();
		JLabel playerWestName = new JLabel(user3.getName());
		playerWestName.setFont(myFont);
		playerWest.add(playerWestName);
		playerWest.setBackground(MY_BKGRD_PINK);
		
		// Played cards area

		// Game logo
		ImageIcon gameLogoImg = new ImageIcon("asset/img/logo.png");
		int width = gameLogoImg.getIconWidth();
		int height = gameLogoImg.getIconHeight();
		Image gameBG = gameLogoImg.getImage().getScaledInstance(width/2, height/2, Image.SCALE_SMOOTH);
		ImageIcon gameBGLogo = new ImageIcon(gameBG);
		JLabel gameLogo = new JLabel(gameBGLogo);

		gameLogo.setVerticalAlignment(SwingConstants.CENTER);
		gameLogo.setHorizontalAlignment(SwingConstants.CENTER);

		// Game area
		JPanel gameStateArea = new JPanel();
		gameStateArea.setLayout(new BorderLayout());
		gameStateArea.setBackground(MY_BKGRD_PINK);
		
		JPanel cardLibrary = new JPanel();
		cardLibrary.setBackground(MY_BKGRD_PINK);
		
		JPanel cardsPlayed = new JPanel();
		cardsPlayed.setBackground(MY_BKGRD_PINK);
		
		JPanel displayPlayedCards = new JPanel();
		displayPlayedCards.setBackground(MY_BKGRD_PINK);
		displayPlayedCards.setLayout(new FlowLayout());
		displayPlayedCards.add(cardLibrary);
		displayPlayedCards.add(cardsPlayed);

		JButton library = new JButton(new ImageIcon("asset/card/back.png"));
		library.setBorder(null);
		
		JButton played = new JButton(new ImageIcon("asset/card/As.png"));
		played.setBorder(null);
		
		cardLibrary.add(library);
		cardsPlayed.add(played);
		
		JPanel logoPlusCards = new JPanel();
		
		logoPlusCards.setVisible(true);
		logoPlusCards.setLayout(new BorderLayout());
		logoPlusCards.setBackground(MY_BKGRD_PINK);
		logoPlusCards.add(BorderLayout.SOUTH, gameLogo);
		logoPlusCards.add(BorderLayout.NORTH, displayPlayedCards);

		// Add logo and players to Game Area
		gameStateArea.add(BorderLayout.CENTER, logoPlusCards);
		gameElements.add(BorderLayout.NORTH, playerNorth);
		gameElements.add(BorderLayout.EAST, playerEast);
		gameElements.add(BorderLayout.WEST, playerWest);

		// Hand
		JPanel handArea = new JPanel();
		handArea.setLayout(new FlowLayout());
		handArea.setBackground(MY_BKGRD_PINK);

		gameElements.add(BorderLayout.SOUTH, handArea);
		gameElements.add(BorderLayout.CENTER, gameStateArea);
		
		// Cards in hand
		addCardsToHand(handArea, HORIZONTAL, VISIBLE, user1);
		addCardsToHand(playerNorthCards, HORIZONTAL, HIDDEN, user2);
		addCardsToHand(playerEast, VERTICAL, HIDDEN, user3);
		addCardsToHand(playerWest, VERTICAL, HIDDEN, user4);

		// Adding components to the outer frame
		//outerElements.add(BorderLayout.NORTH, menuBar);
		outerElements.add(BorderLayout.EAST, console);
		outerElements.add(BorderLayout.CENTER, gameElements);
		
		// -------------------- Menu Bar --------------------
		
		// Menu Bar
		JMenuBar mBar = new JMenuBar();
		mBar.setVisible(true);
		mBar.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		/* ----- Main menu options ----- */
		
		// Menu "Host Game" option
		JMenu mHostGame = new JMenu("Start Game");
		mHostGame.setVisible(true);
		mHostGame.setEnabled(true);
		
		// Menu "Host Game" option
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
		
		/* ----- Submenu options ----- */
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
		
		// teseting messages
		addConsoleMessage(chatDisplay, uConsole, "User Cooper played Ace (S)");
		addConsoleMessage(chatDisplay, user1, "brb, grabbing a coffee + pie.");
		addConsoleMessage(chatDisplay, user2, "np");
		addConsoleMessage(chatDisplay, user3, "it's foggy out again");
		addConsoleMessage(chatDisplay, user4, "whose turn is it?");

		// -------------------- Main Frame --------------------

		// Icon
		ImageIcon icon = new ImageIcon("asset/img/icon.png");

		// Draw the gui
		JFrame gui = new JFrame();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setResizable(true);
		gui.setTitle("Crazy Eights");
		gui.setIconImage(icon.getImage());
		gui.getContentPane().add(outerElements);
		gui.setVisible(true);
		gui.setJMenuBar(mBar);
		gui.pack();
		gui.setLocationRelativeTo(null);
		
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
	 * @author Cailean Bernard
	 * @since JDK 22
	 * */
	static void addCardsToHand(JPanel area, boolean horizontal, boolean visible, User user) {
		if (horizontal) {
			area.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
			JButton cardSlice;

			for (int i = 0; i < 11; i++) {
				if (visible) {
					// Set the card to be a horizontal slice of a visible card
					cardSlice = new JButton(new ImageIcon("asset/card/l1c.png"));
				} else {
					// Set the card to be a horizontal slice of a hidden card
					cardSlice = new JButton(new ImageIcon("asset/card/lback.png"));
				}
				cardSlice.setBorder(null);
				area.add(cardSlice);
			}
			if (visible) {
				cardSlice = new JButton(new ImageIcon("asset/card/Qs.png"));
				cardSlice.setBorder(null);
				area.add(cardSlice);
			} else {
				cardSlice = new JButton(new ImageIcon("asset/card/back.png"));
				cardSlice.setBorder(null);
				area.add(cardSlice);
			}
			
		} else {
			area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));
			area.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
			JButton cardSlice;
			for (int i = 0; i < 11; i++) {
				cardSlice = new JButton(new ImageIcon("asset/card/tback.png"));
				cardSlice.setBorder(null);
				cardSlice.setEnabled(true);
				area.add(cardSlice);
			}
			cardSlice = new JButton(new ImageIcon("asset/card/back.png"));
			cardSlice.setBorder(null);
			area.add(cardSlice);
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
	
	/***/
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
