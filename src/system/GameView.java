package system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import sysobj.Card;
import sysobj.Player;
import sysobj.Rank;
import sysobj.Suit;

/**
 * The View, where all UI elements are generated and drawn.
 * @since 23
 */
public class GameView extends JFrame {

	/* --------------------------------------------------------- */
	/* ------------------------ FIELDS ------------------------- */
	/* --------------------------------------------------------- */

	/* ---------- Player Information ---------- */

	/** Panel that holds the cards for the player in the the North zone of the UI */
	private JPanel playerNorthCards;

	/** Label for the North player's name */
	private JLabel playerNorthName;

	/** Panel that holds the cards for the player in the the East zone of the UI */
	private JPanel playerEastCards;

	/** Label for the East player's name */
	private JLabel playerEastName;

	/** Panel that holds the cards for the player in the the West zone of the UI */
	private JPanel playerWestCards;

	/** Label for the West player's name */
	private JLabel playerWestName;

	/** Panel that holds the cards for the player in the the South zone of the UI */
	private JPanel playerSouthCards;

	/** Label for the South player's name */
	private JLabel playerSouthName;

	/** Score for North player */
	private JLabel playerNorthScore;

	/** Score for East player */
	private JLabel playerEastScore;

	/** Score for West player */
	private JLabel playerWestScore;

	/** Score for South player */
	private JLabel playerSouthScore;

	
	/* ---------- GUI Menu Buttons ---------- */

	/** Menu bar */
	private JMenuBar mBar;

	/** Opens submenu for starting singleplayer/multiplayer game */
	private JMenu mStartGame;

	/** Joins a multiplayer game that is currently running */
	private JMenuItem mJoinGame;

	/** Disconnects from a running multiplayer game */
	private JMenuItem mDisconnect;

	/** Submenu that contains various options */
	private JMenu mOptions;

	/** Opens the rules */
	private JMenuItem mRules;

	/** Submenu for language selection */
	private JMenu langSelect;

	/** Changes language to English */
	private JMenuItem langEng;

	/** Changes language to French */
	private JMenuItem langFr;

	/** Toggles sound effects on or off */
	private JCheckBoxMenuItem soundToggle;

	/** Toggles music on or off */
	private JCheckBoxMenuItem musicToggle;

	/** Starts a new game against AI */
	private JMenuItem mSinglePlayer;

	/** Starts a new game against humans and AI */
	private JMenuItem mHostGame;

	/** The last played (discarded) card */
	private JButton playedCards;

	/** The panel containing the button "playedCards". */
	private JPanel cardsPlayed;

	/** The stack of un-drawn cards */
	private JButton library;

	/** Chat send button (for sending chat messages) */
	private JButton chatSend;

	
	/* ---------- Miscellaneous ---------- */

	/**
	 * The window containing the game.
	 */
	JFrame gui;

	/**
	 * SerialVersionID.
	 * Default: @value 1L
	 */
	private static final long serialVersionUID = 1L;

	/** Font used by UI elements */
	private Font myFont;

	/** GridBagConstraints for vertically aligned elements */
	private GridBagConstraints myGBC;

	/** Where chat messages appear */
	private JTextPane chatDisplay;

	/** Where chat messages are typed before being sent */
	private TextField chatInput;

	/** UI representation of the current turn order */
	private JLabel turnOrder;

	/** How many times has pack() been called during game init */
	private int packCalls;

	/** The status of how many players are connected. */
	private JLabel connectionStatus;

	/** The dialog containing the "waiting for players..." information. */
	private JDialog waitingDialog;

	
	/* ---------- Internationalization ---------- */

	/** The current resource bundle */
	private ResourceBundle translatable;

	/** The current language */
	private Locale language;

	/** The title "score" in the scorebox */
	private JLabel scoreTitle;

	/** The chat box (where chat messages appear/are input) */
	private JPanel chatBox;
	

	/* ----------------------------------------------------------- */
	/* ------------------------- METHODS ------------------------- */
	/* ----------------------------------------------------------- */

	/**
	 * Default constructor for the GameView. Initializes a few things such as the
	 * gridbagconstraints, locale, resourcebundle, and then draws the splash and
	 * the main UI for the application.
	 * 
	 * @since 23
	 */
	public GameView() {
		myGBC = new GridBagConstraints();
		myGBC.gridx = 0;
		myGBC.gridy = GridBagConstraints.RELATIVE;
		myGBC.anchor = GridBagConstraints.CENTER;
		packCalls = 0;
		language = Locale.ENGLISH;
		translatable = ResourceBundle.getBundle("resources.MessagesBundle", language);
	}

	/**
	 * Draws the GUI elements. Each GUI element is a JPanel, which holds other
	 * JPanels. There are outer elements, which hold the title bar, console,
	 * player's hand, and the gameplay area. Each of these areas is further broken
	 * down into several other components.
	 * 
	 * 
	 * @since 23
	 */
	public void drawMainWindow() {

		/* ---------- PREAMBLE ---------- */

		// Fetch my custom font from the assets folder
		myFont = getMyFont("asset/font/snes-fonts-mario-paint.ttf");

		// Outer panel holds play area and console
		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BorderLayout());

		// Inner holds the game play area
		JPanel gameElements = new JPanel();
		gameElements.setLayout(new BorderLayout());

		/* ------------------------------------------------------------------------- */
		/* ------------------------- CONSOLE PANEL SECTION ------------------------- */
		/* ------------------------------------------------------------------------- */

		// Console holds the chat entry, chat display, and score display
		JPanel console = new JPanel();
		console.setLayout(new BorderLayout());
		console.setBackground(Const.BACKGROUND_PINK);
		console.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.BLACK));

		/* ---------- Score Elements ---------- */

		// ScoreBoxWrapper to glue everything together
		JPanel scoreBoxWrapper = new JPanel();
		scoreBoxWrapper.setLayout(new BorderLayout());
		scoreBoxWrapper.setBackground(null);

		// Scorebox holds the "score" label and the users, with their scores
		JPanel scoreBox = new JPanel();
		scoreBox.setBackground(Color.WHITE);
		scoreBox.setBorder(BorderFactory.createLineBorder(Const.BORDER_BLUE, 2));
		scoreBox.setLayout(new GridBagLayout());

		// Score label
		scoreTitle = new JLabel(translatable.getString("score").toUpperCase());
		scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		scoreTitle.setFont(myFont.deriveFont(14f));

		// Initialize score box labels
		playerNorthScore = new JLabel();
		playerNorthScore.setFont(myFont);
		playerEastScore = new JLabel();
		playerEastScore.setFont(myFont);
		playerWestScore = new JLabel();
		playerWestScore.setFont(myFont);
		playerSouthScore = new JLabel();
		playerSouthScore.setFont(myFont);
		turnOrder = new JLabel();
		turnOrder.setFont(myFont);

		// Adding elements to score box
		scoreBox.add(scoreTitle, myGBC);
		scoreBox.add(playerNorthScore, myGBC);
		scoreBox.add(playerEastScore, myGBC);
		scoreBox.add(playerWestScore, myGBC);
		scoreBox.add(playerSouthScore, myGBC);
		scoreBox.add(turnOrder, myGBC);

		// Adding console to wrappers
		scoreBoxWrapper.add(scoreBox);
		scoreBoxWrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

		/* ---------- Chat Elements ---------- */

		// Wrapper Panel for chat elements
		JPanel chatBoxWrapper = new JPanel();
		chatBoxWrapper.setLayout(new FlowLayout());
		chatBoxWrapper.setBackground(null);
		chatBoxWrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

		// Wrapper Panel for chat display elements
		JPanel chatDisplayWrapper = new JPanel();
		chatDisplayWrapper.setLayout(new BorderLayout());
		chatDisplayWrapper.setBackground(null);
		chatDisplayWrapper.setLayout(new BorderLayout());
		chatDisplayWrapper.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

		// TextArea for chat input
		chatInput = new TextField(20);
		chatInput.setBackground(Color.WHITE);
		chatInput.setFont(new Font("Times New Roman", Font.PLAIN, 12));

		// JTextArea for chat display
		chatDisplay = new JTextPane();
		chatDisplay.setEditorKit(new StyledEditorKit());
		chatDisplay.setEditable(false);
		chatDisplay.setBorder(BorderFactory.createLineBorder(Const.BORDER_BLUE, 2));

		// Scroll pane for chat display
		JScrollPane chatDisplayScroll = new JScrollPane(chatDisplay);

		// Chat "send" button
		chatSend = new JButton(translatable.getString("send").toUpperCase());
		chatSend.setFocusable(false);
		chatSend.setFont(myFont.deriveFont(12f));

		// ChatBox = chatInput + chatSend
		chatBox = new JPanel();
		chatBox.setBackground(Color.WHITE);
		chatBox.setLayout(new FlowLayout());
		chatBox.setBorder(BorderFactory.createLineBorder(Const.BORDER_BLUE, 2));

		// Add chatInput and chatSend button to chatBox panel
		chatBox.add(chatInput);
		chatBox.add(chatSend);

		// Wrapping the scrolling chat display
		chatDisplayWrapper.add(chatDisplayScroll, BorderLayout.CENTER);

		// Wrapping the chat box
		chatBoxWrapper.add(chatBox);

		// Adding wrapped Chat, Display, and Score panels to the console
		console.add(BorderLayout.SOUTH, chatBoxWrapper);
		console.add(BorderLayout.CENTER, chatDisplayWrapper);
		console.add(BorderLayout.NORTH, scoreBoxWrapper);

		/* ---------------------------------------------------------------------- */
		/* ------------------------- GAME PANEL SECTION ------------------------- */
		/* ---------------------------------------------------------------------- */

		/* ---------- NORTH PLAYER ---------- */

		/*
		 * playerNorth holds the cards (playerNorthCards) in its SOUTH border, its name
		 * (playerNorthName) in the NORTH border, and then within the cards panel,
		 * FlowLayout is used to add cards to hand. All of this is then added to the
		 * NORTH section of the main game panel
		 */
		JPanel playerNorth = new JPanel();
		playerNorth.setLayout(new BorderLayout());
		this.playerNorthCards = new JPanel();
		playerNorthCards.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		playerNorthCards.setBackground(null);
		playerNorthName = new JLabel("");
		playerNorthName.setFont(myFont);
		playerNorthName.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		playerNorthName.setHorizontalAlignment(SwingConstants.CENTER);
		playerNorth.add(BorderLayout.NORTH, playerNorthName);
		playerNorth.add(BorderLayout.SOUTH, playerNorthCards);
		playerNorth.setBackground(Const.BACKGROUND_PINK);

		/* ---------- EAST PLAYER ---------- */

		// Panel to hold Name and Cards
		JPanel playerEast = new JPanel();
		playerEast.setBackground(Const.BACKGROUND_PINK);
		playerEast.setLayout(new GridBagLayout());

		// Label for the name
		playerEastName = new JLabel("");
		playerEastName.setFont(myFont);
		playerEastName.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		// Panel for cards held
		playerEastCards = new JPanel(new GridBagLayout());

		// Add elements to the pEast container
		playerEast.add(playerEastName, myGBC);
		playerEast.add(playerEastCards, myGBC);

		// Wrapper to hold East Player's elements
		JPanel pEastWrapper = new JPanel();
		pEastWrapper.setBackground(Const.BACKGROUND_PINK);
		pEastWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		pEastWrapper.add(playerEast);

		/* ---------- WEST PLAYER ---------- */

		// Panel to hold Name and Cards
		JPanel playerWest = new JPanel();
		playerWest.setBackground(Const.BACKGROUND_PINK);
		playerWest.setLayout(new GridBagLayout());

		// Label for the name
		playerWestName = new JLabel("");
		playerWestName.setFont(myFont);
		playerWestName.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		// Panel for cards held
		playerWestCards = new JPanel(new GridBagLayout());

		// Add elements to the pWest container
		playerWest.add(playerWestName, myGBC);
		playerWest.add(playerWestCards, myGBC);

		// Wrapper to hold East Player's elements
		JPanel pWestWrapper = new JPanel();
		pWestWrapper.setBackground(Const.BACKGROUND_PINK);
		pWestWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		pWestWrapper.add(playerWest);

		/* ---------- MAIN PLAYER / SOUTH PLAYER ---------- */

		// Hand for the user interacting with the app (located in the south)
		JPanel playerSouth = new JPanel(new BorderLayout());
		playerSouth.setBackground(Const.BACKGROUND_PINK);
		playerSouthName = new JLabel("");
		playerSouthName.setFont(myFont);
		playerSouthName.setHorizontalAlignment(SwingConstants.CENTER);
		playerSouthCards = new JPanel();
		playerSouthCards.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		playerSouthCards.setBackground(Const.BACKGROUND_PINK);
		playerSouth.add(BorderLayout.NORTH, playerSouthName);
		playerSouth.add(BorderLayout.SOUTH, playerSouthCards);

		/* ---------- GAME LOGO ---------- */

		// Icon that sits in the south-to-center of screen
		ImageIcon gameLogoImg = new ImageIcon("asset/img/logo_sm.png");
		JLabel gameLogo = new JLabel(gameLogoImg);
		gameLogo.setVerticalAlignment(SwingConstants.CENTER);
		gameLogo.setHorizontalAlignment(SwingConstants.CENTER);

		/* ---------- PLAYING AREA ---------- */

		// Library refers to the pile of face-down cards that have yet to be drawn
		JPanel cardLibrary = new JPanel();
		cardLibrary.setBackground(Const.BACKGROUND_PINK);
		library = new JButton(new ImageIcon("asset/card/back.png"));
		library.setBorder(null);
		cardLibrary.add(library);

		// Played cards have been played by players and are face-up
		cardsPlayed = new JPanel();
		cardsPlayed.setBackground(Const.BACKGROUND_PINK);
		playedCards = new JButton();
		playedCards.setBorder(null);
		cardsPlayed.add(playedCards);

		// Panel to hold the library and the played cards
		JPanel playingArea = new JPanel();
		playingArea.setBackground(Const.BACKGROUND_PINK);
		playingArea.setLayout(new FlowLayout());
		playingArea.add(cardLibrary);
		playingArea.add(cardsPlayed);

		// Holds the logo panel plus the combined library + played cards panel
		JPanel playingAreaAndLogo = new JPanel();
		playingAreaAndLogo.setLayout(new BorderLayout());
		playingAreaAndLogo.setBackground(Const.BACKGROUND_PINK);
		playingAreaAndLogo.add(BorderLayout.SOUTH, gameLogo);
		playingAreaAndLogo.add(BorderLayout.NORTH, playingArea);

		// Add all gameplay elements to game area
		gameElements.add(BorderLayout.NORTH, playerNorth);
		gameElements.add(BorderLayout.EAST, pEastWrapper);
		gameElements.add(BorderLayout.WEST, pWestWrapper);
		gameElements.add(BorderLayout.SOUTH, playerSouth);
		gameElements.add(BorderLayout.CENTER, playingAreaAndLogo);

		/* ------------------------------------------------------ */
		/* -------------------- MENU SECTION -------------------- */
		/* ------------------------------------------------------ */

		// Menu Bar
		mBar = new JMenuBar();
		mBar.setLayout(new FlowLayout(FlowLayout.LEADING));

		/* ----- Main menu options ----- */

		mStartGame = new JMenu(translatable.getString("startGame"));

		mJoinGame = new JMenuItem(translatable.getString("joinGame"));
		mJoinGame.setEnabled(true);

		mDisconnect = new JMenuItem(translatable.getString("disconnect"));
		mDisconnect.setEnabled(false);

		mOptions = new JMenu(translatable.getString("options"));
		mOptions.setVisible(true);

		mRules = new JMenuItem(translatable.getString("rules"));
		mRules.setEnabled(true);

		/* ----- SUBMENUS ----- */

		langSelect = new JMenu(translatable.getString("language"));
		langEng = new JMenuItem(translatable.getString("english"));

		/*
		 * Game will start in English by default. The language the program is currently
		 * running in will be disabled as a selection.
		 */
		langEng.setEnabled(false);
		langFr = new JMenuItem(translatable.getString("french"));
		soundToggle = new JCheckBoxMenuItem(translatable.getString("soundEffects"));
		soundToggle.setSelected(true);
		musicToggle = new JCheckBoxMenuItem(translatable.getString("music"));
		musicToggle.setSelected(false);
		mSinglePlayer = new JMenuItem(translatable.getString("singlePlayer"));
		mHostGame = new JMenuItem(translatable.getString("hostGame"));

		// Add submenu items
		mOptions.add(soundToggle);
		mOptions.add(musicToggle);
		mOptions.add(langSelect);
		mOptions.add(mRules);
		langSelect.add(langEng);
		langSelect.add(langFr);
		mStartGame.add(mSinglePlayer);
		mStartGame.add(mHostGame);
		mStartGame.add(mJoinGame);
		mStartGame.add(mDisconnect);

		// Add menu items
		mBar.add(mStartGame);
		mBar.add(mOptions);

		/* ----------------------------------------------------------------------- */
		/* ------------------------- MAIN WINDOW SECTION ------------------------- */
		/* ----------------------------------------------------------------------- */

		// Adding components to the outer frame
		outerPanel.add(BorderLayout.EAST, console);
		outerPanel.add(BorderLayout.CENTER, gameElements);

		// Building the main window
		ImageIcon icon = new ImageIcon("asset/img/icon.png");
		gui = new JFrame();
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
	 * 
	 * @since 23
	 */
	public void drawSplash() {

		ImageIcon splashImage = new ImageIcon("asset/img/splash.png");
		JLabel splashImageLabel = new JLabel(splashImage);
		JPanel splashElements = new JPanel();
		splashElements.setLayout(new BorderLayout());
		splashElements.add(BorderLayout.CENTER, splashImageLabel);

		// Fake loading bar
		JProgressBar loading = new JProgressBar();
		loading.setMinimum(0);
		loading.setMaximum(100);
		loading.setStringPainted(Const.VISIBLE);

		splashElements.add(BorderLayout.SOUTH, loading);

		JWindow splashFrame = new JWindow();
		splashFrame.add(splashElements);
		splashFrame.setVisible(true);
		splashFrame.pack();
		splashFrame.setLocationRelativeTo(null);

		// Fake loading
		for (int i = 0; i <= 100; i += 5) {
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
	 * Resizes the Frame (after components have been added to it). When the app 
	 * is launched, there are no cards in any zones, but when the game begins,
	 * cards are added to all game zones of the UI. This refreshes the UI once for
	 * each zone, and calls pack each time.
	 * @param panel The panel that is contained by the frame as an ancestor.
	 * 
	 * @since 23
	 */
	public void resizeWindow(JPanel panel) {
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(panel);

		// Only pack the frame during initialization; once per play-zone
		if (packCalls <= 4) {
			frame.pack();
			packCalls++;
		}			
	}

	/**
	 * Packs the window.
	 */
	public void packWindow() {
		gui.pack();
	}

	/**
	 * Fetches my font from the asset folder.
	 * @param path The path to the font.
	 * @return The font to be used.
	 * 
	 * @since 23
	 */
	public Font getMyFont(String path) {
		File fontFile = new File(path);
		Font myFont = new Font("Arial", Font.PLAIN, 12);

		try {
			myFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(12f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}

		return myFont;
	}


	/* ----------------------------------------------------------- */
	/* -------------------- REFRESHER METHODS -------------------- */
	/* ----------------------------------------------------------- */

	/**
	 * Refresh the card image of the last card played to the played cards pile.
	 * This card image changes many times during the course of a game.
	 * @param card The last played card.
	 * 
	 * @since 23
	 */
	public void displayLastPlayedCard(Card card) {
		// Display the full card, face-up.
		ImageIcon playedCardImg = card.fetchCardImg(Const.FULL_CARD, Const.FULL_CARD, Const.FULL_CARD);
		playedCards.setIcon(playedCardImg);
		this.revalidate();
		this.repaint();
	}

	/**
	 * Displays all cards in a player's hand. Fetches a player's orientation (as
	 * this changes how the card is displayed) and clears the panel that holds 
	 * their cards. Then, the hand is rebuilt. This is called whenever a card is
	 * removed or added to the hand, as depending on if the card is the last card
	 * in the hand, it is drawn differently.
	 * @param p The player who's hand is to be redrawn.
	 * 
	 * @since 23
	 */
	public void displayCardsInHand(Player p) {
		int handSize = p.getHandSize();
		int orientation = p.getOrientation();
		JPanel handDisplay = null;

		// Defensive programming for future versions of this game. Never reached in current state.
		if (orientation < 0 || orientation > 3) {
			System.out.println("Orientation was < 0 || > 3 in displayCardsInHand");
			return;
		}

		switch (orientation) {
		case Const.NORTH:
			handDisplay = playerNorthCards;
			break;
		case Const.EAST:
			handDisplay = playerEastCards;
			break;
		case Const.SOUTH:
			handDisplay = playerSouthCards;
			break;
		case Const.WEST:
			handDisplay = playerWestCards;
			break;
		default:
			System.out.println("Default switch case reached in GameView.displayCardsInHand()");
		}

		handDisplay.removeAll();

		for (int i = 0; i < handSize; i++) {
			Card card = p.getHand().get(i);

			if (orientation == Const.SOUTH) {
				card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			} else {
				card.setBorder(null);
			}

			boolean isLastCard = (i == handSize - 1);

			// Only South sees their cards
			boolean isHidden = (orientation != Const.SOUTH); 

			if (orientation == Const.NORTH || orientation == Const.SOUTH) {
				// FlowLayout (Left-to-right)
				if (!isLastCard) {

					// Left slice for all but last card
					card.fetchCardImg(false, true, isHidden);  
				} else {

					// Full card for last card
					card.fetchCardImg(false, false, isHidden); 
				}
			} else if (orientation == Const.EAST || orientation == Const.WEST) {
				// GridBagLayout (Top-to-bottom)
				if (!isLastCard) {

					// Top slice for all but last card
					card.fetchCardImg(true, false, isHidden);  
				} else {

					// Full card for last card
					card.fetchCardImg(false, false, isHidden); 
				}
			}

			if (orientation == Const.EAST || orientation == Const.WEST) {
				handDisplay.add(card, myGBC);
			} else {
				handDisplay.add(card);
			}
		}

		handDisplay.revalidate();
		handDisplay.repaint();
		resizeWindow(handDisplay);
	}

	/**
	 * Removes a card image from a player's hand.
	 * 
	 * 
	 * @since 23
	 * @param card no description
	 */
	public void removeCardFromHand(Card card) {
		playerSouthCards.remove(card);
		playerSouthCards.revalidate();
		playerSouthCards.repaint();
	}

	/**
	 * Displays the cards in the client's hand.
	 * 
	 * 
	 * @since 23
	 * @param hand no description
	 * @param listener no description
	 */
	public void refreshClientHand(String hand, GameControllerListener listener) {

		if (hand.isEmpty()) {
			// No cards to display
			playerSouthCards.removeAll();
			playerSouthCards.revalidate();
			playerSouthCards.repaint();
			return;
		}

		String[] handStrArr = hand.split(",");
		Vector<Card> newHand = new Vector<>();

		playerSouthCards.removeAll();

		// convert the string representatoins of cards to card objects, add to temp hand
		for (String c : handStrArr) {
			newHand.add(Card.getCardFromStr(c));
		}

		// remove border, fetch the image, and add card imgs to the panel for the player
		for (int i = 0; i < newHand.size()-1; i++) {
			Card c = newHand.get(i);
			c.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			c.fetchCardImg(false, true, false);
			playerSouthCards.add(c);
		}

		// last card in hand is displayed as a full card image
		Card lastCard = newHand.getLast();
		lastCard.setBorder(null);
		lastCard.fetchCardImg(false, false, false);
		playerSouthCards.add(lastCard);

		// add listeners to the client's hand
		listener.onHandRefreshed(newHand);
		playerSouthCards.revalidate();
		playerSouthCards.repaint();
		resizeWindow(playerSouthCards);
	}

	/**
	 * Displays each opponent's "hand".
	 * 
	 * 
	 * @since 23
	 * @param opponentCardCount no description
	 * @param clientId no description
	 */
	public void refreshOpponentHands(String opponentCardCount, int clientId) {
		// Remove existing cards
		playerEastCards.removeAll();
		playerNorthCards.removeAll();
		playerWestCards.removeAll();

		// Handle empty string. highly unlikely
		if (opponentCardCount.isEmpty()) {
			System.out.println("refreshOpponentHands() was passed an empty string.");
			refreshOppHandsHelper(0, Const.EAST, playerEastCards);
			refreshOppHandsHelper(0, Const.NORTH, playerNorthCards);
			refreshOppHandsHelper(0, Const.WEST, playerWestCards);
			return;
		}

		String[] oppCardCountArr = opponentCardCount.split(",");

		// Total opponents (should be 3: 4 players - 1 self)
		int numOpponents = oppCardCountArr.length;
		/* Rotate indices based on clientId
		East: Previous player (clientId - 1)
		West: Next player (clientId + 1)
		North: Next + 1 player (clientId + 2) */
		int eastIdx = (clientId - 1 + numOpponents) % numOpponents;
		int westIdx = (clientId + 1) % numOpponents;
		int northIdx = (clientId + 2) % numOpponents;

		int oppEast = Integer.parseInt(oppCardCountArr[eastIdx]);
		int oppWest = Integer.parseInt(oppCardCountArr[westIdx]);
		int oppNorth = Integer.parseInt(oppCardCountArr[northIdx]);

		System.out.println("Client ID: " + clientId + ", East: " + oppEast + ", West: " + oppWest + ", North: " + oppNorth);

		refreshOppHandsHelper(oppEast, Const.EAST, playerEastCards);
		refreshOppHandsHelper(oppNorth, Const.NORTH, playerNorthCards);
		refreshOppHandsHelper(oppWest, Const.WEST, playerWestCards);
	}

	/**
	 * Displays the cards in the opponent's hands. These are all hidden, so the
	 * only needed information is how many cards to display and in what orientation
	 * are the cards being displayed.
	 * 
	 * 
	 * @since 23
	 * @param numCards no description
	 * @param orientation no description
	 * @param panel no description
	 */
	private void refreshOppHandsHelper(int numCards, int orientation, JPanel panel) {
		for (int i = 0; i < numCards; i++) {
			Card card = new Card(Rank.TWO, Suit.HEARTS);
			card.setBorder(null);
			boolean isLastCard = (i == numCards - 1);

			// Only South sees their cards
			boolean isHidden = (orientation != Const.SOUTH); 

			if (orientation == Const.NORTH || orientation == Const.SOUTH) {
				// FlowLayout (Left-to-right)
				if (!isLastCard) {

					// Left slice for all but last card
					card.fetchCardImg(false, true, isHidden);  
				} else {

					// Full card for last card
					card.fetchCardImg(false, false, isHidden); 
				}
			} else if (orientation == Const.EAST || orientation == Const.WEST) {
				// GridBagLayout (Top-to-bottom)
				if (!isLastCard) {

					// Top slice for all but last card
					card.fetchCardImg(true, false, isHidden);  
				} else {

					// Full card for last card
					card.fetchCardImg(false, false, isHidden); 
				}
			}

			if (orientation == Const.EAST || orientation == Const.WEST) {
				panel.add(card, myGBC);
			} else {
				panel.add(card);
			}
		}

		panel.revalidate();
		panel.repaint();
		resizeWindow(panel);
	}

	/**
	 * Update the labels that represent each player's name, i.e. when the game
	 * begins or when a new game is started and the name on the screen is no longer
	 * relevant.
	 * @param p The player who's name is to be displayed.
	 * @since 23
	 */
	public void updatePlayerNames(Player p) {
		String playerName = p.getName();
		int orientation = p.getOrientation();
		switch (orientation) {
		case Const.NORTH: playerNorthName.setText(playerName + " (" + p.getHandSize() + ")"); break;
		case Const.EAST: playerEastName.setText(playerName + " (" + p.getHandSize() + ")"); break;
		case Const.SOUTH: playerSouthName.setText(playerName + " (" + p.getHandSize() + ")"); break;
		case Const.WEST: playerWestName.setText(playerName + " (" + p.getHandSize() + ")"); break;
		default: System.out.println("Default case reached in GameView.updateNames().");
		}
	}

	/**
	 * Updates the status of a players score, as well as cards in hand, based on
	 * their orientation.
	 * @param p The player to update information for.
	 * @since 23
	 */
	private void updateScoreTable(Player p) {		
		String labelText = p.getName() + " = " + p.getScore();
		switch (p.getOrientation()) {
		case Const.NORTH: playerNorthScore.setText(labelText); break;
		case Const.EAST: playerEastScore.setText(labelText); break;
		case Const.WEST: playerWestScore.setText(labelText); break;
		case Const.SOUTH: playerSouthScore.setText(labelText); break;
		default: System.out.println("Default case reached in GameView.updateScoreTable().");
		}
	}

	/**
	 * Calls updateScoreTable for every player in the list of players, and also
	 * displays the current turn order (clockwise or counterclockwise).
	 * @param players The collection of players.
	 * @param isReversed The current turn order.
	 * @since 23
	 */
	public void refreshScores(Vector<Player> players, boolean isReversed) {
		for (Player p : players) {
			updateScoreTable(p);
			updatePlayerNames(p);
		}

		turnOrder.setText(isReversed ? translatable.getString("counterclockwise").toUpperCase() : 
			translatable.getString("clockwise").toUpperCase());

	}

	/**
	 * Refreshes the client's score table.
	 * 
	 * @since 23
	 * @param southName: the client's own name.
	 * @param id: the client's own id.
	 * @param names: the name of each connected player.
	 * @param scores: the score of each connected player.
	 * @param counts: the number of cards in each connected player's hand.
	 * @param turnDirection: clockwise/counterclockwise.
	 */
	public void refreshClientScoreTable(String southName, int id, String[] names, String scores, String counts, String turnDirection) {
		String[] scoreArr = scores.split(",");	    
		String[] countArr = counts.split(",");
		boolean isReversed = Boolean.parseBoolean(turnDirection);

		turnOrder.setText(isReversed ? translatable.getString("counterclockwise").toUpperCase() : 
			translatable.getString("clockwise").toUpperCase());

		// South = local player. Display name + card count
		playerSouthName.setText(southName + " (" + countArr[id] + ")");

		// Rotate others: West, North, East. Modulus is less readable imo but it keeps the code neater
		int playerCount = names.length;
		int westIdx = (id + 1) % playerCount;
		int northIdx = (id + 2) % playerCount;
		int eastIdx = (id + 3) % playerCount;

		playerWestName.setText(names[westIdx] + " (" + countArr[westIdx] + ")");
		playerNorthName.setText(names[northIdx] + " (" + countArr[northIdx] + ")");
		playerEastName.setText(names[eastIdx] + " (" + countArr[eastIdx] + ")");

		for (int i = 0; i < names.length; i++) {
			refreshClientScoreHelper(names[i], scoreArr[i], i);
		}
	}

	/**
	 * Updates the score table for each player with the player's name and score.
	 * 
	 * @since 23
	 * @param name player names
	 * @param score player scores
	 * @param orientation player orientations
	 */
	private void refreshClientScoreHelper(String name, String score, int orientation) {
		switch (orientation) {		
		case 0: playerSouthScore.setText(name + " = " + score); break;
		case 1: playerWestScore.setText(name + " = " + score); break;
		case 2: playerNorthScore.setText(name + " = " + score); break;
		case 3: playerEastScore.setText(name + " = " + score); break;
		default: System.out.println("Default case reached in GameView.refreshClientScoreHelper().");
		}
	}

	/**
	 * Revalidates and repaints the name and cards of each player. Used during
	 * testing to test different approaches for ending the game gracefully.
	 * @since 23
	 */
	public void refreshView() {
		playerSouthCards.revalidate();
		playerSouthCards.repaint();
		playerSouthName.revalidate();
		playerSouthName.repaint();
		playerWestCards.revalidate();
		playerWestCards.repaint();
		playerWestName.revalidate();
		playerWestName.repaint();
		playerNorthCards.revalidate();
		playerNorthCards.repaint();
		playerNorthName.revalidate();
		playerNorthName.repaint();
		playerEastCards.revalidate();
		playerEastCards.repaint();
		playerEastName.revalidate();
		playerEastName.repaint();
	}

	/**
	 * Resets the player names to blank.
	 * 
	 * @since 23
	 */
	public void resetPlayerNames() {
		playerSouthName.setText("");
		playerWestName.setText("");
		playerNorthName.setText("");
		playerEastName.setText("");		
	}

	/**
	 * Resets the player scoreboard to blank.
	 * 
	 * @since 23
	 */
	public void resetScoreBoard() {
		playerSouthScore.setText("");
		playerWestScore.setText("");
		playerNorthScore.setText("");
		playerEastScore.setText("");
		turnOrder.setText("");
	}

	/**
	 * Resets the player's console to blank.
	 * 
	 * @since 23
	 */
	public void resetConsole() {
		chatDisplay.setText("");
	}

	/**
	 * Resets the last played card's graphic to null.
	 * 
	 * @since 23
	 */
	public void resetLastPlayedCard() {
		playedCards.setIcon(null);
	}

	/**
	 * Resets the player hands to blank. 
	 * 
	 * @since 23
	 */
	public void resetHands() {
		playerSouthCards.removeAll();
		playerWestCards.removeAll();
		playerNorthCards.removeAll();
		playerEastCards.removeAll();
	}


	/* -------------------------------------------------------- */
	/* -------------------- DIALOGS/POPUPS -------------------- */
	/* -------------------------------------------------------- */

	/**
	 * Displays the dialog that shows "waiting for players... x/x.
	 * 
	 * @since 23
	 * @param port the port to connect
	 * @param cancelAction listener
	 * @param maxPlayers max no of players
	 */
	public void awaitConnectionsDialog(int port, ActionListener cancelAction, int maxPlayers) {
		waitingDialog = new JDialog((Frame) null, translatable.getString("hostGame"), true);
		waitingDialog.setLayout(new BorderLayout());

		connectionStatus = new JLabel(translatable.getString("waitingForOpponents") + " (0/" + maxPlayers + ")...");
		waitingDialog.add(connectionStatus, BorderLayout.CENTER);
		String ip;

		// Get IP dynamically, should always be localhost but good for if this changes
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// default to localhost
			ip = "127.0.0.1";
		}

		JLabel info = new JLabel(translatable.getString("ipAddress") + " " + ip + ":" + port, SwingConstants.CENTER);
		waitingDialog.add(info, BorderLayout.NORTH);

		// cancel button
		JButton btnCancel = new JButton(translatable.getString("cancel"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelAction.actionPerformed(e);
				waitingDialog.dispose();
			}
		});

		JPanel cancelPanel = new JPanel();
		cancelPanel.add(btnCancel);
		waitingDialog.add(cancelPanel, BorderLayout.SOUTH);
		waitingDialog.pack();
		waitingDialog.setLocationRelativeTo(null);
		waitingDialog.setVisible(true);
	}

	/**
	 * Updates the "waiting for players..." dialog with the amt. of players that
	 * have connected.
	 * 
	 * 
	 * @since 23
	 * @param playerCount no description
	 * @param maxPlayers no description
	 */
	public void updateWaitingStatus(int playerCount, int maxPlayers) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (connectionStatus != null) {
					connectionStatus.setText(translatable.getString("waitingForOpponents") + "(" + 
							playerCount + "/" + maxPlayers + ")...");
				}
			}
		});
	}

	/**
	 * Closes the "waiting for players..." dialog.
	 * 
	 * 
	 * @since 23
	 */
	public void closeWaitingDialog() {
		if (waitingDialog != null) {
			waitingDialog.dispose();
		}
	}

	/**
	 * Displays the rules of the game as a dialog.
	 * 
	 * @since 23
	 */
	public void displayRules() {
		String lang = "EN";

		if (language == Locale.ENGLISH) {
			lang = "EN";
		} else {
			lang = "FR";
		}

		String line;
		StringBuilder sb = new StringBuilder();
		try (BufferedReader r = new BufferedReader(new FileReader("asset/Rules_" + lang + ".txt"))){
			while ((line = r.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not find Rules.txt.");
		} catch (IOException e) {
			System.out.println("IO stream interrupted.");
		}

		JOptionPane.showMessageDialog(this, sb.toString(), translatable.getString("rules"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays the round winner in a dialog.
	 * @param player The winner of the round.
	 * 
	 * @since 23
	 */
	public void displayRoundWinner(Player player) {
		JOptionPane.showMessageDialog(this, player.getName() + " " + translatable.getString("roundWinner") +"!",
				translatable.getString("roundWinnerLabel"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays the round winner in a dialog.
	 * @param playerName player The winner of the round.
	 * 
	 * @since 23
	 */
	public void displayRoundWinner(String playerName) {
		JOptionPane.showMessageDialog(this, playerName + " " + translatable.getString("roundWinner") +"!",
				translatable.getString("roundWinnerLabel"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays the game winner in a dialog.
	 * @param winners The winners of the game.
	 * 
	 * @since 23
	 */
	public void displayGameWinners(Vector<Player> winners) {
		StringBuilder sb = new StringBuilder();
		for (Player p : winners) {
			sb.append(p.getName());
			sb.append(" ");
		}
		JOptionPane.showMessageDialog(this, sb.toString() + translatable.getString("gameWinner") + "!",
				translatable.getString("gameWinnerLabel"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Dialog that displays the game winners.
	 * 
	 * @param winnerNames no description
	 */
	public void displayGameWinners(String winnerNames) {
		JOptionPane.showMessageDialog(this, winnerNames + translatable.getString("gameWinner") + "!",
				translatable.getString("gameWinnerLabel"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Getter for player name.
	 * @return The player name.
	 * 
	 * @since 23
	 */
	public String getPlayerName() {
		UIManager.put("OptionPane.cancelButtonText", translatable.getString("cancel").toUpperCase());
		String playerName = JOptionPane.showInputDialog(null, translatable.getString("enterNameSinglePlayer"),
				translatable.getString("playerName"), JOptionPane.QUESTION_MESSAGE);

		if (playerName != null && !playerName.trim().isEmpty()) {
			if (playerName.length() > 20) {

				if (language == Locale.ENGLISH) {
					return "JOHN Q. LONGNAME";
				} else {
					return "JEAN Q. NOMARALLONGE";
				}
			}
			return playerName;
		} else {
			if (language == Locale.ENGLISH) {
				return "BUDDY COLE";
			} else {
				return "MONSIEUR PIEDLOURDE";
			}
		}
	}

	/**
	 * Dialog box for suit selection whenever an 8 is played by a human player.
	 * If the player clicks cancel or x, the suit defaults to the suit displayed
	 * on the card as it was when in the hand of the player who played it.
	 * @return The suit chosen.
	 * 
	 * @since 23
	 */
	public Suit dialogEightSuit() {
		Suit s = null;
		System.out.println("Eight has been played, selecting suit...");
		String[] suits = { "Hearts", "Diamonds", "Clubs", "Spades" };

		while (s == null) {
			int choice = JOptionPane.showOptionDialog(
					null, 
					translatable.getString("suitChoice"), 
					translatable.getString("suitChoiceLabel"), 
					JOptionPane.DEFAULT_OPTION, 
					JOptionPane.QUESTION_MESSAGE, 
					null, 
					suits, 
					suits[0]
					);

			if (choice >= 0) {
				switch (suits[choice]) {
				case "Hearts": s = Suit.HEARTS; break;
				case "Diamonds": s = Suit.DIAMONDS; break;
				case "Clubs": s = Suit.CLUBS; break;
				case "Spades": s = Suit.SPADES; break;
				}
			}
		}

		return s;
	}

	/**
	 * Sends a chat message to the chat window.
	 * @param msg The message content.
	 * @since 23
	 */
	public void displayChat(String msg) {
		StyledDocument doc = chatDisplay.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setForeground(attrs, Color.BLACK);

		try {
			doc.insertString(doc.getLength(), msg + "\n", attrs);
			chatDisplay.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a console message to the chat window (an update from the game informing
	 * players of actions taking place).
	 * @param optName Optional prefix. Used to display translatable console messages.
	 * @param msg The message content.
	 * @param optCard An optional suffix for console messages.
	 * @since 23
	 */
	public void sendConsoleMsg(String optName, String msg, String optCard) {
		StyledDocument doc = chatDisplay.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();

		// Console messages appear in bold-ed red font and are assembled by passed arguments
		String translatedStr = "";
		String completedStr = "";
		StyleConstants.setForeground(attrs, Color.RED);
		StyleConstants.setBold(attrs, true);

		// This is where console messages are assembled
		switch (msg) {
		case "currentTurn":
			translatedStr = translatable.getString("currentTurn");
			completedStr = translatedStr + ": " + optName;
			break;
		case "suitChanged":
			translatedStr = translatable.getString("suitChanged");
			completedStr = translatedStr + " " + optCard;
			break; 
		case "cantDraw":
			translatedStr = translatable.getString("cantDraw");
			completedStr = translatedStr;
			if (getSFXStatus()) {
				soundInvalidMove(musicToggle.isSelected());
			}
			break;
		case "notYourTurn":
			translatedStr = translatable.getString("notYourTurn");
			completedStr = translatedStr;
			if (getSFXStatus()) {
				soundInvalidMove(musicToggle.isSelected());
			}
			break;
		case "passTurn":
			translatedStr = translatable.getString("passTurn");
			completedStr = optName + " " + translatedStr;
			break;
		case "playCard":
			translatedStr = translatable.getString("playCard");
			completedStr = optName + " " + translatedStr + " " + optCard;
			if (getSFXStatus()) {
				soundPlayCard(musicToggle.isSelected());
			}
			break;
		case "drawCard":
			translatedStr = translatable.getString("drawCard");
			completedStr = optName + " " + translatedStr;
			if (getSFXStatus()) {
				soundDrawCard(musicToggle.isSelected());
			}
			break;
		case "forceDraw":
			translatedStr = translatable.getString("forceDraw");
			completedStr = optName + " " + translatedStr + optCard;
			break;
		case "turnReversed":
			translatedStr = translatable.getString("turnReversed");
			completedStr = optName + " " + translatedStr;
			if (getSFXStatus()) {
				soundTurnReversed(musicToggle.isSelected());
			}
			break;
		case "turnSkipped":
			translatedStr = translatable.getString("turnSkipped");
			completedStr = optName + " " + translatedStr;
			if (getSFXStatus()) {
				soundTurnSkipped(musicToggle.isSelected());
			}
			break;
		case "newRound":
			translatedStr = translatable.getString("newRound");
			completedStr = translatedStr;
			if (getSFXStatus()) {
				soundNewRound(musicToggle.isSelected());
			}
			break;
		}

		try {
			doc.insertString(doc.getLength(), completedStr + "\n", attrs);
			chatDisplay.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Fetches the message from the chat input area so that it can be displayed
	 * in the chat box.
	 * @return The message to be displayed.
	 * 
	 * @since 23
	 */
	public String fetchMsg() {
		String msg = chatInput.getText();
		chatInput.setText("");
		return msg;
	}

	/**
	 * Sets the language to French by changing the Locale and the ResourceBundle.
	 * 
	 * @since 23
	 */
	public void setLanguageToFrench() {
		language = Locale.FRENCH;
		translatable = ResourceBundle.getBundle("resources.MessagesBundle", language);
		langFr.setEnabled(false);
		langEng.setEnabled(true);
		repaintTranslatable();
	}

	/**
	 * Sets the language to English by changing the Locale and the ResourceBundle.
	 * 
	 * @since 23
	 */
	public void setLanguageToEnglish() {
		language = Locale.ENGLISH;
		langFr.setEnabled(true);
		langEng.setEnabled(false);
		translatable = ResourceBundle.getBundle("resources.MessagesBundle", language);
		repaintTranslatable();
	}

	/**
	 * Some elements require revalidating/repainting after translation. This method
	 * just repaints all those elements.
	 * 
	 * @since 23
	 */
	public void repaintTranslatable() {
		scoreTitle.revalidate();
		scoreTitle.repaint();
		turnOrder.revalidate();
		turnOrder.repaint();
		chatSend.setText(translatable.getString("send").toUpperCase());
		mStartGame.setText(translatable.getString("startGame"));
		mJoinGame.setText(translatable.getString("joinGame"));
		mDisconnect.setText(translatable.getString("disconnect"));
		mOptions.setText(translatable.getString("options"));
		mRules.setText(translatable.getString("rules"));
		langSelect.setText(translatable.getString("language"));
		langEng.setText(translatable.getString("english"));
		langFr.setText(translatable.getString("french"));
		soundToggle.setText(translatable.getString("soundEffects"));
		musicToggle.setText(translatable.getString("music"));
		mSinglePlayer.setText(translatable.getString("singlePlayer"));
		mHostGame.setText(translatable.getString("hostGame"));
	}

	/**
	 * Shows the dialog for when a new game starts.
	 * 
	 * 
	 * @since 23
	 */
	public void gameStartDialog() {
		JOptionPane.showMessageDialog(null, translatable.getString("newGame"), 
				translatable.getString("letsPlay"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Sets the status of the menu buttons to the single player configuration.
	 * 
	 * 
	 * @since 23
	 */
	public void setBtnsSingleplayer() {
		mHostGame.setEnabled(false);
		mJoinGame.setEnabled(false);
		mDisconnect.setEnabled(true);
	}

	/**
	 * Sets the status of the menu buttons to the multiplayer configuration.
	 * 
	 * 
	 * @since 23
	 */
	public void setBtnsMultiplayer() {
		mDisconnect.setEnabled(true);
		mSinglePlayer.setEnabled(false);
		mHostGame.setEnabled(false);
		mJoinGame.setEnabled(false);
	}

	/**
	 * Sets the status of the menu buttons to the main menu configuration.
	 * 
	 * 
	 * @since 23
	 */
	public void setBtnsMainMenu() {
		mDisconnect.setEnabled(false);
		mSinglePlayer.setEnabled(true);
		mHostGame.setEnabled(true);
		mJoinGame.setEnabled(true);
	}

	
	/* ------------------------------------------------------------------- */
	/* ------------------------- SOUND AND MUSIC ------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * Plays the sound effect for when a makes an invalid move.
	 * 
	 * 
	 * @since 23
	 * @param is8bit plays a bitcrushed version of the sound instead.
	 */
	public void soundInvalidMove(boolean is8bit) {
		String fileName;
		if (is8bit) {
			fileName = "crushed_invalidMove.wav";
		} else {
			fileName = "invalidMove.wav";
		}

		File cantPlay = new File("asset/sound/" + fileName);

		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(cantPlay));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays the sound effect for when a player passes the turn.
	 * 
	 * 
	 * @since 23
	 * @param is8bit plays a bitcrushed version of the sound instead.
	 */
	public void soundPassTurn(boolean is8bit) {
		String fileName;
		if (is8bit) {
			fileName = "crushed_powerUp.wav";
		} else {
			fileName = "powerUp.wav";
		}

		File passTurn = new File("asset/sound/" + fileName);

		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(passTurn));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays the sound effect for when a new round starts.
	 * 
	 * 
	 * @since 23
	 * @param is8bit plays a bitcrushed version of the sound instead.
	 */
	public void soundNewRound(boolean is8bit) {
		String fileName;
		if (is8bit) {
			fileName = "crushed_pickupCoin.wav";
		} else {
			fileName = "pickupCoin.wav";
		}

		File newRound = new File("asset/sound/" + fileName);

		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(newRound));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays the sound effect for when a player plays a card.
	 * 
	 * 
	 * @since 23
	 * @param is8bit plays a bitcrushed version of the sound instead.
	 */
	public void soundPlayCard(boolean is8bit) {
		String fileName;
		if (is8bit) {
			fileName = "crushed_playCard.wav";
		} else {
			fileName = "playCard.wav";
		}

		File playCard = new File("asset/sound/" + fileName);

		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(playCard));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays the sound effect for when a player draws a card.
	 * 
	 * 
	 * @since 23
	 * @param is8bit plays a bitcrushed version of the sound instead.
	 */
	public void soundDrawCard(boolean is8bit) {
		String fileName;
		if (is8bit) {
			fileName = "crushed_drawCard.wav";
		} else {
			fileName = "drawCard.wav";
		}

		File drawCard = new File("asset/sound/" + fileName);

		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(drawCard));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays the sound effect for when the turn order is reversed.
	 * 
	 * 
	 * @since 23
	 * @param is8bit plays a bitcrushed version of the sound instead.
	 */
	public void soundTurnReversed(boolean is8bit) {
		String fileName;
		if (is8bit) {
			fileName = "crushed_powerUp.wav";
		} else {
			fileName = "powerUp.wav";
		}

		File turnReversed = new File("asset/sound/" + fileName);

		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(turnReversed));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays the sound effect for when a player's turn is skipped.
	 * 
	 * 
	 * @since 23
	 * @param is8bit plays a bitcrushed version of the sound instead.
	 */
	public void soundTurnSkipped(boolean is8bit) {
		String fileName;
		if (is8bit) {
			fileName = "crushed_jump.wav";
		} else {
			fileName = "jump.wav";
		}

		File turnSkipped = new File("asset/sound/" + fileName);

		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(turnSkipped));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter for the status of the Sound effects tickbox.
	 * 
	 * 
	 * @since 23
	 * @return true if on, false if off.
	 */
	public boolean getSFXStatus() {
		return soundToggle.isSelected();
	}

	/**
	 * Getter for the status of the music tickbox.
	 * 
	 * 
	 * @since 23
	 * @return true if on, false if off.
	 */
	public boolean getMusicStatus() {
		return musicToggle.isSelected();
	}


	/* ----------------------------------------------------------- */
	/* -------------------- LISTENERS & OTHER -------------------- */
	/* ----------------------------------------------------------- */

	/**
	 * Pack should only be called 4 times while instantiating the game, this
	 * allows the flag to be reset.
	 * @param i The number to reset the flag to.
	 * @since 23
	 */
	public void setPackCalls(int i) {
		this.packCalls = i;
	}

	/**
	 * Sets the action listener for the single-player button.
	 *
	 * @param listener The action listener to handle single-player selection.
	 * @since 23
	 */
	public void setSinglePlayerListener(ActionListener listener) {
		mSinglePlayer.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the multiplayer button.
	 *
	 * @param listener The action listener to handle multiplayer selection.
	 * @since 23
	 */
	public void setMultiPlayerListener(ActionListener listener) {
		mHostGame.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the join game button.
	 *
	 * @param listener The action listener to handle joining a game.
	 * @since 23
	 */
	public void setJoinGameListener(ActionListener listener) {
		mJoinGame.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the disconnect button.
	 *
	 * @param listener The action listener to handle disconnection.
	 * @since 23
	 */
	public void setDisconnectListener(ActionListener listener) {
		mDisconnect.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the options button.
	 *
	 * @param listener The action listener to open the options menu.
	 * @since 23
	 */
	public void setOptionsListener(ActionListener listener) {
		mOptions.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the about button.
	 *
	 * @param listener The action listener to display the about section. 
	 * @since 23
	 */
	public void setAboutListener(ActionListener listener) {
		mRules.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the English language selection.
	 *
	 * @param listener The action listener to switch language to English.
	 * @since 23
	 */
	public void setLangEnglishListener(ActionListener listener) {
		langEng.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the French language selection.
	 *
	 * @param listener The action listener to switch language to French.
	 * @since 23
	 */
	public void setLangFrenchListener(ActionListener listener) {
		langFr.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the sound toggle button.
	 *
	 * @param listener The action listener to toggle sound on/off.
	 * @since 23
	 */
	public void setSoundToggleListener(ActionListener listener) {
		soundToggle.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the music toggle button.
	 *
	 * @param listener The action listener to toggle music on/off.
	 * @since 23
	 */
	public void setMusicToggleListener(ActionListener listener) {
		musicToggle.addActionListener(listener);
	}

	/**
	 * Sets the action listener for drawing a card from the library.
	 *
	 * @param listener The action listener to draw a card.
	 * @since 23
	 */
	public void setDrawFromLibraryListener(ActionListener listener) {
		library.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the chat send button.
	 *
	 * @param listener The action listener to send a chat message.
	 * @since 23
	 */
	public void setChatSendButtonListener(ActionListener listener) {
		chatSend.addActionListener(listener);
		chatInput.addActionListener(listener);
	}
	
	/**
	 * Returns the resourcebundle.
	 * 
	 * @return the translatable
	 */
	public ResourceBundle getTranslatable() {
		return translatable;
	}

}
