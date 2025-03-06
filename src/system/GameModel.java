package system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sysobj.AIPlayer;
import sysobj.Card;
import sysobj.Player;
import sysobj.Rank;
import sysobj.Suit;

public class GameModel {

	private List<Player> players;
	private Player currentPlayer;
	private List<Card> deck;
	private List<Card> playedCards;
	private boolean isTurnOrderReversed;
	private int currentTurn;
	private int nextTurn;

	public GameModel() {
	}

	public GameModel(int numHumanPlayers) {
		int numAIPlayers = 0;
		this.players = new ArrayList<Player>();
		instantiateDeck();
		shuffleDeck();
		this.playedCards = new ArrayList<Card>();
		this.isTurnOrderReversed = false;
		this.currentTurn = 0;

		// Add AI players up to 4 based on how many human players are going to play
		if (numHumanPlayers == 1) {
			numAIPlayers = 3;
		} else if (numHumanPlayers == 2) {
			numAIPlayers = 2;
		} else {
			System.out.println("GameModel constructor tried to set numAIPlayers to number other than 2/3.");
		}

		for (int i = 0; i < numAIPlayers; i++) {
			this.players.add(createCPUOpponent());
		}

		// TODO: add the human players to the list here
	}

	public void instantiateDeck(){
		this.deck = new ArrayList<Card>();
		for (Suit s : Suit.values()) {
			for (Rank r : Rank.values()) {
				deck.add(new Card(r, s));
			}
		}
	}

	public void shuffleDeck() {
		Collections.shuffle(this.deck);
	}

	public AIPlayer createCPUOpponent() {
		return new AIPlayer(getAIPlayerName());
	}

	public String getAIPlayerName() {
		// TODO update this method to select a random name from the .csv
		return "AIPlayer";
	}

	public void startGame() {
		/* When the game starts, deal cards to each player. Then, the rest of the
		 * deck acts as the "draw" pile, and the top card of the deck is flipped
		 * over onto the "played cards" pile. Turns go in clockwise order to
		 * start (that is to say, 0, 1, 2, 3, 0, 1, 2... etc. */
		dealCards(6);
		playedCards.add(deck.remove(deck.size()-1));
		
		
		
		
		
		
		
		
		
		// THIS IS WHERE YOU WERE
		
		
		
		
		
		
	}
	
	public int getNextTurn() {
		int numPlayers = players.size();
		if (isTurnOrderReversed) {
			currentTurn--;
			if (currentTurn < 0) {
				currentTurn = numPlayers-1;
			}
			return currentTurn;
		} else {
			currentTurn++;
			if (currentTurn >= numPlayers) {
				currentTurn = 0;
			}
			return currentTurn;
		}
	}

	public void dealCards(int numCards) {
		int cardsNeeded = players.size() * numCards;
		
		if (cardsNeeded > deck.size()) {
			System.out.println("dealCards(): insufficient cards in deck to deal to players.");
			return;
		}
		
		for (Player p : players) {
			for (int i = 0; i < numCards; i++) {
				p.addCardToHand(deck.remove(deck.size()-1));
			}
		}
	}

}
