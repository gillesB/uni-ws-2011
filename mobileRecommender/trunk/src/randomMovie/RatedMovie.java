package randomMovie;

/***
 * This class is used to save the rating of each seen movie, made by a customers
 * 
 * @author Baatz
 * 
 */
public class RatedMovie {

	private Movie movie;
	private double rating;

	public RatedMovie(Movie movie) {
		super();
		this.movie = movie;
		this.rating = 5;
	}

	public RatedMovie(Movie movie, double rating) {
		super();
		this.movie = movie;
		this.rating = rating;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	@Override
	public int hashCode() {
		return movie.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return movie.equals(obj);
	}

	@Override
	public String toString() {
		return movie.toString();
	}

}
