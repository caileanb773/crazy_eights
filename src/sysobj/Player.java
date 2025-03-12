package sysobj;

import java.util.ArrayList;
import java.util.List;

public class Player {

	protected List<Card> hand;
	protected boolean isHuman;
	protected String name;
	protected int score;
	protected int orientation;

	public Player() {

	}

	public Player(String n, int orientation) {
		this.name = n;
		this.hand = new ArrayList<Card>();
		this.score = 0;
		this.orientation = orientation;
		this.isHuman = false;
	}

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

	public void removeCardFromHand(Card card) {
		this.hand.remove(card);
	}

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

	public boolean isHuman() {
		return isHuman;
	}

	public void setHuman(boolean isHuman) {
		this.isHuman = isHuman;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScore(int scr) {
		this.score = scr;
	}

	public String getName() {
		return this.name;
	}

	public int getScore() {
		return this.score;
	}

	public List<Card> getHand(){
		return this.hand;
	}

	public int getHandSize() {
		return this.hand.size();
	}

	public int getOrientation() {
		return this.orientation;
	}

	public void clearHand() {
		this.hand.clear();
	}

	@Override
	public String toString() {
		return "Player " + this.name + " current score: " + this.score + " orientation: " + this.orientation + " isHuman: " + isHuman;
	}

}
