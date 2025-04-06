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

/**
 * The server holds a Vector of sockets, representing each client connected to it.
 * The server allows the controller to communicate to each connected client, and
 * send/receive packets.
 * 
 * 
 * @since 23
 */
public class GameServer {

    /** The server socket that listens for incoming client connections. */
    private ServerSocket serverSocket;

    /** The list of sockets connected to each client/player. */
    private Vector<Socket> connectedPlayers;

    /** Listener interface for forwarding game-related events to the controller. */
    private GameControllerListener listener;

    /** Thread responsible for continuously accepting new client connections. */
    private Thread acceptThread;

    /** The number of human (non-AI) opponents expected in the game. */
    private int numHumanOpponents;

    /** The names of connected clients (used for display or identification). */
    private Vector<String> clientNames;

    /** Auto-incrementing ID assigned to the next connecting client. */
    private int nextClientId = 1;
    
    /** Output writer */
    PrintWriter out;

	/**
	 * Initializes a new Server and starts listening for packets on the socket.
	 * 
	 * 
	 * @since 23
	 * @param port that the socket should listen on
	 * @param listener allows the server to call methods based on received packets.
	 * @param maxPlayers the max no of players
	 * @throws IOException if the input/output is interrupted
	 */
	public GameServer(int port, GameControllerListener listener, int maxPlayers) throws IOException {
		this.serverSocket = new ServerSocket(port);
		this.listener = listener;
		this.connectedPlayers = new Vector<>();
		this.clientNames = new Vector<>();
		this.numHumanOpponents = maxPlayers;
	}

	/**
	 * Starts the acceptThread which is what the server uses to listen for
	 * incoming connections from clients. Once the number of connected clients
	 * matches the number of clients that the server is waiting for, the server
	 * alerts the listener to start the game.
	 * @since 23
	 */
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

	/**
	 * Private inner class that handles incoming packets from clients. Includes
	 * a constructor and run(). This is where the server listens for and parses
	 * incoming packets from different clients using the defined protocol of 
	 * MSGTYPE|OPTIONALINFO|OPTIONALINFO... etc.
	 * @since 23
	 */
	private class ClientHandler implements Runnable {
		
		/** Client's socket */
		private Socket client;
		
		/** Client's id */
		private int clientId;

		/**
		 * Constructor for the private inner class ClientHandler; makes a new
		 * client with the passed socket and clientId. 
		 * @since 23
		 * @param client the client
		 * @param clientId the client id
		 */
		public ClientHandler(Socket client, int clientId) {
			this.client = client;
			this.clientId = clientId;
		}

		/**
		 * Starts the thread that receives messages from the server. 
		 * @since 23
		 */
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out = new PrintWriter(client.getOutputStream(), true);
				out.println("ID|" + clientId);
				String line;
				while ((line = in.readLine()) != null && !client.isClosed()) {
					String[] packet = line.split("\\|");

					// switching on packet type (CHAT, TURN, PLAY, DRAW, etc...)
					switch (packet[0]) {

					// client sending name to the server. server adds it to an internal list
					case "NAME":

						// synchronized so that multiple threads don't access at once
						synchronized (clientNames) {
							System.out.println("Client name added: " + packet[1]);
							clientNames.add(packet[1]);
						}
						break;

						// chat msgs are rebroadcast to each connected client
					case "CHAT":

						// In case users include the '|' char in their chat msg
						if (packet.length != 2) {

							// stringbuilder re-builsds the msg without the '|'
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

						// play packet, [1] is the client ID, [2] is the card
					case "PLAY":
						System.out.println("Server received a play request packet: " +
								packet[1] + " is trying to play card: " + packet[2]);
						listener.onClientPlayReceived(line);
						break;

						// draw packet. only information is [1]: client ID
					case "DRAW":
						System.out.println("Server received a draw request packet: " );
						listener.onClientDrawReceived(line);
						break;

						// packet containing a suit chosen by a client (when an 8 is played)
					case "SUITCHOICE":
						System.out.println("Server has been informed that client " +
								packet[1] + " has chosen " + packet[2] + " to replace " + packet[3]);
						listener.onClientSuitReceived(line);
						break;

						// client disconnect packet
					case "DISCONNECT":
						System.out.println("Client " + packet[1] + " is disconnecting...");
						listener.onPlayerDisconnect(line);
						break;

						// need default for when new packets are implemented and not added
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

	/**
	 * Broadcasts a chat send from a client to all connected clients. 
	 * @since 23
	 * @param msg The chat message to be broadcast.
	 */
	public void broadcastChat(String msg) {
		System.out.println("Server is broadcasting a chat to all connected clients: " + msg);
		for (Socket client : connectedPlayers) {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				out.println("CHAT|" + msg);
			} catch (IOException e) {
				System.out.println("Failed to broadcast chat to a client." + e.getStackTrace());
			}
		}
	}

	/**
	 * Broadcasts a console message (game notification) to all connected clients. 
	 * @since 23
	 * @param optName: some console messages have an optional name section.
	 * @param msg: the main contents of the console message.
	 * @param optCard: some console messages have an optional card section.
	 */
	public void broadcastConsoleMsg(String optName, String msg, String optCard) {
		System.out.println("Server is broadcasting console message to all connected clients: " +
				optName + " " + msg + " " + optCard);
		for (Socket client : connectedPlayers) {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				out.println("CONSOLE|" + optName + "|" + msg + "|" + optCard);
			} catch (IOException e) {
				System.out.println("Failed to broadcast chat to a client." + e.getStackTrace());
			}
		}
	}

	/**
	 * Broadcasts the round winner to all connected clients. 
	 * @since 23
	 * @param winnerName name of winner
	 */
	public void broadcastRoundWinner(String winnerName) {
		System.out.println("Server is broadcasting round winner to all connected clients.");
		for (Socket client : connectedPlayers) {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				out.println("ROUNDOVER" + "|" + winnerName);
			} catch (IOException e) {
				System.out.println("Failed to broadcast round winner to client." + e.getStackTrace());
			}
		}
	}

	/**
	 * Broadcasts the game winner(s) to all connected clients. 
	 * @since 23
	 * @param winnerNames names of winners
	 */
	public void broadcastGameWinners(String winnerNames) {
		System.out.println("Server is broadcasting game winner to all connected clients.");
		for (Socket client : connectedPlayers) {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				out.println("GAMEOVER" + "|" + winnerNames);
			} catch (IOException e) {
				System.out.println("Failed to broadcast game winner to client." + e.getStackTrace());
			}
		}
	}

	/**
	 * Requests that the client who played an Eight choose a suit for the Eight
	 * via a JDialog. 
	 * @since 23
	 * @param clientId: the client who's turn it currently is.
	 * @param cardToPlay: The true card (read: suit) of the eight. This is needed
	 * for game logic reasons.
	 */
	public void requestSuitChoice(int clientId, String cardToPlay) {
		System.out.println("Server is sending a request to client " + clientId + " to choose a suit.");

		// connectedPlayers doesn't have the host, so subtract 1 from client Id
		Socket client = connectedPlayers.get(clientId - 1);
		try {
			out = new PrintWriter(client.getOutputStream(), true);
			out.println("SUITREQUEST" + "|" + cardToPlay);
		} catch (IOException e) {
			System.out.println("Failed to request suit from client " + clientId);
		}
	}

	/**
	 * The UI should have a certain combination of buttons enabled/disabled based
	 * on a given "state"; is the user idle, with no game running? Are they playing
	 * a single player game, or multiplayer? The buttons are in different configs
	 * for each state. 
	 * @since 23
	 * @param mode: which button mode the UI should be configured for.
	 */
	public void broadCastButtonMode(String mode) {
		System.out.println("Server is telling its clients to adjust which buttons are active.");

		for (Socket s : connectedPlayers) {
			try {
				out = new PrintWriter(s.getOutputStream(), true);
				out.println("BTN" + "|" + mode);
			} catch (IOException e) {
				System.out.println("Failed to broadcast button state to client.");
			}

		}
	}

	/**
	 * Tells the "client" (read: socket) at the passed index to shutdown. Its 
	 * receive thread is interrupted and its UI is reset to default. 
	 * @since 23
	 * @param index the index to close socket
	 */
	public void closeSocket(int index) {
		Socket client = connectedPlayers.get(index - 1);
		try {
			out = new PrintWriter(client.getOutputStream(), true);
			out.println("SHUTDOWN");
			client.close();
		} catch (IOException e) {
			System.out.println("IO exception occurred while a client was disconnecting.");
		}
	}

	/**
	 * Tell each connected client to reset their UI to the default state. 
	 * @since 23
	 */
	public void terminateGame() {
		// tell each client to clean up their UI
		for (Socket client : connectedPlayers) {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				out.println("CLEANUP");
			} catch (IOException e) {
				System.out.println("Failed to tell client to cleanup UI.");
			}
		}
	}

	/**
	 * Terminates (interrupts) the receiveThread of each connected client, then 
	 * does the same for the host's acceptThread.  
	 * @since 23
	 */
	public void terminateThreads() {
		for (Socket client : connectedPlayers) {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				out.println("SHUTDOWN");
				client.close();
			} catch (IOException e) {
				System.out.println("Failed to close socket while terminating threads.");
			}
		}

		if (acceptThread.isAlive()) {
			acceptThread.interrupt();
		}
	}

	/**
	 * Getter for the vector of connected clients (their sockets). 
	 * @since 23
	 * @return connected clients
	 */
	public Vector<Socket> getConnectedClients(){
		return this.connectedPlayers;
	}

	/**
	 * Requests that each client refresh their view. A "view refresh" is basically
	 * an update of each component in the UI that *could* have a new value (hands,
	 * last played card, player scores, etc.). 
	 * @since 23
	 * @param players: the list of players, passed from the model.
	 * @param lastPlayedCard: the last played (discarded) card.
	 * @param turnDirection: clockwise/counterclockwise (normal vs. reversed)
	 */
	public void requestViewRefresh(Vector<Player> players, Card lastPlayedCard, boolean turnDirection) {
		for (int i = 0; i < connectedPlayers.size(); i++) {
			try {
				out = new PrintWriter(connectedPlayers.get(i).getOutputStream(), true);
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

	/**
	 * Closes the socket of each connected client. 
	 * @since 23
	 */
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

	/**
	 * Returns the number of connected clients.  
	 * @since 23
	 * @return client count
	 */
	public int getClientCount() {
		return connectedPlayers.size();
	}

	/**
	 * Returns the Vector of client names. 
	 * @since 23
	 * @return client names
	 */
	public Vector<String> getClientNames(){

		// return a copy of the internal vector of client names
		return new Vector<>(clientNames);
	}

}
