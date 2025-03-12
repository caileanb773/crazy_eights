package sysobj;

import java.util.Random;
import system.Const;

public class AIPlayer extends Player {

	public AIPlayer() {

	}

	public AIPlayer(String name, int orientation) {
		super(name, orientation);
	}

	public int decidePlayDraw(Card lastPlayedCard) {
		// store the result of hasLegalMove locally instead of calling it twice
		boolean hasMove = hasLegalMove(lastPlayedCard);
		if (!hasMove && getHandSize() >= Const.MAX_HAND_SIZE) {
			return Const.PASS;
		}
		
		if (hasMove) {
			return Const.PLAY;
		} else {
			return Const.DRAW;
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
