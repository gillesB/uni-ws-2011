package me.rosebudClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import transmit.MovieDetailShared;
import transmit.MovieShared;
import transmit.RateMovieShared;
import transmit.ShowShared;
import transmit.TicketShared;
import android.util.Log;

/**
 * A singleton class, which is accessible through the whole client. It is the only class which has a direct connection to the server.
 * It implements the socket commands, described in the document of Rosebud
 * 
 *
 */
public class NetworkSingleton {

	private int portNumber = 2004;
	private Socket socket;
	private ObjectOutputStream socketOut;
	private ObjectInputStream socketIn;
	private static final NetworkSingleton instance = new NetworkSingleton();

	private String username;
	private Boolean loggedIn;

	// Private constructor prevents instantiation from other classes
	private NetworkSingleton() {
		Log.d("NetworkSingleton", "NetworkSingleton started");
	}

	public static NetworkSingleton getInstance() {
		return instance;
	}

	/**
	 * tries to login to the server. first the connection to the server is opened.
	 * Then the username and password are sent for verification. 
	 * 
	 * @param activity
	 * @param username
	 * @param password
	 * @return 3 possible return values 0 - login successful 1 - network
	 *         problems 2 - wrong username or password
	 */
	public int login(String IPadress, final String username, final String password) {

		Thread t = new Thread() {

			public void run() {
				Log.d("NetworkSingleton", "Try Login");
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

		try {
			openConnection(IPadress);
			if (openConnectionSuccess) {
				t.start();
				t.join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("NetworkSingleton", "Login was: " + new Boolean(loggedIn).toString());
		if (loggedIn) {
			return 0;
		} else if (openConnectionSuccess == false) {
			return 1;
		} else { // loggedIn == false
			return 2;
		}
	}

	/**
	 * logs out. the connection to the server is closed on every log out
	 */
	public void logout() {
		loggedIn = false;
		if (socket != null) {
			closeConnection();
		}
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

		Log.d("NetworkSingleton", "get current movies");
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

		Log.d("NetworkSingleton", "get current movies");
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

		Log.d("NetworkSingleton", "get movie details");
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
				Log.d("NetworkSingleton", "Buy Tickets");
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

		Log.d("NetworkSingleton", "Buying tickets was: " + new Boolean(boughtTickets).toString());
		return boughtTickets;
	}

	TicketShared[] tickets;

	public TicketShared[] getMyTickets() {
		Thread t = new Thread() {

			public void run() {
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

		Log.d("NetworkSingleton", "Buying tickets was: " + new Boolean(boughtTickets).toString());
		return tickets;
	}

	public void rate(final Long movieID, final Integer rating) {
		Thread t = new Thread() {

			public void run() {
				Log.d("NetworkSingleton", "send movie rating");
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

	public void insertMovieIntoDB(final MovieShared movie) {
		Thread t = new Thread() {

			public void run() {
				try {
					socketOut.writeObject("insertMovie");
					socketOut.writeObject(movie);
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

	public void insertRating(final Long movieID, final Integer rating) {
		Thread t = new Thread() {

			public void run() {
				Log.d("NetworkSingleton", "send movie rating");
				try {
					socketOut.writeObject("insertRating " + username + " " + movieID.toString() + " "
					+ rating.toString());
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

	private boolean openConnectionSuccess;

	private void openConnection(final String IPaddress) throws InterruptedException {
		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					InetAddress serverAddr = InetAddress.getByName(IPaddress);

					Log.d("NetworkSingleton", "Create Socket");

					socket = new Socket();
					socket.connect(new InetSocketAddress(serverAddr, portNumber));
					
					socketOut = new ObjectOutputStream(socket.getOutputStream());
					socketIn = new ObjectInputStream(socket.getInputStream());
					openConnectionSuccess = true;
				} catch (UnknownHostException e) {
					Log.e("NetworkSingleton", "UnknownHostException", e);
				} catch (IOException e) {
					Log.e("NetworkSingleton", "IOException", e);
				}
			}
		};
		openConnectionSuccess = false;
		t.start();
		t.join();
	}

	private void closeConnection() {
		Thread t = new Thread() {
			public void run() {
				try {
					socketOut.writeObject("bye");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						socketOut.close();
						socketIn.close();
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
	}

}
