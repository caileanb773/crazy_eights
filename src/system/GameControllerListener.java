package system;

import java.net.http.WebSocket.Listener;
import java.util.Vector;

import sysobj.Card;

public interface GameControllerListener extends Listener {
	
	public void onChatReceived(String msg);
	public void onConsoleMsgReceived(String optName, String msg, String optCard);
	public void onPlayerMove(String move);
	public void onClientDrawReceived();
	public void onClientPlayReceived(String card);
	public void onPlayerConnected(int numPlayers, int maxPlayers);
	public void onPlayerDisconnect();
	public void onRoundOver();
	public void onGameOver();
	public void onHandRefreshed(Vector<Card> hand);
	public void onGameStateUpdated(String state);
	public void onViewRefresh(String hand, String id, String playedCard, String opponentCardCount, String playerNames, String playerScores, String turnDirection);
	
}