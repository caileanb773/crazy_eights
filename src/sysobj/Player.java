package sysobj;

import java.util.Vector;

/**
 * Players have a name, a hand of cards, a score, an orientation in the UI, and 
 * a flag for if they are human or not.
 * 
 * @since 23
 */
public class Player {

	/** The list of cards held by the player. */
	protected Vector<Card> hand;

	/** Indicates whether the player is human. */
	protected boolean isHuman;

	/** Indicates whether the player is the host. */
	protected boolean isHost;

	/** The name of the player. */
	protected String name;

	/** The current score of the player. */
	protected int score;

	/** The orientation of the player (e.g., vertical or horizontal). */
	protected int orientation;

	/** The unique ID of the player. */
	protected int id;

	/**
	 * Default Player constructor.
	 * @since 23
	 */
	public Player() {
	}
	
	/***
	 * Name-only constructor for a Player.
	 * @param name of the player
	 * @since 23
	 */
	public Player(String name) {
		this.name = name;
		this.score = 0;
		this.hand = new Vector<Card>();
	}

	/**
	 * Parameterized constructor for Player.
	 * @param n - The name for the player.
	 * @param orientation - The orientation in the UI that the player is occupying.
	 * 
	 * @since 23
	 */
	public Player(String n, int orientation) {
		this.name = n;
		this.hand = new Vector<Card>();
		this.score = 0;
		this.orientation = orientation;
		this.isHuman = false;
	}
	
	/**
	 * Constructor for a player.
	 * 
	 * @param n name
	 * @param orientation player orientation
	 * @param id player id
	 * @param isHuman are they human or not 
	 */
	public Player(String n, int orientation, int id, boolean isHuman) {
		this.name = n;
		this.hand = new Vector<Card>();
		this.score = 0;
		this.orientation = orientation;
		this.id = id;
		this.isHuman = isHuman;
		
		// Human players are clients unless explicitly specified otherwise
		isHost = false;
	}

	/**
	 * Adds a card to this player's hand. Checks that the hand is not full before
	 * adding the card to it.
	 * @param card - The card to be added to the hand.
	 * @since 23
	 */
	public void addCardToHand(Card card) {
		if (this.getHandSize() >= 12) {
			System.out.println("Player.addCardToHand() tried to add card to full hand.");
			return;
		}

		if (card == null) {
			System.out.println("Player.addCardToHand() was passed a null card.");
			return;
		}
		hand.add(card);
	}

	/**
	 * Removes a card from the player's hand.
	 * @param card - The card to remove.
	 * @since 23
	 */
	public void removeCardFromHand(Card card) {
		this.hand.remove(card);
	}

	/**
	 * Determines if the player has any legal moves based on the status of the 
	 * last played card. This is used to force players into certain actions or to
	 * force the player to pass the turn.
	 * @param lastPlayedCard - The last card on the played cards pile.
	 * @return true if they have a legal move, false if not.
	 * @since 23
	 */
	public boolean hasLegalMove(Card lastPlayedCard) {
		if (this.hand.isEmpty()) {
			System.out.println("Player.hasLegalMove() found no cards in hand.");
			return false;
		}

		/* a player had a legal move if they have an 8 in hand OR they have a
		 * card in hand that matches the last played card's rank OR suit */
		for (Card card : this.hand) {
			Rank rank = card.getRank();
			if (rank == Rank.EIGHT
					|| rank == lastPlayedCard.getRank()
					|| card.getSuit() == lastPlayedCard.getSuit()) {
				return true;
			}
		}

		// no legal moves found
		return false;
	}

	/**
	 * Returns the value of the "isHuman" flag.
	 * @return the value of the flag. True = human, false = AI.
	 * @since 23
	 */
	public boolean isHuman() {
		return isHuman;
	}

	/**
	 * Set the "isHuman" flag on a Player object.
	 * @param isHuman - Whether the flag should be set to true or false.
	 * @since 23
	 */
	public void setHuman(boolean isHuman) {
		this.isHuman = isHuman;
	}
	
	/**
	 * Checks if the player is the host
	 * @return boolean true if host, false if not
	 * @since 23
	 */
	public boolean isHost() {
		return this.isHost;
	}
	
	/**
	 * Getter for if the player is a host or not.
	 * @param isHost are they the host or not
	 */
	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

	/**
	 * Sets the player's name.
	 * @param name The name to set.
	 * @since 23
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the player's score.
	 * @param scr The score to set.
	 * @since 23
	 */
	public void setScore(int scr) {
		this.score = scr;
	}

	/**
	 * Retrieves the player's name.
	 * @return The player's name.
	 * @since 23
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the player's score.
	 * @return The player's score.
	 * @since 23
	 */
	public int getScore() {
		return this.score;
	}

	/**
	 * Retrieves the player's hand of cards.
	 * @return A list of cards in the player's hand.
	 * @since 23
	 */
	public Vector<Card> getHand(){
		return this.hand;
	}
	
	/**
	 * Setter for a player's hand
	 * @param hand Vector of Cards (the hand)
	 * @since 23
	 */
	public void setHand(Vector<Card> hand) {
		this.hand = hand;
	}
	
	/**
	 * Setter for a player's orientation
	 * @param orienttn from 0 to 3; S, W, N, E
	 * @since 23
	 */
	public void setOrientation(int orienttn) {
		this.orientation = orienttn;
	}
	
	/**
	 * Returns a string representation of all cards in a player's hand.
	 * @return String
	 * @since 23
	 */
	public String stringifyHand() {
		if (hand.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hand.size(); i++) {
			sb.append(hand.get(i).toString());
			sb.append(",");
		}
		
		return sb.substring(0, sb.toString().length() - 1);
	}

	/**
	 * Retrieves the size of the player's hand.
	 * @return The number of cards in the player's hand.
	 * @since 23
	 */
	public int getHandSize() {
		return this.hand.size();
	}

	/**
	 * Retrieves the player's orientation.
	 * @return The orientation of the player.
	 * @since 23
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/**
	 * Clears the player's hand, removing all cards.
	 * @since 23
	 */
	public void clearHand() {
		this.hand.clear();
	}

	/**
	 * Setter for player ID
	 * @param id int to set the ID as
	 * @since 23
	 */
	public void setID(int id) {
		this.id = id;
	}
	
	/**
	 * Getter for player ID
	 * @return the player's ID
	 * @since 23
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Returns a string representation of the player.
	 * @return A string describing the player's name, score, orientation, and 
	 * whether they are human.
	 * @since 23
	 */
	@Override
	public String toString() {
		return "Player " + this.name + " current score: " + this.score + " orientation: " 
	+ this.orientation + " isHuman: " + isHuman + " id: " + this.id;
	}

}
