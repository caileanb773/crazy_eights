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

	/**
	 * SerialVersionID.
	 * @value 1L
	 */
	private static final long serialVersionUID = 1L;
	private final Rank rank;
	private Suit suit;
	private ImageIcon cardImg;

	/**
	 * Parameterized constructor for Card objects. Each card has a suit and a rank.
	 * Based on this suit and rank, an ImageIcon is fetched that serves as a default
	 * image for the card, since each Card is a button.
	 * @param r The rank of the card.
	 * @param s The suit of the card.
	 * @author Cailean Bernard
 * @since 23
	 */
	public Card(Rank r, Suit s) {
		this.rank = r;
		this.suit = s;

		// Default orientation for a card !top, !left, !hidden = F, F, T
		this.cardImg = fetchCardImg(false,false,true);
		this.setBorder(null);
		this.setIcon(cardImg);
	}

	/**
	 * Fetches a new ImageIcon (and sets it) for the Card button based on some
	 * factors that vary during play (is the card supposed to be hidden, is it
	 * displayed as a left slice or a top slice). Dynamically builds the path
	 * for the card object using conditionals to assign the correct path for the
	 * correct image.
	 * @param isTop Should the card be rendered as a top slice?
	 * @param isLeft Should the card be rendered as a left slice?
	 * @param isHidden Should the card face be visible or rendered as a card back?
	 * @return ImageIcon that reflects the current state of the card.
	 * @author Cailean Bernard
	 * @since 23
	 */
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
			
			// Get the rank and suit of the card as part of the path
			path.append(rankToString(r));
			path.append(suitToString(s));
		}

		// All cards are .png files
		path.append(".png");
		this.setIcon(new ImageIcon(path.toString()));
		return new ImageIcon(path.toString());
	}

	/**
	 * Assigns returns an appropriate String form of each Suit, to be used when
	 * fetching the image from the assets folder.
	 * @param s The passed suit, to convert to String.
	 * @return a String representation of the suit.
	 * @author Cailean Bernard
	 * @since 23
	 */
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

	/**
	 * Assigns returns an appropriate String form of each Rank, to be used when
	 * fetching the image from the assets folder.
	 * @param r The passed rank, to convert to String.
	 * @return a String representation of the rank.
	 * @author Cailean Bernard
	 * @since 23
	 */
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

	/**
	 * Setter for the Suit.
	 * @param s The suit to set the card to.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setSuit(Suit s) {
		this.suit = s;
	}

	/**
	 * Getter for the Rank.
	 * @return the rank of the card.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Rank getRank() {
		return this.rank;
	}

	/**
	 * Getter for the Suit.
	 * @return the Suit of the card.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public Suit getSuit() {
		return this.suit;
	}

	/**
	 * Getter for the ImageIcon of the card.
	 * @return the ImageIcon of the card.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public ImageIcon getImage() {
		return this.cardImg;
	}

	/**
	 * Setter for the ImageIcon of the card.
	 * @param img The new image to be displayed on the card.
	 * @author Cailean Bernard
	 * @since 23
	 */
	public void setImage(ImageIcon img) {
		this.cardImg = img;
		this.setIcon(img);
	}

	/**
	 * Returns a string representation of a card, to be used in console print
	 * statements and System.out calls, mainly.
	 * @return a String representation of the card.
	 * @author Cailean Bernard
	 * @since 23
	 */
	@Override
	public String toString() {
		return this.rank + " of " + this.suit;
	}

}
