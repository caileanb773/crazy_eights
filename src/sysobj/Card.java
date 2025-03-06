package sysobj;

import javax.swing.ImageIcon;

/**
 * Each card is composed of a suit, a rank, and a display image (if the card is
 * face up).
 * @author Cailean Bernard
 * @since 23
 * */
public class Card {

	private final Rank rank;
	private Suit suit;
	private ImageIcon cardImg;

	public Card(Rank r, Suit s) {
		this.rank = r;
		this.suit = s;
		cardImg = fetchCardImg(r, s);
	}

	private ImageIcon fetchCardImg(Rank r, Suit s) {
		StringBuilder path = new StringBuilder();		
		path.append("asset/card/");

		switch (r) {
		case ACE: path.append("A"); break;
		case TWO: path.append("2"); break;
		case THREE: path.append("3"); break;
		case FOUR: path.append("4"); break;
		case FIVE: path.append("5"); break;
		case SIX: path.append("6"); break;
		case SEVEN: path.append("7"); break;
		case EIGHT: path.append("8"); break;
		case NINE: path.append("9"); break;
		case TEN: path.append("1"); break;
		case JACK: path.append("J"); break;
		case QUEEN: path.append("Q"); break;
		case KING: path.append("K"); break;
		default: System.out.println("Card.fetchCardImg() failed in switch case for Rank.");
		return null;
		}

		switch (s) {
		case CLUBS: path.append("c"); break;
		case DIAMONDS: path.append("d"); break;
		case HEARTS: path.append("h"); break;
		case SPADES: path.append("s"); break;
		default: System.out.println("Card.fetchCardImg() failed in switch case for Suit.");
		return null;
		}

		path.append(".png");

		return new ImageIcon(path.toString());
	}
	
	public void setSuit(Suit s) {
		this.suit = s;
	}

	public Rank getRank() {
		return this.rank;
	}

	public Suit getSuit() {
		return this.suit;
	}

	public ImageIcon getImage() {
		return this.cardImg;
	}
	
	@Override
	public String toString() {
		return this.rank + " of " + this.suit;
	}

}
