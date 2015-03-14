package randomMovie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class RecommenderSystemMain {

	static private Random generator = new Random();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Movie> movieList = initMovies();
		HashMap<String, Customer> customerList = initcustomers(movieList);
		HashMap<String, Customer> visitorList = initVisitorsForRandomMovieSession(customerList);
		RecommenderSystem recommender = new RecommenderSystem(customerList);
		for (Customer visitor : visitorList.values()) {
			recommender.addRandomMovieVisitor(visitor);
		}
		// new Client(recommender, customerList.get(2));
		// new Client(recommender, customerList.get(5));

	}

	private static ArrayList<Movie> initMovies() {
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		movieList.add(initMovie("The Shawshank Redemption", 142, 9.2, Categorie.CRIME, Categorie.DRAMA));
		movieList.add(initMovie("The Godfather", 175, 9.2, Categorie.CRIME, Categorie.DRAMA));
		movieList.add(initMovie("The Good, the Bad and the Ugly", 161, 9.0, Categorie.ADVENTURE, Categorie.WESTERN));
		movieList.add(initMovie("Pulp Fiction", 154, 9.0, Categorie.CRIME, Categorie.THRILLER));
		movieList.add(initMovie("12 Angry Men", 96, 8.9, Categorie.DRAMA));
		movieList.add(initMovie("Schindler's List", 195, 8.9, Categorie.DRAMA));
		movieList.add(initMovie("One Flew Over the Cuckoo's Nest", 133, 8.8, Categorie.DRAMA));
		movieList.add(initMovie("The Dark Knight", 152, 8.9, Categorie.ACTION, Categorie.CRIME, Categorie.DRAMA));
		movieList.add(initMovie("The Lord of the Rings: The Return of the King", 201, 8.9, Categorie.ACTION,
		Categorie.ADVENTURE, Categorie.DRAMA));
		movieList.add(initMovie("Star Wars: Episode V - The Empire Strikes Back", 124, 8.8, Categorie.ACTION,
		Categorie.ADVENTURE, Categorie.SCIFI));
		movieList.add(initMovie("Inception", 148, 8.8, Categorie.ACTION, Categorie.ADVENTURE, Categorie.SCIFI));
		movieList.add(initMovie("Fight Club", 139, 8.8, Categorie.DRAMA));
		movieList.add(initMovie("Seven Samurai", 207, 8.8, Categorie.ADVENTURE, Categorie.DRAMA));
		movieList.add(initMovie("Casablanca", 102, 8.7, Categorie.DRAMA, Categorie.ROMANCE, Categorie.WAR));
		movieList.add(initMovie("Once Upon a Time in the West", 175, 8.7, Categorie.WESTERN));
		movieList.add(initMovie("The Matrix", 136, 8.7, Categorie.ACTION, Categorie.ADVENTURE, Categorie.SCIFI));
		movieList.add(initMovie("Raiders of the Lost Ark", 115, 8.7, Categorie.ACTION, Categorie.ADVENTURE));
		return movieList;
	}

	private static Movie initMovie(String name, int length, double rating, Categorie... categories) {

		Movie movie = new Movie(name);
		movie.setLength(length);
		movie.setOverallRanking(rating);
		for (Categorie c : categories) {
			movie.addCategorie(c);
		}
		return movie;
	}

	private static HashMap<String, Customer> initcustomers(ArrayList<Movie> movieList) {
		HashMap<String, Customer> customerList = new HashMap<String, Customer>();
		customerList.put("xyz", initCustomer("xyz", movieList));
		customerList.put("James Oliver", initCustomer("James Oliver", movieList));
		customerList.put("Liam Neeson", initCustomer("Liam Neeson", movieList));
		customerList.put("Tom Hanks", initCustomer("Tom Hanks", movieList));
		customerList.put("Bruce Willis", initCustomer("Bruce Willis", movieList));
		customerList.put("Gary Oldman", initCustomer("Gary Oldman", movieList));
		customerList.put("Stephen Fry", initCustomer("Stephen Fry", movieList));
		customerList.put("Tim Burton", initCustomer("Tim Burton", movieList));
		customerList.put("Johnny Depp", initCustomer("Johnny Depp", movieList));
		customerList.put("Michelle Pfeiffer", initCustomer("Michelle Pfeiffer", movieList));
		customerList.put("Denzel Washington", initCustomer("Denzel Washington", movieList));
		customerList.put("Stephen Lang", initCustomer("Stephen Lang", movieList));
		customerList.put("Robin Williams", initCustomer("Robin Williams", movieList));
		customerList.put("Brad Bird", initCustomer("Brad Bird", movieList));
		customerList.put("Stellan Skarsgård", initCustomer("Stellan Skarsgård", movieList));
		customerList.put("Sean Bean", initCustomer("Sean Bean", movieList));
		customerList.put("Anil Kapoor", initCustomer("Anil Kapoor", movieList));
		customerList.put("Viggo Mortensen", initCustomer("Viggo Mortensen", movieList));
		customerList.put("Bryan Cranston", initCustomer("Bryan Cranston", movieList));
		customerList.put("Mel Gibson", initCustomer("Mel Gibson", movieList));
		customerList.put("Beverly D'Angelo", initCustomer("Beverly D'Angelo", movieList));
		customerList.put("Kevin Spacey", initCustomer("Kevin Spacey", movieList));
		return customerList;
	}

	private static Customer initCustomer(String name, ArrayList<Movie> movieList) {
		Customer customer = new Customer(name);
		int watchedMovies = generator.nextInt(movieList.size());
		for (int i = 0; i < watchedMovies; i++) {
			customer.hasSeen(movieList.get(generator.nextInt(movieList.size())));
		}
		return customer;
	}

	private static HashMap<String, Customer> initVisitorsForRandomMovieSession(HashMap<String, Customer> customerList) {
		HashMap<String, Customer> visitors = new HashMap<String, Customer>();
		Customer[] customerArray = customerList.values().toArray(new Customer[0]);
		int amountVisitors = generator.nextInt(customerList.size());
		for (int i = 0; i < amountVisitors; i++) {
			Customer c = customerArray[generator.nextInt(customerList.size())];
			visitors.put(c.getName(), c);
		}
		return visitors;
	}

}
