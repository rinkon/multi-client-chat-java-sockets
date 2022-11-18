import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private Socket socket;
	private BufferedReader inputBuffer;
	private BufferedWriter outputBuffer;
	private String username;
	
	public Client(Socket s, String uname) {
		try {
			this.socket = s;
			this.username = uname;
			
			inputBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputBuffer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch (IOException e){
			closeAllConnection();
		}
		
	}

	private void closeAllConnection() {
		System.out.println("Closing client");
		try {
			if(socket!= null) socket.close();
			if(inputBuffer != null) inputBuffer.close();
			if(outputBuffer != null) outputBuffer.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage() {
		try {
			outputBuffer.write(username);
			outputBuffer.newLine();
			outputBuffer.flush();
			
			Scanner scanner = new Scanner(System.in);
			while(socket.isConnected()) {
				String messageToSend = scanner.nextLine();
				outputBuffer.write(messageToSend);
				outputBuffer.newLine();
				outputBuffer.flush();
				
			}
		}
		catch(IOException e) {
			closeAllConnection();
		}
	}
	
	public void listenForMessages() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				while(socket.isConnected()) {
					try {
						String incomingMessage = inputBuffer.readLine();
						System.out.println(incomingMessage);
					}
					catch(IOException e) {
						closeAllConnection();
						break;
					}
				}
			}
			
		}).start();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter an username:");
		String username = scanner.nextLine();
		Socket socket = new Socket("localhost", 9999);
		Client client = new Client(socket, username);
		client.listenForMessages();
		client.sendMessage();
		
	}
}
