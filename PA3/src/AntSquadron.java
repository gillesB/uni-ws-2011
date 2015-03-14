import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;


public class AntSquadron implements Callable<Ant> {
	
	private int nodeAmount,squadronSize;
	private int[][] weights;
	private double[][] trails;
	
	public AntSquadron(int nodeAmount, int squadronSize, int[][] weights, double[][] trails) {		
		super();
		this.nodeAmount = nodeAmount;
		this.squadronSize = squadronSize;
		this.weights = weights;
		this.trails = trails;
	}

	@Override
	public Ant call() throws Exception {
		Ant bestSquadronAnt = new Ant();
		Ant[] ants = new Ant[squadronSize];
		for(int i = 0; i < squadronSize; i++){			
			ants[i] = new Ant(ThreadLocalRandom.current().nextInt(nodeAmount), nodeAmount, weights, trails);					
			ants[i].walk();
		}
		for(Ant a : ants){
			if(a.getDistance() < bestSquadronAnt.getDistance()){
				bestSquadronAnt = a;
			}
		}		
		return bestSquadronAnt;
	}
	
	

}
