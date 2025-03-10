package system;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	private List<Card> library;
	private List<String> aiNames;
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
	
	// TODO: thought. the user should not just be able to draw cards whenever they want.
	// the game will have to check if they have a move and force them to play a card if they do.
	// they are only allowed to draw a card if they do not have a legal play
	
	// checkwincon and checkroundover can be abstracted out of processturn

	public GameModel() {
	}

	public GameModel(int numHumanPlayers) {
		int numAIPlayers = 0;
		players = new ArrayList<Player>();
		pGameWinner = null;
		pRoundWinner = null;
		playedCards = new ArrayList<Card>();
		aiNames = new ArrayList<String>();
		isTurnOrderReversed = false;
		currentTurn = 3;
		nextTurn = 1;
		numTwosPlayed = 0;
		loadAINames();
		
		// Add AI players up to 4 based on how many human players are going to play
		if (numHumanPlayers == 1) {
			numAIPlayers = 3;
		} else if (numHumanPlayers == 2) {
			numAIPlayers = 2;
		} else {
			System.out.println("GameModel constructor tried to set numAIPlayers to number other than 2/3.");
		}
		
		int orientation = 0;

		for (int i = 0; i < numAIPlayers; i++) {
			this.players.add(createCPUOpponent(orientation++));
		}

		// TODO: replace this with a method that returns a real player's name
		players.add(new Player("ME", GameView.SOUTH));
	}

	public void instantiateDeck(){
		this.library = new ArrayList<Card>();
		for (Suit s : Suit.values()) {
			for (Rank r : Rank.values()) {
				library.add(new Card(r, s));
			}
		}
	}

	public void shuffleDeck() {
		Collections.shuffle(this.library);
	}

	public AIPlayer createCPUOpponent(int orientation) {
		return new AIPlayer(getAIPlayerName(), orientation);
	}
	
	public void loadAINames() {
		String name;
		try (BufferedReader reader = new BufferedReader(new FileReader("asset/AINames.txt"))){
			while ((name = reader.readLine()) != null) {
				aiNames.add(name);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found in GameModel.getAIPlayerName().");
		} catch (IOException e) {
			System.out.println("IOException encountered");
		}
	}

	public String getAIPlayerName() {
		String name;
		Collections.shuffle(aiNames);
		name = aiNames.getLast();
		aiNames.remove(aiNames.size()-1);
		return "AI " + name.toUpperCase();
	}

	public void startGame() {
		/* When the game starts, deal cards to each player. Then, the rest of the
		 * deck acts as the "draw" pile, and the top card of the deck is flipped
		 * over onto the "played cards" pile. After this, a "round" of the game
		 * may begin until it is won and the points from it are tallied. */
		instantiateDeck();
		shuffleDeck();
		dealCards(6);
		playedCards.add(library.remove(library.size()-1));
		pActivePlayer = players.get(currentTurn);
		System.out.println(pActivePlayer.toString());
		// TODO a more fitting name for this method might be "initializeRound();
	}
	
	public boolean playCard(Card card) {
		// defensive programming, unlikely scenarios
		if (!pActivePlayer.getHand().contains(card)) {
			System.out.println("Player attempted to play a card that wasn't in their hand in GameModel.playCard().");
			return false;
		} else if (pActivePlayer.getHandSize() == 0){
			System.out.println("Player attempted to play a card from an empty hand in GameModel.playCard().");
			return false;
		} else if (card == null) {
			System.out.println("GameModel.playCard() was passed a null card.");
			return false;
		} else {
			
			// determine legality of play
			if (isPlayLegal(card)) {
				pActivePlayer.removeCardFromHand(card);
				playedCards.add(card);
				return true;
			} else {
				return false;
			}
			
		}
	}
	
	public boolean isPlayLegal(Card card) {
		if (card == null) {
			System.out.println("GameModel.isPlayLegal() passed null card.");
			return false;
		}
		
		Card lastPlayedCard = playedCards.getLast();
		Rank cardRank = card.getRank();
		if (cardRank == Rank.EIGHT
				|| cardRank == lastPlayedCard.getRank()
				|| card.getSuit() == lastPlayedCard.getSuit()) {
			return true;
		}
		System.out.println("Illegal move!");
		return false;
	}
	
	public void dealCards(int numCards) {
		int cardsNeeded = players.size() * numCards;

		if (cardsNeeded > library.size()) {
			System.out.println("dealCards(): insufficient cards in deck to deal to players.");
			return;
		}

		for (Player p : players) {
			for (int i = 0; i < numCards; i++) {
				p.addCardToHand(library.remove(library.size()-1));
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
	
	public void drawCard() {
		// Check that the library is not empty
		if (!library.isEmpty()) {
			if (pActivePlayer.getHandSize() < 12) {
				Card drawnCard = library.remove(library.size()-1);
				pActivePlayer.addCardToHand(drawnCard);
				System.out.println(drawnCard.toString());
			} else {
				System.out.println("Hand is full, cannot draw card");
			}
			System.out.println("Size of library is now " + library.size());
		} else {
			// TODO: Reshuffle all but the top card of the played cards back into the library
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
			if (library.isEmpty()) {
				handleEmptyDeck();
			}
			
			// if the passive player has room in their hand, force them to draw. else, the active player must draw
			if (passivePlayer.getHandSize() < 12) {
				passivePlayer.addCardToHand(library.remove(library.size()-1));
			} else  if (pActivePlayer.getHandSize() < 12){
				pActivePlayer.addCardToHand(library.remove(library.size()-1));
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
		library.addAll(playedCards);
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
		library.addAll(playedCards);
		playedCards.clear();
		
		for (Player p : players) {
			library.addAll(p.getHand());
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
		library.clear();
		for (Player p: players) {
			p.clearHand();
		}
	}
	
	public void incrementScore(Player player, int amt) {
		player.setScore(player.getScore() + amt);
	}
	
	public Player getActivePlayer() {
		return this.pActivePlayer;
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}
	
	public List<Card> getDeck() {
		return this.library;
	}
	
	public List<Card> getPlayedCards() {
		return this.playedCards;
	}

}
