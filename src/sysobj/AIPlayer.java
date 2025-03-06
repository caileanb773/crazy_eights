package sysobj;

import java.util.Random;

public class AIPlayer extends Player {

	public final int PLAY = 1;
	public final int DRAW = 2;
	public final int PASS = 3;

	public AIPlayer() {

	}

	public AIPlayer(String name) {
		super(name);
	}

	public int decidePlayDraw(Card lastPlayedCard) {
		// store the result of hasLegalMove locally instead of calling it twice
		boolean hasMove = hasLegalMove(lastPlayedCard);
		if (!hasMove && getHandSize() >= 12) {
			return PASS;
		}
		else if (hasMove) {
			return PLAY;
		} else {
			return DRAW;
		}
	}

	//TODO change this if adding difficulty option
	public Card decideCard(Card lastPlayedCard) {
		/* ai will look through its hand and pick the first card that matches 
		 * the last played card in either rank or suit. if it picks an 8, it will
		 * pick a suit/rank at random. */
		for (Card c : this.hand) {
			Rank rank = c.getRank();
			if (rank == Rank.EIGHT) {
				Suit suit = chooseSuit();
				c.setSuit(suit);
				return c;
			}
			if (rank == lastPlayedCard.getRank()
					|| c.getSuit() == lastPlayedCard.getSuit()) {
				return c;
			}
		}
		System.out.println("AIPlayer.decideCard() returned null card selection.");
		return null;
	}
	
	public Suit chooseSuit() {
		Random r = new Random();
		int choice = r.nextInt(4);
		switch (choice) {
		case 0: return Suit.CLUBS;
		case 1: return Suit.DIAMONDS;
		case 2: return Suit.HEARTS;
		case 3: return Suit.SPADES;
		default: System.out.println("AIPlayer.chooseSuit() reached default case."); return null;
		}
	}

}
