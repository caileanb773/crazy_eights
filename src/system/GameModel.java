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
	private boolean isPlayerTurnOver;
	private boolean isGameRunning;

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
		currentTurn = 0;
		nextTurn = 1;
		numTwosPlayed = 0;
		isPlayerTurnOver = false;
		isGameRunning = false;
		loadAINames();

		// Add AI players up to 4 based on how many human players are going to play
		if (numHumanPlayers == 1) {
			numAIPlayers = 3;
		} else if (numHumanPlayers == 2) {
			numAIPlayers = 2;
		} else {
			System.out.println("GameModel constructor tried to set numAIPlayers to number other than 2/3.");
		}

		// TODO: This is horrible, refactor this
		int orientation = 0;
		// TODO: replace this with a method that returns a real player's name
		Player humanPlayer = new Player("Me", orientation);
		humanPlayer.setHuman(true);
		players.add(humanPlayer);

		for (int i = 0; i < numAIPlayers; i++) {
			this.players.add(createCPUOpponent(++orientation));
		}

	}

	public void instantiateDeck(){
		if (library != null)
				library.clear();
		
		library = new ArrayList<Card>();
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
		aiNames.removeLast();
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
		
		// Flip the top card of the library into the played cards zone
		playedCards.add(library.removeLast());
		
		// Set the active player to the current turn
		pActivePlayer = players.get(currentTurn);
		isGameRunning = true;
		// DEBUGGING
		System.out.println(pActivePlayer.toString());
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
				isPlayerTurnOver = true;
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
		if (library == null || library.isEmpty()) {
			System.out.println("Library was null or empty in GameModel.dealCards().");
		}
		int cardsNeeded = players.size() * numCards;

		if (cardsNeeded > library.size()) {
			System.out.println("dealCards(): insufficient cards in deck to deal to players.");
			return;
		}

		for (Player p : players) {
			for (int i = 0; i < numCards; i++) {
				p.addCardToHand(library.removeLast());
			}
		}
	}

	public void executeAIPlayerTurn() {
		AIPlayer player = (AIPlayer) pActivePlayer;
		Card lastPlayedCard = playedCards.getLast();
		int choice = player.decidePlayDraw(lastPlayedCard);
		switch (choice) {

		// 1 = PLAY, 2 = DRAW, 3 = PASS
		case 1:
			Card cardChoice = player.decideCard(lastPlayedCard);
			player.removeCardFromHand(cardChoice);
			playedCards.add(cardChoice);
			
			break;
		case 2:
			break;
		case 3:
			break;
		default:
			System.out.println("Default case reached in executeAIPlayerTurn()");
			return;
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

	public Player getNextPlayer() {
		int numPlayers = players.size();
		if (isTurnOrderReversed) {
			currentTurn--;
			if (currentTurn < 0) {

				// wrap turn around to numplayers-1
				currentTurn = numPlayers-1;
			}
		} else {
			currentTurn++;
			if (currentTurn >= numPlayers) {

				// wrap turn around to 0
				currentTurn = 0;
			}
		}
		return players.get(currentTurn);
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
				Card drawnCard = library.removeLast();
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
				passivePlayer.addCardToHand(library.removeLast());
			} else  if (pActivePlayer.getHandSize() < 12){
				pActivePlayer.addCardToHand(library.removeLast());
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
		Card topCard = playedCards.removeLast();
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

		Card topCard = playedCards.removeLast();
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
	
	public void setActivePlayer(Player p) {
		this.pActivePlayer = p;
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

	public boolean getIsPlayerTurnOver() {
		return this.isPlayerTurnOver;
	}

	public void setIsPlayerTurnOver(boolean b) {
		this.isPlayerTurnOver = b;
	}
	
	public boolean isGameRunning() {
		return this.isGameRunning;
	}
	
	public void setGameRunning(boolean isGameRunning) {
		this.isGameRunning = isGameRunning;
	}

}
