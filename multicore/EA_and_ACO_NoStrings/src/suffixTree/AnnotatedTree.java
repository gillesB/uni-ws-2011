package suffixTree;

import utils.IntegerArrayWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;

public class AnnotatedTree extends SuffixTree {

    // replacements
    // for firstChar and lastChar see at class SuffixTree
    public static final int nonTerminal = 0x4E; // N
    public static final int delimiter = 0x23; // #
    public static final int escape = 0x5C; // \
    public static final int nonTerminalCounterStart = 200;
    private HashMap<IntegerArrayWrapper, RepetitionInformation> repetitions;
    //int occurence;
    //int[] bestRepetition;
    int minStringDepth;
    IRR_States state;

    public AnnotatedTree(int[] text, int minStringDepth) {
        super(text);
        annotadeNodes(super.root, 0);
        this.minStringDepth = minStringDepth;
    }

    public AnnotatedTree(int[] text, int minStringDepth, IRR_States state) {

        super(text);
        annotadeNodes(super.root, 0);
        this.minStringDepth = minStringDepth;
        this.state = state;
    }
    final ForkJoinPool forkJoinPool = new ForkJoinPool();

    private HashMap<IntegerArrayWrapper, RepetitionInformation> calculateRepetitionsInParallel(Node node, int minStringDepth) {
        HashMap<IntegerArrayWrapper, RepetitionInformation> ret = forkJoinPool.invoke(new CalculateRepetitionsTask(node, minStringDepth, super.text, state));
        forkJoinPool.shutdownNow();
        return ret;
    }

    // the algorithm to find the repetitions was found in the paper:
    // Bioinformatics I, WS’09-10, D. Huson p.203
    private void calculateRepetitions(Node node, int minStringDepth) {
        // for each internal node w of string-depth q ≥ L do
        ArrayList<Node> children = node.getChildren();
        for (Node child : children) {
            calculateRepetitions(child, minStringDepth);
        }
        int nodeStringDepth = node.getStringDepth();
        if (nodeStringDepth < minStringDepth || node.isLeaf() || node.isRoot) {
            return;
        }

        // for each pair of children vf and vg of w do
        for (int i = 0; i < children.size() - 1; i++) {
            Node child1 = children.get(i);
            for (int j = i + 1; j < children.size(); j++) {
                Node child2 = children.get(j);

                // for each letter c ∈ Σ with A(vf , c) != ∅ do (where A is the annotation)
                for (int letter_child1 : child1.annotation.keySet()) {
                    // for each i ∈ A(vf , c) do
                    for (Integer value_child1 : child1.annotation.get(letter_child1)) {

                        // for each letter d ∈ Σ with d != c and A(vg , d) != ∅ do
                        for (int letter_child2 : child2.annotation.keySet()) {
                            // if (letter_child1 != letter_child2) {

                            // for each j ∈ A(vg , d) do
                            for (Integer value_child2 : child2.annotation.get(letter_child2)) {

                                int child1_end = value_child1 + nodeStringDepth;
                                int child2_end = value_child2 + nodeStringDepth;

                                // check if the repetitions overlap

                                if ((value_child1 < value_child2 && value_child2 < child1_end)
                                        || (value_child1 > value_child2 && value_child1 < child2_end)) {
                                    continue;
                                }

                                int[] repetition = Arrays.copyOfRange(
                                        super.text, value_child1 - 1,
                                        (value_child1 + nodeStringDepth) - 1);

                                if (!isValidRepetition(repetition,
                                        value_child1 - 1)) {
                                    continue;
                                }

                                int repetitionLength = repetition.length;

                                IntegerArrayWrapper repetitionInWrapper = new IntegerArrayWrapper(
                                        repetition);
                                RepetitionInformation repetitionInformation = repetitions.get(repetitionInWrapper);
                                if (repetitionInformation != null) {

                                    // check if it is the first occurrence
                                    // of
                                    // the repetition on this positions
                                    BitSet bitset = repetitionInformation.alreadyOccuredOnPosition;
                                    if (bitset.get(value_child1)
                                            && bitset.get(value_child2)) {
                                        continue;
                                    }

                                    repetitionInformation.occurences++;

                                    repetitionInformation.score = repetitionInformation.occurences
                                            * repetitionLength
                                            - repetitionInformation.occurences
                                            - repetitionLength - 1;

                                    bitset.set(value_child1, true);
                                    bitset.set(value_child2, true);

                                } else {

                                    repetitionInformation = new RepetitionInformation(state);
                                    repetitionInformation.repetition = repetition;
                                    repetitionInformation.occurences = 2;
                                    repetitionInformation.score = 2
                                            * repetitionLength - 2
                                            - repetitionLength - 1;

                                    BitSet bitset = new BitSet(text.length);
                                    bitset.set(value_child1, true);
                                    bitset.set(value_child2, true);

                                    repetitions.put(repetitionInWrapper,
                                            repetitionInformation);

                                }
                            }
                            // }
                        }
                    }
                }
            }
        }
    }

    private boolean isValidRepetition(int[] repetition, int positionInText) {

        // if the repetition contains an nonTerminal it must be completely in
        // the repetition
        // only accept "*N_firstInteger_secondInteger*"
        int lastIndex = repetition.length - 1;
        int secondLastIndex = repetition.length - 2;

        if (positionInText != 1 && text[positionInText - 2] == nonTerminal) {
            return false;
        }
        if (text[positionInText - 1] == nonTerminal
                || (positionInText - 2 >= 0 && text[positionInText - 2] == nonTerminal)
                || repetition[lastIndex] == nonTerminal
                || repetition[secondLastIndex] == nonTerminal) {
            return false;
        }

        // the repetition must not contain only the nonTerminal
        if (repetition[0] == nonTerminal && repetition.length == 3) {
            return false;
        }

        // the repetition must not contain an delimiter
        for (int b : repetition) {
            if (b == delimiter) {
                return false;
            }
        }

        // the repetition must not split an escape character and the character
        // which is escaped
        // ex: \N must stay together
        if (text[positionInText - 1] == escape
                && (repetition[0] == delimiter || repetition[0] == escape || repetition[0] == nonTerminal)) {
            return false;
        }

        return true;
    }

    private void annotadeNodes(Node node, int suffixLenght) {
        if (!node.isRoot) {
            suffixLenght += node.getIncomingEdge().getSpan() + 1;
        }
        if (node.isLeaf() && node.nodeDepth > 1) {
            int lengthDifference = text.length - suffixLenght;
            // -1 for index is needed as text index starts with 0
            int leftChar = text[lengthDifference - 1];
            if (node.annotation.containsKey(leftChar)) {
                node.annotation.get(leftChar).add(lengthDifference + 1);
            } else {
                HashSet<Integer> set = new HashSet<Integer>();
                set.add(lengthDifference + 1);
                node.annotation.put(leftChar, set);
            }
        } else {
            // write all annotations of children
            ArrayList<Node> children = node.getChildren();
            for (Node child : children) {
                annotadeNodes(child, suffixLenght);
            }
            // write own annotation
            for (Node child : children) {
                for (Integer key : child.annotation.keySet()) {
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
    }

    public HashMap<IntegerArrayWrapper, RepetitionInformation> getRepetitions() {
        if (repetitions == null) {
            repetitions = calculateRepetitionsInParallel(root, minStringDepth);
        }
        return repetitions;
    }
}
