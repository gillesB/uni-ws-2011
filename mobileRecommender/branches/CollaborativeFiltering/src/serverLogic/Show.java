package serverLogic;

import transmit.ShowShared;

public class Show {
	
	private Integer showID;
	private Movie movie;
	private Float price;
	private String hallNr;
	private long time;
	
	public Show(int databaseID, Movie movie, float price, String hallNr, long time) {
		super();
		this.showID = databaseID;
		this.movie = movie;
		this.price = price;
		this.hallNr = hallNr;
		this.time = time;
	}

	public ShowShared getShowToTransmit(){
		ShowShared ret = new ShowShared();
		
		ret.showID = showID;
		ret.movieName = movie.getName();
		ret.price = price.toString();
		ret.hallNr = hallNr;
		ret.time = time;
		
		return ret;
	}
	
	

}
