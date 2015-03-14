package evolutionaryAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import suffixTree.AnnotatedTree;
import suffixTree.RepetitionInformation;

public class Individual implements Comparable<Individual> {
	private ListSet<String> productions, productionsWithoutNonTerminals;
	private HashMap<String, RepetitionInformation> allRepetitions;
	private RepetitionInformation[] repetitions;
	private String nonTerminal = AnnotatedTree.nonTerminal;
	Integer length;

	public Individual(Collection<String> productions) {
		this.productions = new ListSet<String>(productions);
		productionsWithoutNonTerminals = new ListSet<String>();
		for (String s : productions) {
			if (!s.contains(nonTerminal)) {
				productionsWithoutNonTerminals.add(s);
			}
		}
		length = productionsLength(this.productions);
	}

	public void mutate() throws Exception {
		// String productionToReplace = pickProduction();
		// removeProduction(productionToReplace);
		getNewRepetitions();
		RepetitionInformation randRepetition = pickNextRepetition();
		
		performProduction(randRepetition.repetition);
		length = productionsLength(productions);
	}

	@Override
	public int compareTo(Individual arg0) {
		return this.length.compareTo(arg0.length);
	}

	private void getNewRepetitions() {
		String text = "";
		for (String p : productions) {
			text += AnnotatedTree.delimiter + p;
		}
		allRepetitions = new AnnotatedTree(text, 2).getRepetitions();
		repetitions = allRepetitions.values().toArray(new RepetitionInformation[0]);
	}

	private RepetitionInformation pickNextRepetition() throws Exception {
		Arrays.sort(repetitions);

		int allRepetitionsSize = repetitions.length > 10 ? 10 : repetitions.length;
		if(allRepetitionsSize <= 0){
			throw new Exception("no new Repetitions found");
		}
		int randomNumber = ThreadLocalRandom.current().nextInt(allRepetitionsSize);
		return repetitions[randomNumber];
	}

	private String pickProduction() {
		// pick a random production with no Nonterminals
		int randomNumber = ThreadLocalRandom.current().nextInt(productionsWithoutNonTerminals.size());
		return productionsWithoutNonTerminals.get(randomNumber);
	}

	// TODO discuss with Paul
	public void performProduction(String production) {
		addProduction(production, true);
	}

	public void addProduction(String production, boolean performProduction) {
		if (productions.contains(production)) {
			return;
		}

		if (performProduction) {
			int lastProduction = productions.size();

			for (int i = 0; i < lastProduction; i++) {
				if (!productions.get(i).equals(production)) {
					productions.set(i, productions.get(i).replace(production,
					nonTerminal + ((char) (200 + lastProduction))));
				}
			}
		}
		productions.add(production);
		if (!production.contains(nonTerminal)) {
			productionsWithoutNonTerminals.add(production);
		}

	}

	public void removeProduction(String productionToRemove) {
		int index = productions.indexOf(productionToRemove);
		int productionNumber = 200 + index;

		// if((char) productionNumber == 'æ'){
		// System.out.println("bla");
		// }

		// productions.remove(index);
		// productionsWithoutNonTerminals.remove(productionToRemove);

		for (int i = 0; i < productions.size(); i++) {
			productions.set(i, productions.get(i).replace(nonTerminal + (char) productionNumber, productionToRemove));
		}

		// productions.set(index, "");
	}

	private int productionsLength(ListSet<String> productions) {
		int length = 0;
		for (String s : productions) {
			length += s.length();
		}
		return length;
	}

	public ListSet<String> getProductions() {
		return productions;
	}

	public ListSet<String> getProductionsWithoutNonTerminals() {
		return productionsWithoutNonTerminals;
	}

}
