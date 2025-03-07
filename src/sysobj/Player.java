package sysobj;

import java.util.ArrayList;
import java.util.List;

public class Player {

	protected List<Card> hand;
	protected String name;
	protected int score;

	public Player() {

	}

	public Player(String n) {
		this.name = n;
		this.hand = new ArrayList<Card>();
		this.score = 0;
	}

	public Card drawCard(ArrayList<Card> deck) {
		// check that the deck is NOT empty
		if (!deck.isEmpty()) {
			Card card = deck.remove(deck.size()-1);
			addCardToHand(card);
			// TODO return Optional.of(card) could be a useful way to do this instead
		}
		System.out.println("Player.drawCard() attempted to draw a card from an empty deck.");
		// TODO need to tell the controller to reshuffle the deck if it's empty
		// this method returning null could be signifier that the deck needs to be reshuffled?
		return null;
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

	public void playCard(Card card, List<Card> playedCards) {
		// defensive programming
		if (this.hand.isEmpty()) {
			System.out.println("Player.playCard() tried to play a card from an empty hand.");
			return;
		}
		if (!this.hand.contains(card)) {
			System.out.println("Player.playCard() tried to play a card not in the player's hand.");
			return;
		}
		if (card == null) {
			System.out.println("Player.playCard() was passed a null card.");
			return;
		}

		playedCards.add(card);
		this.hand.remove(card);
		// TODO remove card from hand here?
	}

	// TODO this one will be a doozy
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
		System.out.println("Player.hasLegalMoves() found no legal moves for player: " + this.name);
		return false;
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
	
	public void clearHand() {
		this.hand.clear();
	}

}
