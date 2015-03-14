package serverGUI;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import serverLogic.Customer;
import serverLogic.DatabaseConnection;
import serverLogic.RecommenderSystem;
import serverLogic.RecommenderSystem.MoviePredictedRating;

/**
 * makes the connection between the Swing GUI and the logic part of the Rosebud
 * server
 * 
 */
public class ServerGUIInterface {

	private ServerGUI gui;
	private RecommenderSystem server;

	public ServerGUIInterface(RecommenderSystem server) {
		super();
		gui = new ServerGUI(this);
		gui.setVisible(true);
		this.server = server;
	}

	/**
	 * executed after the "Recommend Movie" Button is pressed. gets the
	 * recommendations from the logic part and shows the top 5 to the user.
	 */
	public void getRecommendation() {

		List<MoviePredictedRating> movies = server.getRecommendations();
		DefaultListModel model = (DefaultListModel) gui.lstMovies.getModel();
		model.clear();
		for (int i = 0; i < 5; i++) {
			int movieID = movies.get(i).getMovieID();
			String movieName = DatabaseConnection.getMovieName(movies.get(i).getMovieID());
			String rating = movies.get(i).getPredictedRating().toString();
			rating = rating.substring(0, 3);
			model.addElement(gui.new MovieListItem(movieName, movieID, rating));
		}
	}

	/**
	 * shows the user how many customers will see the Surprise Movie
	 * 
	 */
	public String getAmountVisitors() {
		Integer amountUsers = DatabaseConnection.getAmountUserOfTodaysSurpriseMovie();
		return amountUsers.toString();
	}

	/**
	 * inserts the default rating, for the chosen movie for all customers visiting the Surprise Movie
	 * 
	 * @param movieID
	 */
	public void selectSurpriseMovie(int movieID) {
		ArrayList<Customer> customers = DatabaseConnection.getUsersOfTodaysSurpriseMovie();
		for (Customer c : customers) {
			DatabaseConnection.insertDefaultRating(c.getCustomerID(), movieID);
		}
	}

}
