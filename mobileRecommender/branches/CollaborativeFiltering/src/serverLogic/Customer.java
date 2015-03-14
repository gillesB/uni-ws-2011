package serverLogic;

import java.util.HashMap;

public class Customer {
	
	private String name;
	private int customerID;
	private double averageRating;
	private HashMap<Integer, Integer> movieRatings;
	//contains the movies a user has seen and how good (or bad) he has rated it
	//default rating is 5 where (0 is a bad rating and 10 a good rating)
	
	public Customer(String name, int customerID) {
		super();
		this.name = name;
		this.customerID = customerID;
		this.averageRating = DatabaseConnection.getAverageRatingOfUser(customerID);
		this.movieRatings = DatabaseConnection.getMoviesRatedbyUserAsHashMap(customerID);
	}
	
	public int getRatingForMovie(int movieID){
		if(movieRatings.containsKey(movieID)){
			return movieRatings.get(movieID);
		} else {
			return -1;
		}		 
	}
	
	public double getAverageRating(){
		return averageRating;
	}
	
	public String getName() {
		return name;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + customerID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (customerID != other.customerID)
			return false;
		return true;
	}

	
	
}
