package randomMovie;

import java.util.HashSet;

public class Customer {
	
	private String name;	
	private HashSet<RatedMovie> seenMovies = new HashSet<RatedMovie>();
	
	public Customer(String name) {
		super();
		this.name = name;
	}
	
	public void hasSeen(Movie movie){
		seenMovies.add(new RatedMovie(movie));
	}

	@Override
	public String toString() {
		String ret = name + " has seen:\n";
		for(RatedMovie m : seenMovies){
			ret += "\t"+m+"\n";
		}		
		return ret; 
	}

	public void setRatingForMovie(Movie movie, double rating){
		seenMovies.add(new RatedMovie(movie,rating));
	}
	
	public String getName() {
		return name;
	}	

}
