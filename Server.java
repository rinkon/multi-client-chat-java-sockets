package multiclient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	ServerSocket serverSocket;
	
	
	private Server(ServerSocket serverSocket) {
		System.out.println("Server started");
		this.serverSocket = serverSocket;
	}
	
	private void StartServer() {
		try{
			while(!serverSocket.isClosed()) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("A new client has joined the chat");
				ClientHandler clientHandler = new ClientHandler(clientSocket);
				
				Thread thread = new Thread(clientHandler);
				thread.start();
			}
		} catch(IOException e) {
			CloseServer();
		}
	}
	
	private void CloseServer() {
		System.out.println("Closing server");
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(9999);
		Server server = new Server(serverSocket);
		
		server.StartServer();
	}
}
