package suffixTree;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IRR_MC {

	public static ArrayList<String> compress(String sequence) throws FileNotFoundException {
		AnnotatedTree tree;
		ArrayList<String> productions = new ArrayList<String>();
		productions.add(sequence);

		short counter = AnnotatedTree.nonTerminalCounterStart + 1;

		int oldLength, newLength;

		oldLength = 0;
		newLength = Integer.MAX_VALUE;
		
		int minNodeStringDepth = 8;
		
		do {

			// TODO proof what is better: make a suffix-tree for the first
			// production or for all productions
			String allProds = "";
			for (String prod : productions) {
				allProds += prod;
				break;
			}
			tree = new AnnotatedTree(allProds, minNodeStringDepth);

			// String bestRepetition = tree.bestRepetition;
			// if (bestRepetition != null && tree.bestScore > 1) {
			// oldLength = newLength;
			//
			// for (int i = 0; i < productions.size(); i++) {
			// productions.set(i, productions.get(i).replace(bestRepetition,
			// AnnotatedTree.nonTerminal + ((char) (counter))));
			// }
			//
			// newLength = productionsLength(productions);
			// productions.add(bestRepetition);
			// } else {
			// break;
			// }

			RepetitionInformation[] repetitions = tree.getRepetitions().values().toArray(new RepetitionInformation[0]);
			Arrays.sort(repetitions);

			if (repetitions.length == 0 || tree.bestScore < 1) {
				break;
			}

			if(repetitions.length < 2000){
				minNodeStringDepth = minNodeStringDepth == 2 ? 2 : minNodeStringDepth-1;
			}
			
			
			oldLength = newLength;
			int amountReplaceRepetitions = repetitions.length / 100;
			for (int i = 0; i < amountReplaceRepetitions; i++) {
				for (int j = 0; j < productions.size(); j++) {

					String toReplace = repetitions[i].repetition;

					String replacedProduction =
					productions.get(j).replace(toReplace, AnnotatedTree.nonTerminal + ((char) (counter)));

					productions.set(j, replacedProduction);
					
//					System.out.println("Replaced repetition: \"" + toReplace + "\": " + repetitions[i].score
//					+ " " + repetitions[i].occurences + " (N" + counter + ")");
				}
				counter++;
				productions.add(repetitions[i].repetition);
			}
			System.out.println(productions.size());
			//counter -= amountReplaceRepetitions; 
			newLength = productionsLength(productions);
//			for (int i = 0; i < amountReplaceRepetitions; i++) {
//				productions.add(repetitions[i].repetition);
//				System.out.println("Replaced repetition: \"" + repetitions[i].repetition + "\": " + tree.bestScore
//				+ " " + tree.occurence + " (N" + counter + ")");
//
//				System.out.println("Replaces: ");
//				System.out.println(repetitions[i].repetition);
//				System.out.println("with");
//				System.out.println(AnnotatedTree.nonTerminal + ((char) (counter)) + " = " + " (N" + counter + ")");
//
//				counter++;
//			}

			System.out.println(oldLength + " " + newLength);
		} while (oldLength > newLength);

		return productions;
	}

	private static int productionsLength(ArrayList<String> productions) {
		int length = 0;
		for (String s : productions) {
			length += s.length();
		}
		return length;
	}

	public static void printGrammarToFile(ArrayList<String> productions, String filename) throws FileNotFoundException {

		String outputString = "";
		int grammarLength = 0;
		short nonTerminalCounter = AnnotatedTree.nonTerminalCounterStart;

		for (String production : productions) {

			outputString += "#" + (char) nonTerminalCounter;
			nonTerminalCounter++;

			for (int i = 0; i < production.length(); i++) {
				char c = production.charAt(i);
				if (c == AnnotatedTree.nonTerminal.charAt(0)) {
					outputString += "N" + production.charAt(i + 1);
					i++;
					grammarLength++;
				} else if (SuffixTree.isFirstChar(c)) {
					continue;
				} else if (SuffixTree.isLastChar(c)) {
					continue;
				} else if (c == AnnotatedTree.delimiter.charAt(0)) {
					outputString += "#" + ((char) (nonTerminalCounter));
					nonTerminalCounter++;
				} else if (c == 'N') {
					outputString += "\\N";
					grammarLength++;
				} else if (c == '#') {
					outputString += "\\#";
					grammarLength++;
				} else if (c == '\\') {
					outputString += "\\\\";
					grammarLength++;
				} else {
					outputString += c;
					grammarLength++;
				}
			}
		}
		grammarLength += productions.size();

		System.out.println("Grammar length: " + grammarLength);
		PrintWriter out = new PrintWriter(filename);
		out.print(outputString);
		out.close();

	}

}
