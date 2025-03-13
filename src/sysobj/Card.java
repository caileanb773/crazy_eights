package sysobj;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Each card is composed of a suit, a rank, and a display image (if the card is
 * face up).
 * @author Cailean Bernard
 * @since 23
 * */
public class Card extends JButton {

	private static final long serialVersionUID = 1L;
	private final Rank rank;
	private Suit suit;
	private ImageIcon cardImg;

	public Card(Rank r, Suit s) {
		this.rank = r;
		this.suit = s;

		// Default orientation for a card !top, !left, !hidden = F, F, T
		this.cardImg = fetchCardImg(false,false,true);
		this.setBorder(null);
		this.setIcon(cardImg);
	}

	public ImageIcon fetchCardImg(boolean isTop, boolean isLeft, boolean isHidden) {
		Rank r = this.rank;
		Suit s = this.suit;
		StringBuilder path = new StringBuilder("asset/card/");

		if (isHidden) {

			// If hidden, determine the correct back image
			if (isTop) {

				// Top section hidden card
				path.append("tback"); 
			} else if (isLeft) {

				// Left section hidden card
				path.append("lback"); 
			} else {

				// Full card hidden
				path.append("back");  
			}
		} else {

			// If visible, determine the appropriate front image
			if (isLeft) {

				// Left section prefix
				path.append("l"); 
			}
			path.append(rankToString(r));
			path.append(suitToString(s));
		}

		path.append(".png");
		this.setIcon(new ImageIcon(path.toString()));
		return new ImageIcon(path.toString());
	}


	public void getSouthCardImg() {

	}

	public void getWestCardImg() {

	}

	public void getNorthCardImg() {

	}

	public void getEastCardImg() {

	}

	public String suitToString(Suit s) {
		switch (s) {
		case CLUBS: return "c";
		case DIAMONDS: return "d";
		case HEARTS: return "h";
		case SPADES: return "s";
		default:
			System.out.println("Default switch case reached while converting Rank to String.");
			return null;
		}
	}

	public String rankToString(Rank r) {
		switch (r) {
		case ACE: return "A";
		case TWO: return "2";
		case THREE: return "3";
		case FOUR: return "4";
		case FIVE: return "5"; 
		case SIX: return "6"; 
		case SEVEN: return "7"; 
		case EIGHT: return "8"; 
		case NINE: return "9"; 
		case TEN: return "1"; 
		case JACK: return "J"; 
		case QUEEN: return "Q"; 
		case KING: return "K";
		default:
			System.out.println("Default switch case reached while converting Rank to String.");
			return null;
		}

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

	public void setImage(ImageIcon img) {
		this.cardImg = img;
		this.setIcon(img);
	}

	@Override
	public String toString() {
		return this.rank + " of " + this.suit;
	}

}
