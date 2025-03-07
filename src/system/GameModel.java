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
	private Player pActivePlayer;
	private Player pGameWinner;
	private Player pRoundWinner;
	private List<Card> deck;
	private List<Card> playedCards;
	private boolean isTurnOrderReversed;
	private int currentTurn;
	private int nextTurn;
	private int numTwosPlayed;
	
	//TODO processturn()
	//TODO handlegameover()
	//TODO handleroundover()
	//TODO endgame()
	//TODO checkPlay()
	
	// checkwincon and checkroundover can be abstracted out of processturn

	public GameModel() {
	}

	public GameModel(int numHumanPlayers) {
		int numAIPlayers = 0;
		this.players = new ArrayList<Player>();
		this.pGameWinner = null;
		this.pRoundWinner = null;
		instantiateDeck();
		shuffleDeck();
		this.playedCards = new ArrayList<Card>();
		this.isTurnOrderReversed = false;
		this.currentTurn = 0;
		this.nextTurn = 1;
		this.numTwosPlayed = 0;

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
		 * over onto the "played cards" pile. After this, a "round" of the game
		 * may begin until it is won and the points from it are tallied. */
		dealCards(6);
		playedCards.add(deck.remove(deck.size()-1));
		// TODO a more fitting name for this method might be "initializeRound();
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
	
	public void processTurn() {
		
		// TODO: wait for player actions (playing a card, drawing cards, passing turn
		
		/* Check if any one player has 0 cards in hand. If one player does, then
		 * the round is over. If the round is over, increment each user's score
		 * according to how many cards remain in their hand. Then, check if any
		 * one user has a score of 50 or more. If no players have empty hands,
		 * then the turn passes to the next player. */
		
		if (isRoundOver()) {
			// Increment each player's score based on current cards in hand
			for (Player p : players) {
				incrementScore(p, p.getHandSize());
			}
			
			// check if the game is over
			if (isGameOver()) {
				endGame();
			} else {
				// start the next round
			}
		} else {
			// move to the next players turn
		}

	}
	
	public boolean isRoundOver() {
		for (Player p : players) {
			if (p.getHandSize() == 0) {
				pRoundWinner = p;
				return true;
			}
		}
		return false;
	}
	
	public boolean isGameOver() {
		for (Player p : players) {
			if (p.getScore() >= 50) {
				return true;
			}
		}
		return false;
	}
	
	public Player getWinningPlayer() {
		Player winningPlayer = null;
		int minScore = 999;
		for (Player p : players) {
			int playerScore = p.getScore();
			if (playerScore < minScore) {
				minScore = playerScore;
				winningPlayer = p;
			}
		}
		return winningPlayer;
		// TODO: handle edge cases where there are 2 winners?
	}

	public int getNextTurn() {
		int numPlayers = players.size();
		if (isTurnOrderReversed) {
			currentTurn--;
			if (currentTurn < 0) {
				
				// wrap turn around to numplayers-1
				currentTurn = numPlayers-1;
			}
			return currentTurn;
		} else {
			currentTurn++;
			if (currentTurn >= numPlayers) {
				
				// wrap turn around to 0
				currentTurn = 0;
			}
			return currentTurn;
		}
	}
	
	public void skipTurn() {
		int numPlayers = players.size();
		if (isTurnOrderReversed) {
			currentTurn--;
			if (currentTurn < 0) {
				currentTurn = numPlayers-1;
			}
		} else {
			currentTurn++;
			if (currentTurn >= numPlayers) {
				currentTurn = 0;
			}
		}
	}

	public void forceDraw(Player passivePlayer, int numCards) {
		/* TODO: this method needs to take into consideration that if the player
		 * being forced to draw cards has 12, the surplus goes to the player who
		 * forced them to draw cards */

		/* if the active player is forcing the passive player to draw x cards, 
		 * and their hand can only hold y cards, the surplus is redirected to 
		 * the active player.  */

		// while there are still cards left to be drawn
		while (numCards > 0) {
			
			// check that the deck is not empty. if it is, reshuffle all but the last played card into a new deck
			if (deck.isEmpty()) {
				handleEmptyDeck();
			}
			
			// if the passive player has room in their hand, force them to draw. else, the active player must draw
			if (passivePlayer.getHandSize() < 12) {
				passivePlayer.addCardToHand(deck.remove(deck.size()-1));
			} else  if (pActivePlayer.getHandSize() < 12){
				pActivePlayer.addCardToHand(deck.remove(deck.size()-1));
			} else {
				// TODO: this method will need to check if incrementing a player's score caused them to go above 50 points
				incrementScore(pActivePlayer, numCards);
				if (isGameOver()) {
					endGame();
				}
			}
			
			// decrement the number of cards
			numCards--;
		}
	}
	
	public void handleEmptyDeck() {
		
		// Defensive programming
		if (playedCards.size() <= 1) {
			System.out.println("handleEmptyDeck() attempted to reshuffle a deck with only 1 card.");
			return;
		}
		
		/* Remove and reserve the top card of the played cards pile, then add all
		 * remaining cards to the deck. Clear the played cards, then add back
		 * the top card, then shuffle the deck. */
		Card topCard = playedCards.remove(playedCards.size()-1);
		deck.addAll(playedCards);
		playedCards.clear();
		shuffleDeck();
		playedCards.add(topCard);
	}
	
	public void resetGame() {
		/* add the cards from the played cards pile back to the deck, then clear
		 * the played cards pile. then remove all cards from all players hands and
		 * add those back to the deck. then shuffle, and flip over the top card
		 * into the play area */
		
		Card topCard = playedCards.remove(playedCards.size()-1);
		deck.addAll(playedCards);
		playedCards.clear();
		
		for (Player p : players) {
			deck.addAll(p.getHand());
			p.clearHand();
		}
		
		shuffleDeck();
		playedCards.add(topCard);
	}
	
	public void endGame() {
		pGameWinner = getWinningPlayer();
		
		// send winner winner chicken dinner message to all players
		// prompt for rematch potentially?
		
		// last thing
		cleanUpGameState();
	}
	
	public void cleanUpGameState() {
		playedCards.clear();
		deck.clear();
		for (Player p: players) {
			p.clearHand();
		}
	}
	
	public void incrementScore(Player player, int amt) {
		player.setScore(player.getScore() + amt);
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}
	
	public List<Card> getDeck() {
		return this.deck;
	}
	
	public List<Card> getPlayedCards() {
		return this.playedCards;
	}

}
