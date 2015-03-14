package clientGUI;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import randomMovie.Client;
import randomMovie.Movie;

public class ClientGUIInterface {
	
	private ClientGUI gui;
	private Client client;

	public ClientGUIInterface(Client client) {
		super();
		gui = new ClientGUI(this);
		gui.setVisible(true);
		this.client = client; 
	}

	public void addCurrentMovies(ArrayList<Movie> currentMovieList) {
		DefaultListModel model = (DefaultListModel) gui.lstCurrentMovies.getModel();
		for(Movie m : currentMovieList){
			model.addElement(m.getName());
		}		
	}

	public void buySelectedTicket(int selectedIndex) {
		client.buySelectedTicket(selectedIndex);		
	}

	public void setUsername(String name) {
		gui.edtUsername.setText(name);
		
	}
	
	

}
