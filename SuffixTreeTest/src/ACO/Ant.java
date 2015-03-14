package ACO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import suffixTree.AnnotatedTree;
import suffixTree.RepetitionInformation;

public class Ant implements Callable<Integer> {

	private double[] probability;
	// private HashMap<String, RepetitionInformation> repetitions;
	// private double[] pheromones;
	private RepetitionInformation[] allRepetitions;
	ArrayList<String> productions;
	private int nonTerminalCounter;
	int oldLength, newLength;
	RepetitionInformation repetition;

	public Ant() {
		newLength = Integer.MAX_VALUE;
	}

	public Ant(ArrayList<String> productions, RepetitionInformation[] allRepetitions, int nonTerminalCounter, int oldLength) {
		super();
		// this.repetitions = repetitions;
		// this.pheromones = pheromones;
		this.productions = productions;
		this.nonTerminalCounter = nonTerminalCounter;
		this.allRepetitions = allRepetitions;
		this.oldLength = oldLength;
	}

	@Override
	// TODO find better Interface than Callable
	public Integer call() throws Exception {
		// int i = 1;
		// while (i < visited.length) {
		// // TODO gets one line
		// int newNodeIndex = pickNextNodeIndex(currentNode);
		// if (newNodeIndex == 1 && notVisited.length == 1) {
		// System.err.println("bla");
		// }
		//
		// int newNode = notVisited[newNodeIndex];
		//
		// distance += getWeight(currentNode, newNode);
		// visited[i] = newNode;
		// removeFromNotVisited(newNodeIndex);
		// currentNode = newNode;
		// i++;
		// }
		// // return to the starting point
		// distance += getWeight(currentNode, startNode);
		// //localSearch();
		// // visited[visited.length] = startNode;
		// return null;
		repetition = pickNextRepetition();

		for (int i = 0; i < productions.size(); i++) {
			productions.set(i, productions.get(i).replace(repetition.repetition, AnnotatedTree.nonTerminal + ((char) (nonTerminalCounter))));
		}

		
		newLength = productionsLength(productions);
		return 0;

	}

	private RepetitionInformation pickNextRepetition() {
		// found here: http://code.activestate.com/recipes/117241-windexpy/
		// see here too:
		// http://www.electricmonk.nl/log/2009/12/23/weighted-random-distribution/
		/*
		 * wtotal = sum([x[1] for x in lst]) n = random.uniform(0, wtotal) for
		 * item, weight in lst: if n &lt; weight: break n = n - weight return
		 * item
		 */

		int allRepetitionsSize = allRepetitions.length;

		probability = new double[allRepetitionsSize];
		double sumHeuristic = getSumHeuristic();
		double sumProbability = 0;
		for (int i = 0; i < allRepetitionsSize; i++) {
			probability[i] /= sumHeuristic;
			sumProbability += probability[i];
		}

		double randomNumber = ThreadLocalRandom.current().nextDouble(sumProbability);
		int i = 0;
		while (i < allRepetitionsSize) {
			if (randomNumber < probability[i]) {
				break;
			}
			randomNumber -= probability[i];
			i++;
		}

		return allRepetitions[i];
	}

	private double getSumHeuristic() {
		double sum = 0;
		for (int i = 0; i < allRepetitions.length; i++) {
			double prob = calculateProbability(allRepetitions[i]);
			sum += prob;
			probability[i] = prob;
		}
		return sum;
	}

	private double calculateProbability(RepetitionInformation rep) {
		if(rep.score<=0){
			return 0;
		} else{
			double n = rep.score; // 1/score of repetition
			return rep.pheromone * n * n * n; //hardcoded alpha and beta
		}
		
		
	}

	public double getPheromoneValue() {
		return (oldLength - (float) newLength) / (oldLength + newLength);
	}

	private static int productionsLength(ArrayList<String> productions) {
		int length = 0;
		for (String s : productions) {
			length += s.length();
		}
		return length;
	}

	public int getLength() {
		return newLength;
	}

}
