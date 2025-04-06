package system;

import java.net.http.WebSocket.Listener;
import java.util.Vector;
import sysobj.Card;

/**
 * Interface that handles network call/responses. Implemented by the controller,
 * these methods execute in response to network messages being sent/received by
 * the host/clients.
 * @author Cailean Bernard
 * @since 23
 */
public interface GameControllerListener extends Listener {

	/**
	 * Called when a chat message is received from another player. 
	 * @param msg The chat message received.
	 */
	public void onChatReceived(String msg);

	/**
	 * Called when a message is received for the console output, 
	 * optionally referencing a player name and card. 
	 * @param optName The optional name of the player related to the message.
	 * @param msg The message to be displayed.
	 * @param optCard The optional card related to the message.
	 */
	public void onConsoleMsgReceived(String optName, String msg, String optCard);

	/**
	 * Called when the client receives a draw card action from the server.
	 *
	 * @param packetData The data representing the draw action.
	 */
	public void onClientDrawReceived(String packetData);

	/**
	 * Called when the client receives a play card action from the server.
	 *
	 * @param packetData The data representing the play action.
	 */
	public void onClientPlayReceived(String packetData);

	/**
	 * Called when the server requests the client to choose a suit 
	 * after playing an Eight.
	 *
	 * @param cardToPlay The card played that triggered the suit request.
	 */
	public void onClientSuitRequest(String cardToPlay);

	/**
	 * Called when the status of in-game buttons should be updated.
	 *
	 * @param packetData The data representing the new button states.
	 */
	public void onButtonStatusReceived(String packetData);

	/**
	 * Called when a suit selection made by a player is received.
	 *
	 * @param packetData The data representing the chosen suit.
	 */
	public void onClientSuitReceived(String packetData);

	/**
	 * Called when a player connects to the game.
	 *
	 * @param numPlayers The current number of connected players.
	 * @param maxPlayers The maximum number of players allowed.
	 */
	public void onPlayerConnected(int numPlayers, int maxPlayers);

	/**
	 * Called when a player disconnects from the game.
	 *
	 * @param packetData The data representing the disconnected player.
	 */
	public void onPlayerDisconnect(String packetData);

	/**
	 * Called when a round of the game ends.
	 *
	 * @param roundWinnerName The name of the player who won the round.
	 */
	public void onRoundOver(String roundWinnerName);

	/**
	 * Called when the game is over and a winner is determined.
	 *
	 * @param gameWinnerName The name of the player who won the game.
	 */
	public void onGameOver(String gameWinnerName);

	/**
	 * Called when the player's hand is refreshed (e.g., after drawing or reshuffling).
	 *
	 * @param hand The new set of cards in the player's hand.
	 */
	public void onHandRefreshed(Vector<Card> hand);

	/**
	 * Called when the overall game state is updated (e.g., pause, resume).
	 *
	 * @param state The new game state.
	 */
	public void onGameStateUpdated(String state);

	/**
	 * Called when the game view should be refreshed.
	 *
	 * @param hand The player's current hand as a string.
	 * @param id The player's ID.
	 * @param playedCard The card that was last played.
	 * @param opponentCardCount The number of cards held by each opponent.
	 * @param playerNames The names of all players.
	 * @param playerScores The current scores of all players.
	 * @param turnDirection The current direction of play.
	 */
	public void onViewRefresh(String hand, String id, String playedCard, String opponentCardCount, String playerNames, String playerScores, String turnDirection);

	/**
	 * Called when the game is being terminated by a player or the server.
	 */
	public void onTerminateGameRequest();
}
