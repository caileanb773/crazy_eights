package system;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import sysobj.AIPlayer;
import sysobj.Card;
import sysobj.Player;
import sysobj.Rank;
import sysobj.Suit;

/**
 * Contains logic and objects/collectinos needed to process game logic.
 * @author Cailean Bernard
 * @since 23
 * */
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

	/**
	 * Default constructor for GameModel
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public GameModel() {
	}

	/**
	 * Initializes the game state. Resets all flags/clears all winners/creates AI
	 * players, adds players to the game.
	 * @param numHumanPlayers - Right now, this is always 1 (single player)
	 * @param playerName - The name of the one human player playing.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Clears the game status. Calls cleanUpGameState(), resets the turn order,
	 * resets game/round winners. This is called at the end of the game to 
	 * simulate the game state as it was when it began.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Clears the deck if it exists, else creates a new deck of 52 cards of each
	 * rank and suit.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Shuffles the deck using the method from Collections.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void shuffleDeck() {
		Collections.shuffle(this.library);
	}

	/**
	 * Deals numCards players to each player in the list of players.
	 * @param numCards - the number of cards to deal to each player
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Creates a CPU opponent by getting a unique name from the list of AI names.
	 * @return AIPlayer - the completed AIPlayer.
	 * @param orientation - Which orientation the AI player belongs to
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public AIPlayer createCPUOpponent(int orientation) {
		return new AIPlayer(getAIPlayerName(), orientation);
	}

	/**
	 * Loads the list of names from the .txt into a local list
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Retrieves a unique name for an AI player from the list of names loaded by
	 * loadAIName
	 * @return String - The name returned.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public String getAIPlayerName() {
		String name;
		Collections.shuffle(aiNames);
		name = aiNames.getLast();
		aiNames.removeLast();
		return "AI " + name.toUpperCase();
	}

	/**
	 * Initializes a new round. Resets the turn to start from the human player,
	 * clears each player's hand/the library/the played cards. Instantiates a new
	 * deck, shuffles it, deals cards to players, and flips the last card from the
	 * library into the played cards zone. Sets the configures the starting turn.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

		//dealCards(Const.DEFAULT_HAND_SIZE);
		dealCards(1);

		// Flip the top card of the library into the played cards zone
		playedCards.add(library.removeLast());

		// Edge case where the first card flipped in a round is a two
		if (getLastPlayedCard().getRank() == Rank.TWO) {
			numTwosPlayed++;
		}

		// Set the active player to the current turn
		pActivePlayer = players.get(currentTurn);
		isTurnOrderReversed = false;
		isGameRunning = true;
	}

	/**
	 * Conserves the last played card and reshuffles all other cards from the 
	 * library and played cards into a new library so that players don't try to
	 * draw from an empty deck.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Attempts to play a card onto the last card of the played cards zone. Only
	 * succeeds if the "play" is legal.
	 * @param card - The card to play.
	 * @return boolean - Is the play successful or not.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Checks if a play is legal. A play is legal if the rank or suit of the card
	 * to be played matches the rank or suit of the last played card, or if the
	 * card to be played is an eight.
	 * @return boolean - Determines if the play was legal or not
	 * @param card - The card to be played
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Draws a card to the hand of the current active user. If the library is
	 * empty, the deck is reshuffled first. Checks that the draw would not cause
	 * the player to have more than 12 (max) cards in hand first.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Forces a player to draw x cards. This happens if a two or four is played.
	 * The passive player is the player being forced to draw cards, and the active
	 * player is the player who's turn it is. Checks that the passive player has
	 * enough room in their hand to receive the cards, and if not, the cards are
	 * redirected to the active player. If that player also has a full hand, then
	 * the cards are not drawn and are instead turned into penalty points for
	 * the active player.
	 * @param passivePlayer - The player being forced to draw cards.
	 * @param penaltyCards - The amount of cards that the method is forcing upon
	 * the player.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Applies special actions that certain cards have, as stated in the rules.
	 * Each card that does not have a special action in the rules has the default
	 * special action of not being a two and resets the count of twos played.
	 * @param c - The card to check for special actions.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void applySpecialAction(Card c) {
		// each card technically has a special action of "not being a two"
		switch (c.getRank()) {
		case ACE: playAce(); break;
		case TWO: playTwo(); break;
		case FOUR: playFour(); break;
		case EIGHT: playEight(); break;
		case QUEEN: playQueen(); break;
		default: numTwosPlayed = 0; break;
		}
	}

	/**
	 * Aces reverse the turn order. Default is clockwise vs. counterclockwise.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void playAce() {
		numTwosPlayed = 0;
		isTurnOrderReversed = !isTurnOrderReversed;
	}

	/**
	 * Twos force the next player in the turn order to draw two times the number
	 * of twos played consecutively cards.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void playTwo() {
		numTwosPlayed++;
		forceDraw(peekNextPlayer(), 2*numTwosPlayed);
	}

	/**
	 * Fours force the next player in the turn order to draw four cards.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void playFour() {
		numTwosPlayed = 0;
		forceDraw(peekNextPlayer(), 4);
	}

	/**
	 * Eights allow the player to choose a new suit for the played eight. AI
	 * players choose a suit at random.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void playEight() {
		if (pActivePlayer.isHuman()) {
			// do nothing, handled by the controller
		} else {
			Suit s = ((AIPlayer) pActivePlayer).chooseSuit();
			getLastPlayedCard().setSuit(s);
			System.out.println(pActivePlayer.getName() + " decided to change the suit to " + s.toString());
		}
	}

	/**
	 * Queens skip the turn of the next player.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void playQueen() {
		numTwosPlayed = 0;
		skipTurn();
	}

	/* ----------------------------------------------------------- */
	/* -------------------- GAMESTATE METHODS -------------------- */
	/* ----------------------------------------------------------- */

	/**
	 * Checks if the current round is over; the round is over when any one player
	 * has no cards in hand. If a player plays their last card, they are the
	 * winner of the round.
	 * @return boolean - True if the round is over, false if not.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public boolean isRoundOver() {
		for (Player p : players) {
			if (p.getHandSize() == Const.HAND_EMPTY) {
				pRoundWinner = p;
				return true;
			}
		}
		return false;
	}

	/**
	 * Tallies up the scores for all players. The "score" is equal to the number
	 * of cards remaining in players hands when the round ends.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void tallyScores() {
		for (Player p : players) {
			int score = p.getScore();
			score += p.getHandSize();
			p.setScore(score);
		}
	}

	/**
	 * Checks if the game is over. The game is over if any one player has reached
	 * or exceeded the max score as outlined in the constants (default 50). If
	 * the game is over, endGame() is called.
	 * @return boolean - True if the game is over, false if not.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public boolean isGameOver() {
		for (Player p : players) {
			if (p.getScore() >= Const.MAX_SCORE) {
				endGame();
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine who the winning player is. At the end of the game, the winning
	 * player is the one with the least points.
	 * @return Player - The winning player.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Retrieves the next player based on the current turn and the turn order and
	 * advances the turn by one in a direction based on if the turn order is 
	 * reversed or not.
	 * @return Player - The next player in the turn order.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Looks at the next player without changing the turn order.
	 * @param Player - The next player in the turn order.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Skips the next player's turn. Increments/decrements the current turn, based
	 * on if the turn order is reversed or not.
	 * @author Cailean Bernard
	 * @since 23
	 * */
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

	/**
	 * Ends the current game. Retrieves the winning player, sets isGameRunning to
	 * false, and cleans up the game state.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void endGame() {
		pGameWinner = getWinningPlayer();
		isGameRunning = false;
		if (pGameWinner == null) {
			System.out.println("getWinningPlayer() returned a null winner in endGame()");
			return;
		}
		cleanUpGameState();
	}

	/**
	 * Clears the collections for played cards, library, and each player's hand.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void cleanUpGameState() {
		playedCards.clear();
		library.clear();
		for (Player p: players) {
			p.clearHand();
		}
	}

	/* ---------------------------------------------------------------- */
	/* -------------------- GETTERS, SETTERS, MISC -------------------- */
	/* ---------------------------------------------------------------- */

	/**
	 * Increments a players score by a passed amount.
	 * @param player - The player who's score to increment.
	 * @param amt - The amount to increment the score by.
	 * @author Cailean Bernard
	 * @since 23
	 * */
	public void incrementScore(Player player, int amt) {
		player.setScore(player.getScore() + amt);
	}

	/**
	 * Getter for the last played card (the top of the played cards pile)
	 * @return Card - the top of the played cards pile
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Card getLastPlayedCard() {
		return this.playedCards.getLast();
	}

	/**
	 * Getter for the player who's turn it is.
	 * @return Player - The active player.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Player getActivePlayer() {
		return this.pActivePlayer;
	}

	/**
	 * Setter to set the active player (the player who's turn it is). 
	 * @param p - Set the active player to this player.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setActivePlayer(Player p) {
		this.pActivePlayer = p;
	}

	/**
	 * Getter for the list of players in the current game.
	 * @return the list of players in the current game.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public List<Player> getPlayers() {
		return this.players;
	}

	/**
	 * Getter for the library (the deck of unplayed cards.
	 * @return the list of unplayed cards.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public List<Card> getDeck() {
		return this.library;
	}

	/**
	 * The "discard pile" or played cards. When a player plays a card, it goes here.
	 * @return the collection of played cards.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public List<Card> getPlayedCards() {
		return this.playedCards;
	}

	/**
	 * Getter for the status of the game.
	 * @return true if the game is running, false if not.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public boolean isGameRunning() {
		return this.isGameRunning;
	}

	/**
	 * Setter for the status of the game.
	 * @param isGameRunning - True if the game is running, false if the game is
	 * not running.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setGameRunning(boolean isGameRunning) {
		this.isGameRunning = isGameRunning;
	}

	/**
	 * Sets the turn order to either normal or reversed.
	 * @param turnOrder - True is reversed/counterclockwise, false is normal or
	 * clockwise.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setTurnOrderReversed(boolean turnOrder) {
		this.isTurnOrderReversed = turnOrder;
	}

	/**
	 * Getter for the turn order.
	 * @return true if the turn order is reversed, false if the turn order is normal.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public boolean getTurnOrderDirection() {
		return this.isTurnOrderReversed;
	}

	/**
	 * Setter for the current turn.
	 * @param turn - sets the current turn to the passed int.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setTurn(int turn) {
		this.currentTurn = turn;
	}

	/**
	 * Getter for the winner of the current round.
	 * @return the winner of the current round as a Player object.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Player getRoundWinner() {
		return this.pRoundWinner;
	}

	/**
	 * Getter for the winner of the current game.
	 * @return the winner of the current game as a Player object.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Player getGameWinner() {
		return this.pGameWinner;
	}

	/**
	 * Setter for the card redirection flag. This flag is used to determine if
	 * card redirection is happening (in force draw scenarios)
	 * @param tf - Set the flag to true/false
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setCardRedirection(boolean tf) {
		this.cardRedirection = tf;
	}

	/**
	 * Getter for card redirection status.
	 * @return the status of the flag for redirection.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public boolean getCardRedirection() {
		return this.cardRedirection;
	}

}
