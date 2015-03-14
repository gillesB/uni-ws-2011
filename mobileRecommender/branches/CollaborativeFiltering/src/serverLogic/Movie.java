package serverLogic;

import java.util.HashSet;

public class Movie {
	
	private int movieID;
	private String name;
	private double defaultRanking;
	private HashSet<Categorie> categories = new HashSet<Categorie>();
	private String Description;
	private boolean PossibleSurprizeMovie;
	private String pictureReference;
	/**
	 * the movie length in minutes
	 */
	private int length;

	
	public Movie() {
		// TODO Auto-generated constructor stub
	}

	public int getMovieID() {
		return movieID;
	}

	public void setMovieID(int movieID) {
		this.movieID = movieID;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public boolean isPossibleSurprizeMovie() {
		return PossibleSurprizeMovie;
	}

	public void setPossibleSurprizeMovie(boolean possibleSurprizeMovie) {
		PossibleSurprizeMovie = possibleSurprizeMovie;
	}

	public String getPictureReference() {
		return pictureReference;
	}

	public void setPictureReference(String pictureReference) {
		this.pictureReference = pictureReference;
	}
	
	public Movie(String name) {
		super();
		this.name = name;
		defaultRanking = 5;
	}

	
	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	

	public double getOverallRanking() {
		return defaultRanking;
	}

	public void setdefaultRanking(double defaultRanking) {
		this.defaultRanking = defaultRanking;
	}
	
	public void addCategorie(Categorie cat){
		categories.add(cat);
	}
	
	public void removeCategorie(Categorie cat){
		categories.remove(cat);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	
}
