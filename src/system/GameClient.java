package system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

import sysobj.Suit;

public class GameClient {

	private Socket clientSocket;
	private int port;
	private String ip;
	private GameControllerListener listener;
	private PrintWriter out;
	private Thread receiveThread;
	private String clientName;
	private int clientId = -1;

	public GameClient(int port, String ip, GameControllerListener listener, String playerName) throws IOException {
		this.port = port;
		this.ip = ip;
		this.listener = listener;
		this.clientSocket = new Socket(ip, port);
		this.out = new PrintWriter(clientSocket.getOutputStream(), true);
		this.clientName = playerName;
		startReceiving();
	}

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

						case "SUIT":
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									System.out.println("Client has received a request to choose a suit.");
									Suit s = listener.onClientSuitRequest();
								}
							});
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

	public void sendChat(String msg) {
		System.out.println("Client " + clientName + " is sending a chat to the Server: " + msg);
		out.println("CHAT" + "|" + msg);
	}

	public void sendName() {
		System.out.println("Client " + clientName + " is sending their name to the Server.");
		out.println("NAME" + "|" + clientName);
	}

	public void sendDraw() {
		System.out.println("Client " + clientName + " is sending a draw request to Server.");
		out.println("DRAW" + "|" + clientId );
	}

	public void sendPlay(String card) {
		System.out.println("Client " + clientName + " is sending a play request to Server: " + card);
		out.println("PLAY" + "|" + clientId + "|" + card);
	}

	public void joinGame() {

	}

	public void disconnect() {

	}

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

	public int getClientId() {
		return this.clientId;
	}

	public String getName() {
		return this.clientName;
	}

}
