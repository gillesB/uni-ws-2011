package randomMovie;

import java.util.HashSet;

public class Movie {
	
	private String name;
	private double overallRanking;
	private HashSet<Categorie> categories = new HashSet<Categorie>();
	/**
	 * the movie length in minutes
	 */
	private int length;
	
	public Movie(String name) {
		super();
		this.name = name;
		overallRanking = 5;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public double getOverallRanking() {
		return overallRanking;
	}

	public void setOverallRanking(double overallRanking) {
		this.overallRanking = overallRanking;
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
