package randomMovie;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Provider {
	ServerSocket providerSocket;
	Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	String[] splitMessage;
	private Boolean loggedIn = false;
	RecommenderSystem recommender;
	private Customer loggedInCustomer;

	Provider(RecommenderSystem recommender) {
		this.recommender = recommender;
	}

	void run() {
		try {
			// 1. creating a server socket
			providerSocket = new ServerSocket(2004, 10);
			// 2. Wait for connection
			System.out.println("Waiting for connection");
			connection = providerSocket.accept();
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			// 3. get Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			//sendMessage("Connection successful");
			// 4. The two parts communicate via the input and output streams
			do {
				try {
					message = (String) in.readObject();
					splitMessage = message.split(" ");
					System.out.println("client>" + message);
					if(loggedIn){
						parseMessage(splitMessage);						
					} else {
						if(splitMessage[0].equals("login")){
							loggedIn = login(splitMessage);
							out.writeObject(loggedIn);
						}
					}
					if (splitMessage[0].equals("bye"))
						sendMessage("bye");
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
			} while (!message.equals("bye"));
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				providerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("server>" + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void parseMessage(String[] message) {
		String command = message[0];
		if(command.equals("currentMovies")){
			currentMovies();
		}
		
	}

	//message: login <username> <password> 
	private boolean login(String[] loginMessage) {
		if(loginMessage.length != 2){
			return false;
		}
		Customer customer = recommender.getCustomer(loginMessage[1]);
		if(customer == null){
			return false;
		} else {
			loggedInCustomer = customer;
			return true;
		}
	}
	
	//message: currentMovies
	//get the current Movies from the recommender system
	//and send them back as String[]
	private void currentMovies() {
		ArrayList<Movie> movies = recommender.getCurrentMovies();
		String[] moviesStr = new String[movies.size()];
		for(int i = 0; i < movies.size(); i++ ){
			moviesStr[i] = movies.get(i).getName();
		}
		try {
			out.writeObject(moviesStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	
	
}
