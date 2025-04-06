package sysobj;

import java.util.Random;
import system.Const;

/**
 * AI Player extends Player, but implements a few methods that Player doesn't
 * need, mainly to do with determining if they have a legal move, what move they
 * should take, and what card they should play if they choose to play a card.
 * 
 * @since 23
 */
public class AIPlayer extends Player {

	/**
	 * Default constructor.
	 * @since 23
	 */
	public AIPlayer() {
	}
	
	/***
	 * Name-only constructor for an AI player.
	 * @param name of the AI player
	 * @since 23
	 */
	public AIPlayer(String name) {
		super(name);
		isHuman = false;
	}

	/**
	 * Parameterized constructor for AIPlayer, setting their name and orientation
	 * in the UI.
	 * @param name The name of the player.
	 * @param orientation Where in the UI they are occupying.
	 * @since 23
	 */
	public AIPlayer(String name, int orientation) {
		super(name, orientation);
	}

	/**
	 * Based on the last card played, decide if the right move is to play a card
	 * or to draw a card, or to pass the turn.
	 * @param lastPlayedCard The last card played to the discard pile.
	 * @return an integer representing which choice they made.
	 * @since 23
	 */
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

	/**
	 * The logic to determine which card the AI player should play. Right now, a
	 * card is simply selected at random. However, if I wanted to add a difficulty
	 * slider, this is where that implementation would go.
	 * @param lastPlayedCard The last card played to the discard pile.
	 * @return The Card the AI player has decided to play.
	 * 
	 * @since 23
	 */
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

	/**
	 * When the AI player plays an 8, they must decide a suit for the 8. Right now,
	 * they choose one at random. If I wanted to modify the difficulty of the AI,
	 * I could get it to take into account how many of x suit they have in their
	 * hand and change the suit to match that suit.
	 * @return Suit The suit that they are changing the 8 to.
	 * @since 23
	 */
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
