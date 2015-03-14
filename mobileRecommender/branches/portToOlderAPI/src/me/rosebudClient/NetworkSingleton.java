package me.rosebudClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import transmit.MovieDetailShared;
import transmit.RateMovieShared;
import transmit.ShowShared;
import transmit.TicketShared;
import android.util.Log;

public class NetworkSingleton {

	private String serverIpAddress = "10.0.2.2";

	private Socket socket;
	private ObjectOutputStream socketOut;
	private ObjectInputStream socketIn;
	private static final NetworkSingleton instance = new NetworkSingleton();

	String username;

	// Private constructor prevents instantiation from other classes
	private NetworkSingleton() {
		Log.d("NetworkService", "C: Networkservice started");
		serverIpAddress = "10.0.2.2";
		Thread t = new Thread() {

			@Override
			public void run() {

				try {
					serverIpAddress = "10.0.2.2";
					InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
					Log.d("NetworkService", "C: Create Socket");

					Socket socket = new Socket(serverAddr, 2004);
					socketOut = new ObjectOutputStream(socket.getOutputStream());
					socketIn = new ObjectInputStream(socket.getInputStream());
				} catch (Exception e) {
					Log.e("ClientActivity", "C: Error", e);
				}
			}
		};

		t.start();
		// try {
		// t.join();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public static NetworkSingleton getInstance() {
		return instance;
	}

	private Boolean loggedIn;

	public boolean login(final String username, final String password) {

		Thread t = new Thread() {

			public void run() {
				Log.d("NetworkService", "C: Try Login");
				try {
					socketOut.writeObject("login " + username + " " + password);
					Object obj = socketIn.readObject();
					loggedIn = (Boolean) obj;
					if (loggedIn) {
						NetworkSingleton.this.username = username;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("NetworkService", "Login was: " + new Boolean(loggedIn).toString());
		return loggedIn;
	}

	private ShowShared[] shows;

	public ShowShared[] getShows(final Date date) {

		Thread t = new Thread() {
			public void run() {

				try {
					socketOut.writeObject("getShows " + date.getTime());
					shows = (ShowShared[]) socketIn.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		Log.d("NetworkService", "get current movies");
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return shows;
	}

	RateMovieShared[] moviesToRate;

	public RateMovieShared[] getSeenMovies() {
		Thread t = new Thread() {
			public void run() {
				try {
					socketOut.writeObject("seenMovies " + username);
					moviesToRate = (RateMovieShared[]) socketIn.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		Log.d("NetworkService", "get current movies");
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return moviesToRate;
	}

	public CharSequence getUsername() {
		return username;
	}

	MovieDetailShared movieDetails;

	public MovieDetailShared getMovieDetailViaShowID(long showID) {
		return getMovieDetail("showID", showID);
	}

	public MovieDetailShared getMovieDetailViaMovieID(long movieID) {
		return getMovieDetail("movieID", movieID);
	}

	private MovieDetailShared getMovieDetail(final String argument, final long ID) {
		Thread t = new Thread() {
			public void run() {
				try {
					socketOut.writeObject("movieDetails " + argument + " " + ID);
					movieDetails = (MovieDetailShared) socketIn.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		Log.d("NetworkService", "get movie details");
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return movieDetails;
	}

	boolean boughtTickets;

	public boolean buyTickets(final long showID, final int amount) {
		Thread t = new Thread() {

			public void run() {
				Log.d("NetworkService", "Buy Tickets");
				try {
					socketOut.writeObject("buyTickets " + username + " " + showID + " " + amount);
					boughtTickets = (Boolean) socketIn.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("NetworkService", "Buying tickets was: " + new Boolean(boughtTickets).toString());
		return boughtTickets;
	}

	TicketShared[] tickets;

	public TicketShared[] getMyTickets() {
		Thread t = new Thread() {

			public void run() {
				Log.d("NetworkService", "Buy Tickets");
				try {
					socketOut.writeObject("tickets " + username);
					tickets = (TicketShared[]) socketIn.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("NetworkService", "Buying tickets was: " + new Boolean(boughtTickets).toString());
		return tickets;
	}

	public void rate(final Long movieID, final Integer rating) {
		Thread t = new Thread() {

			public void run() {
				Log.d("NetworkService", "send movie rating");
				try {
					socketOut.writeObject("rate " + username + " " + movieID.toString() + " " + rating.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
