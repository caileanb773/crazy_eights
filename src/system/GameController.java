package system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import sysobj.Card;
import sysobj.Player;

public class GameController implements Observer {

	private GameModel model;
	private GameView view;
	private int processTurnCalls = 0;

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
	}
	
	@Override
	public void update() {
		
	}
	
	public void handleStartGame() {
		model.startGame();
		List<Card> playedCards = model.getPlayedCards();
		int orientation = 0;
		for (Player p : model.getPlayers()) {
			view.updateScoreTable(p);
			view.updatePlayerNames(p, orientation);
			view.displayCardsInHand(p, orientation++);
		}
		addListenersToPlayerHand(model.getActivePlayer());
		view.displayLastPlayedCard(playedCards.getLast());
		beginGame();
	}
	
	public void beginGame() {
		
	}
	
	public void processTurn() {
		System.out.println("ProcessTurn() Calls: " + processTurnCalls++);
		
		if (model.isRoundOver()) {
			if (model.isGameOver()) {
				System.out.println("Game is now over.");
				endGame();
				return;
			}
			System.out.println("The round is now over.");
			endRound();
			return;
		}
		
		if (model.getActivePlayer().isHuman()) {
			System.out.println("Waiting for human player's turn...");
			return;
		} else {
			// AI player's turn
			
			
		}
		
	}
	
	public void endGame() {
		System.out.println("In endGame()");
	}
	
	public void endRound() {
		System.out.println("In endRound()");
	}

	public void addListenersToPlayerHand(Player player) {
		for (Card c: player.getHand()) {
			for (ActionListener al : c.getActionListeners()) {
				c.removeActionListener(al);
			}
			c.addActionListener(new CardPlayListener());
		}
	}

	private class SinglePlayerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			handleStartGame();
		}
	}

	private class CardDrawListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			handleCardDraw();
		}
	}
	
	public void handleCardDraw() {
		System.out.println("Library clicked");
		Player activePlayer = model.getActivePlayer();
		System.out.println("DEBUGGING: active player is: " + activePlayer.toString());

		// check that the activeplayer is human before allowing them to draw a card
		if (activePlayer.isHuman()) {
			// block player from drawing a card if they have a legal play in hand
			if (activePlayer.hasLegalMove(model.getPlayedCards().getLast())) {
				System.out.println("Cannot draw a card if you have a legal play in hand. Play a card instead.");
			} else {
				model.drawCard();
				view.refreshHand(activePlayer, activePlayer.getOrientation());
				addListenersToPlayerHand(activePlayer);

				// if their hand is full after card draw, end their turn
				if (activePlayer.getHandSize() == 12) {
					System.out.println(activePlayer.getName() + "'s turn is now over.");
					model.setActivePlayer(model.getNextPlayer());
					processTurn();
				}
			}
		} else {
			System.out.println("It is not your turn!");
		}
	}

	private class CardPlayListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Card c = (Card) e.getSource();
			handleCardPlay(c);
		}
	}
	
	public void handleCardPlay(Card c) {
		Player activePlayer = model.getActivePlayer();

		if (activePlayer.isHuman()) {
			if (model.playCard(c)) {
				for (ActionListener al : c.getActionListeners()) {
					c.removeActionListener(al);
				}
				view.refreshHand(activePlayer, activePlayer.getOrientation());
				view.displayLastPlayedCard(c);
				System.out.println(activePlayer.getName() + "'s turn is now over.");
				model.setActivePlayer(model.getNextPlayer());
				processTurn();
			}
		} else {
			System.out.println("It is not your turn!");
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
			// TODO Auto-generated method stub

		}

	}


}
