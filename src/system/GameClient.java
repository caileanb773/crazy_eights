package system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;

/**
 * Client class that contains the socket to used for communication to the server,
 * as well as protocols for network messages. Maps to a Player contained in the
 * server's model.
 * 
 * 
 * @since 23
 */
public class GameClient {

	/** The socket connection to the server. */
	private Socket clientSocket;

	/** Listener for handling game-related events. */
	private GameControllerListener listener;

	/** Writer for sending messages to the server. */
	private PrintWriter out;

	/** Thread for receiving messages from the server. */
	private Thread receiveThread;

	/** The name of the client player. */
	private String clientName;

	/** The unique ID of the client, initialized to -1. */
	private int clientId = -1;


	/**
	 * Constructor that creates a new GameClient object with a specified name.
	 * Once the object is created, the client starts receiving commands from the
	 * host.
	 * 
	 * @param port the port to listen on
	 * @param ip the ip to connect to
	 * @param listener listener
	 * @param playerName the player's name
	 * @throws IOException if something goes wrong
	 * @since 23
	 */
	public GameClient(int port, String ip, GameControllerListener listener, String playerName) throws IOException {
		this.listener = listener;
		this.clientSocket = new Socket(ip, port);
		this.out = new PrintWriter(clientSocket.getOutputStream(), true);
		this.clientName = playerName;
		startReceiving();
	}

	/**
	 * Start the thread that is responsible for receiving network packets from
	 * the host. Packet protocol is MSGTYPE|OPTIONALINFO|OPTIONALINFO... etc. 
	 * The method switches on the packet "type", which is the 0th index of the
	 * packet. What happens next depends on the packet type, but mostly involves
	 * calling some method from the listener.
	 * 
	 * 
	 * @since 23
	 */
	private void startReceiving() {
		receiveThread = new Thread(new Runnable() {
			public void run() {
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String line;
					while ((line = in.readLine()) != null && !clientSocket.isClosed()) {
						System.out.println("Client received packet: " + line);
						String[] packet = line.split("\\|");

						switch (packet[0]) {

						case "ID":
							System.out.println("Client: " + clientName + " received a packet assigning them the ID: " + packet[1]);
							clientId = Integer.parseInt(packet[1]);
							break;

						case "CHAT":
							System.out.println("Client received a request from Server to display a chat: " + packet[1]);
							listener.onChatReceived(packet[1]);
							break;

						case "REFRESH":
							if (packet.length == 8) {
								System.out.println("Client received a request to refresh its UI: " + line);
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {		// id		  hand	 	 lastPlayed no. Cards  plyrNames  plyrScore	 turnDir
										listener.onViewRefresh(packet[1], packet[2], packet[3], packet[4], packet[5], packet[6], packet[7]);
									}
								});
							} else {
								System.out.println("Client received a refresh packet with an invalid length.");
							}
							break;

						case "CONSOLE":
							System.out.println("Client has received a console message from the Server.");
							listener.onConsoleMsgReceived(packet[1], packet[2], packet[3]);
							break;

						case "ROUNDOVER":
							System.out.println("Client has received a request to display the round winner " + packet[1]);
							listener.onRoundOver(packet[1]);
							break;

						case "GAMEOVER":
							System.out.println("Client has received a request to display the game winner.");
							listener.onGameOver(packet[1]);
							break;

						case "SUITREQUEST":
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									System.out.println("Client has received a request to choose a suit.");
									listener.onClientSuitRequest(packet[1]);
								}
							});
							break;

						case "BTN":
							System.out.println("Client received a button status packet from server.");
							listener.onButtonStatusReceived(packet[1]);
							break;
							
						case "CLEANUP":
							listener.onTerminateGameRequest();
							break;
							
						case "SHUTDOWN":
							listener.onTerminateGameRequest();
							receiveThread.interrupt();
							break;

						default:
							System.out.println("Client received packet of unknown type: " + packet[0]);
							break;
						}

					}
				} catch (IOException e) {
					System.out.println("Client receive error: " + e.getMessage());
				}
			}
		});
		receiveThread.start();
	}

	/**
	 * Sends a chat message to the server.
	 *
	 * @param msg The message to send.
	 * @since 23
	 */
	public void sendChat(String msg) {
		System.out.println("Client " + clientName + " is sending a chat to the Server: " + msg);
		out.println("CHAT" + "|" + msg);
	}

	/**
	 * Sends the client's name to the server.
	 * Used for identification after connection.
	 * 
	 * @since 23
	 */
	public void sendName() {
		System.out.println("Client " + clientName + " is sending their name to the Server.");
		out.println("NAME" + "|" + clientName);
	}

	/**
	 * Sends a draw request to the server to draw a card.
	 * @since 23
	 */
	public void sendDraw() {
		System.out.println("Client " + clientName + " is sending a draw request to Server.");
		out.println("DRAW" + "|" + clientId );
	}

	/**
	 * Sends a play request to the server with the specified card.
	 *
	 * @param card The card to play.
	 * @since 23
	 */
	public void sendPlay(String card) {
		System.out.println("Client " + clientName + " is sending a play request to Server: " + card);
		out.println("PLAY" + "|" + clientId + "|" + card);
	}

	/**
	 * Sends the chosen suit and card to the server, typically after playing an eight.
	 *
	 * @param suit The chosen suit.
	 * @param cardToPlay The card being played.
	 * @since 23
	 */
	public void sendSuit(String suit, String cardToPlay) {
		System.out.println("Client " + clientName + " is sending a chosen suit to Server: " + suit);
		out.println("SUITCHOICE" + "|" + clientId + "|" + suit + "|" + cardToPlay);
	}

	/**
	 * Sends a disconnect request to the server.
	 * 
	 * @since 23
	 */
	public void disconnect() {
		System.out.println("Client " + clientName + " is attempting to disconnect...");
		out.println("DISCONNECT" + "|" + clientId);
	}

	/**
	 * Facilitates shutdown of the GameClient. Closes the PrintWriter, the socket,
	 * and interrupts the thread.
	 * 
	 * @since 23
	 */
	public void shutdown() {
		System.out.println("Client has called shutdown(), closing 'out' and the clientSocket...");
		try {
			out.close();
			clientSocket.close();
			if (receiveThread != null) {
				receiveThread.interrupt();
			}
		} catch (IOException e) {
			System.out.println("IO Exception encountered while shutting down client socket.");
		}
	}

	/**
	 * Getter for client ID.
	 * 
	 * 
	 * @since 23
	 * @return the id
	 */
	public int getClientId() {
		return this.clientId;
	}

	/**
	 * Getter for client Name.
	 * 
	 * @since 23
	 * @return the name
	 */
	public String getName() {
		return this.clientName;
	}

}
