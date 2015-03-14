package randomMovie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import serverGUI.ServerGUIInterface;

public class RecommenderSystem {

	private HashMap<String, Customer> customers = new HashMap<String, Customer>();
	private ArrayList<Customer> randomMovieVisitors = new ArrayList<Customer>();

	private ArrayList<Movie> currentMovies = new ArrayList<Movie>();

	private ServerGUIInterface guiInterface;

	private Movie randomMovie = new Movie("Random Movie");

	private Provider provider;

	public RecommenderSystem(HashMap<String, Customer> customers) {
		super();
		this.customers = customers;
		guiInterface = new ServerGUIInterface(this);
		provider = new Provider(this);
		new Thread() {
			public void run() {
				while (true) {
					provider.run();
				}
			};
		}.start();
		addCurrentMovies();
	}

	private void addCurrentMovies() {
		currentMovies.add(new Movie("The Adventures of Tintin"));
		currentMovies.add(new Movie("In Time"));
		currentMovies.add(new Movie("Intouchables"));
		currentMovies.add(randomMovie);
	}

	public void addCustomer(Customer customer) {
		customers.put(customer.getName(), customer);
	}

	// TODO
	/**
	 * This one does the magic. it gets an list with some customers of the
	 * cinema. And selects a movie each of these customers likes.
	 */
	public List<Movie> getRecommendationForRandomMovieShow(ArrayList<Customer> visitors) {
		System.out.println("The visitors are and have seen:");
		for (Customer c : customers.values()) {
			System.out.println(c);
		}
		ArrayList<Movie> ret = new ArrayList<Movie>();
		ret.add(new Movie("no Movie"));
		return ret;
	}

	public List<Movie> getRecommendationForRandomMovieShow() {
		return getRecommendationForRandomMovieShow(randomMovieVisitors);
	}

	public void addRandomMovieVisitor(Customer visitor) {
		randomMovieVisitors.add(visitor);
		guiInterface.addVisitor(visitor);
	}

	public ArrayList<Movie> getCurrentMovies() {
		return currentMovies;
	}

	public String buyTicket(Customer customer, Movie movie) {
		if (!customers.containsValue(customer)) {
			return "Unkown Customer";
		}
		if (!currentMovies.contains(movie)) {
			return "Unkown Movie";
		}
		if (movie.equals(randomMovie)) {
			addRandomMovieVisitor(customer);
		} else {
			customer.hasSeen(movie);
		}
		return "Ticket successfully bought";
	}

	public Customer getCustomer(String username) {
		return customers.get(username);
	}

}
