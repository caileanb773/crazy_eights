package system;

import java.net.http.WebSocket.Listener;
import java.util.Vector;

import sysobj.Card;
import sysobj.Suit;

public interface GameControllerListener extends Listener {
	
	public void onChatReceived(String msg);
	public void onConsoleMsgReceived(String optName, String msg, String optCard);
	public void onClientDrawReceived(String packetData);
	public void onClientPlayReceived(String packetData);
	public Suit onClientSuitRequest();
	public void onPlayerConnected(int numPlayers, int maxPlayers);
	public void onPlayerDisconnect();
	public void onRoundOver(String roundWinnerName);
	public void onGameOver(String gameWinnerName);
	public void onHandRefreshed(Vector<Card> hand);
	public void onGameStateUpdated(String state);
	public void onViewRefresh(String hand, String id, String playedCard, String opponentCardCount, String playerNames, String playerScores, String turnDirection);
	
}