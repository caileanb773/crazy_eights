package system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import sysobj.Card;
import sysobj.Player;
import sysobj.Suit;

public class GameView extends JFrame {

	// Player hands and names
	private JPanel playerNorthCards;
	private JLabel playerNorthName;
	private JPanel playerEastCards;
	private JLabel playerEastName;
	private JPanel playerWestCards;
	private JLabel playerWestName;
	private JPanel playerSouthCards;
	private JLabel playerSouthName;

	// GUI Menu buttons
	private JMenuBar mBar;
	private JMenu mHostGame;
	private JMenu mJoinGame;
	private JMenu mDisconnect;
	private JMenu mOptions;
	private JMenuItem mAbout;
	private JMenu langSelect;
	private JMenuItem langEng;
	private JMenuItem langFr;
	private JCheckBoxMenuItem soundToggle;
	private JCheckBoxMenuItem musicToggle;
	private JMenuItem mSinglePlayer;
	private JMenuItem mMultiPlayer;
	private JButton playedCards;
	private JButton library;
	private JButton chatSend;

	// Player scores
	private JLabel playerNorthScore;
	private JLabel playerEastScore;
	private JLabel playerWestScore;
	private JLabel playerSouthScore;

	// Other stuff
	private Font myFont;
	private GridBagConstraints myGBC;
	private static final long serialVersionUID = 1L;
	private JTextPane chatDisplay;
	private TextField chatInput;
	private JLabel turnOrder;
	private int packCalls;

	// Internationalization + all translatable elements (that aren't above)
	private ResourceBundle translatable;
	private Locale language;
	private JLabel scoreTitle;
	private JPanel chatBox;

	public GameView() {
		// Fetch my custom font from the assets folder
		myFont = getMyFont("asset/font/snes-fonts-mario-paint.ttf");
		myGBC = new GridBagConstraints();
		myGBC.gridx = 0;
		myGBC.gridy = GridBagConstraints.RELATIVE;
		myGBC.anchor = GridBagConstraints.CENTER;
		packCalls = 0;
		language = Locale.ENGLISH;
		translatable = ResourceBundle.getBundle("resources.MessagesBundle", language);

		// This is where the application is drawn. First the splash, then main window
		// TODO: don't forget to uncomment this
		//drawSplash();
		drawMainWindow();
	}

	/**
	 * Draws the GUI elements. Each GUI element is a JPanel, which holds other
	 * JPanels. There are outer elements, which hold the title bar, console,
	 * player's hand, and the gameplay area. Each of these areas is further broken
	 * down into several other components.
	 * 
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void drawMainWindow() {

		/* ---------- PREAMBLE ---------- */

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
		scoreTitle = new JLabel(translatable.getString("score"));
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
		JPanel cardsPlayed = new JPanel();
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
		mBar.setVisible(true);
		mBar.setLayout(new FlowLayout(FlowLayout.LEADING));

		/* ----- Main menu options ----- */

		mHostGame = new JMenu(translatable.getString("startGame"));
		mHostGame.setVisible(true);
		mHostGame.setEnabled(true);

		mJoinGame = new JMenu(translatable.getString("joinGame"));
		mJoinGame.setVisible(true);
		mJoinGame.setEnabled(true);

		mDisconnect = new JMenu(translatable.getString("disconnect"));
		mDisconnect.setVisible(true);
		mDisconnect.setEnabled(false);

		mOptions = new JMenu(translatable.getString("options"));
		mOptions.setVisible(true);
		mOptions.setEnabled(true);

		mAbout = new JMenuItem(translatable.getString("rules"));
		mAbout.setVisible(true);
		mAbout.setEnabled(true);

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
		musicToggle.setSelected(true);
		mSinglePlayer = new JMenuItem(translatable.getString("singlePlayer"));
		mMultiPlayer = new JMenuItem(translatable.getString("multiplayer"));

		// Add submenu items
		mOptions.add(soundToggle);
		mOptions.add(musicToggle);
		mOptions.add(langSelect);
		langSelect.add(langEng);
		langSelect.add(langFr);
		mHostGame.add(mSinglePlayer);
		mHostGame.add(mMultiPlayer);

		// Add menu items
		mBar.add(mHostGame);
		mBar.add(mJoinGame);
		mBar.add(mDisconnect);
		mBar.add(mOptions);
		mBar.add(mAbout);

		/* ----------------------------------------------------------------------- */
		/* ------------------------- MAIN WINDOW SECTION ------------------------- */
		/* ----------------------------------------------------------------------- */

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
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void resizeWindow(JPanel panel) {
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(panel);

		// Only pack the frame during initialization; once per play-zone
		if (packCalls != 4) {
			frame.pack();
			packCalls++;
		}			
		frame.revalidate();
		frame.repaint();
	}

	/**
	 * Fetches my font from the asset folder.
	 * @param path The path to the font.
	 * @return The font to be used.
	 * @author Cailean Bernard
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
	 * @author Cailean Bernard
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
	 * @author Cailean Bernard
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
	 * Update the labels that represent each player's name, i.e. when the game
	 * begins or when a new game is started and the name on the screen is no longer
	 * relevant.
	 * @param p The player who's name is to be displayed.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void updatePlayerNames(Player p) {
		String playerName = p.getName();
		int orientation = p.getOrientation();
		switch (orientation) {
		case Const.NORTH: playerNorthName.setText(playerName); break;
		case Const.EAST: playerEastName.setText(playerName); break;
		case Const.SOUTH: playerSouthName.setText(playerName); break;
		case Const.WEST: playerWestName.setText(playerName); break;
		default: System.out.println("Default case reached in GameView.updateNames().");
		}
	}

	/**
	 * Updates the status of a players score, as well as cards in hand, based on
	 * their orientation.
	 * @param p The player to update information for.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private void updateScoreTable(Player p) {		
		String labelText = p.getName() + " = " + p.getScore() + ", " +
				translatable.getString("cards").toUpperCase() + " = " +
				p.getHandSize();
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
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void refreshScores(List<Player> players, boolean isReversed) {

		for (Player p : players) {
			updateScoreTable(p);
		}
		if (isReversed) {
			turnOrder.setText(translatable.getString("counterclockwise"));
		} else {
			turnOrder.setText(translatable.getString("clockwise"));
		}
	}

	/**
	 * Revalidates and repaints the name and cards of each player. Used during
	 * testing to test different approaches for ending the game gracefully.
	 * @author Cailean Bernard
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
		playerWestCards.revalidate();
		playerWestCards.repaint();
		playerWestName.revalidate();
		playerWestName.repaint();
	}

	/* -------------------------------------------------------- */
	/* -------------------- DIALOGS/POPUPS -------------------- */
	/* -------------------------------------------------------- */

	/**
	 * Displays the rules of the game as a dialog.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void displayAbout() {
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
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void displayRoundWinner(Player player) {
		JOptionPane.showMessageDialog(this, player.getName() + " " + translatable.getString("roundWinner") +"!",
				translatable.getString("roundWinnerLabel"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays the game winner in a dialog.
	 * @param player The winner of the game.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void displayGameWinner(Player player) {
		JOptionPane.showMessageDialog(this, player.getName() + " " + translatable.getString("gameWinner") + "!",
				translatable.getString("gameWinnerLabel"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Getter for player name.
	 * @return The player name.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public String getPlayerName() {
		String playerName = JOptionPane.showInputDialog(null, translatable.getString("enterNameSinglePlayer"),
				translatable.getString("playerName"), JOptionPane.QUESTION_MESSAGE);

		if (playerName != null && !playerName.trim().isEmpty()) {
			if (playerName.length() > 20) {
				
				if (language == Locale.ENGLISH) {
					return "JOHN Q. LONGNAME";
				} else {
					return "JEAN Q. NOMÀRALLONGE";
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
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Suit dialogEightSuit() {
		Suit s = null;
		System.out.println("Eight has been played, selecting suit...");
		String[] suits = { "Hearts", "Diamonds", "Clubs", "Spades" };
		String chosenSuit = (String) JOptionPane.showInputDialog(
				null, 
				translatable.getString("suitChoice"), 
				translatable.getString("suitChoiceLabel"), 
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				suits, 
				suits[0]
				);

		if (chosenSuit != null) {
			switch (chosenSuit) {
			case "Hearts": s = Suit.HEARTS; break;
			case "Diamonds": s = Suit.DIAMONDS; break;
			case "Clubs": s = Suit.CLUBS; break;
			case "Spades": s = Suit.SPADES; break;
			default: System.out.println("default reached while choosing suit for eight.");
			return null;
			}
		}
		return s;
	}

	/**
	 * Sends a chat message to the chat window.
	 * @param msg The message to be displayed in the chat box.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void sendChatMsg(String msg) {
		StyledDocument doc = chatDisplay.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), msg, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetches the message from the chat input area so that it can be displayed
	 * in the chat box.
	 * @return The message to be displayed.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public String fetchMsg() {
		String msg = "";
		msg = chatInput.getText();
		return msg;
	}

	/**
	 * Pack should only be called 4 times while instantiating the game, this
	 * allows the flag to be reset.
	 * @param i The number to reset the flag to.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setPackCalls(int i) {
		this.packCalls = i;
	}

	/**
	 * Sets the language to French by changing the Locale and the ResourceBundle.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setLanguageToFrench() {
		language = Locale.FRENCH;
		translatable = ResourceBundle.getBundle("resources.MessagesBundle", language);
		repaintTranslatable();
	}

	/**
	 * Sets the language to English by changing the Locale and the ResourceBundle.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setLanguageToEnglish() {
		language = Locale.ENGLISH;
		translatable = ResourceBundle.getBundle("resources.MessagesBundle", language);
		repaintTranslatable();
	}
	
	/**
	 * Some elements require revalidating/repainting after translation. This method
	 * just repaints all those elements.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void repaintTranslatable() {
		chatSend.revalidate();
		chatSend.repaint();
		chatBox.revalidate();
		chatBox.repaint();
	}

	/* --------------------------------------------------- */
	/* -------------------- LISTENERS -------------------- */
	/* --------------------------------------------------- */

	/**
	 * Sets the action listener for the single-player button.
	 *
	 * @param listener The action listener to handle single-player selection.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setSinglePlayerListener(ActionListener listener) {
		mSinglePlayer.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the multiplayer button.
	 *
	 * @param listener The action listener to handle multiplayer selection.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setMultiPlayerListener(ActionListener listener) {
		mMultiPlayer.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the host game button.
	 *
	 * @param listener The action listener to handle hosting a game.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setHostGameListener(ActionListener listener) {
		mHostGame.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the join game button.
	 *
	 * @param listener The action listener to handle joining a game.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setJoinGameListener(ActionListener listener) {
		mJoinGame.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the disconnect button.
	 *
	 * @param listener The action listener to handle disconnection.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setDisconnectListener(ActionListener listener) {
		mDisconnect.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the options button.
	 *
	 * @param listener The action listener to open the options menu.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setOptionsListener(ActionListener listener) {
		mOptions.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the about button.
	 *
	 * @param listener The action listener to display the about section.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setAboutListener(ActionListener listener) {
		mAbout.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the English language selection.
	 *
	 * @param listener The action listener to switch language to English.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setLangEnglishListener(ActionListener listener) {
		langEng.addActionListener(listener);
		System.out.println("Translating elements to English...");
	}

	/**
	 * Sets the action listener for the French language selection.
	 *
	 * @param listener The action listener to switch language to French.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setLangFrenchListener(ActionListener listener) {
		langFr.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the sound toggle button.
	 *
	 * @param listener The action listener to toggle sound on/off.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setSoundToggleListener(ActionListener listener) {
		soundToggle.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the music toggle button.
	 *
	 * @param listener The action listener to toggle music on/off.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setMusicToggleListener(ActionListener listener) {
		musicToggle.addActionListener(listener);
	}

	/**
	 * Sets the action listener for drawing a card from the library.
	 *
	 * @param listener The action listener to draw a card.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setDrawFromLibraryListener(ActionListener listener) {
		library.addActionListener(listener);
	}

	/**
	 * Sets the action listener for the chat send button.
	 *
	 * @param listener The action listener to send a chat message.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setChatSendButtonListener(ActionListener listener) {
		chatSend.addActionListener(listener);
	}

}
