package system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {

	private Socket clientSocket;
	private int port;
	private String ip;
	private GameControllerListener listener;
	private PrintWriter out;
	private Thread receiveThread;

	public GameClient(int port, String ip, GameControllerListener listener) throws IOException {
		this.port = port;
		this.ip = ip;
		this.listener = listener;
		this.clientSocket = new Socket(ip, port);
		this.out = new PrintWriter(clientSocket.getOutputStream(), true);
		startReceiving();
	}

	public void startReceiving() {
		receiveThread = new Thread(new Runnable() {
			public void run() {
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String line;
					while ((line = in.readLine()) != null && !clientSocket.isClosed()) {
						String[] packet = line.split("\\|");
						if (packet.length >= 2 && packet[0].equals("CHAT")) {
							listener.onChatReceived(packet[1]);
							System.out.println("Client received: " + packet[1]);
						}
					}
				} catch (IOException e) {
					System.out.println("Client could not receive packet.");
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

	public void sendMove(String move) {

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

}
