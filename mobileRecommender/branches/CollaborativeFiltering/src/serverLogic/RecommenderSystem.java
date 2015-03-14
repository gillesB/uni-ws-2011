package serverLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import serverGUI.ServerGUIInterface;

public class RecommenderSystem {

	private ArrayList<Customer> allCustomers = new ArrayList<Customer>();
	private ArrayList<Movie> allMovies = new ArrayList<Movie>();
	public static final int DEFAULT_RATING = 5;

	public RecommenderSystem() {
		super();
		ParallelProvider parallelProvider = new ParallelProvider(this);
		new Thread(parallelProvider).start();
		new ServerGUIInterface(this);
	}

	/**
	 * Gathers the needed data from the DB (movies, customers, surprise movie
	 * visitors), so that the recommendations for the Surprise Movie can be
	 * calculated.
	 * 
	 * @return a list with the recommended movies, sorted by the predicted
	 *         rating
	 */
	public List<MoviePredictedRating> getRecommendations() {
		allCustomers = DatabaseConnection.getAllCustomers();
		allMovies = DatabaseConnection.getAllMovies();
		ArrayList<Customer> randomMovieVisitors = DatabaseConnection.getUsersOfTodaysSurpriseMovie();
		ArrayList<Movie> possibleSurpriseMovies = DatabaseConnection.getPossibleSurpriseMovies();
		List<MoviePredictedRating> l = getMovieRecommendationForGroup(randomMovieVisitors, possibleSurpriseMovies);
		return l;
	}

	/**
	 * predicts the rating for each movie in {@code possibleSurpriseMovies} and
	 * for each customer in {@code customers}. The predicted ratings are summed
	 * up, and the average is taken. Afterwards the movies are sorted by their
	 * (average) predicted rating. This is the "trick" to make a group
	 * recommendation.
	 * 
	 * 
	 * @param customers
	 *            group of customers, usually those visiting the Surprise Movie
	 * @param possibleSurpriseMovies
	 *            set of movies, which can be recommended as Surprise Movie
	 * @return a list with the recommended movies, sorted by the predicted
	 *         rating
	 */
	private ArrayList<MoviePredictedRating> getMovieRecommendationForGroup(List<Customer> customers,
	List<Movie> possibleSurpriseMovies) {

		ArrayList<MoviePredictedRating> moviesWithPredictedRating =
		new ArrayList<RecommenderSystem.MoviePredictedRating>();
		for (Movie m : possibleSurpriseMovies) {
			int movieID = m.getMovieID();
			MoviePredictedRating movieWithRating = new MoviePredictedRating(movieID, 0);
			for (Customer c : customers) {
				movieWithRating.predictedRating += getPredictedRating(c, movieID);
			}
			// take average
			movieWithRating.predictedRating = movieWithRating.predictedRating / customers.size();
			moviesWithPredictedRating.add(movieWithRating);
		}
		Collections.sort(moviesWithPredictedRating);

		return moviesWithPredictedRating;
	}

	/**
	 * gets the neighborhood of {@code customer} and calculates a prediction for
	 * the {@code customer}'s rating for the movie {@code movieID}. The
	 * predicted rating is returned.
	 * 
	 * @param customer
	 *            predicts a rating for this customer
	 * @param movieID
	 *            the predicted rating is for this movie
	 * @return the predicted rating for {@code customer} for {@code movieID}
	 */
	private double getPredictedRating(Customer customer, int movieID) {
		ArrayList<CustomerSimilarity> neighbourhood = getNeighbourhood(customer);

		double nominator = 0, denominator = 0;
		for (CustomerSimilarity neighbour : neighbourhood) {
			int neighbourMovieRating = neighbour.customer.getRatingForMovie(movieID);
			double neighbourAverageRating = neighbour.customer.getAverageRating();
			if (neighbourMovieRating != -1) {
				nominator += neighbour.similarity * (neighbourMovieRating - neighbourAverageRating);
				denominator += neighbour.similarity;
			}
		}

		// check if denominator is 0, to avoid division by 0
		if (denominator != 0) {
			return customer.getAverageRating() + nominator / denominator;
		} else {
			// denominator was 0, return a bad rating, as their are probably not
			// enough
			// data available, to make an accurate prediction
			return 0;
		}
	}

	/**
	 * creates the neighborhood for {@code customer}
	 * 
	 * iterates over the customers in {@code allCustomers} and calculates the
	 * similarity between that customer and {@code customer} returns the top 10
	 * most similar customers (less if their are not 10 users available)
	 * 
	 * @param customer
	 *            the neighbourhood is created for this customer
	 * @return the top 10 most similar customers (less if their are not 10 users
	 *         available)
	 */
	private ArrayList<CustomerSimilarity> getNeighbourhood(Customer customer) {
		ArrayList<CustomerSimilarity> neighbourhood = new ArrayList<CustomerSimilarity>();
		for (Customer c : allCustomers) {
			if (customer != c) {
				double sim = similarity(customer, c);
				neighbourhood.add(new CustomerSimilarity(c, sim));
			}
		}

		// create top ten list
		Collections.sort(neighbourhood);
		if (neighbourhood.size() < 10) {
			return neighbourhood;
		} else {
			return new ArrayList<CustomerSimilarity>(neighbourhood.subList(0, 10));
		}

	}

	/**
	 * calculates the similarity between {@code customer1} and {@code customer2}
	 * . This is done by Pearson’s correlation coefficient.
	 * 
	 * @param customer1
	 *            first customer to get similarity
	 * @param customer2
	 *            second customer to get similarity
	 * @return the similarity between {@code customer1} and {@code customer2}
	 */
	private double similarity(Customer customer1, Customer customer2) {
		double averageRatingA = customer1.getAverageRating();
		double averageRatingB = customer2.getAverageRating();
		double nominator = 0, denominator1 = 0, denominator2 = 0;

		for (Movie m : allMovies) {
			int movieID = m.getMovieID();
			int ratingCust1 = customer1.getRatingForMovie(movieID);
			int ratingCust2 = customer2.getRatingForMovie(movieID);
			if (ratingCust1 != -1 || ratingCust2 != -1) {
				nominator += (ratingCust1 - averageRatingA) * (ratingCust2 - averageRatingB);
				denominator1 += Math.pow((ratingCust1 - averageRatingA), 2);
				denominator2 += Math.pow((ratingCust2 - averageRatingB), 2);
			}
		}

		denominator1 = Math.sqrt(denominator1);
		denominator2 = Math.sqrt(denominator2);

		if (denominator1 != 0 && denominator2 != 0) {
			return nominator / (denominator1 * denominator2);
		} else {
			return 0;
		}

	}

	/**
	 * Contains a {@code Customer} and the similarity relative to an other
	 * customer
	 * 
	 */
	private class CustomerSimilarity implements Comparable<CustomerSimilarity> {
		Customer customer;
		Double similarity;

		public CustomerSimilarity(Customer customer, double similarity) {
			super();
			this.customer = customer;
			this.similarity = similarity;
		}

		public int compareTo(CustomerSimilarity o) {
			return o.similarity.compareTo(similarity);
		}

	}

	/**
	 * contains a movieID and the predicted rating for all the users visiting
	 * the Surprise Movie
	 * 
	 * 
	 */
	public class MoviePredictedRating implements Comparable<MoviePredictedRating> {
		int movieID;
		Double predictedRating;

		public MoviePredictedRating(int movieID, double predictedRating) {
			super();
			this.movieID = movieID;
			this.predictedRating = predictedRating;
		}

		public int compareTo(MoviePredictedRating o) {
			return o.predictedRating.compareTo(predictedRating);
		}

		public int getMovieID() {
			return movieID;
		}

		public Double getPredictedRating() {
			return predictedRating;
		}

	}

}
