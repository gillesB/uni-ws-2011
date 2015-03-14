import java.util.concurrent.ThreadLocalRandom;

public class Ant {

	private int[] notVisited, visited;
	private double[] probability;
	private int[][] weights;
	private double[][] trails;
	private int distance;
	private int startNode;

	public Ant() {
		distance = Integer.MAX_VALUE;
	}

	public Ant(int startNode, int nodeAmount, int[][] weights, double[][] trails) {
		super();
		this.startNode = startNode;
		// visited must contain nodeAmount+1 beacuse the starting point will be
		// added as endpoint too
		visited = new int[nodeAmount];
		visited[0] = startNode;
		notVisited = new int[nodeAmount - 1];
		// TODO ugly
		int cityIndex = 0;
		for (int i = 0; i < nodeAmount; i++) {
			if (i != startNode) {
				notVisited[cityIndex] = i;
				cityIndex++;
			}
		}
		this.weights = weights;
		this.trails = trails;
	}

	public void walk() {
		int currentNode = startNode;
		int i = 1;
		while (i < visited.length) {
			// TODO gets one line
			int newNodeIndex = pickNextNodeIndex(currentNode);
			if (newNodeIndex == 1 && notVisited.length == 1) {
				System.err.println("bla");
			}

			int newNode = notVisited[newNodeIndex];

			distance += weights[currentNode][newNode];
			visited[i] = newNode;
			removeFromNotVisited(newNodeIndex);
			currentNode = newNode;
			i++;
		}
		// return to the starting point
		distance += weights[currentNode][startNode];
		// localSearch();
		// visited[visited.length] = startNode;
	}

	public void localSearch() {
		// 2-opt
		int i = 0;
		int veryOldDistance = distance;

		int oldDistance, newDistance;
		// System.out.println(distance);
		int[] newTour = new int[visited.length];
		oldDistance = distance;
		System.arraycopy(visited, 0, newTour, 0, visited.length);
		do {
			make_2opt_move(newTour);

			newDistance = 0;
			for (int j = 1; j < visited.length; j++) {
				newDistance += weights[j - 1][j];
			}
			if (newDistance < oldDistance || newDistance == oldDistance) {
				break;
			}

		} while (true);
		if ((distance - veryOldDistance) < 0) {
			System.arraycopy(newTour, 0, visited, 0, visited.length);
			distance = newDistance;
			System.out.println(distance + " - " + veryOldDistance + ": " + (distance - veryOldDistance));
		}

	}

	private void make_2opt_move(int[] newTour) {

		for (int a = 0; a < newTour.length - 2; a++) {
			for (int c = 0; c < newTour.length - 2; c++) {
				if (a == c || a + 1 == c) {
					continue;
				}
				if (weights[a][a + 1] > weights[a][c] && weights[c][c + 1] > weights[a + 1][c + 1]) {
					if (weights[a + 1][a + 2] > weights[c][a + 2] && weights[c - 1][c] > weights[c - 1][a + 1]) {
						System.out.println(weights[a][a + 1] + " " + weights[a][c] + " " + weights[c][c + 1] + " " + weights[a + 1][c + 1]);
						System.out.println(weights[a + 1][a + 2] + " " + weights[c][a + 2] + " " + weights[c - 1][c] + " " + weights[c - 1][a + 1]);
						int temp = newTour[a + 1];
						newTour[a + 1] = newTour[c];
						newTour[c] = temp;
					}
				}
			}
		}
	}

	private int pickNextNodeIndex(int currentNode) {
		// found here: http://code.activestate.com/recipes/117241-windexpy/
		// see here too:
		// http://www.electricmonk.nl/log/2009/12/23/weighted-random-distribution/
		/*
		 * wtotal = sum([x[1] for x in lst]) n = random.uniform(0, wtotal) for
		 * item, weight in lst: if n &lt; weight: break n = n - weight return
		 * item
		 */
		if (notVisited.length == 1) {
			return 0;
		}

		probability = new double[notVisited.length];
		double sumHeuristic = getSumHeuristic(currentNode);
		double sumProbability = 0;
		for (int i = 0; i < notVisited.length; i++) {
			probability[i] /= sumHeuristic;
			sumProbability += probability[i];
		}

		double randomNumber = ThreadLocalRandom.current().nextDouble(sumProbability);
		int i = 0;
		while (i < notVisited.length) {
			if (randomNumber < probability[i]) {
				break;
			}
			randomNumber -= probability[i];
			i++;
		}
		// TODO remove this one
		if (notVisited.length == i) {
			System.err.println("notVisited.length == i: should not happen");
			i--;
		}

		return i;
		// return ThreadLocalRandom.current().nextInt(notVisited.length);
	}

	private double getSumHeuristic(int currentNode) {
		double sum = 0;
		for (int i = 0; i < notVisited.length; i++) {
			double prob = calculateProbability(currentNode, notVisited[i]);
			sum += prob;
			probability[i] = prob;
		}
		return sum;
	}

	private double calculateProbability(int currentNode, int secondNode) {
		double n = 1. / weights[currentNode][secondNode]; // 1/distance
		return trails[currentNode][secondNode] * n * n * n;
	}

	private void removeFromNotVisited(int i) {
		int[] temp = new int[notVisited.length - 1];
		System.arraycopy(notVisited, 0, temp, 0, i);
		System.arraycopy(notVisited, i + 1, temp, i, notVisited.length - i - 1);
		notVisited = temp;
	}

	public int getDistance() {
		return distance;
	}

	public int[] getVisited() {
		return visited;
	}

}
