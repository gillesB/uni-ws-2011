package serverLogic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import transmit.MovieDetailShared;
import transmit.MovieShared;
import transmit.RateMovieShared;
import transmit.ShowShared;
import transmit.TicketShared;

/**
 * contains all the Querys needed to fetch informations from the database
 * 
 */
public class DatabaseConnection {

	/**
	 * @param userName
	 * @param Password
	 * @return log in true or false
	 */
	public static boolean getLoginDetailsFromDB(String userName, String Password) {
		boolean logedIn = false;
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String query =
		"SELECT Customer_Name FROM Customer WHERE Customer_Name='" + userName + "' and Password='" + Password + "'";
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				logedIn = true;
			} else {
				logedIn = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return logedIn;
	}

	/**
	 * To fetch the movies from the Database from the Movies Table.
	 * 
	 * @return
	 */
	public static ArrayList<Movie> getAllMovies() {
		ArrayList<Movie> currentMovieList = new ArrayList<Movie>();
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String query = "Select Movie_ID,Title,Description,Picture,PossibleSurprizeMovie from movie;";
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			int size = 0;
			if (resultSet != null) {
				System.out.println("size of Movie result set= " + size);
				while (resultSet.next()) {
					Movie m = new Movie();
					m.setMovieID(resultSet.getInt("Movie_ID"));
					m.setName(resultSet.getString("Title"));
					m.setDescription(resultSet.getString("Description"));
					m.setPictureReference(resultSet.getString("Picture"));
					m.setPossibleSurprizeMovie(resultSet.getBoolean("PossibleSurprizeMovie"));
					currentMovieList.add(m);
				}
			} else {
				System.out.println("NO ROWS FETCHED FROM THE DATABASE OR RESULTSET EMPTY");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return currentMovieList;
	}

	/**
	 * fetches the movies which can be shown as Surprise Movie. That means,
	 * those movies, whose PossibleSurprizeMovie value in the DB equals to 1
	 * 
	 * @return fetches the movies which can be shown as Surprise Movie
	 */
	public static ArrayList<Movie> getPossibleSurpriseMovies() {
		ArrayList<Movie> currentMovieList = new ArrayList<Movie>();
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String query =
		"Select Movie_ID,Title,Description,Picture,PossibleSurprizeMovie from movie where PossibleSurprizeMovie = 1";
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			int size = 0;
			if (resultSet != null) {
				System.out.println("size of Movie result set= " + size);
				while (resultSet.next()) {
					Movie m = new Movie();
					m.setMovieID(resultSet.getInt("Movie_ID"));
					m.setName(resultSet.getString("Title"));
					m.setDescription(resultSet.getString("Description"));
					m.setPictureReference(resultSet.getString("Picture"));
					m.setPossibleSurprizeMovie(resultSet.getBoolean("PossibleSurprizeMovie"));
					currentMovieList.add(m);

				}
			} else {
				System.out.println("NO ROWS FETCHED FROM THE DATABASE OR RESULTSET EMPTY");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return currentMovieList;
	}

	/**
	 * To fetch the shows from the database from the Shows table.
	 * 
	 * @return
	 */
	public static ArrayList<ShowShared> getShowsFromDB(String date) {

		ArrayList<ShowShared> showsArr = new ArrayList<ShowShared>();
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String tempDateAsString = null;

		System.out.println("Date as String for DB query " + date);

		String query =
		"SELECT Show_ID, show.Movie_id, Price, TimeandDate, Hall_No,Picture, Title FROM show, movie "
		+ "WHERE show.Movie_id = movie.Movie_Id AND show.TimeandDate LIKE '" + date + "';";
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				while (resultSet.next()) {
					ShowShared ss = new ShowShared();
					ss.showID = resultSet.getInt("Show_ID");
					ss.movieName = resultSet.getString("Title");
					ss.price = resultSet.getString("Price");
					tempDateAsString = resultSet.getString("TimeandDate");
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:ss");
					Date temp = new Date();
					temp = sdf.parse(tempDateAsString);
					ss.time = temp.getTime();
					ss.hallNr = resultSet.getString("Hall_No");
					ss.image = resultSet.getBytes("Picture");
					showsArr.add(ss);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return showsArr;
	}

	/**
	 * @param movieID
	 * @return
	 */
	public static MovieDetailShared getMovieDetailViaMovieID(int movieID) {
		String query = "SELECT Description, Picture FROM movie where Movie_ID = " + movieID;
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		MovieDetailShared returnValue = new MovieDetailShared();
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				returnValue.averageRating = getAverageRating(movieID);
				returnValue.description = resultSet.getString("Description");
				returnValue.image = resultSet.getBytes("Picture");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return returnValue;
	}

	/**
	 * To get the userid of the user from the Customer table, when username is
	 * given.
	 * 
	 * @param username
	 * @return
	 */
	public static MovieDetailShared getMovieDetailViaShowID(int showID) {
		String query = "SELECT Movie_id FROM show WHERE Show_ID = " + showID;
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		MovieDetailShared returnValue = new MovieDetailShared();
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				int movieID = resultSet.getInt("Movie_id");
				returnValue = getMovieDetailViaMovieID(movieID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return returnValue;
	}

	public static int getUserID(String username) {
		String query = "SELECT Customer_ID FROM customer where Customer_Name = \"" + username + "\"";
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		int userID = 0;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				userID = resultSet.getInt("Customer_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userID;
	}

	public static RateMovieShared[] getMoviesRatedbyUser(int userID) {
		ArrayList<RateMovieShared> ratedMovies = new ArrayList<RateMovieShared>();
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String query =
		"SELECT movie.Movie_ID, Title, Rating, Picture FROM Rating, movie where Customer  = " + userID
		+ "  and Rating.Movie = movie.Movie_ID";
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				while (resultSet.next()) {
					RateMovieShared r = new RateMovieShared();
					r.image = resultSet.getBytes("Picture");
					r.movieID = resultSet.getInt("Movie_ID");
					r.moviename = resultSet.getString("Title");
					r.userRating = resultSet.getInt("Rating");
					ratedMovies.add(r);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ratedMovies.toArray(new RateMovieShared[0]);
	}

	public static HashMap<Integer, Integer> getMoviesRatedbyUserAsHashMap(int customerID) {
		HashMap<Integer, Integer> ratedMovies = new HashMap<Integer, Integer>();
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String query = "SELECT movie, rating FROM rating WHERE customer = " + customerID;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				while (resultSet.next()) {
					int movieID = resultSet.getInt("movie");
					int rating = resultSet.getInt("rating");
					ratedMovies.put(movieID, rating);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ratedMovies;
	}

	public static void addTickets(int userID, int showID, int amount) {
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			Statement s = connection.createStatement();
			// Validated with value 0 means that the tickets have not been
			// validated
			String query =
			"INSERT INTO ticket (Customer,Show,Amount,Validated) VALUES (" + userID + "," + showID + "," + amount + ","
			+ 0 + ")";
			int update_count = s.executeUpdate(query);

			System.out.println(update_count + " rows inserted.");
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<TicketShared> getNotValidatedTickets(int userID) {
		ArrayList<TicketShared> tickets = new ArrayList<TicketShared>();
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String tempDateAsString = null;
		String query =
		"SELECT ticket.show, title, Hall_no, TimeandDate, Picture, amount FROM ticket, show, movie WHERE Customer  = "
		+ userID + " and Validated = 0 and ticket.show = show.show_id and show.movie_id = movie.movie_id";

		// "SELECT movie.Movie_ID, Title, Rating, Picture FROM Rating, movie where Customer  = "
		// + userID
		// + "  and Rating.Movie = movie.Movie_ID";
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				while (resultSet.next()) {
					TicketShared r = new TicketShared();
					r.hallNr = resultSet.getString("hall_no");
					r.movieName = resultSet.getString("title");
					r.showID = resultSet.getInt("show");
					tempDateAsString = resultSet.getString("TimeandDate");
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:ss");
					Date temp = new Date();
					temp = sdf.parse(tempDateAsString);
					r.time = temp.getTime();
					r.amount = resultSet.getInt("amount");
					r.image = resultSet.getBytes("Picture");
					tickets.add(r);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tickets;
	}

	public static void updateRating(int userID, int movieID, int rating) {
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			Statement s = connection.createStatement();
			int update_count =
			s.executeUpdate("UPDATE rating " + "SET rating= " + rating + " WHERE customer= " + userID + " and movie= "
			+ movieID);

			System.out.println(update_count + " rows inserted.");
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static int getAverageRating(int movieID) {
		int ret = 0;
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String query = "select avg(rating) from rating where movie = " + movieID;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				ret = resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static int getMovieIDviaShowID(int showID) {
		int ret = 0;
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String query = "select movie_ID from show where show_id =" + showID;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				ret = resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static void insertDefaultRating(int userID, int movieID) {
		Connection connection = null;
		if (movieID == 1) {
			return;
		}
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			Statement s = connection.createStatement();
			// Validated with value 0 means that the tickets have not been
			// validated
			String query =
			"INSERT INTO rating (Customer,Movie,Rating) VALUES (" + userID + "," + movieID + ","
			+ RecommenderSystem.DEFAULT_RATING + ")";
			int update_count = s.executeUpdate(query);

			System.out.println(update_count + " rows inserted.");
			s.close();
		} catch (SQLException e) {
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static int getRating(int customerID, int movieID) {
		int rating = 0;
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		// TODO care about date (Gilles)
		// the movie with the movieID 1, is the surprise movie
		String query = "SELECT rating FROM rating WHERE customer = " + customerID + " AND movie = " + movieID;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				rating = resultSet.getInt("rating");
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rating;
	}

	public static double getAverageRatingOfUser(int customerID) {
		int ret = 0;
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String query = "select avg(rating) from rating where customer = " + customerID;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				ret = resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static String getMovieName(int movieID) {
		String moviename = "";
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		String query = "select Title from movie where movie_id =  " + movieID;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				moviename = resultSet.getString("Title");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return moviename;
	}

	public static ArrayList<Customer> getUsersOfTodaysSurpriseMovie() {
		ArrayList<Customer> customers = new ArrayList<Customer>();
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;

		// TODO care about date (Gilles)
		String query =
		"SELECT customer.Customer_id, customer_name FROM ticket, show ,customer WHERE ticket.show = show.show_id and movie_id = 1 and ticket.customer = customer.customer_id";

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				while (resultSet.next()) {
					Customer customer =
					new Customer(resultSet.getString("customer_name"), resultSet.getInt("Customer_id"));
					customers.add(customer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return customers;
	}

	public static Integer getAmountUserOfTodaysSurpriseMovie() {
		Integer amountUsers = 0;
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		// TODO care about date (Gilles)
		// the movie with the movieID 1, is the surprise movie
		String query = "SELECT count(*) FROM ticket, show WHERE ticket.show = show.show_id and movie_id = 1";
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				amountUsers = resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return amountUsers;
	}

	public static ArrayList<Customer> getAllCustomers() {
		ArrayList<Customer> customers = new ArrayList<Customer>();
		// Mine code
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;

		String query = "SELECT customer.Customer_id, customer_name FROM customer";

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null) {
				while (resultSet.next()) {
					Customer customer =
					new Customer(resultSet.getString("customer_name"), resultSet.getInt("Customer_id"));
					customers.add(customer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return customers;
	}

	public static void insertMovie(MovieShared movie) {
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + RecommenderSystemMain.DATABASE_LOCATION);
			Statement s = connection.createStatement();
			// Validated with value 0 means that the tickets have not been
			// validated

			String query =
			"INSERT INTO movie (Movie_ID, Title, Description, Picture, PossibleSurprizeMovie ) VALUES (?,?,?,?,?)";
			// int update_count = s.executeUpdate(query);

			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, movie.movieID);
			ps.setString(2, movie.moviename);
			ps.setString(3, movie.description);
			ps.setBytes(4, movie.image);
			ps.setInt(5, 0);
			int count = ps.executeUpdate();
			ps.close();

			System.out.println(count + " rows inserted.");
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
