package suffixTree;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

public class AnnotatedTree extends SuffixTree {

	// replacements
	//for firstChar and lastChar see at class SuffixTree
	public static final String nonTerminal = String.valueOf((char) (198));
	public static final String delimiter = String.valueOf((char) (199));
	public static final int nonTerminalCounterStart = 200;

	private HashMap<String, RepetitionInformation> repetitions = new HashMap<String, RepetitionInformation>();

	int bestScore = -2;
	int occurence;
	String bestRepetition;

	public AnnotatedTree(String text, int minStringDepth) {

		super(text);
		//System.out.println("Tree built");

		annotadeNodes(super.root, 0);
		//System.out.println("Tree annotated");

		calculateRepetitions(super.root, minStringDepth);
		//System.out.println("got Repetitions: "+repetitions.size());
	}

	// (?s) means that a newline etc are treated as normal
	// character. newlines are normally not matched
	// by .
	private final String regex_isOnlyNonterminal = "(?s)" + nonTerminal + ".?.?";
	private final String regex_nonTerminalNotCompleteInRepetition = "(?s).*" + nonTerminal + ".?";
	private final char nonTerminalChar = nonTerminal.charAt(0);

	private void calculateRepetitions(Node node, int minStringDepth) {
		for (Node child : node.getChildren()) {
			calculateRepetitions(child, minStringDepth);
		}
		int nodeStringDepth = node.getStringDepth();
		if (nodeStringDepth < minStringDepth || node.isLeaf()) {
			return;
		}
		for (int i = 0; i < node.getChildren().size() - 1; i++) {
			Node child1 = node.getChildren().get(i);
			for (int j = i + 1; j < node.getChildren().size(); j++) {
				Node child2 = node.getChildren().get(j);

				for (String key_child1 : child1.annotation.keySet()) {
					for (Integer value_child1 : child1.annotation.get(key_child1)) {

						for (String key_child2 : child2.annotation.keySet()) {
							if (!key_child1.equals(key_child2)) {
							for (Integer value_child2 : child2.annotation.get(key_child2)) {

								int child1_end = value_child1 + nodeStringDepth;
								int child2_end = value_child2 + nodeStringDepth;

								// check if the repetitions overlap

								if ((value_child1 < value_child2 && value_child2 < child1_end)
								|| (value_child1 > value_child2 && value_child1 < child2_end)) {
									continue;
								}

								String repetition =
								super.text.substring(value_child1 - 1, (value_child1 + nodeStringDepth) - 1);

								// TODO verify line
								// super.text.charAt(value_child1 - 2) ==
								// Main3.nonTerminal.charAt(0)
								if (super.text.charAt(value_child1 - 2) == nonTerminalChar
								|| repetition.contains(delimiter)
								|| repetition.matches(regex_isOnlyNonterminal)
								|| repetition.matches(regex_nonTerminalNotCompleteInRepetition)) {
									continue;
								}
								int repetitionLength = repetition.length();

								RepetitionInformation repetitionInformation = repetitions.get(repetition);
								if (repetitionInformation != null) {

									// check if it is the first occurrence of
									// the repetition on this positions
									BitSet bitset = repetitionInformation.alreadyOccuredOnPosition;
									if (bitset.get(value_child1) && bitset.get(value_child2)) {
										continue;
									}

									repetitionInformation.occurences++;

									repetitionInformation.score =
									repetitionInformation.occurences * repetitionLength
									- repetitionInformation.occurences - repetitionLength - 1;

									bitset.set(value_child1, true);
									bitset.set(value_child2, true);

								} else {

									repetitionInformation = new RepetitionInformation();
									
									repetitionInformation.repetition = repetition;
									repetitionInformation.occurences = 2;
									repetitionInformation.score = 2 * repetitionLength - 2 - repetitionLength - 1;

									BitSet bitset = new BitSet(text.length());
									bitset.set(value_child1, true);
									bitset.set(value_child2, true);

									repetitions.put(repetition, repetitionInformation);

								}
								if (repetitionInformation.score > bestScore) {
									bestScore = repetitionInformation.score;
									// TODO clean this
									occurence = repetitionInformation.occurences;

									bestRepetition = repetition;
								}

								// System.out.println(repetition +
								// ", occurences: " +
								// repetitionInformation.occurences
								// + ", score: " + repetitionInformation.score);
								// System.out.println("((" + value_child1 + ","
								// + (value_child1 + node.getStringDepth() - 1)
								// + ")," + "(" + value_child2 + ","
								// + (value_child2 + node.getStringDepth() - 1)
								// + ")) " + " -> \""
								// + super.text.substring(value_child1 - 1,
								// (value_child1 + node.getStringDepth()) - 1)
								// + "\" | \""
								// + super.text.substring(value_child2 - 1,
								// (value_child2 + node.getStringDepth()) - 1)
								// + "\"\n");
							}
						}
						}
					}
				}
			}
		}
	}

	private void annotadeNodes(Node node, int suffixLenght) {
		if (!node.isRoot()) {
			suffixLenght += node.getIncomingEdge().getSpan() + 1;
		}
		if (node.isLeaf() && node.getNodeDepth() > 1) {
			int lengthDifference = text.length() - suffixLenght;
			// -1 for index is needed as text index starts with 0
			String leftChar = Character.toString(text.charAt(lengthDifference - 1));
			if (node.annotation.containsKey(leftChar)) {
				node.annotation.get(leftChar).add(lengthDifference + 1);
			} else {
				HashSet<Integer> set = new HashSet<Integer>();
				set.add(lengthDifference + 1);
				node.annotation.put(leftChar, set);
			}
		} else {
			// write all annotations of children
			for (Node child : node.getChildren()) {
				annotadeNodes(child, suffixLenght);
			}
			// write own annotation
			for (Node child : node.getChildren()) {
				for (String key : child.annotation.keySet()) {
					if (!node.annotation.containsKey(key)) {
						HashSet<Integer> set = new HashSet<Integer>();
						node.annotation.put(key, set);
					}
					// add values from child annotation to node annotation
					for (int value : child.annotation.get(key)) {
						node.annotation.get(key).add(value);
					}
				}
			}
		}
		// if (node.getIncomingEdge() != null)
		// System.out.println(node + ": " +
		// text.substring(node.getIncomingEdge().getBeginIndex(),
		// node.getIncomingEdge().getEndIndex()+1) + " " + node.annotation);
	}

	public HashMap<String, RepetitionInformation> getRepetitions() {
		return repetitions;
	}
	
	

}
