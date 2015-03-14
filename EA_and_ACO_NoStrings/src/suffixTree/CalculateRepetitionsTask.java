package suffixTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import suffixTree.SuffixTree.Node;
import utils.IntegerArrayWrapper;

public class CalculateRepetitionsTask extends RecursiveTask<HashMap<IntegerArrayWrapper, RepetitionInformation>> {

    private Node node;
    private int minStringDepth;
    private HashMap<IntegerArrayWrapper, RepetitionInformation> repetitions = new HashMap<>();
    private int[] text;
    private IRR_States state;

    CalculateRepetitionsTask(Node node, int minStringDepth, int[] text, IRR_States state) {
        super();
        this.node = node;
        this.minStringDepth = minStringDepth;
        this.text = text;
        this.state=state;
    }

    @Override
    protected HashMap<IntegerArrayWrapper, RepetitionInformation> compute() {

        List<RecursiveTask<HashMap<IntegerArrayWrapper, RepetitionInformation>>> forks = new LinkedList<>();
        ArrayList<Node> children = node.getChildren();

            for (Node child : children) {
                CalculateRepetitionsTask task = new CalculateRepetitionsTask(child, minStringDepth, text, state);
                forks.add(task);
                //task.fork();
            }
            invokeAll(forks);
            for (RecursiveTask<HashMap<IntegerArrayWrapper, RepetitionInformation>> task : forks) {
                HashMap<IntegerArrayWrapper, RepetitionInformation> childReps = task.join();
                repetitions.putAll(childReps);
            }
        


        // for each internal node w of string-depth q ≥ L do
        int nodeStringDepth = node.getStringDepth();
        if (nodeStringDepth < minStringDepth || node.isLeaf() || node.isRoot) {
            return repetitions;
        }

        // for each pair of children vf and vg of w do
        for (int i = 0; i < children.size() - 1; i++) {
            Node child1 = children.get(i);
            for (int j = i + 1; j < children.size(); j++) {
                Node child2 = children.get(j);

                // for each letter c ∈ Σ with A(vf , c) != ∅ do (where A is the
                // annotation)
                for (int letter_child1 : child1.annotation.keySet()) {
                    // for each i ∈ A(vf , c) do
                    for (Integer value_child1 : child1.annotation.get(letter_child1)) {

                        // for each letter d ∈ Σ with d != c and A(vg , d) != ∅
                        // do
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
                                        text, value_child1 - 1,
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

        return repetitions;
    }

    private boolean isValidRepetition(int[] repetition, int positionInText) {

        // if the repetition contains an nonTerminal it must be completely in
        // the repetition
        // only accept "*N_firstInteger_secondInteger*"
        int lastIndex = repetition.length - 1;
        int secondLastIndex = repetition.length - 2;

        if (positionInText != 1 && text[positionInText - 2] == AnnotatedTree.nonTerminal) {
            return false;
        }
        if (text[positionInText - 1] == AnnotatedTree.nonTerminal
                || (positionInText - 2 >= 0 && text[positionInText - 2] == AnnotatedTree.nonTerminal)
                || repetition[lastIndex] == AnnotatedTree.nonTerminal
                || repetition[secondLastIndex] == AnnotatedTree.nonTerminal) {
            return false;
        }

        // the repetition must not contain only the nonTerminal
        if (repetition[0] == AnnotatedTree.nonTerminal && repetition.length == 3) {
            return false;
        }

        // the repetition must not contain an delimiter
        for (int b : repetition) {
            if (b == AnnotatedTree.delimiter) {
                return false;
            }
        }

        // the repetition must not split an escape character and the character
        // which is escaped
        // ex: \N must stay together
        if (text[positionInText - 1] == AnnotatedTree.escape
                && (repetition[0] == AnnotatedTree.delimiter || repetition[0] == AnnotatedTree.escape || repetition[0] == AnnotatedTree.nonTerminal)) {
            return false;
        }

        return true;
    }
}
