package system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

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
						if (packet.length >= 2) {
							switch (packet[0]) {
							case "ID":
								clientId = Integer.parseInt(packet[1]);
								System.out.println("Assigned ID: " + packet[1]);
								break;
							case "CHAT":
								listener.onChatReceived(packet[1]);
								break;
							case "REFRESH":
								if (packet.length == 8) {
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											listener.onViewRefresh(packet[1], packet[2], packet[3], packet[4], packet[5], packet[6], packet[7]);
										}
									});
								}
								break;
							case "CONSOLE":
								listener.onConsoleMsgReceived(packet[1], packet[2], packet[3]);
								break;
							}
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
		// formatting a chat packet, with '|' as a separator
		out.println("CHAT" + "|" + msg);

		// Protocol: MSG_TYPE|MSG_CONTENTS
	}

	public void sendName() {
		out.println("NAME" + "|" + clientName);
	}

	public void sendDraw(String card) {
		out.println("DRAW" + "|" + clientName + "|" + card);
	}

	public void sendPlay(String card) {
		System.out.println("Sending play packet: PLAY" + "|" + clientId + "|" + card);
		out.println("PLAY" + "|" + clientId + "|" + card);
	}

	public void joinGame() {

	}

	public void disconnect() {

	}

	public void shutdown() {
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
