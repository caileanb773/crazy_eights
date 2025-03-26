package system;

import java.net.http.WebSocket.Listener;

public interface GameControllerListener extends Listener {
	
	public void onChatReceived(String msg);
	public void onPlayerMove(String move);
	public void onPlayerConnected(int numPlayers);
	public void onPlayerDisconnect();
	public void onRoundOver();
	public void onGameOver();
	public void onGameStateUpdated(String state);
	
}
