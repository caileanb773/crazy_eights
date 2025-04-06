package system;

import java.awt.Color;

/**
 * Constants that are used project-wide. Instead of cluttering classes with their
 * own specific constants and then having to hunt them down when they are needed
 * in another class, this class contains every constant that is used within the 
 * project. If a constant needs to be changed for debugging purposes, it is easy
 * to locate.
 * 
 * @author Cailean Bernard
 * @since 23
 */
public class Const {

	/** The background color pink. Default value is (255, 241, 241). */
	public static final Color BACKGROUND_PINK = new Color(255, 241, 241);

	/** The background color blue. Default value is (33, 65, 202). */
	public static final Color BACKGROUND_BLUE = new Color(33, 65, 202);

	/** The border color blue. Default value is (136, 200, 238). */
	public static final Color BORDER_BLUE = new Color(136, 200, 238);

	/** Represents vertical orientation. Default value is {@value}. */
	public static final boolean VERTICAL = false;

	/** Represents horizontal orientation. Default value is {@value}. */
	public static final boolean HORIZONTAL = true;

	/** Represents visible state (card front). Default value is {@value}. */
	public static final boolean VISIBLE = true;

	/** Represents hidden state (card back). Default value is {@value}. */
	public static final boolean HIDDEN = false;

	/** Represents a sliced card view. Default value is {@value}. */
	public static final boolean CARD_SLICE = true;

	/** Represents a full card view. Default value is {@value}. */
	public static final boolean FULL_CARD = false;

	/** The default number of cards in hand. Default value is {@value}. */
	public static final int DEFAULT_HAND_SIZE = 6;

	/** The maximum number of cards in hand. Default value is {@value}. */
	public static final int MAX_HAND_SIZE = 12;

	/** Represents single-player mode. Default value is {@value}. */
	public static final int SINGLE_PLAYER = 1;

	/** Represents multi-player mode. Default value is {@value}. */
	public static final int MULTI_PLAYER = 2;

	/** Represents an empty hand. Default value is {@value}. */
	public static final int HAND_EMPTY = 0;

	/** The maximum possible score. Default value is {@value}. */
	public static final int MAX_SCORE = 50;

	/** Represents the South position. Default value is {@value}. */
	public static final int SOUTH = 0;

	/** Represents the West position. Default value is {@value}. */
	public static final int WEST = 1;

	/** Represents the North position. Default value is {@value}. */
	public static final int NORTH = 2;

	/** Represents the East position. Default value is {@value}. */
	public static final int EAST = 3;

	/** Represents the play action. Default value is {@value}. */
	public static final int PLAY = 1;

	/** Represents the draw action. Default value is {@value}. */
	public static final int DRAW = 2;

	/** Represents the pass action. Default value is {@value}. */
	public static final int PASS = 3;
	
	/**
	 * Default constructor
	 */
	Const(){}

}
