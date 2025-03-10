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
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledEditorKit;

import sysobj.Card;
import sysobj.Player;

public class GameView extends JFrame {

	// Constants (global, but mostly used in this class)
	static final boolean VERTICAL = false;
	static final boolean HORIZONTAL = true;
	static final boolean VISIBLE = true;
	static final boolean HIDDEN = false;
	static final Color BACKGROUND_PINK = new Color(255, 241, 241);
	static final Color BACKGROUND_BLUE = new Color(33, 65, 202);
	static final Color BORDER_BLUE = new Color(136, 200, 238);
	static final int NORTH = 0;
	static final int EAST = 1;
	static final int WEST = 2;
	static final int SOUTH = 3;

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
	
	// Player cards in hand
	private JLabel playerNorthScore;
	private JLabel playerEastScore;
	private JLabel playerWestScore;
	private JLabel playerSouthScore;
	
	// Other stuff
	private Font myFont;
	private GridBagConstraints myGBC;
	private static final long serialVersionUID = 1L;
	private Locale language;
	private ResourceBundle translatable;
	private boolean isSoundOn;

	public GameView() {
		myFont = getMyFont("asset/font/snes-fonts-mario-paint.ttf");
		myGBC = new GridBagConstraints();
		myGBC.gridx = 0;
		myGBC.gridy = GridBagConstraints.RELATIVE;
		myGBC.anchor = GridBagConstraints.CENTER;
		drawMainApplication();

		// TODO: handle the unhandled fields in the constructor
	}

	public void drawMainApplication() {

		/* ---------- PREAMBLE ---------- */

		// Outer panel holds play area and console
		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BorderLayout());
		
		// Inner holds the game play area
		JPanel gameElements = new JPanel();
		gameElements.setLayout(new BorderLayout());

		/* ------------------------- CONSOLE PANEL SECTION ------------------------- */

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
		scoreBox.setLayout(new GridBagLayout());

		// Score label
		JLabel scoreTitle = new JLabel("--- SCORE ---");
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
		
		// Adding elements to score box
		scoreBox.add(scoreTitle, myGBC);
		scoreBox.add(playerNorthScore, myGBC);
		scoreBox.add(playerEastScore, myGBC);
		scoreBox.add(playerWestScore, myGBC);
		scoreBox.add(playerSouthScore, myGBC);

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
		TextField chatInput = new TextField(20);
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
		chatBox.setLayout(new FlowLayout());
		chatBox.setBorder(BorderFactory.createLineBorder(BORDER_BLUE, 2));

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

		/* ------------------------- GAME PANEL SECTION ------------------------- */

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
		playerNorth.setBackground(BACKGROUND_PINK);

		/* ---------- EAST PLAYER ---------- */

		// Panel to hold Name and Cards
		JPanel playerEast = new JPanel();
		playerEast.setBackground(BACKGROUND_PINK);
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
		pEastWrapper.setBackground(BACKGROUND_PINK);
		pEastWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		pEastWrapper.add(playerEast);

		/* ---------- WEST PLAYER ---------- */

		// Panel to hold Name and Cards
		JPanel playerWest = new JPanel();
		playerWest.setBackground(BACKGROUND_PINK);
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
		pWestWrapper.setBackground(BACKGROUND_PINK);
		pWestWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		pWestWrapper.add(playerWest);

		/* ---------- MAIN PLAYER / SOUTH PLAYER ---------- */

		// Hand for the user interacting with the app (located in the south)
		JPanel playerSouth = new JPanel(new BorderLayout());
		playerSouth.setBackground(BACKGROUND_PINK);
		playerSouthName = new JLabel("");
		playerSouthName.setFont(myFont);
		playerSouthName.setHorizontalAlignment(SwingConstants.CENTER);
		playerSouthCards = new JPanel();
		playerSouthCards.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		playerSouthCards.setBackground(BACKGROUND_PINK);
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
		cardLibrary.setBackground(BACKGROUND_PINK);
		library = new JButton(new ImageIcon("asset/card/back.png"));
		library.setBorder(null);
		cardLibrary.add(library);

		// Played cards have been played by players and are face-up
		JPanel cardsPlayed = new JPanel();
		cardsPlayed.setBackground(BACKGROUND_PINK);
		playedCards = new JButton();
		playedCards.setBorder(null);
		cardsPlayed.add(playedCards);

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

		// Add all gameplay elements to game area
		gameElements.add(BorderLayout.NORTH, playerNorth);
		gameElements.add(BorderLayout.EAST, pEastWrapper);
		gameElements.add(BorderLayout.WEST, pWestWrapper);
		gameElements.add(BorderLayout.SOUTH, playerSouth);
		gameElements.add(BorderLayout.CENTER, playingAreaAndLogo);

		/* -------------------- MENU SECTION -------------------- */

		// Menu Bar
		mBar = new JMenuBar();
		mBar.setVisible(true);
		mBar.setLayout(new FlowLayout(FlowLayout.LEADING));

		/* ----- Main menu options ----- */

		mHostGame = new JMenu("Start Game");
		mHostGame.setVisible(true);
		mHostGame.setEnabled(true);

		mJoinGame = new JMenu("Join Game");
		mJoinGame.setVisible(true);
		mJoinGame.setEnabled(true);

		mDisconnect = new JMenu("Disconnect");
		mDisconnect.setVisible(true);
		mDisconnect.setEnabled(false);

		mOptions = new JMenu("Options");
		mOptions.setVisible(true);
		mOptions.setEnabled(true);

		mAbout = new JMenuItem("About");
		mAbout.setVisible(true);
		mAbout.setEnabled(true);

		/* ----- SUBMENUS ----- */

		langSelect = new JMenu("Language");
		langEng = new JMenuItem("English");

		/*
		 * Game will start in English by default. The language the program is currently
		 * running in will be disabled as a selection.
		 */
		langEng.setEnabled(false);
		langFr = new JMenuItem("French");
		soundToggle = new JCheckBoxMenuItem("Sound effects on/off");
		soundToggle.setSelected(true);
		musicToggle = new JCheckBoxMenuItem("Music on/off");
		musicToggle.setSelected(true);
		mSinglePlayer = new JMenuItem("Single Player");
		mMultiPlayer = new JMenuItem("Multiplayer");

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

		/* ------------------------- MAIN WINDOW SECTION ------------------------- */

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
	
	public void refreshHand(Player player, int orientation) {
		JPanel handPanel = null;
		switch (orientation) {
		case NORTH: handPanel = playerNorthCards; break;
		case EAST: handPanel = playerEastCards; break;
		case SOUTH: handPanel = playerSouthCards; break;
		case WEST: handPanel = playerWestCards; break;
		default: System.out.println("Default case reached in GameView.refreshHand().");
		}
	    handPanel.removeAll();

	    int handSize = player.getHandSize();
	    System.out.println("Refreshing images of cards in hand...");
	    for (int i = 0; i < handSize; i++) {
	    	Card card = player.getHand().get(i);
	    	if (i < handSize -1) {
	    		//draw a slice
	    		card.setIcon(card.fetchCardImg(false, true));
	    		handPanel.add(card);
	    	} else {
	    		// draw the whole card
	    		card.setIcon(card.fetchCardImg(false, false));
		        handPanel.add(card);
	    	}
	    }

	    handPanel.revalidate();  
	    handPanel.repaint();
	}

	public void displayLastPlayedCard(Card card) {
		ImageIcon playedCardImg = card.fetchCardImg(false, false);
		playedCards.setIcon(playedCardImg);
		this.revalidate();
		this.repaint();
	}

	public void displayCardsInHand(Player p, int orientation) {
		int handSize = p.getHandSize();
		if (orientation < 0 || orientation > 3) {
			System.out.println("Orientation was < 0 || > 3 in displayCardsInHand");
			return;
		}
		JPanel handDisplay = null;
		switch (orientation) {
		case NORTH: handDisplay = playerNorthCards; break;
		case EAST: handDisplay = playerEastCards; break;
		case SOUTH: handDisplay = playerSouthCards; break;
		case WEST: handDisplay = playerWestCards; break;
		default: System.out.println("Default switch case reached in GameView.displayCardsInHand()");
		}
		handDisplay.removeAll();
		for (int i = 0; i < handSize; i++){
			Card card = p.getHand().get(i);
			
			// Display card slices for all but the last card
			if (i < handSize -1) {
				if (orientation == EAST || orientation == WEST) {
					// change the displayed card's image
					card.setImage(card.fetchCardImg(true, true));
					handDisplay.add(card, myGBC);
				} else {
					card.setImage(card.fetchCardImg(false, true));
					handDisplay.add(card);
				}
			} else {
				if (orientation == EAST || orientation == WEST) {
					card.setImage(card.fetchCardImg(true, false));
					handDisplay.add(card, myGBC);
				} else {
					card.setImage(card.fetchCardImg(false, false));
					handDisplay.add(card);
				}
			}

		}
		handDisplay.revalidate();
		handDisplay.repaint();
		
		resizeWindow(handDisplay);
	}
	
	public void updatePlayerNames(Player p, int orientation) {
		String playerName = p.getName();
		switch (orientation) {
		case NORTH: playerNorthName.setText(playerName); break;
		case EAST: playerEastName.setText(playerName); break;
		case SOUTH: playerSouthName.setText(playerName); break;
		case WEST: playerWestName.setText(playerName); break;
		default: System.out.println("Default case reached in GameView.updateNames().");
		}
	}
	
	public void updateScoreTable(Player p) {
		String labelText = p.getName() + " = " + p.getScore();
		switch (p.getOrientation()) {
		case NORTH: playerNorthScore.setText(labelText); break;
		case EAST: playerEastScore.setText(labelText); break;
		case WEST: playerWestScore.setText(labelText); break;
		case SOUTH: playerSouthScore.setText(labelText); break;
		default: System.out.println("Default case reached in GameView.updateScoreTable().");
		}
	}
	
	public void resizeWindow(JPanel panel) {
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(panel);
        frame.pack();
        frame.revalidate();
        frame.repaint();
	}
	
	public void displayAbout() {
		String line;
		StringBuilder sb = new StringBuilder();
		try (BufferedReader r = new BufferedReader(new FileReader("asset/Rules.txt"))){
			while ((line = r.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not find Rules.txt.");
		} catch (IOException e) {
			System.out.println("IO stream interrupted.");
		}

		JOptionPane.showMessageDialog(this, sb.toString(), "Rules", JOptionPane.INFORMATION_MESSAGE);
	}

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

	public void setSinglePlayerListener(ActionListener listener) {
		mSinglePlayer.addActionListener(listener);
	}

	public void setMultiPlayerListener(ActionListener listener) {
		mMultiPlayer.addActionListener(listener);
	}

	public void setHostGameListener(ActionListener listener) {
		mHostGame.addActionListener(listener);
	}

	public void setJoinGameListener(ActionListener listener) {
		mJoinGame.addActionListener(listener);
	}

	public void setDisconnectListener(ActionListener listener) {
		mDisconnect.addActionListener(listener);
	}

	public void setOptionsListener(ActionListener listener) {
		mOptions.addActionListener(listener);
	}

	public void setAboutListener(ActionListener listener) {
		mAbout.addActionListener(listener);
	}

	public void setLangEnglishListener(ActionListener listener) {
		langEng.addActionListener(listener);
	}

	public void setLangFrenchListener(ActionListener listener) {
		langFr.addActionListener(listener);
	}

	public void setSoundToggleListener(ActionListener listener) {
		soundToggle.addActionListener(listener);
	}

	public void setMusicToggleListener(ActionListener listener) {
		musicToggle.addActionListener(listener);
	}

	public void setDrawFromLibraryListener(ActionListener listener) {
		library.addActionListener(listener);
	}
	
	public void playCardFromHandListener(ActionListener listener) {
		
	}
	
}
