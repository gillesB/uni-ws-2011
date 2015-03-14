package ACO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import suffixTree.AnnotatedTree;
import suffixTree.IRR_MC;
import suffixTree.RepetitionInformation;

//solves the Smalest Grammar Problem with an Ant Colony Optimization
public class Smallest_Grammar_ACO {

	// replacements
	public static final String nonTerminal = String.valueOf((char) (198));
	public static final String delimiter = String.valueOf((char) (199));

	private ArrayList<String> productions;
	private String initialSequence;
	private HashMap<String, RepetitionInformation> repetitions;
	int productionsLength;
	private int nonTerminalCounter;
	
	private static String filenameIn;
	private static String filenameOut;


	int threadnumber = 16;
	private double pheromoneMax, pheromoneMin;
	double trailPersistence;
	ExecutorService threadPool;
	int globalBestAntFrequency = 25;
	//
	Ant globalBestAnt = new Ant();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		filenameIn = args[0];
		
		java.io.File inFile = new java.io.File(filenameIn);
		String fileName = inFile.getName();
		filenameOut = "./" + fileName+".out";
		
		
		new Smallest_Grammar_ACO().start();

	}

	public Smallest_Grammar_ACO() {
		super();
		this.pheromoneMax = 100;
		this.pheromoneMin = 0.1;
		this.trailPersistence = 0.90;
		threadPool = Executors.newFixedThreadPool(this.threadnumber);
	}

	public void start() throws Exception {
		initSmallestGrammar();

		int maxIterations = 10000;

		int i = 0;
		nonTerminalCounter = AnnotatedTree.nonTerminalCounterStart + productions.size();
		do {
			constructSolution(i);
			i++;
		} while (i < maxIterations);

		threadPool.shutdown();
		IRR_MC.printGrammarToFile(productions, filenameOut);
	}

	private void initSmallestGrammar() throws Exception {
		loadSequence();
		initHeuritsic();
		productionsLength = productionsLength(productions);
		getNewRepetitions();
		initPheromone();
	}

	private void getNewRepetitions() {
		String text = "";
		for (String p : productions) {
			text += p;
		}
		repetitions = new AnnotatedTree(text,2).getRepetitions();
		initPheromone();
	}

	private void initHeuritsic() throws FileNotFoundException {
		// use heuristic or not
		if (true) {
			productions = IRR_MC.compress(initialSequence);
		} else {
			productions = new ArrayList<String>();
			productions.add(initialSequence);
		}
	}

	private void loadSequence() throws IOException {
		initialSequence = readFile(filenameIn);
	}

	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	private void initPheromone() {
		for (RepetitionInformation rep : repetitions.values()) {
			rep.pheromone = pheromoneMax;
		}
	}

	private void constructSolution(int iterationCounter) throws Exception {
		Ant iterationBestAnt = new Ant();
		ArrayList<Ant> ants = new ArrayList<Ant>();

		for (int i = 0; i < threadnumber; i++) {
			ants.add(new Ant((ArrayList<String>) productions.clone(), repetitions.values().toArray(
			new RepetitionInformation[0]), nonTerminalCounter, productionsLength));
		}
		threadPool.invokeAll(ants);

		for (int i = 0; i < threadnumber; i++) {
			Ant a = ants.get(i);
			if (a.getLength() < iterationBestAnt.getLength()) {
				iterationBestAnt = a;
			}
		}

		// iterationBestAnt.localSearch();

		if (iterationBestAnt.getLength() < globalBestAnt.getLength()) {
			globalBestAnt = iterationBestAnt;
			productions = globalBestAnt.productions;
			productions.add(globalBestAnt.repetition.repetition);
			productionsLength = globalBestAnt.newLength;
			nonTerminalCounter++;
			setPheromoneMax_Min();
			System.out.println(nonTerminalCounter + ": " + globalBestAnt.getLength() + " (Iteration: "
			+ iterationCounter + ")");
			getNewRepetitions();
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
		if (iterationCounter % 100 == 0) {
			initPheromone();
		}

	}

	private void updateTrails(Ant ant) {

		double newPheromone = ant.getPheromoneValue();
		String usedRepetition = ant.repetition.repetition;

		// pheromone evaporates
		for (RepetitionInformation rep : repetitions.values()) {
			rep.pheromone *= trailPersistence;
			if (rep.pheromone < pheromoneMin) {
				rep.pheromone = pheromoneMin;
			}
		}

		// add pheromone to used repetitions
		RepetitionInformation rep = repetitions.get(usedRepetition);
		if (rep == null) {
			System.err.println(usedRepetition + "can not be found in repetitions anymore.");
			return;
		}
		rep.pheromone += newPheromone;
		if (rep.pheromone > pheromoneMax) {
			rep.pheromone = pheromoneMax;
		}

	}

	private void setPheromoneMax_Min() {
		pheromoneMax = (1. / (1 - trailPersistence)) * (1. / globalBestAnt.getLength());
		// System.out.println((1./best.getDistance()));
	}

	private int productionsLength(ArrayList<String> productions) {
		int length = 0;
		for (String s : productions) {
			length += s.length();
		}
		return length;

	}

}
