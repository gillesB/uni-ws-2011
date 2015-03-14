package evolutionaryAlgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import suffixTree.AnnotatedTree;
import suffixTree.IRR_MC;
import suffixTree.RepetitionInformation;


public class Smallest_Grammar_EA {
	
	// replacements
	public static final String nonTerminal = String.valueOf((char) (198));
	public static final String delimiter = String.valueOf((char) (199));
	
	private String initialSequence;
	private ArrayList<String> productions;
	private ArrayList<Individual> population;
	
	private static String filenameIn;
	private static String filenameOut;
		
	
	public static void main(String[] args) throws Exception{
		filenameIn = args[0];
		
		java.io.File inFile = new java.io.File(filenameIn);
		String fileName = inFile.getName();
		filenameOut = "./" + fileName+".out";
		
		new Smallest_Grammar_EA().start();
	}
	
	public void start() throws Exception {
		initSmallestGrammar();
		
		int iterationCounter = 0;
		
		while(iterationCounter < 500){
			select();
			performCrossover();
			try {
				performMutation();
			} catch (Exception e) {
				
				e.printStackTrace();
				break;
			}
			
			int bestLength = Integer.MAX_VALUE;
			for(Individual i : population){
				if(i.length < bestLength){
					bestLength = i.length;
				}
			}
			System.out.println(iterationCounter+": "+bestLength);
			
			iterationCounter++;
		}
		
		Individual bestIndividual = population.get(0);
		for(Individual i : population){
			if(i.length < bestIndividual.length){
				bestIndividual = i;
			}
		}
		IRR_MC.printGrammarToFile(bestIndividual.getProductions().getArrayList(),filenameOut);
		
		
	}
	
	private void performMutation() throws Exception {
		for(int i = 0; i < 20; i++){
			int randomIndex = ThreadLocalRandom.current().nextInt(population.size());
			population.get(randomIndex).mutate();
		}		
	}

	private void performCrossover() {
		int amountParents = population.size();
		for(int i = 0; i < amountParents/2; i++){
			//pick randomly two individuals as parents
			int p1 = ThreadLocalRandom.current().nextInt(amountParents);
			int p2 = ThreadLocalRandom.current().nextInt(amountParents);			
			Individual parent1 = population.get(p1);
			Individual parent2 = population.get(p2);
				
			Individual child = crossover(parent1,parent2);
			population.add(child);
		}		
	}

	private Individual crossover(Individual parent1, Individual parent2) {

		Individual mainParent, secondParent;
		if(parent1.length < parent2.length){
			mainParent =  parent1;
			secondParent = parent2;
		} else{
			mainParent = parent2;
			secondParent = parent1;
		}
		Individual child = new Individual(mainParent.getProductions());
		
		ListSet<String> child_prodsNo_NonTerminals = child.getProductionsWithoutNonTerminals();
		
		int amountRemoveProds = (int) Math.ceil(child_prodsNo_NonTerminals.size()/2);
		for(int i = 0; i < amountRemoveProds; i++){
			int index = ThreadLocalRandom.current().nextInt(child_prodsNo_NonTerminals.size());
			String productionToRemove = child_prodsNo_NonTerminals.get(index);
			child.removeProduction(productionToRemove);
		}
		
		ListSet<String> secondParent_prodsNo_NonTerminals = secondParent.getProductionsWithoutNonTerminals();		
		int amountAddProds = (int) Math.ceil(secondParent_prodsNo_NonTerminals.size()/2);
		for(int i = 0; i < amountAddProds; i++){
			int index = ThreadLocalRandom.current().nextInt(secondParent_prodsNo_NonTerminals.size());
			String productionToAdd = secondParent_prodsNo_NonTerminals.get(index);
			child.addProduction(productionToAdd, true);
		}
	
		return child;
	}

	private void initSmallestGrammar() throws Exception {
		loadSequence();
		initHeuritsic();
		createFirstGeneration();
	}
	
	private void createFirstGeneration() {
		population = new ArrayList<Individual>();
		RepetitionInformation[] repetitions = getNewRepetitions().values().toArray(new RepetitionInformation[0]);
		for(int i = 0; i < 100; i++){
			Individual indi = new Individual(productions);
			
			int randomIndex = ThreadLocalRandom.current().nextInt(repetitions.length);
			indi.performProduction(repetitions[randomIndex].repetition);			
			
			population.add(indi);
		}
	}
	
	private void select(){
		Collections.sort(population);
		population = new ArrayList<Individual>(population.subList(0, 40));		
	}

	private void loadSequence() throws IOException {
		String sequence = readFile("/dev/shm/grammar.lsp");
		initialSequence = sequence;
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
	
	private static int productionsLength(ArrayList<String> productions) {
		int length = 0;
		for (String s : productions) {
			length += s.length();
		}
		return length;
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
	
	private HashMap<String, RepetitionInformation> getNewRepetitions() {
		String text = "";
		for (String p : productions) {
			text += p;
		}
		return new AnnotatedTree(text, 2).getRepetitions();
	}

}
