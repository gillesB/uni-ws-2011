import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//solves the Travel Service Problem with an Ant Colony Optimization
public class TSP_ACO {

	// both arrays are only half filled because we only consider symmetric TSP
	// e.g. distance[42][23] does not exist, fetch distance[23][42] instead
	int[][] weights;
	double[][] trails;
	int threadnumber, threshold, nodeAmount;
	String path;
	private double pheromoneMax, pheromoneMin;
	double trailPersistence;
	ExecutorService threadPool;
	// double average, probability_best, probability_dec;
	int globalBestAntFrequency = 25;

	Ant globalBestAnt = new Ant();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Thread.sleep(15000);
			new TSP_ACO(args[0], args[1], args[2]).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TSP_ACO(String threadnumber, String threshold, String path) {
		super();
		this.threadnumber = Integer.parseInt(threadnumber);
		this.threshold = Integer.parseInt(threshold);
		this.path = path;
		this.pheromoneMax = 100;
		this.pheromoneMin = 0.1;
		this.trailPersistence = 0.90;
		threadPool = Executors.newFixedThreadPool(this.threadnumber);
	}

	public void start() throws Exception {
		initTSP();

		int i = 0;
		do {
			constructSolution(i);
			i++;
		} while (globalBestAnt.getDistance() > threshold);

		threadPool.shutdown();

		java.io.File inFile = new java.io.File(path);
		String fileName = inFile.getName();
		int index = fileName.lastIndexOf('.');

		String outFile = "./" + fileName.substring(0, index);
		
		FileWriter fstream = new FileWriter(outFile);
		BufferedWriter out = new BufferedWriter(fstream);

		for (int j = 0; j < globalBestAnt.getVisited().length; j++) {
			out.write((globalBestAnt.getVisited()[j] + 1)+"\n");
		}
		out.write((globalBestAnt.getVisited()[0] + 1)+"\n");
		out.flush();
		
		out.close();
	}

	private void initTSP() throws Exception {
		loadGraph();
		initTrails();
	}

	private void loadGraph() throws Exception {
		// weights = new TSP_WeightCalculator(path).getWeights();
		// nodeAmount = weights.length;
		conveniantParser();
	}

	private void initTrails() {
		trails = new double[nodeAmount][nodeAmount];
		for (int i = 0; i < nodeAmount; i++) {
			for (int j = 0; j < nodeAmount; j++) {
				trails[i][j] = pheromoneMax;
			}
		}
	}

	private void constructSolution(int iterationCounter) throws Exception {
		Ant iterationBestAnt = new Ant();
		ArrayList<AntSquadron> squadrons = new ArrayList<AntSquadron>(threadnumber);
		int squadronSize;
		if (threadnumber <= 20) {
			squadronSize = 100 / threadnumber;
		} else {
			squadronSize = 5;

		}
		for (int i = 0; i < threadnumber; i++) {
			squadrons.add(new AntSquadron(nodeAmount, squadronSize, weights, trails));
		}
		List<Future<Ant>> futures = threadPool.invokeAll(squadrons);

		for (Future<Ant> f : futures) {
			Ant a = f.get();
			if (a.getDistance() < iterationBestAnt.getDistance()) {
				iterationBestAnt = a;
			}
		}

		//iterationBestAnt.localSearch();

		if (iterationBestAnt.getDistance() < globalBestAnt.getDistance()) {
			globalBestAnt = iterationBestAnt;
			setPheromoneMax_Min();
			System.out.println(iterationCounter + ": " + globalBestAnt.getDistance());
		}

		if (globalBestAntFrequency == 0) {
			updateTrails(globalBestAnt);
			if (iterationCounter < 75) {
				globalBestAntFrequency = 5;
			} else if (iterationCounter < 125) {
				globalBestAntFrequency = 3;
			} else if (iterationCounter < 250) {
				globalBestAntFrequency = 2;
			} else {
				globalBestAntFrequency = 1;
			}
		} else {
			updateTrails(iterationBestAnt);
			globalBestAntFrequency--;
		}
	}

	private void updateTrails(Ant ant) {
		double newPheromone = 1 / ant.getDistance();
		int[] visited = ant.getVisited();

		for (int i = 0; i < trails.length; i++) {
			for (int j = 0; j < trails.length; j++) {
				trails[i][j] = trailPersistence * trails[i][j];
			}
		}
		for (int i = 1; i < visited.length; i++) {
			addToTrail(visited[i - 1], visited[i], newPheromone);
		}

	}

	private void addToTrail(int nodeA, int nodeB, double amount) {
		if (trails[nodeA][nodeB] < pheromoneMin) {
			trails[nodeA][nodeB] = pheromoneMin;
			trails[nodeB][nodeA] = pheromoneMin;
		} else if (trails[nodeA][nodeB] > pheromoneMax) {
			trails[nodeA][nodeB] = pheromoneMax;
			trails[nodeB][nodeA] = pheromoneMax;
		} else {
			trails[nodeA][nodeB] += amount;
			trails[nodeB][nodeA] += amount;
		}
	}

	private void setPheromoneMax_Min() {
		pheromoneMax = (1. / (1 - trailPersistence)) * (1. / globalBestAnt.getDistance());
		// System.out.println((1./best.getDistance()));
	}

	private void conveniantParser() {
		BufferedReader source;
		try {
			source = new BufferedReader(new FileReader(path));
			String str = source.readLine();
			nodeAmount = Integer.parseInt(str);
			weights = new int[nodeAmount][nodeAmount];
			trails = new double[nodeAmount][nodeAmount];
			str = source.readLine();
			int i = 0;
			while (str != null) {
				String[] line = str.split(" ");
				for (int j = 0; j < line.length; j++) {
					weights[i][j] = Integer.parseInt(line[j]);
				}
				i++;
				str = source.readLine();
			}
			source.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
