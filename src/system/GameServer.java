package system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import sysobj.Player;

public class GameServer {
	
	private ServerSocket serverSocket;
	private int port;
	private Vector<Socket> connectedPlayers;
	private GameControllerListener listener;
	private Thread acceptThread;
	
	public GameServer(int port, GameControllerListener listener) throws IOException {
		this.port = port;
		this.serverSocket = new ServerSocket(port);
		this.listener = listener;
		this.connectedPlayers = new Vector<Socket>();
		acceptConnections();
	}
	
	public void createGame() {
		
	}
	
	public void startServer(int numPlayers) {
		
	}
	
	public void acceptConnections() {
		acceptThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (connectedPlayers.size() < 3 && !serverSocket.isClosed()) {
						Socket client = serverSocket.accept();
						System.out.println("A new player has connected.");
						connectedPlayers.add(client);
						listener.onPlayerConnected(connectedPlayers.size());
						new Thread(new ClientHandler(client)).start();
						if (connectedPlayers.size() == 3) {
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
		
		public ClientHandler(Socket client) {
			this.client = client;
		}
		
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String line;
				while ((line = in.readLine()) != null && !client.isClosed()) {
					String[] packet = line.split("\\|");
					if (packet.length >= 2 && packet[0].equals("CHAT")) {
						listener.onChatReceived(packet[1]);
						System.out.println("Packet contents: " + packet[1]);	
					} else {
						System.out.println("Invalid packet: " + line);
					}
				}
			} catch (IOException e) {
				System.out.println("IO Exception encountered in GameServer.ClientHandler.run().");
			}
		} 
	}
	
	public void broadcastChat(String msg) {
		System.out.println("Broadcasting: " + msg);
		for (Socket client : connectedPlayers) {
			try {
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				out.println("CHAT" + "|" + msg);
				System.out.println("Sent to client: " + client.getInetAddress());
			} catch (IOException e) {
				System.out.println("Failed to broadcast chat to a client.");
			}
		}
	}
	
	public void broadcastConsoleMsg(String msg) {
		
	}
	
	public void replacePlayer(Player disconnectedPlayer) {
		
	}
	
	public void terminateGame() {
		
	}
	
	public void requestViewRefresh() {
		
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

}
