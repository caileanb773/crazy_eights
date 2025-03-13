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
	private List<String> aiNames;
	private List<Card> library;
	private List<Card> playedCards;
	private Player pActivePlayer;
	private Player pGameWinner;
	private Player pRoundWinner;
	private boolean isTurnOrderReversed;
	private boolean isGameRunning;
	private boolean cardRedirection;
	private int currentTurn;
	private int numTwosPlayed;

	/* -------------------- CONSTRUCTORS -------------------- */

	public GameModel() {
	}
	
	public void initGame(int numHumanPlayers, String playerName) {
		int numAIPlayers = 0;
		players = new ArrayList<Player>();
		pGameWinner = null;
		pRoundWinner = null;
		playedCards = new ArrayList<Card>();
		aiNames = new ArrayList<String>();
		isTurnOrderReversed = false;
		currentTurn = 0;
		numTwosPlayed = 0;
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

		int orientation = 0;
		// TODO: replace this with a method that returns a real player's name
		Player humanPlayer = new Player(playerName, orientation);
		humanPlayer.setHuman(true);
		players.add(humanPlayer);

		for (int i = 0; i < numAIPlayers; i++) {
			this.players.add(createCPUOpponent(++orientation));
		}
	}
	
	public void clearGame() {
		cleanUpGameState();
		setTurnOrderReversed(false);
		currentTurn = 0;
		numTwosPlayed = 0;
		isGameRunning = false;
		pGameWinner = null;
		pRoundWinner = null;
		aiNames.clear();
		players.clear();
		players = null;
		playedCards = null;
		aiNames = null;
	}

	/* ------------------------------------------------------------------- */
	/* -------------------- CARD MANIPULATION METHODS -------------------- */
	/* ------------------------------------------------------------------- */

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

	public void initRound() {

		// New rounds always start from the host (south player), regardless of winner
		currentTurn = Const.SOUTH;

		// clear each player's hand, clear the library, clear the played cards
		if (library != null) {
			cleanUpGameState();
		}
		
		// set up a new game. shuffle a new deck and deal cards to each player
		instantiateDeck();
		shuffleDeck();
		dealCards(Const.DEFAULT_HAND_SIZE);

		// Flip the top card of the library into the played cards zone
		playedCards.add(library.removeLast());

		// Set the active player to the current turn
		pActivePlayer = players.get(currentTurn);
		isTurnOrderReversed = false;
		isGameRunning = true;
	}

	public void reshuffleSpentDeck() {

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

	/* -------------------------------------------------------- */
	/* -------------------- PLAYER ACTIONS -------------------- */
	/* -------------------------------------------------------- */

	public boolean playCard(Card card) {
		// defensive programming, unlikely scenarios
		if (!pActivePlayer.getHand().contains(card)) {
			System.out.println("Player attempted to play a card that wasn't in their hand in GameModel.playCard().");
			return false;
		} else if (pActivePlayer.getHandSize() == Const.HAND_EMPTY){
			System.out.println("Player attempted to play a card from an empty hand in GameModel.playCard().");
			return false;
		} else if (card == null) {
			System.out.println("GameModel.playCard() was passed a null card.");
			return false;
		} else {

			// determine legality of play
			if (isPlayLegal(card)) {
				System.out.println(pActivePlayer.getName() + " is playing a " + card.toString());
				pActivePlayer.removeCardFromHand(card);
				playedCards.add(card);
				applySpecialAction(card);
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

	public void drawCard() {
		// Check that the library is not empty. If it is, reshuffle it
		if (library.isEmpty()) {
			System.out.println("Library was emptied. Reshuffling...");
			reshuffleSpentDeck();
		}

		// DEBUG: this might be where that weird hand size bug is coming from
		if (pActivePlayer.getHandSize() < Const.MAX_HAND_SIZE) {
			Card drawnCard = library.removeLast();
			pActivePlayer.addCardToHand(drawnCard);
			System.out.println(pActivePlayer.getName() + " drew a " + drawnCard.toString());
		} else {
			System.out.println("Hand is full, cannot draw card");
		}
	}

	public void forceDraw(Player passivePlayer, int penaltyCards) {
		/* if the active player is forcing the passive player to draw x cards, 
		 * and their hand can only hold y cards, the surplus is redirected to 
		 * the active player.  */
		int remainingCards = penaltyCards;

		// while there are still cards left to be drawn
		while (remainingCards > 0) {

			// check that the deck is not empty. if it is, reshuffle all but the last played card into a new deck
			if (library.isEmpty()) {
				reshuffleSpentDeck();
				if (library.isEmpty()) {
					System.out.println("Deck remains empty after reshuffling. Ending this madness.");
					break;
				}
			}

			// if the passive player has room in their hand, force them to draw. else, the active player must draw
			if (passivePlayer.getHandSize() < Const.MAX_HAND_SIZE) {
				passivePlayer.addCardToHand(library.removeLast());
			} else if (pActivePlayer.getHandSize() < Const.MAX_HAND_SIZE){
				System.out.println("CARDS REDIRECTED TO PLAYER WHO PLAYED CARD!");
				pActivePlayer.addCardToHand(library.removeLast());
				cardRedirection = true;
			} else {
				// TODO: this method will need to check if incrementing a player's score caused them to go above 50 points
				int penaltyPoints = remainingCards;
				System.out.println("PENALTY POINTS assigned to " + pActivePlayer + " = " + penaltyPoints);
				incrementScore(pActivePlayer, penaltyPoints);
				if (isGameOver()) {
					endGame();
				}
			}
			// decrement the number of cards
			remainingCards--;
		}
	}

	/* -------------------------------------------------------------- */
	/* -------------------- SPECIAL CARD ACTIONS -------------------- */
	/* -------------------------------------------------------------- */

	
	public void applySpecialAction(Card c) {
		// each card technically has a special action of "not being a two"
		switch (c.getRank()) {
		case Rank.ACE: playAce(); break;
		case Rank.TWO: playTwo(); break;
		case Rank.FOUR: playFour(); break;
		case Rank.EIGHT: playEight(); break;
		case Rank.QUEEN: playQueen(); break;
		default: numTwosPlayed = 0; break;
		}
	}

	public void playAce() {
		numTwosPlayed = 0;
		isTurnOrderReversed = !isTurnOrderReversed;
	}

	public void playTwo() {
		numTwosPlayed++;
		forceDraw(peekNextPlayer(), 2*numTwosPlayed);
	}

	public void playFour() {
		numTwosPlayed = 0;
		forceDraw(peekNextPlayer(), 4);
	}

	public void playEight() {
		if (pActivePlayer.isHuman()) {
			// do nothing, handled by the controller
		} else {
			Suit s = ((AIPlayer) pActivePlayer).chooseSuit();
			getLastPlayedCard().setSuit(s);
			System.out.println(pActivePlayer.getName() + " decided to change the suit to " + s.toString());
		}
	}

	public void playQueen() {
		numTwosPlayed = 0;
		skipTurn();
	}

	/* ----------------------------------------------------------- */
	/* -------------------- GAMESTATE METHODS -------------------- */
	/* ----------------------------------------------------------- */

	public boolean isRoundOver() {
		for (Player p : players) {
			if (p.getHandSize() == Const.HAND_EMPTY) {
				pRoundWinner = p;
				return true;
			}
		}
		return false;
	}

	public void tallyScores() {
		for (Player p : players) {
			int score = p.getScore();
			score += p.getHandSize();
			p.setScore(score);
		}
	}

	public boolean isGameOver() {
		for (Player p : players) {
			if (p.getScore() >= Const.MAX_SCORE) {
				endGame();
				return true;
			}
		}
		return false;
	}

	public Player getWinningPlayer() {
		Player winningPlayer = null;
		
		// arbitrarily large value
		int minScore = Integer.MAX_VALUE;
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

	public Player peekNextPlayer() {
		int numPlayers = players.size();
		int nextTurn = isTurnOrderReversed ? currentTurn - 1 : currentTurn + 1;

		// Wrapping around when underflowing/overflowing max players
		if (nextTurn < 0) {
			nextTurn = numPlayers - 1;
		} else if (nextTurn >= numPlayers) {
			nextTurn = 0;
		}

		return players.get(nextTurn);
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

	public void endGame() {
		pGameWinner = getWinningPlayer();
		
		if (pGameWinner == null) {
			System.out.println("getWinningPlayer() returned a null winner in endGame()");
			return;
		}
		cleanUpGameState();
	}

	public void cleanUpGameState() {
		playedCards.clear();
		library.clear();
		for (Player p: players) {
			p.clearHand();
		}
	}

	/* ------------------------------------------------------------- */
	/* -------------------- GETTERS AND SETTERS -------------------- */
	/* ------------------------------------------------------------- */

	public void incrementScore(Player player, int amt) {
		player.setScore(player.getScore() + amt);
	}

	public Card getLastPlayedCard() {
		return this.playedCards.getLast();
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

	public boolean isGameRunning() {
		return this.isGameRunning;
	}

	public void setTurnOrderReversed(boolean turnOrder) {
		this.isTurnOrderReversed = turnOrder;
	}
	
	public void setTurn(int turn) {
		this.currentTurn = turn;
	}

	public void setGameRunning(boolean isGameRunning) {
		this.isGameRunning = isGameRunning;
	}

	public Player getRoundWinner() {
		return this.pRoundWinner;
	}
	
	public Player getGameWinner() {
		return this.pGameWinner;
	}
	
	public void setCardRedirection(boolean tf) {
		this.cardRedirection = tf;
	}
	
	public boolean getCardRedirection() {
		return this.cardRedirection;
	}

}
