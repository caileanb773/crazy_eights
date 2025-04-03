package sysobj;

import java.util.List;
import java.util.Vector;

/**
 * Players have a name, a hand of cards, a score, an orientation in the UI, and 
 * a flag for if they are human or not.
 * @author Cailean Bernard
 * @since 23
 */
public class Player {

	protected Vector<Card> hand;
	protected boolean isHuman;
	protected boolean isHost;
	protected String name;
	protected int score;
	protected int orientation;
	protected int id;

	/**
	 * Default Player constructor.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Player() {
	}
	
	/***
	 * Name-only constructor for a Player.
	 * @param name of the player
	 * @author Cailean Bernard
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
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Player(String n, int orientation) {
		this.name = n;
		this.hand = new Vector<Card>();
		this.score = 0;
		this.orientation = orientation;
		this.isHuman = false;
	}
	
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
	 * @author Cailean Bernard
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
	 * @author Cailean Bernard
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
	 * @author Cailean Bernard
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
	 * @author Cailean Bernard
	 * @since 23
	 */
	public boolean isHuman() {
		return isHuman;
	}

	/**
	 * Set the "isHuman" flag on a Player object.
	 * @param isHuman - Whether the flag should be set to true or false.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setHuman(boolean isHuman) {
		this.isHuman = isHuman;
	}
	
	public boolean isHost() {
		return this.isHost;
	}
	
	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

	/**
	 * Sets the player's name.
	 * @param name The name to set.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the player's score.
	 * @param scr The score to set.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setScore(int scr) {
		this.score = scr;
	}

	/**
	 * Retrieves the player's name.
	 * @return The player's name.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the player's score.
	 * @return The player's score.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public int getScore() {
		return this.score;
	}

	/**
	 * Retrieves the player's hand of cards.
	 * @return A list of cards in the player's hand.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Vector<Card> getHand(){
		return this.hand;
	}
	
	public void setHand(Vector<Card> hand) {
		this.hand = hand;
	}
	
	public void setOrientation(int orienttn) {
		this.orientation = orienttn;
	}
	
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
	 * @author Cailean Bernard
	 * @since 23
	 */
	public int getHandSize() {
		return this.hand.size();
	}

	/**
	 * Retrieves the player's orientation.
	 * @return The orientation of the player.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/**
	 * Clears the player's hand, removing all cards.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void clearHand() {
		this.hand.clear();
	}

	public void setID(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	/**
	 * Returns a string representation of the player.
	 * @return A string describing the player's name, score, orientation, and 
	 * whether they are human.
	 * @author Cailean Bernard
	 * @since 23
	 */
	@Override
	public String toString() {
		return "Player " + this.name + " current score: " + this.score + " orientation: " + this.orientation + " isHuman: " + isHuman + " id: " + this.id;
	}


}
