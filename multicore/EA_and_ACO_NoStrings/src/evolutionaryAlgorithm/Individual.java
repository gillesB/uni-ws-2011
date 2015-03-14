package evolutionaryAlgorithm;

import utils.NumberAndProduction;
import utils.ListSet;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import suffixTree.AnnotatedTree;
import suffixTree.RepetitionInformation;
import java.util.List;
import utils.BoyerMoore;
import utils.IntegerArrayWrapper;

public class Individual implements Comparable<Individual> {

    private ListSet<IntegerArrayWrapper> productions;
    private HashMap<IntegerArrayWrapper, RepetitionInformation> allRepetitions;
    private RepetitionInformation[] repetitions;
    // private String nonTerminal = AnnotatedTree.nonTerminal;
    Integer length;
    private int needToFetchNewRepetitions = 0;

    public Individual(List<int[]> productions) {
        this.productions = new ListSet<>(productions.size());
        this.productions.addAt(productions.size() - 1, new IntegerArrayWrapper(new int[]{-1}));

        for (int i = 0; i < productions.size(); i++) {
            int[] prod = productions.get(i);
            if (prod != null) {
                this.productions.set(i, new IntegerArrayWrapper(prod.clone()));
            }
        }

        length = productionsLength(this.productions);
    }

    private Individual() {
    }

    public void mutate() throws Exception {

        if (needToFetchNewRepetitions == 0) {
            getNewRepetitions();
            needToFetchNewRepetitions = 3 + ThreadLocalRandom.current().nextInt(5);
        } else {
            needToFetchNewRepetitions--;
        }

        RepetitionInformation randRepetition = pickNextRepetition();
        //addProduction(new IntegerArrayWrapper(randRepetition.repetition));

    }

    private void getNewRepetitions() {
        RepetitionThread thread = new RepetitionThread();
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    static int treecount = 0;

    private class RepetitionThread extends Thread {

        @Override
        public void run() {
            //concat all productions in an int[], to build a new tree
            int totalLength = productions.get(0).data.length + 1;
            for (int i = 1; i < productions.size(); i++) {
                if (productions.get(i) != null) {
                    totalLength += productions.get(i).data.length + 1;
                }
            }
            int[] text = new int[totalLength];
            text[0] = AnnotatedTree.delimiter;
            int offset = 1;
            for (int i = 0; i < productions.size(); i++) {
                IntegerArrayWrapper production = productions.get(i);
                if (production != null) {
                    System.arraycopy(production.data, 0, text, offset, production.data.length);
                    offset += production.data.length;

                    if (i != productions.size() - 1) {
                        text[offset] = AnnotatedTree.delimiter;
                        offset++;
                    }
                }
            }


            allRepetitions = new AnnotatedTree(text, 2).getRepetitions();
            repetitions = allRepetitions.values().toArray(
                    new RepetitionInformation[0]);
        }
    }

    private RepetitionInformation pickNextRepetition() throws Exception {

        int allRepetitionsSize = repetitions.length > 10 ? 10 : repetitions.length;

        if (allRepetitionsSize <= 0) {
            throw new Exception("no new Repetitions found");

        }
        int randomNumber = ThreadLocalRandom.current().nextInt(allRepetitionsSize);
        RepetitionInformation ret = repetitions[randomNumber];
        //remove element with index randomNumber from array repetitions
        System.arraycopy(repetitions, randomNumber + 1, repetitions, randomNumber, repetitions.length - 1 - randomNumber);

        return ret;
    }

    public int addProduction(int[] production) {
        return addProduction(new IntegerArrayWrapper(production));
    }

    public int addProduction(IntegerArrayWrapper production) {
        int lastProduction = getProductionNumber();
        int[] replacement = new int[3];
        replacement[0] = AnnotatedTree.nonTerminal;

        //get the two bytes written after the Nonterminal Sign. (N_lowbyte_highbyte)
        int[] counterArr = new int[2];
        counterArr[0] = (lastProduction & 0xff);
        counterArr[1] = ((lastProduction >> 8) & 0xff);

        replacement[1] = counterArr[0];
        replacement[2] = counterArr[1];

        for (int i = 0; i < productions.size(); i++) {
            IntegerArrayWrapper cmpProduction = productions.get(i);
            if (cmpProduction != null) {
                IntegerArrayWrapper replacedProduction = replaceInArray(cmpProduction, production.data, replacement, true);
                productions.set(i, replacedProduction);
            }
        }
        productions.addAt(lastProduction, production);
        length = productionsLength(productions);

        return lastProduction;
    }

    private int getProductionNumber() {
        int counter = productions.size();
        //get the two bytes written after the Nonterminal Sign. (N_lowbyte_highbyte)
        int[] counterArr = new int[2];
        counterArr[0] = (counter & 0xff);
        counterArr[1] = ((counter >> 8) & 0xff);

        //the bytes behind the Nonterminal must not be...
        while (counterArr[0] == AnnotatedTree.delimiter
                || counterArr[1] == AnnotatedTree.delimiter
                || counterArr[0] == AnnotatedTree.escape
                || counterArr[1] == AnnotatedTree.escape
                || counterArr[0] == AnnotatedTree.nonTerminal
                || counterArr[1] == AnnotatedTree.nonTerminal
                || (counterArr[0] == 0xd && counterArr[1] == 0xa)
                || counterArr[1] == 0xd) {
            counter++;
            counterArr[0] = (counter & 0xff);
            counterArr[1] = ((counter >> 8) & 0xff);
        }
        return counter;

    }

    public void removeProduction(int index) {

        int[] decompressedProduction = decompressProductionNoWriteBack(index);

        int[] replacement = new int[3];
        replacement[0] = AnnotatedTree.nonTerminal;

        //get the two bytes written after the Nonterminal Sign. (N_lowbyte_highbyte)
        int[] counterArr = new int[2];
        counterArr[0] = (index & 0xff);
        counterArr[1] = ((index >> 8) & 0xff);
        replacement[1] = counterArr[0];
        replacement[2] = counterArr[1];

        for (int i = 0; i
                < productions.size(); i++) {

            IntegerArrayWrapper production = productions.get(i);

            if (production != null) {
                IntegerArrayWrapper newProduction = replaceInArray(production, replacement, decompressedProduction, true);
                productions.set(i, newProduction);

            }
        }

        productions.set(index, null);

    }

    private int[] decompressProductionNoWriteBack(int index) {

        IntegerArrayWrapper production = productions.get(index);

        if (production == null) {
            return null;
        }

        IntegerArrayWrapper decompressedProduction = new IntegerArrayWrapper(new int[0]);

        for (int j = 0; j < production.data.length; j++) {
            int c = production.data[j];

            if (c == AnnotatedTree.escape) {
                decompressedProduction = concat(decompressedProduction, new int[]{AnnotatedTree.escape, production.data[j + 1]});
                j++;
            } else if (c == AnnotatedTree.nonTerminal) {

                //get the production ID from the two bytes followed by the nonterminal. N_lowbyte_highbyte
                int productionID = production.data[j + 1] + (production.data[j + 2] << 8);

                j += 2;

                decompressedProduction = concat(decompressedProduction, decompressProductionNoWriteBack(productionID));
            } else {
                decompressedProduction = concat(decompressedProduction, new int[]{production.data[j]});
            }
        }

        return decompressedProduction.data;


    }

    private IntegerArrayWrapper concat(IntegerArrayWrapper A, int[] B) {
        int[] C = new int[A.data.length + B.length];
        System.arraycopy(A.data, 0, C, 0, A.data.length);
        System.arraycopy(B, 0, C, A.data.length, B.length);

        return new IntegerArrayWrapper(C);
    }

    public ListSet<IntegerArrayWrapper> getProductions() {
        return productions;
    }

    @Override
    public int compareTo(Individual arg0) {
        return this.length.compareTo(arg0.length);
    }

    public ProductionHierarchy getHierarchyOfProduction(int index) {
        int[] production;
        int productionID;
        ProductionHierarchy hierarchy = new ProductionHierarchy();

        production = productions.get(index).data.clone();
        hierarchy.add(new NumberAndProduction(index, new IntegerArrayWrapper(production)));
        for (int j = 0; j < production.length; j++) {
            int c = production[j];

            if (c == AnnotatedTree.escape) {
                j++;
            } else if (c == AnnotatedTree.nonTerminal) {
                //get the production ID from the two bytes followed by the nonterminal. N_lowbyte_highbyte
                productionID = production[j + 1] + (production[j + 2] << 8);
                j += 2;
                ProductionHierarchy subHierarchy = getHierarchyOfProduction(productionID);

                for (NumberAndProduction n : subHierarchy) {
                    hierarchy.add(n);
                }
            }
        }
        return hierarchy;
    }

    public void addProductionHierarchy(ProductionHierarchy hierarchy) {
        for (int i = hierarchy.size() - 1; i > -1; i--) {
            IntegerArrayWrapper production = hierarchy.get(i).production;

            if (productions.contains(production)) {
                int index = productions.indexOf(production);
                hierarchy.get(i).alreadyContained = true;
                hierarchy.replaceIndex(hierarchy.get(i).number, index);
            } else {
                int addedOnIndex = this.addProduction(production.data.clone());
                hierarchy.replaceIndex(hierarchy.get(i).number, addedOnIndex);
            }
        }
    }

    public void removeProductionHierarchy(ProductionHierarchy hierarchy) {
        for (int i = hierarchy.size() - 1; i > -1; i--) {
            NumberAndProduction numberProduction = hierarchy.get(i);
            if (!numberProduction.alreadyContained) {
                this.removeProduction(hierarchy.get(i).number);
            }
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Individual clone = new Individual();
        clone.productions = new ListSet<>(this.productions.size());
        for (IntegerArrayWrapper i : this.productions) {
            if (i != null) {
                clone.productions.add(new IntegerArrayWrapper(i.data.clone()));
            }
        }
        clone.length = this.length;
        return clone;
    }

    private int productionsLength(ListSet<IntegerArrayWrapper> productions) {

        int grammarLength = 0;
        for (IntegerArrayWrapper production : productions) {

            if (production == null) {
                continue;
            }

            for (int i = 0; i < production.data.length; i++) {
                int c = production.data[i];

                if (c == AnnotatedTree.escape) {
                    i++;
                    grammarLength++;
                } else if (c == AnnotatedTree.nonTerminal) {
                    i += 2;
                    grammarLength++;
                } else {
                    grammarLength++;
                }
            }
        }
        grammarLength += productions.size();

        return grammarLength;
    }

    private IntegerArrayWrapper replaceInArray(IntegerArrayWrapper arr, int[] replace, int[] replacement, boolean replaceWithNonterminals) {
        return new IntegerArrayWrapper(replaceInArray(arr.data, replace, replacement, replaceWithNonterminals));
    }

    private int[] replaceInArray(int[] arr, int[] replace, int[] replacement, boolean replaceWithNonterminals) {

        if (arr.length < replace.length) {
            return arr;
        }

        List<Integer> occurences = new BoyerMoore().match(replace, arr);
        if (occurences.size() == 0) {
            return arr;
        }

        int[] newArray = new int[arr.length + occurences.size()
                * replacement.length];
        int indexOriginal = 0;
        int indexNew = 0;

        // remove overlapping occurences
        for (int i = 0; i < occurences.size() - 1; i++) {
            if (occurences.get(i + 1) - occurences.get(i) < replace.length) {
                occurences.remove(i + 1);
                i--;
            }
        }

        for (int positionOfReplacement : occurences) {

            if (replaceWithNonterminals) {
                if (positionOfReplacement >= 3) {
                    if ((arr[positionOfReplacement - 1] == AnnotatedTree.nonTerminal && arr[positionOfReplacement - 2] != AnnotatedTree.escape)
                            || (arr[positionOfReplacement - 2] == AnnotatedTree.nonTerminal && arr[positionOfReplacement - 3] != AnnotatedTree.escape)) {
                        continue;
                    }
                } else if (positionOfReplacement == 2) {
                    if ((arr[positionOfReplacement - 1] == AnnotatedTree.nonTerminal && arr[positionOfReplacement - 2] != AnnotatedTree.escape)
                            || (arr[positionOfReplacement - 2] == AnnotatedTree.nonTerminal)) {
                        continue;
                    }
                } else if (positionOfReplacement == 1) {
                    if (arr[positionOfReplacement - 1] == AnnotatedTree.nonTerminal) {
                        continue;
                    }
                }
            }

            int amountOfIntsToCopy = positionOfReplacement - indexOriginal;
            System.arraycopy(arr, indexOriginal, newArray, indexNew,
                    amountOfIntsToCopy);
            indexNew += amountOfIntsToCopy;
            indexOriginal += amountOfIntsToCopy;
            System.arraycopy(replacement, 0, newArray, indexNew,
                    replacement.length);
            indexNew += replacement.length;
            indexOriginal += replace.length;

        }

        int actualSizeNewArray = indexNew + arr.length - indexOriginal;
        System.arraycopy(arr, indexOriginal, newArray, indexNew, arr.length
                - indexOriginal);

        // find actual length of newArray
        int[] ret = new int[actualSizeNewArray];
        System.arraycopy(newArray, 0, ret, 0, actualSizeNewArray);

        return ret;

    }
}
