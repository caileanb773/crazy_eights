package system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import sysobj.Card;
import sysobj.Player;

public class GameServer {

	private ServerSocket serverSocket;
	private int port;
	private Vector<Socket> connectedPlayers;
	private GameControllerListener listener;
	private Thread acceptThread;
	private int numHumanOpponents;
	private Vector<String> clientNames;
	private int nextClientId = 1;

	public GameServer(int port, GameControllerListener listener, int maxPlayers) throws IOException {
		this.port = port;
		this.serverSocket = new ServerSocket(port);
		this.listener = listener;
		this.connectedPlayers = new Vector<>();
		this.clientNames = new Vector<>();
		this.numHumanOpponents = maxPlayers;
		acceptConnections();
	}

	public void acceptConnections() {
		acceptThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (connectedPlayers.size() < numHumanOpponents && !serverSocket.isClosed()) {
						Socket client = serverSocket.accept();
						System.out.println("A new player has connected.");
						connectedPlayers.add(client);
						listener.onPlayerConnected(connectedPlayers.size(), numHumanOpponents);
						new Thread(new ClientHandler(client, nextClientId++)).start();
						if (connectedPlayers.size() == numHumanOpponents) {
							listener.onGameStateUpdated("Game Starting");
						}
					}
				} catch (IOException e) {
					System.out.println("IO Exception encountered while accepting connections.");
				}
			}
		});
		acceptThread.start();
	}

	private class ClientHandler implements Runnable {
		private Socket client;
		private int clientId;

		public ClientHandler(Socket client, int clientId) {
			this.client = client;
			this.clientId = clientId;
		}

		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				out.println("ID|" + clientId);
				String line;
				while ((line = in.readLine()) != null && !client.isClosed()) {
					String[] packet = line.split("\\|");

					// switching on packet type (CHAT, TURN, PLAY, DRAW, etc...)
					switch (packet[0]) {

					case "NAME":
						// synchronize this so that multiple threads don't access at once
						synchronized (clientNames) {
							System.out.println("Client name added: " + packet[1]);
							clientNames.add(packet[1]);
						}
						break;

					case "CHAT":

						// In case users include the '|' char in their chat msg
						if (packet.length != 2) {
							StringBuilder sb = new StringBuilder();
							for (int i=1;i<packet.length;i++) {
								sb.append(packet[i]);
							}
							listener.onChatReceived(sb.toString());
							System.out.println("Chat received: " + sb.toString());

						} else {
							listener.onChatReceived(packet[1]);
							System.out.println("Chat received: " + packet[1]);
						}
						break;

					case "PLAY":
						System.out.println("Server received a play request packet: " +
								packet[1] + " is trying to play card: " + packet[2]);
						listener.onClientPlayReceived(line);
						break;

					case "DRAW":
						System.out.println("Server received a draw request packet: " );
						listener.onClientDrawReceived(line);
						break;

					default:
						System.out.println("Unknown packet type: " + packet[0]);
						break;
					}
				}
			} catch (IOException e) {
				System.out.println("IO Exception encountered in GameServer.ClientHandler.run().");
			}
		} 
	}

	public void broadcastChat(String msg) {
		System.out.println("Server is broadcasting a chat to all connected clients: " + msg);
		for (Socket client : connectedPlayers) {
			try {
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				out.println("CHAT|" + msg);
			} catch (IOException e) {
				System.out.println("Failed to broadcast chat to a client." + e.getStackTrace());
			}
		}
	}

	public void broadcastConsoleMsg(String optName, String msg, String optCard) {
		System.out.println("Server is broadcasting console message to all connected clients: " +
				optName + " " + msg + " " + optCard);
		for (Socket client : connectedPlayers) {
			try {
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				out.println("CONSOLE|" + optName + "|" + msg + "|" + optCard);
			} catch (IOException e) {
				System.out.println("Failed to broadcast chat to a client." + e.getStackTrace());
			}
		}
	}

	public void broadcastRoundWinner(String winnerName) {
		System.out.println("Server is broadcasting round winner to all connected clients.");
		for (Socket client : connectedPlayers) {
			try {
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				out.println("ROUNDOVER" + "|" + winnerName);
			} catch (IOException e) {
				System.out.println("Failed to broadcast round winner to client." + e.getStackTrace());
			}
		}
	}
	
	public void requestSuitChoice(int clientId) {
		System.out.println("Server is sending a request to client " + clientId + " to choose a suit.");

		// connectedPlayers doesn't have the host, so subtract 1 from client Id
		Socket client = connectedPlayers.get(clientId - 1);
		try {
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println("SUIT" + "|" + client);
		} catch (IOException e) {
			System.out.println("Failed to request suit from client " + clientId);
		}
	}

	public void replacePlayer(Player disconnectedPlayer) {

	}

	public void terminateGame() {

	}

	public Vector<Socket> getConnectedClients(){
		return this.connectedPlayers;
	}

	public void requestViewRefresh(Vector<Player> players, Card lastPlayedCard, boolean turnDirection) {
		for (int i = 0; i < connectedPlayers.size(); i++) {
			try {
				PrintWriter out = new PrintWriter(connectedPlayers.get(i).getOutputStream(), true);
				Player clientPlayer = players.get(i + 1);
				String hand = clientPlayer.stringifyHand();
				String played = lastPlayedCard.toString();

				StringBuilder sbCardCount = new StringBuilder();
				StringBuilder sbPlayerNames = new StringBuilder();
				StringBuilder sbPlayerScores = new StringBuilder();

				// build string versions of each player's information
				for (int j = 0; j < players.size(); j++) {
					sbCardCount.append(players.get(j).getHand().size()).append(",");
					sbPlayerNames.append(players.get(j).getName().trim().replace(",", ";")).append(",");
					sbPlayerScores.append(players.get(j).getScore()).append(",");
				}

				// remove the trailing comma from all
				String counts = sbCardCount.substring(0, sbCardCount.length() - 1);
				String names = sbPlayerNames.substring(0, sbPlayerNames.length() - 1);
				String scores = sbPlayerScores.substring(0, sbPlayerScores.length() - 1);

				// 			packetType	 clientID		hand		lastPlayed		no. cards	  playerNames	playerScores	turnDir
				out.println("REFRESH|" + (i + 1) + "|" + hand + "|" + played + "|" + counts + "|" + names + "|" + scores + "|" + turnDirection);
				System.out.println("Refresh sent to client " + i + ": " + hand);
			} catch (IOException e) {
				System.out.println("Failed to refresh client " + i + ": " + e.getMessage());
			}
		}
	}

	public void shutdown() {
		try {
			for (Socket s : connectedPlayers) {
				s.close();
			}
			serverSocket.close();
			if (acceptThread != null) {
				acceptThread.interrupt();
			}
		} catch (IOException e) {
			System.out.println("IO Exception encountered while shutting down server.");
		}
		System.out.println("Shutting down...");
	}

	public int getClientCount() {
		return connectedPlayers.size();
	}

	public Vector<String> getClientNames(){

		// return a copy of the internal vector of client names
		return new Vector<>(clientNames);
	}

}
