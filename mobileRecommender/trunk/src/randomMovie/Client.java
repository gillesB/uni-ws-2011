package randomMovie;

import java.util.ArrayList;

import clientGUI.ClientGUIInterface;

public class Client {
	
	private RecommenderSystem recommender;
	private ClientGUIInterface guiInterface;
	private ArrayList<Movie> currentMovieList;
	
	private Customer clientOwner;

	public Client(RecommenderSystem recommender, Customer clientOwner) {
		super();
		this.recommender = recommender;
		guiInterface = new ClientGUIInterface(this);
		this.clientOwner = clientOwner;
		getCurrentMovies();
		guiInterface.setUsername(clientOwner.getName());
	}
	
	private void getCurrentMovies(){
		currentMovieList = recommender.getCurrentMovies();
		guiInterface.addCurrentMovies(currentMovieList);
	}

	public void buySelectedTicket(int selectedIndex) {
		recommender.buyTicket(clientOwner,currentMovieList.get(selectedIndex));
		
	}
	
	

}
