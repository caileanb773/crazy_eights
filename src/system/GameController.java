package system;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import sysobj.AIPlayer;
import sysobj.Card;
import sysobj.Player;
import sysobj.Rank;
import sysobj.Suit;

/**
 * Controller for the game, which calls methods in both the view and model.
 * @since 23
 */
public class GameController implements GameControllerListener {

	/** The game model containing game logic and state. */
	private GameModel model;

	/** The view component handling the UI. */
	private GameView view;

	/** The name of the current player. */
	private String playerName;

	/** The server instance for multiplayer games. */
	private GameServer server;

	/** The client instance for connecting to a server. */
	private GameClient client;

	/** Formatter for displaying timestamps in "HH:mm" format. */
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	/** The selected game mode (e.g., single-player or multiplayer). */
	private int gameMode;


	/**
	 * Parameterized constructor for GameController. GameController acts as a bridge
	 * between the model and the view, and so it is passed both a model and a view
	 * object.
	 * 
	 * @param m The model.
	 * @param v The view.
	 * 
	 * @since 23
	 */
	public GameController(GameModel m, GameView v) {
		model = m;
		view = v;
		playerName = "defaultName";
		gameMode = Const.SINGLE_PLAYER;
	}


	/* --------------------------------------------------------------- */
	/* ------------------- MULTIPLAYER FLOW METHODS ------------------ */
	/* --------------------------------------------------------------- */

	/**
	 * Collects "network info" from clients or host, depending on the boolean
	 * passed to it. If isHost, creates a new server object and starts listening
	 * for connections on that port. If not, starts a new client and tries to 
	 * connect to a server at a specific port/ip.
	 * 
	 * 
	 * @since 23
	 * @param isHost determines which object the user will be instantiated as; if they
	 * want to host a new game, they are the host (server). If they are joining
	 * a game, they are a client.
	 */
	public void gatherNetworkInfo(boolean isHost) {

		// set gamemode
		gameMode = Const.MULTI_PLAYER;

		// Set up the panel dynamically based on role
		JPanel panel = new JPanel(new GridLayout(3, 2));
		JTextField nameField = new JTextField(10);
		JTextField portField = new JTextField(10);
		JTextField numPlField = new JTextField(10);

		// Only for client
		JTextField ipField = isHost ? null : new JTextField(15);
		ResourceBundle translatable = view.getTranslatable();

		panel.add(new JLabel(translatable.getString("playerName")));
		panel.add(nameField);
		panel.add(new JLabel(translatable.getString("portNumber")));
		panel.add(portField);

		if (!isHost) {
			panel.add(new JLabel(translatable.getString("ipAddress")));
			panel.add(ipField);
		} else {
			panel.add(new JLabel(translatable.getString("numOpponents")));
			panel.add(numPlField);
		}

		// Show dialog
		int result = JOptionPane.showConfirmDialog(null, panel, isHost ? 
				translatable.getString("hostGame") : translatable.getString("joinGame"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			try {
				String nameInput = nameField.getText().trim();
				String portInput = portField.getText().trim();

				// Validate name and port
				if (nameInput.isEmpty()) {
					JOptionPane.showMessageDialog(null, translatable.getString("nameErr"),
							translatable.getString("error"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				int port = Integer.parseInt(portInput);
				if (port < 10000 || port > 65535) {
					JOptionPane.showMessageDialog(null, translatable.getString("portErr"),
							translatable.getString("error"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Store name locally
				playerName = nameInput;

				// Instantiate based on role
				if (isHost) {
					String numPlInput = numPlField.getText().trim();
					int numHumanOpponents = Integer.parseInt(numPlInput);

					if (numHumanOpponents < 1 || numHumanOpponents > 3) {
						JOptionPane.showMessageDialog(null, translatable.getString("playerErr"),
								translatable.getString("error"),
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					server = new GameServer(port, this, numHumanOpponents);
					server.acceptConnections();
					client = null;
					System.out.println(translatable.getString("hostingAs") +
							playerName + translatable.getString("onPort") + port);

					// define an action listener specifically for the cancel button
					ActionListener cancelAction = new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							cancelHosting();
						}
					};

					view.awaitConnectionsDialog(port, cancelAction, numHumanOpponents);
				} else {
					String ipInput = ipField.getText().trim();
					if (ipInput.isEmpty()) {
						JOptionPane.showMessageDialog(null, translatable.getString("ipErr"),
								translatable.getString("error"), JOptionPane.ERROR_MESSAGE);
						return;
					}

					// create a new client, send client's name to the server
					client = new GameClient(port, ipInput, this, playerName);
					client.sendName();
					server = null;
					System.out.println("Joining as: " + playerName + " at " + ipInput + ":" + port);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, translatable.getString("portErr"),
						translatable.getString("error"), JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, translatable.getString("networkErr") +
						e.getMessage(), translatable.getString("error"),
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			System.out.println((isHost ? "Host" : "Join") + " Game canceled");
		}
	}

	/**
	 * Cancels the hosting dialog and resets any flags that were set in the interim.
	 * 
	 * @since 23
	 */
	public void cancelHosting() {
		if (server != null) {
			server.shutdown();
			server = null;
		}
		gameMode = Const.SINGLE_PLAYER;
		model.setGameRunning(false);
	}

	/**
	 * Sends a chat message to the host's UI and tells the server to send it to
	 * all clients.
	 * 
	 * @since 23
	 * @param msg the chat msg
	 */
	public void sendChat(String msg) {
		String formattedMsg = playerName + ": " + msg; // Add player name
		if (server != null) {
			// Host: Broadcast to all clients and update own UI
			server.broadcastChat(formattedMsg);
			view.displayChat(formattedMsg);
		} else if (server != null) {
			// Client: Send to host
			server.broadcastChat(formattedMsg);
		}
	}

	/**
	 * Sets flags for the multiplayer game mode, syncs clients with new Player
	 * objects that map to the client ID, and then clears some additional flags
	 * and fields by calling initMultiPlayerRound() before starting a new round.
	 * 
	 * @since 23
	 */
	public void startMultiplayerGame() {
		gameMode = Const.MULTI_PLAYER;
		view.setBtnsMultiplayer();

		if (server != null) {
			server.broadCastButtonMode("MULTIPLAYER");
		}

		int numHumanPlayers = 1 + server.getClientCount();
		int numAiPlayers = 4 - numHumanPlayers;
		int id = 0;

		Vector<Player> players = new Vector<>();

		// the Host needs a special parameter set
		Player host = new Player(playerName, id, id++, true);
		host.setHost(true);
		players.add(host);

		// creating players that map to the ids of connected clients
		for (int i = 0; i < server.getConnectedClients().size(); i++) {
			Player newPlayer = new Player(server.getClientNames().get(i), id, id++, true);
			players.add(newPlayer);
		}

		// creating AI players w/ unique names to fill the missing slots up to 4
		if (numAiPlayers > 0) {
			model.loadAINames();
			for (int i = 0; i < numAiPlayers; i++) {
				Player AI = new AIPlayer(model.getAIPlayerName());
				AI.setOrientation(id);
				AI.setID(id++);
				players.add(AI);
			}
		}

		model.initMultiplayerGame(players);
		model.initRound();

		// refreshing host's UI
		for (Player p : model.getPlayers()) {
			view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
			view.displayCardsInHand(p);
		}
		view.displayLastPlayedCard(model.getLastPlayedCard());
		refreshListenersInPlayerHand(model.getActivePlayer());

		// refreshing client's UI
		server.requestViewRefresh(players, model.getLastPlayedCard(), model.getTurnOrderDirection());

		// gamestate console messages (there's a new round, it's the host's turn)
		processConsoleMsg("", "newRound", "");
		processConsoleMsg(playerName, "currentTurn", "");

		// off to the races
		model.setGameRunning(true);
	}

	/**
	 * Logic that picks up after a client has picked a suit for an eight.
	 * 
	 * @since 23
	 * @param cardToPlay: the original card (eight)
	 * @param suitChoice: the replacing suit
	 */
	public void handleClientPostSuitChoice(Card cardToPlay, Suit suitChoice) {
		Player activePlayer = model.getActivePlayer();

		handleCardPlay(cardToPlay);

		// Find and remove the matching card
		Vector<Card> playerHand = activePlayer.getHand();
		Card cardToRemove = null;
		for (Card c : playerHand) {

			// find the exact object matching cardToPlay
			if (c.toString().equals(cardToPlay.toString())) {
				cardToRemove = c;
				break;
			}
		}

		// remove the card from their hand
		if (cardToRemove != null) {
			playerHand.remove(cardToRemove);
			activePlayer.setHand(playerHand);
		} else {
			System.out.println("Card not found in hand: " + cardToPlay);
		}

		model.getLastPlayedCard().setSuit(suitChoice);
		view.displayLastPlayedCard(model.getLastPlayedCard());
		server.requestViewRefresh(model.getPlayers(), cardToPlay, model.getTurnOrderDirection());
	}

	/**
	 * Replaces a Player object with an AIPlayer object with the same values.
	 * 
	 * @since 23
	 * @param player the player to replace
	 * @return the ai player
	 */
	public AIPlayer replacePlayer(Player player) {
		AIPlayer replacement = new AIPlayer(player.getName());
		replacement.setScore(player.getScore());
		replacement.setHand(player.getHand());
		replacement.setName("AI " + player.getName().toUpperCase());
		replacement.setID(player.getId());
		return replacement;
	}

	/**
	 * Sends a console message to the host's UI, if they are the host, and tells
	 * each client to show the console message if they are a client.
	 * 
	 * @since 23
	 * @param optName optional data
	 * @param msg the packet data
	 * @param optCard optional data
	 */
	public void processConsoleMsg(String optName, String msg, String optCard) {
		view.sendConsoleMsg(optName, msg, optCard);
		if (optCard.isEmpty()) {
			optCard = " ";
		}
		if (server != null) {
			server.broadcastConsoleMsg(optName, msg, optCard);
			server.requestViewRefresh(model.getPlayers(), model.getLastPlayedCard(), model.getTurnOrderDirection());
		}

	}


	/* --------------------------------------------------------------------- */
	/* ---------- METHODS IMPLEMENTED FROM GAMECONTROLLERLISTENER ---------- */
	/* --------------------------------------------------------------------- */

	/**
	 * When a chat is received by a client, this method tells the server to
	 * broadcast that chat to all clients.
	 * 
	 * @param msg The chat message.
	 * @since 23
	 */
	@Override
	public void onChatReceived(String msg) {
		view.displayChat(msg);
		if (server != null) {
			server.broadcastChat(msg);
		}
	}

	/**
	 * When a console message is received from a client, this method tells the
	 * server to broadcast that console msg to all clients.
	 * 
	 * @param optName optional flag
	 * @param msg message contents
	 * @param optCard optional flag
	 * @since 23
	 */
	@Override
	public void onConsoleMsgReceived(String optName, String msg, String optCard) {
		view.sendConsoleMsg(optName, msg, optCard);
		if (server != null) {

			// Host rebroadcasts to clients
			server.broadcastConsoleMsg(optName, msg, optCard);
		}
	}

	/**
	 * Determines if the client requesting to draw may draw (is it their turn?
	 * do they have room in their hand?).
	 * 
	 * @param packetData: the draw request.
	 * @since 23
	 */
	@Override
	public void onClientDrawReceived(String packetData) {
		String[] packetInfo = packetData.split("\\|");
		int clientId = Integer.parseInt(packetInfo[1]);

		if (model.getActivePlayer().getId() == clientId) {
			System.out.println("Client turn verified, proceeding with draw...");
			handleClientDraw(packetInfo[1]);
			server.requestViewRefresh(model.getPlayers(), model.getLastPlayedCard(), model.getTurnOrderDirection());
		} else {
			System.out.println("Active player ID did not match client ID, draw request rejected.");
		}
	}

	/**
	 * Determines if a received "play" from a client is legal (is it their turn?
	 * is the card a legal play based on the last played card?). If it is, handle
	 * it, if not, quietly reject it (do nothing).
	 * 
	 * @param playData: the move, containing the card the player wishes to play.
	 * @since 23
	 */
	@Override
	public void onClientPlayReceived(String playData) {

		// PLAY|clientID|card
		String[] parts = playData.split("\\|");
		int clientId = Integer.parseInt(parts[1]);
		Card cardToPlay = Card.getCardFromStr(parts[2]);
		Player activePlayer = model.getActivePlayer();

		if (activePlayer.getId() == clientId) {
			System.out.println("Client turn verified, proceeding with play...");

			Card lastPlayedCard = model.getLastPlayedCard();

			// block illegal plays
			if (!model.isPlayLegal(cardToPlay, lastPlayedCard)) {
				System.out.println("Client attempted to play an illegal card.");
				return;
			}

			/* handle when the client plays an eight. server requests that the
			 * appropriate client shows the suit selection dialog, and immediately
			 * returns from this method. call stack is now clear and only when
			 * the client selects a suit is processTurn() called again, restarting
			 * the game loop. */
			if (cardToPlay.getRank() == Rank.EIGHT) {
				System.out.println("Client has played an Eight and is choosing a suit...");
				if (server != null) {
					server.requestSuitChoice(clientId, cardToPlay.toString());
					return;
				}
			}

			handleCardPlay(cardToPlay);

			// Find and remove the matching card
			Vector<Card> playerHand = activePlayer.getHand();
			Card cardToRemove = null;
			for (Card c : playerHand) {

				// find the exact object matching cardToPlay
				if (c.toString().equals(cardToPlay.toString())) {
					cardToRemove = c;
					break;
				}
			}

			// remove the card from their hand
			if (cardToRemove != null) {
				playerHand.remove(cardToRemove);
				activePlayer.setHand(playerHand);
			} else {
				System.out.println("Card not found in hand: " + cardToPlay);
			}

			server.requestViewRefresh(model.getPlayers(), cardToPlay, model.getTurnOrderDirection());
		} else {
			System.out.println("Active player ID did not match client ID, play request rejected.");
		}
	}

	/**
	 * Sends a chosen suit from client to server.
	 * 
	 * @param cardToPlay: the original card (eight).
	 * @since 23
	 */
	@Override
	public void onClientSuitRequest(String cardToPlay) {
		System.out.println("Client is choosing a suit to replace " + cardToPlay);
		Suit s = view.dialogEightSuit();
		System.out.println("Client has chosen " + s.toString());
		client.sendSuit(s.toString(), cardToPlay);
	}

	/**
	 * Intermediary step for a client deciding a suit for an eight they have 
	 * played; at this point the call stack is clear besides this function. Game
	 * loop resumes once handleClientPostSuitChoice() is called.
	 * 
	 * @param packetData packet data
	 * @since 23
	 */
	@Override
	public void onClientSuitReceived(String packetData) {
		String[] parsedPacket = packetData.split("\\|");
		String suit = parsedPacket[2];
		Card cardToPlay = Card.getCardFromStr(parsedPacket[3]);
		Suit s = Card.getSuitFromStr(suit);
		handleClientPostSuitChoice(cardToPlay, s);
	}

	/**
	 * Sets certain buttons on/off based on the packet received.
	 * 
	 * @param packetData packet data
	 * @since 23
	 */
	@Override
	public void onButtonStatusReceived(String packetData) {
		switch (packetData) {
		case "SINGLEPLAYER": view.setBtnsSingleplayer(); break;
		case "MULTIPLAYER": view.setBtnsMultiplayer(); break;
		case "MAINMENU": view.setBtnsMainMenu(); break;
		default: System.out.println("Invalid button display status in onButtonStatusReceived().");
		break;
		}
	}

	/**
	 * Updates the dialog for the server when it is waiting for new players to
	 * connect (visual feedback).
	 * 
	 * @param playerCount the how many players have connected
	 * @param maxPlayers the max no of players
	 * @since 23
	 */
	@Override
	public void onPlayerConnected(int playerCount, int maxPlayers) {
		view.updateWaitingStatus(playerCount, maxPlayers);
	}

	/**
	 * When a player (client) disconnects, shut down their threads, close their
	 * socket, and replace their player object with an AI player.
	 * 
	 * @param packetData packet data
	 * @since 23
	 */
	@Override
	public void onPlayerDisconnect(String packetData) {
		String[] parsedPacket = packetData.split("\\|");
		int clientId = Integer.parseInt(parsedPacket[1]);
		Player clientPlayer = model.getPlayers().get(clientId);
		AIPlayer replacement = replacePlayer(clientPlayer);

		model.getPlayers().set(clientId, replacement);
		server.closeSocket(clientId);

	}

	/**
	 * Informs the clients of the round winner via dialog.
	 * 
	 * @param roundWinnerName name of the round winner
	 * @since 23
	 */
	@Override
	public void onRoundOver(String roundWinnerName) {
		if (server != null) {
			server.requestViewRefresh(model.getPlayers(), model.getLastPlayedCard(), model.getTurnOrderDirection());
		} else {
			view.displayRoundWinner(roundWinnerName);
		}
	}

	/**
	 * Informs the client of the game winner(s) via dialog.
	 * 
	 * @param gameWinnerNames the winners names
	 * @since 23
	 */
	@Override
	public void onGameOver(String gameWinnerNames) {
		if (server != null) {
			server.requestViewRefresh(model.getPlayers(), model.getLastPlayedCard(), model.getTurnOrderDirection());
		} else {
			view.displayGameWinners(gameWinnerNames);
		}
	}

	/**
	 * Resets the client's UI to initial state.
	 * 
	 * @since 23
	 */
	@Override
	public void onTerminateGameRequest() {
		// reset the model to initial state
		model.resetGameFlags();

		// clean up the ui
		view.resetHands();
		view.resetLastPlayedCard();
		view.resetScoreBoard();
		view.resetPlayerNames();
		view.resetConsole();
		view.setBtnsMainMenu();
		view.refreshView();
		view.packWindow();

		// reset controller to initial state
		server = null;
		client = null;
	}

	/**
	 * Refreshes each part of the client's UI with the passed information from
	 * the server.
	 * 
	 * @param plyrID: the client's id (player id)
	 * @param hand: the hand of the client
	 * @param playedCard: the last played card
	 * @param noCardsInHands: the number of cards in each player's hand
	 * @param playerNames: the name of each player
	 * @param playerScores: the score of each player
	 * @param turnDirection: clockwise/counterclockwise
	 * @since 23
	 */
	@Override
	public void onViewRefresh(String plyrID, String hand, String playedCard, String noCardsInHands, String playerNames,
			String playerScores, String turnDirection) {
		int clientId = Integer.parseInt(plyrID);

		// refresh the hand of the client first
		view.refreshClientHand(hand, this);

		// set the client's model to the last played card (for internal logic)
		Card lastPlayed = Card.getCardFromStr(playedCard);
		model.setLastPlayedCard(lastPlayed);
		view.displayLastPlayedCard(lastPlayed);

		// get all player names into array form
		String[] nameArr = playerNames.split(",");

		// refresh hands of opponents (string looks like "2,1,6")
		view.refreshOpponentHands(noCardsInHands, clientId);

		// refresh the score table
		String southName = nameArr[clientId];
		view.refreshClientScoreTable(southName, clientId, nameArr, playerScores, noCardsInHands, turnDirection);
	}

	/**
	 * Informs the server that the amount of players waiting to connect have 
	 * connected and that the game should start.
	 * 
	 * @param state the gamestate
	 * @since 23
	 */
	@Override
	public void onGameStateUpdated(String state) {
		System.out.println("Game state update invoked.");
		if (state.equals("Game Starting")) {
			view.closeWaitingDialog();
			view.gameStartDialog();
			startMultiplayerGame();
		}
	}

	/**
	 * Sets up listeners for a client's hand.
	 * 
	 * @param hand the player's hand
	 * @since 23
	 */
	public void onHandRefreshed(Vector<Card> hand) {
		for (Card c : hand) {
			for (ActionListener al : c.getActionListeners()) {
				c.removeActionListener(al);
			}
			c.addActionListener(new CardPlayListener());
			c.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

			c.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
				}

				public void mouseExited(MouseEvent e) {
					c.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				}
			});
		}
	}


	/* ----------------------------------------------------------- */
	/* -------------------- GAME FLOW METHODS -------------------- */
	/* ----------------------------------------------------------- */

	/**
	 * Runs the game by drawing the splash and then the main screen.
	 * 
	 * @since 23
	 */
	public void launchGame() {
		view.drawSplash();
		view.drawMainWindow();
		view.setSinglePlayerListener(new SinglePlayerListener());
		view.setMultiPlayerListener(new MultiPlayerListener());
		view.setJoinGameListener(new JoinGameListener());
		view.setDisconnectListener(new DisconnectListener());
		view.setAboutListener(new AboutListener());
		view.setLangEnglishListener(new LangEnglishListener());
		view.setLangFrenchListener(new LangFrenchListener());
		view.setSoundToggleListener(new SoundToggleListener());
		view.setMusicToggleListener(new MusicToggleListener());
		view.setDrawFromLibraryListener(new CardDrawListener());
		view.setChatSendButtonListener(new ChatSendButtonListener());
	}

	/**
	 * Handles a new round starting. If the game is not running, then the round is
	 * the first round played, and so special initialization must take place. The
	 * name of the player is retrieved and a game is initialized with this new
	 * player. If the game is running, then it's some subsequent round, so just
	 * initialize a new round from the model, refresh the score box, update the
	 * player names (which contain hand information), refresh the view's hand areas
	 * to reflect what the model contains, attach listeners to the human player's
	 * hand cards, and display the last played card, which starts the game.
	 * 
	 * @since 23
	 */
	public void handleStartRound() {
		if (!model.isGameRunning() && gameMode == Const.SINGLE_PLAYER) {
			playerName = view.getPlayerName();
			model.initSingleplayerGame(playerName);
			view.setBtnsSingleplayer();
		} 

		model.initRound();
		processConsoleMsg("", "newRound", "");
		processConsoleMsg(playerName, "currentTurn", "");
		Vector<Player> players = model.getPlayers();
		boolean turnOrderDir = model.getTurnOrderDirection();

		// host refreshing their own UI + listeners
		List<Card> playedCards = model.getPlayedCards();
		for (Player p : players) {
			view.refreshScores(players, turnOrderDir);
			view.displayCardsInHand(p);
		}
		refreshListenersInPlayerHand(model.getActivePlayer());
		view.displayLastPlayedCard(playedCards.getLast());

		if (gameMode == Const.MULTI_PLAYER && server != null) {
			server.requestViewRefresh(players, model.getLastPlayedCard(), turnOrderDir);
		}
	}

	/**
	 * Core logic of the gameplay loop. This method is called whenever a human
	 * player completes an action that would end their turn, and is "recursively"
	 * called for as long as there are AI players left who need to have a turn. Once
	 * all AI players have taken their turn, the method returns up the call stack,
	 * and the game is now "waiting" for the next human player to take some action.
	 * 
	 * The method begins by checking if the round is over or not. If it is, the
	 * round is ended gracefully. If the round is over, then that means scores were
	 * added to, which means the game might also be over, so that is also checked.
	 * If the game is over, end it gracefully. If neither of these two things are
	 * true, then begin a new round. If the round is not currently over, proceed
	 * with a turn as normal. If the active player is human, then they may have been
	 * forced to draw cards in the process of waiting for their turn, so refresh
	 * their listeners. If they aren't human, they're AI, so execute their turn. The
	 * next processTurn() call is called within the process of the AI player
	 * executing their turn.
	 * 
	 * @since 23
	 */
	public void processTurn() {
		System.out.println("Calling processTurn()");
		Player activePlayer = model.getActivePlayer();
		String playerName = activePlayer.getName();
		processConsoleMsg(playerName, "currentTurn", "");
		System.out.println("It is now " + playerName + "'s turn.");

		// Check game state, starting with the status of the current round
		if (model.isRoundOver()) {
			System.out.println("The current round has ended.");

			/* ---------- MULTIPLAYER ---------- */

			if (gameMode == Const.MULTI_PLAYER) {
				System.out.println("Cleaning up a multiplayer round...");
				// handle multiplayer rounds ending
				endMultiplayerRound();

				// check if the game is over
				if (model.isGameOver()) {
					endMultiplayerGame();
					return;
				}
				// handle game still ongoing
				handleStartRound();
				return;

			} else {
				endRound();
				// Check the status of the game
				if (model.isGameOver()) {
					endGame();
					view.setBtnsMainMenu();
					return;
				}

				// The round is over, but the game is still going, so reset the round
				handleStartRound();
				return;
			}
		}

		// handle card draw in single player vs multiplayer scenarios
		if (gameMode == Const.SINGLE_PLAYER) {
			if (activePlayer.isHuman()) {
				refreshListenersInPlayerHand(activePlayer);
			} else {
				executeAIPlayerTurn((AIPlayer) activePlayer);
			}

		} else if (gameMode == Const.MULTI_PLAYER) {
			if (activePlayer.isHost()) {
				refreshListenersInPlayerHand(activePlayer);
			} else if (!activePlayer.isHuman()) {
				executeAIPlayerTurn((AIPlayer) activePlayer);
			} else {
				System.out.println("Call stack cleared - awaiting player move...");
			}
		}
	}

	/**
	 * Executes an AI player's turn, which is mostly completed by
	 * decideAIPlayerMove(). Once they have finished a turn, advance the turn and
	 * refresh the scores, then process the next turn.
	 * 
	 * @param AIPlayer The player who should have their turn executed.
	 * @since 23
	 */
	public void executeAIPlayerTurn(AIPlayer AIPlayer) {
		Timer timer = new Timer(1500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				decideAIPlayerMove(AIPlayer);
				model.setActivePlayer(model.getNextPlayer());
				view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
				processTurn();
			}
		});

		timer.setRepeats(false);
		timer.start();
	}

	/**
	 * Handles the AI player's turn. When the AI player makes a decision, the
	 * information must be retrieved from the model so that the view can be updated
	 * to reflect the changes in the model. Fetches the AI player's move decision
	 * based on the game state, and handles each case.
	 * 
	 * @param AIPlayer The AI player who's turn it is.
	 * @since 23
	 */
	public void decideAIPlayerMove(AIPlayer AIPlayer) {
		String playerName = AIPlayer.getName();
		Card lastPlayedCard = model.getLastPlayedCard();
		Card cardToPlay = null;
		int choice = 0;

		while (true) {
			choice = AIPlayer.decidePlayDraw(lastPlayedCard);

			switch (choice) {
			case Const.PASS:
				processConsoleMsg(playerName, "passTurn", "");
				System.out.println(AIPlayer.getName() + " is passing their turn.");
				return;
			case Const.PLAY:
				cardToPlay = AIPlayer.decideCard(lastPlayedCard);
				if (model.playCard(cardToPlay)) {
					processConsoleMsg(playerName, "playCard", cardToPlay.toString());
					handleCardActions(cardToPlay);
					view.displayCardsInHand(AIPlayer);
					view.displayLastPlayedCard(model.getLastPlayedCard());
				} else {
					System.out.println(playerName + " tried to break the rules by playing an illegal card.");
				}
				return;

			case Const.DRAW:
				model.drawCard();
				processConsoleMsg(playerName, "drawCard", "");
				view.displayCardsInHand(AIPlayer);
				break;
			default:
				System.out.println("Default switch case reached while AI was deciding move.");
				return;
			}
		}

	}

	/**
	 * Ends the current round. Resets some flags in the model, tallies up the scores
	 * of each player at round end, displays those scores, and displays the winner
	 * via dialog.
	 * 
	 * @since 23
	 */
	public void endRound() {
		model.setTurnOrderReversed(false);
		model.tallyScores();
		Vector<Player> players = model.getPlayers();
		view.refreshScores(players, model.getTurnOrderDirection());
		view.displayRoundWinner(model.getRoundWinner());
	}

	/**
	 * Handles the ending of a multiplayer round. Tells each client's UI to refresh
	 * back to the initial state.
	 * 
	 * @since 23
	 */
	public void endMultiplayerRound() {
		view.setPackCalls(0);
		model.setTurnOrderReversed(false);
		model.tallyScores();
		Vector<Player> players = model.getPlayers();
		Player roundWinner = model.getRoundWinner();
		boolean turnOrderDir = model.getTurnOrderDirection();

		// refresh host UI
		view.refreshScores(players, turnOrderDir);

		// refresh client UIs
		if (server != null) {
			server.requestViewRefresh(players, model.getLastPlayedCard(), turnOrderDir);
		}

		// broadcast round over message to client
		if (server != null) {
			server.broadcastRoundWinner(roundWinner.getName());
		} else {
			System.out.println("Server was null in processTurn() after multiplayer round ended.");
		}

		// host needs the dialog of who won, too
		view.displayRoundWinner(roundWinner);
	}

	/**
	 * Handles the end of the game. Resets the pack call flag, displays the game
	 * winner as a dialog, refreshes the scoreboard, clears each player's hand, and
	 * resets the logic of the game within the model.
	 * 
	 * @since 23
	 */
	public void endGame() {
		System.out.println("The game has ended. Cleaning up...");
		//view.setPackCalls(0);
		if (model.isGameOver()) {
			view.displayGameWinners(model.getGameWinners());
			view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
		}

		cleanUp();
	}

	/**
	 * Ends a multiplayer game, resetting flags and UI elements.
	 * 
	 * @since 23
	 */
	public void endMultiplayerGame() {
		view.setPackCalls(0);

		// display game winner for host and clients
		view.displayGameWinners(model.getGameWinners());
		if (server != null) {
			StringBuilder winnerNames = new StringBuilder();
			Vector<Player> winners = model.getGameWinners();

			for (Player p : winners) {
				winnerNames.append(p.getName());
				winnerNames.append(" ");
			}
			server.broadcastGameWinners(winnerNames.toString());
			server.terminateGame();
			server.terminateThreads();
		}

		cleanUp();

		server = null;
		client = null;
	}

	/**
	 * Cleans up UI elements.
	 * 
	 * @since 23
	 */
	public void cleanUp() {
		// clear player's hands
		clearPlayerHands();

		// set the player names in everyone's zone to ""
		view.resetScoreBoard();

		// reset the scoreboard
		view.resetPlayerNames();

		// remove lastplayed card
		view.resetLastPlayedCard();

		// clear the console?
		view.resetConsole();

		// set players, deck, played cards, to null. reset all flags.
		model.resetGameFlags();

		view.setBtnsMainMenu();

		view.packWindow();
	}

	/**
	 * Called when cleaning up the game state. The cards in each player's hand are
	 * refreshed (now showing none, at the end of a game) and refreshView() is
	 * called on the view.
	 * 
	 * @since 23
	 */
	public void clearPlayerHands() {
		for (Player p : model.getPlayers()) {
			p.clearHand();
			view.displayCardsInHand(p);
		}
		view.refreshView();
	}

	/* -------------------------------------------------------------- */
	/* -------------------- CARD ACTION HANDLERS -------------------- */
	/* -------------------------------------------------------------- */

	/**
	 * Some cards have special cases, and this method determines which of their
	 * special functions should be executed based on the rank of the card. If the
	 * card has no special actions, the default case is reached and the method
	 * returns.
	 * 
	 * @param c The card to determine the special action for.
	 * @since 23
	 */
	public void handleCardActions(Card c) {
		switch (c.getRank()) {
		case TWO:
			handleForcedDraw(2);
			processConsoleMsg(model.peekNextPlayer().getName(), "forceDraw",
					(model.getNumTwosPlayed() * 2) + " cards!");
			break;
		case FOUR:
			handleForcedDraw(4);
			processConsoleMsg(model.peekNextPlayer().getName(), "forceDraw", 4 + " cards!");
			break;
		case EIGHT:
			handleEight();
			break;
		case ACE:
			processConsoleMsg(model.getActivePlayer().getName(), "turnReversed", "");
			break;
		case QUEEN:
			processConsoleMsg(model.getActivePlayer().getName(), "turnSkipped", "");
			break;
		default:
			break;
		}
	}

	/**
	 * When a player is forced to draw cards by some effect, their hand must be
	 * refreshed to reflect the drawn cards. If they are human, listeners must be
	 * added to the cards so that they are clickable when it comes around to their
	 * turn.
	 * 
	 * @param num The number of cards to be drawn.
	 * 
	 * @since 23
	 */
	public void handleForcedDraw(int num) {
		Player passive = model.peekNextPlayer();
		view.displayCardsInHand(passive);
		if (gameMode == Const.SINGLE_PLAYER) {
			if (passive.isHuman()) {
				refreshListenersInPlayerHand(passive);
			}
		} else if (gameMode == Const.MULTI_PLAYER) {
			if (passive.isHost()) {
				refreshListenersInPlayerHand(passive);
			} else {
				// do nothing
			}
		}

		view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
	}

	/**
	 * Handles when an eight is played. If the active player is human, they must be
	 * prompted to choose a suit (which is handled in the view). The chosen suit is
	 * then applied to the last played card, or it is left default if they cancelled
	 * out of the dialog.
	 * 
	 * 
	 * @since 23
	 */
	public void handleEight() {
		Player activePlayer = model.getActivePlayer();
		if (activePlayer.isHuman()) {
			Suit startingSuit = model.getLastPlayedCard().getSuit();
			Suit chosenSuit = null;

			// this only happens if the player is host
			if (gameMode == Const.SINGLE_PLAYER || activePlayer.isHost()) {
				chosenSuit = view.dialogEightSuit();

				// get the client to choose a suit
			} 

			//			else {
			//				int clientId = activePlayer.getId();
			//				server.requestSuitChoice(clientId);
			//			}

			if (chosenSuit != null) {
				processConsoleMsg("", "suitChanged", chosenSuit.toString());
				System.out.println("Player chose: " + chosenSuit);
				model.getLastPlayedCard().setSuit(chosenSuit);
			} else {
				System.out.println("No suit selected. Keeping current suit.");
				model.getLastPlayedCard().setSuit(startingSuit);
			}
		}
		view.displayLastPlayedCard(model.getLastPlayedCard());
	}

	/**
	 * Handles the drawing of a card. Based on the active player, when they draw a
	 * card, their hand must be redrawn, as a freshly drawn card will be displayed
	 * in its entirety, but each subsequent card drawn after will cause that card to
	 * be displayed as a card slice. This method checks if the player has a legal
	 * move in their hand, and if so, blocks them from drawing additional cards. The
	 * scores are refreshed (as this is where cards in hand information is) and they
	 * must continue drawing cards until their hand is full, or until they draw a
	 * card that is legal to play.
	 * 
	 * 
	 * @since 23
	 */
	public void handleCardDraw() {

		Player activePlayer = model.getActivePlayer();
		String playerName = activePlayer.getName();

		// check that the activeplayer is human before allowing them to draw a card
		if (activePlayer.isHost()) {
			// block player from drawing a card if they have a legal play in hand
			if (activePlayer.hasLegalMove(model.getLastPlayedCard())) {
				processConsoleMsg("", "cantDraw", "");
			} else {
				model.drawCard();
				processConsoleMsg(playerName, "drawCard", "");
				view.displayCardsInHand(activePlayer);
				view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());

				if (gameMode == Const.SINGLE_PLAYER) {
					refreshListenersInPlayerHand(activePlayer);
				} else if (activePlayer.isHost()) {
					refreshListenersInPlayerHand(activePlayer);
				}

				// if their hand is full after card draw, end their turn
				if (activePlayer.getHandSize() >= Const.MAX_HAND_SIZE) {
					processConsoleMsg(activePlayer.getName(), "passTurn", "");
					model.setActivePlayer(model.getNextPlayer());
					processTurn();
				}
			}
		} else {
			// TODO: this message should only be visible to the host (?)
			processConsoleMsg("", "notYourTurn", "");
			System.out.println("It is not your turn!");
		}
	}

	/**
	 * Handles when a client draws a card.
	 * 
	 * @since 23
	 * @param packetData packet data
	 */
	public void handleClientDraw(String packetData) {
		int clientID = Integer.parseInt(packetData);
		Player activePlayer = model.getActivePlayer();
		Card lastPlayedCard = model.getLastPlayedCard();

		if (activePlayer.getId() == clientID) {
			if (activePlayer.hasLegalMove(lastPlayedCard)) {
				processConsoleMsg("", "cantDraw", "");
			} else {
				// active player doesn't have a legal move, draw the card
				model.drawCard();
				processConsoleMsg(activePlayer.getName(), "drawCard", "");

				// efficiency
				Vector<Player> players = model.getPlayers();
				boolean turnOrder = model.getTurnOrderDirection();

				// refresh host's view
				view.displayCardsInHand(activePlayer);
				view.refreshScores(players, turnOrder);

				// refresh view of all clients
				server.requestViewRefresh(players, model.getLastPlayedCard(), turnOrder);

				// auto-pass turn
				if (activePlayer.getHandSize() >= Const.MAX_HAND_SIZE) {
					processConsoleMsg(activePlayer.getName(), "passTurn", "");
					model.setActivePlayer(model.getNextPlayer());
					processTurn();
				}
			}
		} else {
			System.out.println("Client ID did not match active player's ID. Card draw request denied.");
		}
	}

	/**
	 * Handles the playing of a card. The card has all special actions, if it has
	 * any, applied to it, before the player who played it's hand is updated, as
	 * well as the played cards zone, where the card now is. The scores are
	 * refreshed (as this is where information about cards in hand is) and since
	 * playing a card is a turn ending action, the next player is retrieved and the
	 * next round of processTurn() can be executed.
	 * 
	 * @param c The card to be played.
	 * 
	 * @since 23
	 */
	public void handleCardPlay(Card c) {
		Player activePlayer = model.getActivePlayer();
		String activePlayerName = activePlayer.getName();

		if (activePlayer.isHuman()) {
			if (model.playCard(c)) {
				for (ActionListener al : c.getActionListeners()) {
					c.removeActionListener(al);
				}
				processConsoleMsg(activePlayerName, "playCard", c.toString());
				handleCardActions(c);

				view.displayCardsInHand(activePlayer);
				view.displayLastPlayedCard(c);
				view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
				if (server != null) {
					server.requestViewRefresh(model.getPlayers(), model.getLastPlayedCard(),
							model.getTurnOrderDirection());
				}
				model.setActivePlayer(model.getNextPlayer());
				processTurn();

			} else {
				if (view.getSFXStatus()) {
					view.soundInvalidMove(view.getMusicStatus());
				}
			}
		} else {
			processConsoleMsg("", "notYourTurn", "");
			System.out.println("It is not your turn!");
		}
	}

	/**
	 * Strips all listeners from a Card button, then adds a single listener back
	 * onto it. As cards change zones (are added to human players hands) they must
	 * be made to be clickable by having listeners added to them.
	 * 
	 * @param player The player who's hand to add listeners to.
	 * 
	 * @since 23
	 */
	public void refreshListenersInPlayerHand(Player player) {
		for (Card c : player.getHand()) {
			for (ActionListener al : c.getActionListeners()) {
				c.removeActionListener(al);
			}
			c.addActionListener(new CardPlayListener());

			c.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

			c.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
				}

				public void mouseExited(MouseEvent e) {
					c.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				}
			});
		}
	}

	/**
	 * Strips all listeners from card buttons in a player's hand and adds new
	 * listeners to that hand.
	 * 
	 * @param hand the player's hand
	 * 
	 * @since 23
	 */
	public void refreshListenersInPlayerHand(String hand) {
		// Dummy player - doesn’t need real one
		Player tempPlayer = new Player("Temp", -1);
		String[] cardStrs = hand.split(",");
		Vector<Card> cards = new Vector<>();
		for (String cardStr : cardStrs) {
			cards.add(Card.getCardFromStr(cardStr));
		}
		tempPlayer.setHand(cards);

		for (Card c : tempPlayer.getHand()) {
			for (ActionListener al : c.getActionListeners()) {
				c.removeActionListener(al);
			}
			c.addActionListener(new CardPlayListener());
			c.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

			c.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
				}

				public void mouseExited(MouseEvent e) {
					c.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				}
			});
		}
	}


	/* --------------------------------------------------- */
	/* -------------------- LISTENERS -------------------- */
	/* --------------------------------------------------- */

	/**
	 * Listens for a card draw action and triggers the corresponding action.
	 * 
	 * 
	 * @since 23
	 */
	private class CardDrawListener implements ActionListener {

		/**
		 * Default constructor
		 */
		CardDrawListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {

			/*
			 * this is hacky but it works. basically clients never have active players in
			 * their models, only the server does.
			 */
			if (model.getActivePlayer() == null && client != null) {
				client.sendDraw();
				return;
			} else {
				if (model.isGameRunning()) {
					handleCardDraw();
				}
			}

		}
	}

	/**
	 * Listens for a card play action and triggers the corresponding action with the
	 * card played.
	 * 
	 * 
	 * @since 23
	 */
	private class CardPlayListener implements ActionListener {

		/**
		 * Default constructor
		 */
		CardPlayListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			Card c = (Card) e.getSource();

			/*
			 * When the game mode is multiplayer, there needs to be a way to differentiate
			 * when the host clicks the button to play a card and when a client clicks a
			 * card to play. If the server is trying to play a card, it should still check
			 * who the active player is and see if that id corresponds to it's own id.
			 * getHost() is used for this.
			 * 
			 * If the host is trying to play a card, just check for their turn and either
			 * allow/block the card play.
			 * 
			 * If the client is trying to play a card, they need to send a message to the
			 * server saying "i'm trying to play this card". the thing is, how do we
			 * differentiate clients from server? a client has a skeletal model that never
			 * has an active player. also, a client will have server == null. so
			 * activeplayer == null and server == null means the user is a client.
			 */

			Player activePlayer = model.getActivePlayer();

			// if the game mode is single player, we can just handle card play logic
			// directly
			if (gameMode == Const.SINGLE_PLAYER) {
				handleCardPlay(c);

				// handle multi-player logic. differentiate between host/client play
			} else if (gameMode == Const.MULTI_PLAYER) {

				// seems hacky but it works
				if (activePlayer == null && server == null) {
					client.sendPlay(c.toString());

					// either there is an active player, or the server != null
				} else {

					// check that the host is the player with the turn
					if (activePlayer.getId() == model.getHost().getId()) {
						handleCardPlay(c);
					} else {
					}
				}
			}
		}
	}

	/**
	 * Listens for the start of a round in single-player mode.
	 * 
	 * 
	 * @since 23
	 */
	private class SinglePlayerListener implements ActionListener {

		/**
		 * Default Constructor doesn't throw a fit
		 */
		SinglePlayerListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			gameMode = Const.SINGLE_PLAYER;
			model.setGameRunning(false);
			handleStartRound();
		}
	}

	/**
	 * Listens for a multiplayer game action.
	 * 
	 * 
	 * @since 23
	 */
	private class MultiPlayerListener implements ActionListener {

		/**
		 * Default Constructor
		 */
		MultiPlayerListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			gatherNetworkInfo(true);
		}
	}

	/**
	 * Listens for a join game action.
	 * 
	 * 
	 * @since 23
	 */
	private class JoinGameListener implements ActionListener {

		/**
		 * Default constructor
		 */
		JoinGameListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			gatherNetworkInfo(false);
		}
	}

	/**
	 * Listens for a disconnect action.
	 * 
	 * 
	 * @since 23
	 */
	private class DisconnectListener implements ActionListener {

		/**
		 * Default constructor
		 */
		DisconnectListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (gameMode == Const.SINGLE_PLAYER) {
				view.setBtnsMainMenu();
				if (model.isGameRunning()) {
					endGame();
				}
			} else {

				// client logic
				if (server == null && client != null) {
					client.disconnect();
					cleanUp();
					client = null;


					// server logic
				} else {
					server.terminateGame();
					server.terminateThreads();
					cleanUp();
					server = null;
					client = null;
				}
			}
		}
	}

	/**
	 * Listens for an "About" action click and triggers the display of the rules.
	 * 
	 * 
	 * @since 23
	 */
	private class AboutListener implements ActionListener {

		/**
		 * Default constructor
		 */
		AboutListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			view.displayRules();
		}
	}

	/**
	 * Listens for a change to the language to English.
	 * 
	 * 
	 * @since 23
	 */
	private class LangEnglishListener implements ActionListener {

		/**
		 * Default constructor
		 */
		LangEnglishListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Translating elements to English...");
			view.setLanguageToEnglish();
		}
	}

	/**
	 * Listens for a change to the language to French.
	 * 
	 * @since 23
	 */
	private class LangFrenchListener implements ActionListener {

		/**
		 * Default Constructor
		 */
		LangFrenchListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Translating elements to French...");
			view.setLanguageToFrench();
		}
	}

	/**
	 * Listens for a toggle of the sound setting.
	 * 
	 * 
	 * @since 23
	 */
	private class SoundToggleListener implements ActionListener {

		/**
		 * Default Constructor
		 */
		SoundToggleListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}

	/**
	 * Listens for a toggle of the music setting.
	 * 
	 * 
	 * @since 23
	 */
	private class MusicToggleListener implements ActionListener {

		/**
		 * Default Constructor
		 */
		MusicToggleListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}

	/**
	 * Listens for the action to send a chat message and triggers the sending of the
	 * message.
	 * 
	 * 
	 * @since 23
	 */
	private class ChatSendButtonListener implements ActionListener {

		/**
		 * Default constructor
		 */
		ChatSendButtonListener(){}

		@Override
		public void actionPerformed(ActionEvent e) {
			String msg = view.fetchMsg();
			if (msg != null && !msg.trim().isEmpty()) {
				String timeStamp = timeFormat.format(new Date());
				String formattedMsg = timeStamp + " " + playerName + ": " + msg.trim();
				if (server != null) {
					server.broadcastChat(formattedMsg);
					view.displayChat(formattedMsg);
				} else if (client != null) {
					client.sendChat(formattedMsg);
				}
			}
		}
	}

}
