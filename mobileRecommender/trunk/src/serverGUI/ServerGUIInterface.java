package serverGUI;

import javax.swing.DefaultListModel;

import randomMovie.*;

public class ServerGUIInterface {
	
	private ServerGUI gui;
	private RecommenderSystem server;

	public ServerGUIInterface(RecommenderSystem server) {
		super();
		gui = new ServerGUI(this);
		gui.setVisible(true);
		this.server = server; 
	}
	
	public void addVisitor(Customer visitor){
		DefaultListModel model = (DefaultListModel) gui.lstVisitors.getModel();
		model.addElement(visitor.getName());
	}
	
	public void getRecommendation(){
		gui.txtRecommendation.setText(server.getRecommendationForRandomMovieShow().toString());
	}
	
	

}
