import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	
	private Socket socket;
	private BufferedReader inputBuffer;
	private BufferedWriter outputBuffer;
	
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
	public static int count = 0;
	
	
	private String clientUsername;
	
	public ClientHandler(Socket s) {
		try {
			this.socket = s;
			this.inputBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.outputBuffer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.clientUsername = inputBuffer.readLine();
			clientHandlers.add(this);
			broadcastMessage(" has entered the chat.");
			
		} catch(IOException e) {
			closeEverything();
		}
	}
	
	public void closeEverything() {
		System.out.println("Closing handler");
		try {
			removeClientHandler();
			if(this.inputBuffer != null) inputBuffer.close();
			if(this.outputBuffer != null) outputBuffer.close();
			if(this.socket != null) socket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void broadcastMessage(String message) {
		for(ClientHandler ch: clientHandlers) {
			if(!ch.clientUsername.equals(this.clientUsername))
				try {
					ch.outputBuffer.write(this.clientUsername + ": " + message);
					ch.outputBuffer.newLine();
					ch.outputBuffer.flush();
				} 
				catch (IOException e) {
					closeEverything();
				}
		}
	}
	
	public void removeClientHandler() {
		clientHandlers.remove(this);
		broadcastMessage("SERVER: " + this.clientUsername + " has left the chat!");
	}

	@Override
	public void run() {
//		String messageFromClient;
		
		while(socket.isConnected()) {
			try {
				String messageFromClient = inputBuffer.readLine();
				broadcastMessage(messageFromClient);
				
			} catch (IOException e) {
				closeEverything();
				break;
			}
		}
	}
	
	
	
}
