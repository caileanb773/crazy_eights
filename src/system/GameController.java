package system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Timer;
import sysobj.AIPlayer;
import sysobj.Card;
import sysobj.Player;
import sysobj.Rank;
import sysobj.Suit;

public class GameController {

	private GameModel model;
	private GameView view;

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
	
	public void endGame() {
		view.setPackCalls(0);
		view.displayGameWinner(model.getGameWinner());
		view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
		clearView();
		model.clearGame();
	}
	
	public void clearView() {
		for (Player p: model.getPlayers()) {
			view.displayCardsInHand(p);	
		}
		view.refreshView();
	}

	/* -------------------------------------------------------------- */
	/* -------------------- CARD ACTION HANDLERS -------------------- */
	/* -------------------------------------------------------------- */

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

	public void handleForcedDraw(int num) {
		Player passive = model.peekNextPlayer();
		view.displayCardsInHand(passive);
		if (passive.isHuman()) {
			refreshListenersInPlayerHand(passive);
		}
		view.refreshScores(model.getPlayers(), model.getTurnOrderDirection());
	}

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

	private class CardDrawListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			handleCardDraw();
		}
	}

	private class CardPlayListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Card c = (Card) e.getSource();
			handleCardPlay(c);
		}
	}

	private class SinglePlayerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			handleStartRound();
		}
	}

	private class MultiPlayerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}

	private class HostGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}

	private class JoinGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}

	private class DisconnectListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}

	private class AboutListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			view.displayAbout();
		}
	}

	private class LangEnglishListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}

	private class LangFrenchListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}

	private class SoundToggleListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}

	private class MusicToggleListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Implement logic here
		}
	}
	
	private class ChatSendButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			view.sendChatMsg(view.fetchMsg());
		}
	}

}
