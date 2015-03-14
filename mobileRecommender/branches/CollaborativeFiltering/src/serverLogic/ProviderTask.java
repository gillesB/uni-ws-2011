package serverLogic;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import transmit.MovieDetailShared;
import transmit.MovieShared;
import transmit.RateMovieShared;
import transmit.ShowShared;
import transmit.TicketShared;
import java.util.Date;

/**
 * makes the actual communication with a client. See Socket Commands in the documentation for more detail.
 * 
 */
public class ProviderTask implements Runnable {
	protected Socket clientSocket = null;
	ObjectInputStream input;
	ObjectOutputStream output;
	String message;
	String[] splitMessage;
	private Boolean loggedIn = false;
	RecommenderSystem recommender;

	public ProviderTask(Socket clientSocket, RecommenderSystem recommender) {
		this.clientSocket = clientSocket;
		this.recommender = recommender;
	}

	public void run() {
		try {
			// 3. get Input and Output streams
			input = new ObjectInputStream(clientSocket.getInputStream());
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			do {
				try {
					message = (String) input.readObject();
					splitMessage = message.split(" ");
					System.out.println("client>" + message);
					if (loggedIn) {
						parseMessage(splitMessage);
					} else {
						if (splitMessage[0].equals("login")) {
							loggedIn = login(splitMessage);
							output.writeObject(loggedIn);
						}
					}
				} catch (ClassNotFoundException e) {
					System.err.println("Data received in unknown format");
				} catch (EOFException e) {// occures when client stalls
					break;
				}
			} while (!message.equals("bye"));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
				input.close();
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param message
	 */
	private void parseMessage(String[] message) {
		String command = message[0];
		if (command.equals("getShows")) {
			getShows(message);
		} else if (command.equals("seenMovies")) {
			seenMovies(message);
		} else if (command.equals("movieDetails")) {
			movieDetails(message);
		} else if (command.equals("buyTickets")) {
			buyTickets(message);
		} else if (command.equals("tickets")) {
			tickets(message);
		} else if (command.equals("rate")) {
			rate(message);
		} else if (command.equals("insertRating")) {
			insertRating(message);
		} else if (command.equals("insertMovie")) {
			insertMovie(message);
		} else if (command.equals("bye")) {
			// do nothing message is handled in run()
		} else {
			System.err.println("Unkown Command: " + command);
		}

	}

	/**
	 * @param msg
	 */
	private void sendMessage(String msg) {
		try {
			output.writeObject(msg);
			output.flush();
			System.out.println("server>" + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * the incoming message should be: login <username> <password>
	 * 
	 * @param message
	 * @return
	 */
	private boolean login(String[] message) {
		String userName = null;
		String passWord = null;
		userName = message[1];
		passWord = message[2];
		loggedIn = DatabaseConnection.getLoginDetailsFromDB(userName, passWord);
		return loggedIn;
	}

	/**
	 * the incoming message should be: getShows <date> the date is in
	 * milliseconds
	 * 
	 * @param message
	 */
	private void getShows(String[] message) {

		ArrayList<ShowShared> showList = new ArrayList<ShowShared>();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:ss");
		Date date = new Date(Long.parseLong(message[1]));
		String dateAsString = sdf.getInstance().format(date);
		dateAsString = dateAsString.substring(0, 7);
		dateAsString = 0 + dateAsString + "%";
		// System.out.println("dateAsString " + dateAsString);
		showList = DatabaseConnection.getShowsFromDB(dateAsString);

		try {
			output.writeObject(showList.toArray(new ShowShared[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * movieDetails (showID or movieID) <ID>
	 * 
	 * @param message
	 */
	private void movieDetails(String[] message) {
		MovieDetailShared callback = new MovieDetailShared();
		if (message[1].equals("movieID")) {
			callback = DatabaseConnection.getMovieDetailViaMovieID(Integer.parseInt(message[2]));
		} else {
			callback = DatabaseConnection.getMovieDetailViaShowID(Integer.parseInt(message[2]));
		}
		try {
			output.writeObject(callback);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * message: seenMovies <username> get the seen Movies from a user and send
	 * them back as String[]
	 * 
	 * @param message
	 */
	private void seenMovies(String[] message) {
		if (message.length != 2) {
			try {
				output.writeObject(new String[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		String username = message[1];
		int userID = DatabaseConnection.getUserID(username);
		RateMovieShared[] moviesToTransmit = DatabaseConnection.getMoviesRatedbyUser(userID);

		try {
			output.writeObject(moviesToTransmit);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * buyTickets <username> <showID> <amount>
	 * 
	 * @param message
	 */
	private void buyTickets(String[] message) {
		int userID = DatabaseConnection.getUserID(message[1]);
		int showID = Integer.parseInt(message[2]);
		int amount = Integer.parseInt(message[3]);
		boolean callback;
		if (amount != 0) {
			DatabaseConnection.addTickets(userID, showID, amount);
			int movieID = DatabaseConnection.getMovieIDviaShowID(showID);
			DatabaseConnection.insertDefaultRating(userID, movieID);
			callback = true;
		} else {
			callback = false;
		}
		try {
			output.writeObject(callback);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * tickets <username>
	 * 
	 * @param message
	 */
	private void tickets(String[] message) {
		int userID = DatabaseConnection.getUserID(message[1]);
		ArrayList<TicketShared> ticketsToTransmit = DatabaseConnection.getNotValidatedTickets(userID);
		try {
			output.writeObject(ticketsToTransmit.toArray(new TicketShared[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * rate <username> <movieID> <rating>
	 * 
	 * @param message
	 */
	private void rate(String[] message) {
		int userID = DatabaseConnection.getUserID(message[1]);
		int movieID = Integer.parseInt(message[2]);
		int rating = Integer.parseInt(message[3]);

		DatabaseConnection.updateRating(userID, movieID, rating);
	}

	/**
	 * insertRating <username> <movieID> <rating>
	 * 
	 * @param message
	 */
	private void insertRating(String[] message) {
		int userID = DatabaseConnection.getUserID(message[1]);
		int movieID = Integer.parseInt(message[2]);
		int rating = Integer.parseInt(message[3]);
		// TODO only default rating is inserted (Gilles)
		DatabaseConnection.insertDefaultRating(userID, movieID);
	}

	/**
	 * insertMovie
	 * 
	 * @param message
	 */
	private void insertMovie(String[] message) {
		try {
			MovieShared movie = (MovieShared) input.readObject();
			DatabaseConnection.insertMovie(movie);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
