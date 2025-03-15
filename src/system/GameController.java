package system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Timer;
import sysobj.AIPlayer;
import sysobj.Card;
import sysobj.Player;
import sysobj.Suit;

/**
 * Controller for the game, which calls methods in both the view and model.
 * @author Cailean Bernard
 * @since 23
 */
public class GameController {

	private GameModel model;
	private GameView view;

	/**
	 * Parameterized constructor for GameController. GameController acts as a
	 * bridge between the model and the view, and so it is passed both a model
	 * and a view object.
	 * @param m The model.
	 * @param v The view.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public GameController(GameModel m, GameView v) {
		this.model = m;
		this.view = v;

		view.setSinglePlayerListener(new SinglePlayerListener());
		view.setMultiPlayerListener(new MultiPlayerListener());
		view.setHostGameListener(new HostGameListener());
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

	/* ----------------------------------------------------------- */
	/* -------------------- GAME FLOW METHODS -------------------- */
	/* ----------------------------------------------------------- */

	/**
	 * Handles a new round starting. If the game is not running, then the round
	 * is the first round played, and so special initialization must take place.
	 * The name of the player is retrieved and a game is initialized with this 
	 * new player. If the game is running, then it's some subsequent round, so 
	 * just initialize a new round from the model, refresh the score box, update 
	 * the player names (which contain hand information), refresh the view's hand
	 * areas to reflect what the model contains, attach listeners to the human
	 * player's hand cards, and display the last played card, which starts the 
	 * game.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void handleStartRound() {
		if (!model.isGameRunning()) {
			String name = view.getPlayerName();
			model.initGame(Const.SINGLE_PLAYER, name);
		}
		model.initRound();
		List<Card> playedCards = model.getPlayedCards();
		for (Player p : model.getPlayers()) {
			view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
			view.updatePlayerNames(p);
			view.displayCardsInHand(p);
		}
		refreshListenersInPlayerHand(model.getActivePlayer());
		view.displayLastPlayedCard(playedCards.getLast());
	}

	/**
	 * Core logic of the gameplay loop. This method is called whenever a human
	 * player completes an action that would end their turn, and is "recursively"
	 * called for as long as there are AI players left who need to have a turn.
	 * Once all AI players have taken their turn, the method returns up the call
	 * stack, and the game is now "waiting" for the next human player to complete
	 * their turn.
	 * 
	 * The method begins by checking if the round is over or not. If it is, the
	 * round is ended gracefully. If the round is over, then that means scores
	 * were added to, which means the game might also be over, so that is also
	 * checked. If the game is over, end it gracefully. If neither of these two
	 * things are true, then begin a new round. If the round is not currently over,
	 * proceed with a turn as normal. If the active player is human, then they
	 * may have been forced to draw cards in the process of waiting for their turn,
	 * so refresh their listeners. If they aren't human, they're AI, so execute 
	 * their turn. The next processTurn() call is called within the process of the
	 * AI player executing their turn.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void processTurn() {
		Player activePlayer = model.getActivePlayer();
		System.out.println("It is now " + activePlayer.getName() + "'s turn.");

		// Check game state, starting with the status of the current round
		if (model.isRoundOver()) {
			
			endRound();
			// Check the status of the game
			if (model.isGameOver()) {
				endGame();
				return;
			}

			// The round is over, but the game is still going, so reset the round
			handleStartRound();
			return;
		}

		if (activePlayer.isHuman()) {
			refreshListenersInPlayerHand(activePlayer);
			return;
		} else {
			// AI player's turn
			executeAIPlayerTurn((AIPlayer) activePlayer);
		}

	}

	/**
	 * Executes an AI player's turn, which is mostly completed by decideAIPlayerMove().
	 * Once they have finished a turn, advance the turn and refresh the scores,
	 * then process the next turn.
	 * @param AIPlayer The player who should have their turn executed.
	 * @author Cailean Bernard
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
	 * information must be retrieved from the model so that the view can be
	 * updated to reflect the changes in the model. Fetches the AI player's
	 * move decision based on the game state, and handles each case.
	 * @param AIPlayer The AI player who's turn it is.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void decideAIPlayerMove(AIPlayer AIPlayer) {
		Card lastPlayedCard = model.getLastPlayedCard();
		Card cardToPlay = null;
		int choice = 0;

		while (true) {
			choice = AIPlayer.decidePlayDraw(lastPlayedCard);

			switch (choice) {
			case Const.PASS:
				System.out.println(AIPlayer.getName() + " is passing their turn.");
				return;
			case Const.PLAY:
				cardToPlay = AIPlayer.decideCard(lastPlayedCard);
				if (model.playCard(cardToPlay)) {
					handleCardActions(cardToPlay);
					view.displayCardsInHand(AIPlayer);
					view.displayLastPlayedCard(model.getLastPlayedCard());
				} else {
					System.out.println(AIPlayer.getName() + " tried to break the rules by playing an illegal card.");
				}
				return;

			case Const.DRAW:
				model.drawCard();
				view.displayCardsInHand(AIPlayer);
				break;
			default: System.out.println("Default switch case reached while AI was deciding move."); return;
			}
		}

	}

	/**
	 * Ends the current round. Resets some flags in the model, tallies up the 
	 * scores of each player at round end, displays those scores, and displays 
	 * the winner via dialog.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void endRound() {
		int totalCards = model.getDeck().size() + model.getPlayedCards().size();
		for (Player p : model.getPlayers()) {
			totalCards += p.getHandSize();
		}

		System.out.println("The round has ended, total cards from all zones: " + totalCards);

		model.setTurnOrderReversed(false);
		model.tallyScores();
		List<Player> players = model.getPlayers();
		view.refreshScores(players, model.getTurnOrderDirection());
		view.displayRoundWinner(model.getRoundWinner());
	}
	
	/**
	 * Handles the end of the game. Resets the pack call flag, displays the game
	 * winner as a dialog, refreshes the scoreboard, clears each player's hand,
	 * and resets the logic of the game within the model.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void endGame() {
		view.setPackCalls(0);
		view.displayGameWinner(model.getGameWinner());
		view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
		clearView();
		model.clearGame();
	}
	
	/**
	 * Called when cleaning up the game state. The cards in each player's hand
	 * are refreshed (now showing none, at the end of a game) and refreshView()
	 * is called on the view.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void clearView() {
		for (Player p: model.getPlayers()) {
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
	 * @param c The card to determine the special action for.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void handleCardActions(Card c) {
		switch (c.getRank()) {
		case TWO:
			handleForcedDraw(2);
			break;
		case FOUR:
			handleForcedDraw(4);
			break;
		case EIGHT: 
			handleEight();
			break;
		default: break;
		}
	}

	/**
	 * When a player is forced to draw cards by some effect, their hand must be
	 * refreshed to reflect the drawn cards. If they are human, listeners must
	 * be added to the cards so that they are clickable when it comes around to
	 * their turn.
	 * @param num The number of cards to be drawn.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void handleForcedDraw(int num) {
		Player passive = model.peekNextPlayer();
		view.displayCardsInHand(passive);
		if (passive.isHuman()) {
			refreshListenersInPlayerHand(passive);
		}
		view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
	}

	/**
	 * Handles when an eight is played. If the active player is human, they must
	 * be prompted to choose a suit (which is handled in the view). The chosen
	 * suit is then applied to the last played card, or it is left default if they
	 * cancelled out of the dialog.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void handleEight() {
		if (model.getActivePlayer().isHuman()) {
			Suit startingSuit = model.getLastPlayedCard().getSuit();
			Suit chosenSuit = view.dialogEightSuit();
			if (chosenSuit != null) {
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
	 * Handles the drawing of a card. Based on the active player, when they draw
	 * a card, their hand must be redrawn, as a freshly drawn card will be displayed
	 * in its entirety, but each subsequent card drawn after will cause that card
	 * to be displayed as a card slice. This method checks if the player has a 
	 * legal move in their hand, and if so, blocks them from drawing additional
	 * cards. The scores are refreshed (as this is where cards in hand information
	 * is) and they must continue drawing cards until their hand is full, or until
	 * they draw a card that is legal to play.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void handleCardDraw() {
		Player activePlayer = model.getActivePlayer();

		// check that the activeplayer is human before allowing them to draw a card
		if (activePlayer.isHuman()) {
			// block player from drawing a card if they have a legal play in hand
			if (activePlayer.hasLegalMove(model.getLastPlayedCard())) {
				System.out.println("Cannot draw a card if you have a legal play in hand. Play a card instead.");
			} else {
				model.drawCard();
				view.displayCardsInHand(activePlayer);
				view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
				refreshListenersInPlayerHand(activePlayer);

				// if their hand is full after card draw, end their turn
				if (activePlayer.getHandSize() >= Const.MAX_HAND_SIZE) {
					model.setActivePlayer(model.getNextPlayer());
					processTurn();
				}
			}
		} else {
			System.out.println("It is not your turn!");
		}
	}

	/**
	 * Handles the playing of a card. The card has all special actions, if it has
	 * any, applied to it, before the player who played it's hand is updated, as 
	 * well as the played cards zone, where the card now is. The scores are refreshed
	 * (as this is where information about cards in hand is) and since playing a
	 * card is a turn ending action, the next player is retrieved and the next
	 * round of processTurn() can be executed.
	 * @param c The card to be played.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void handleCardPlay(Card c) {
		Player activePlayer = model.getActivePlayer();

		if (activePlayer.isHuman()) {
			if (model.playCard(c)) {
				for (ActionListener al : c.getActionListeners()) {
					c.removeActionListener(al);
				}
				handleCardActions(c);
				view.displayCardsInHand(activePlayer);
				view.displayLastPlayedCard(c);
				System.out.println(activePlayer.getName() + "'s turn is now over.");
				view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
				model.setActivePlayer(model.getNextPlayer());
				processTurn();
			}
		} else {
			System.out.println("It is not your turn!");
		}
	}

	/**
	 * Strips all listeners from a Card button, then adds a single listener back
	 * onto it. As cards change zones (are added to human players hands) they must
	 * be made to be clickable by having listeners added to them.
	 * @param player The player who's hand to add listeners to.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void refreshListenersInPlayerHand(Player player) {
		for (Card c: player.getHand()) {
			for (ActionListener al : c.getActionListeners()) {
				c.removeActionListener(al);
			}
			c.addActionListener(new CardPlayListener());
		}
	}

	/* --------------------------------------------------- */
	/* -------------------- LISTENERS -------------------- */
	/* --------------------------------------------------- */

	/**
	 * Listens for a card draw action and triggers the corresponding action.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class CardDrawListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        handleCardDraw();
	    }
	}

	/**
	 * Listens for a card play action and triggers the corresponding action with 
	 * the card played.
	 * @param e the action event that contains the source of the event (the card 
	 * played)
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class CardPlayListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        Card c = (Card) e.getSource();
	        handleCardPlay(c);
	    }
	}

	/**
	 * Listens for the start of a round in single-player mode.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class SinglePlayerListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        handleStartRound();
	    }
	}

	/**
	 * Listens for a multiplayer game action.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class MultiPlayerListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	/**
	 * Listens for a host game action.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class HostGameListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	/**
	 * Listens for a join game action.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class JoinGameListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	/**
	 * Listens for a disconnect action.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class DisconnectListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	/**
	 * Listens for an "About" action click and triggers the display of the rules.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class AboutListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        view.displayAbout();
	    }
	}

	/**
	 * Listens for a change to the language to English.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class LangEnglishListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	    	System.out.println("Translating elements to English...");
	        view.setLanguageToEnglish();
	    }
	}

	/**
	 * Listens for a change to the language to French.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class LangFrenchListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        System.out.println("Translating elements to French...");
	        view.setLanguageToFrench();
	    }
	}

	/**
	 * Listens for a toggle of the sound setting.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class SoundToggleListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	/**
	 * Listens for a toggle of the music setting.
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class MusicToggleListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Implement logic here
	    }
	}

	/**
	 * Listens for the action to send a chat message and triggers the sending of the message.
	 * @param e the action event triggered by the user sending a message
	 * @author Cailean Bernard
	 * @since 23
	 */
	private class ChatSendButtonListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        view.sendChatMsg(view.fetchMsg());
	    }
	}


}
