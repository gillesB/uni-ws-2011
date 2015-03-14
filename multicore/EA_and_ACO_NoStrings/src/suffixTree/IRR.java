package suffixTree;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import evolutionaryAlgorithm.ExtendedArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import utils.BoyerMoore;

public class IRR {

    private IRR_States state;

    public enum FileSize {

        SMALL_FILE, LARGE_FILE, HUGE_FILE
    };
    public AtomicBoolean finished = new AtomicBoolean(false);
    ExtendedArrayList<int[]> productions = new ExtendedArrayList<>();
    Thread compressThread;
    FileSize filesize;

    public IRR(IRR_States state) {
        this.state = state;
    }

    public ArrayList<int[]> compress(int[] sequence)
            throws FileNotFoundException {

        if (sequence.length < 50_000) {
            filesize = FileSize.SMALL_FILE;
        } else if (sequence.length < 400_000) {
            filesize = FileSize.LARGE_FILE;
        } else {
            filesize = FileSize.HUGE_FILE;
        }

        productions.add(sequence);

        compressThread = new Thread() {

            @Override
            public void run() {
                AnnotatedTree tree;
                short counter = 1; // counter starts with 1 because the production rule
                // 0 is the start production

                int oldLength, newLength;

                oldLength = 0;
                newLength = productionsLength(productions);

                int minNodeStringDepth;
                switch (filesize) {
                    case SMALL_FILE:
                        minNodeStringDepth = 2;
                        break;
                    case LARGE_FILE:
                        minNodeStringDepth = 8;
                        break;
                    default:
                        minNodeStringDepth = 8;
                }

                int bestScore;
                int oldIsSmallerCounter = 3;

                do {
                    //concat all productions in an int[], to build a new tree
                    int totalLength = productions.get(0).length + 1;
                    for (int i = 1; i < productions.size(); i++) {
                        if (productions.get(i) != null) {
                            totalLength += productions.get(i).length + 1;
                        }
                    }

                    int[] allProds = new int[totalLength];
                    allProds[0] = AnnotatedTree.delimiter;
                    int offset = 1;
                    for (int i = 0; i < productions.size(); i++) {
                        int[] production = productions.get(i);
                        if (production != null) {
                            System.arraycopy(production, 0, allProds, offset, production.length);
                            offset += production.length;

                            if (i != productions.size() - 1) {
                                allProds[offset] = AnnotatedTree.delimiter;
                                offset++;
                            }
                        }
                    }
                    tree = new AnnotatedTree(allProds, minNodeStringDepth, state);

                    // use first production
                    RepetitionInformation[] repetitions = tree.getRepetitions().values().toArray(new RepetitionInformation[0]);

                    Arrays.sort(repetitions);
                    bestScore = repetitions[0].score;

                    if (repetitions.length < 2000 || bestScore < 25) {
                        minNodeStringDepth = minNodeStringDepth == 2 ? 2 : minNodeStringDepth - 1;
                    }

                    //System.out.println("Amount repetitions: " + repetitions.length + ", best score: " + bestScore);

                    oldLength = newLength;

                    int amountReplaceRepetitions;
                    switch (filesize) {
                        case SMALL_FILE:
                            amountReplaceRepetitions = 1;
                            break;
                        case LARGE_FILE:
                            amountReplaceRepetitions = repetitions.length >= 1000 ? repetitions.length / 200
                                    : Math.min(repetitions.length, 5);
                            break;
                        default:
                            amountReplaceRepetitions = repetitions.length >= 1000 ? repetitions.length / 200
                                    : Math.min(repetitions.length, 5);
                    }


                    for (int i = 0; i < amountReplaceRepetitions; i++) {

                        int[] toReplace = repetitions[i].repetition;

                        int[] replacement = new int[3];
                        replacement[0] = AnnotatedTree.nonTerminal;

                        //get the two bytes written after the Nonterminal Sign. (N_lowbyte_highbyte)
                        int[] counterArr = new int[2];
                        counterArr[0] = (counter & 0xff);
                        counterArr[1] = ((counter >> 8) & 0xff);

                        replacement[1] = counterArr[0];
                        replacement[2] = counterArr[1];

                        for (int j = 0; j < productions.size(); j++) {
                            int[] production = productions.get(j);
                            if (production != null) {
                                int[] replacedProduction = replaceInArray(production, toReplace, replacement, true);
                                productions.set(j, replacedProduction);
                            }

                        }

                        productions.addAt(counter, repetitions[i].repetition);

                        // get next valid counter
                        do {
                            counter++;
                            //get the two bytes written after the Nonterminal Sign. (N_lowbyte_highbyte)
                            counterArr[0] = (counter & 0xff);
                            counterArr[1] = ((counter >> 8) & 0xff);
                        } while (counterArr[0] == AnnotatedTree.delimiter
                                || counterArr[1] == AnnotatedTree.delimiter
                                || counterArr[0] == AnnotatedTree.escape
                                || counterArr[1] == AnnotatedTree.escape
                                || counterArr[0] == AnnotatedTree.nonTerminal
                                || counterArr[1] == AnnotatedTree.nonTerminal
                                || (counterArr[0] == 0xd && counterArr[1] == 0xa)
                                || counterArr[1] == 0xd);

                    }

                    newLength = productionsLength(productions);
                    //System.out.println(oldLength + " " + newLength);

                    if (oldLength < newLength) {
                        oldIsSmallerCounter--;
                    } else {
                        oldIsSmallerCounter = 3;
                    }

                } //while (minNodeStringDepth >= 2 && bestScore >= 2);
                while (minNodeStringDepth >= 2 && oldIsSmallerCounter > 0);
            }
        };
        compressThread.start();
        try {
            compressThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finished.set(true);


        return productions;


    }

    public ArrayList<int[]> stop() {
        compressThread.interrupt();
        return productions;
    }

    private int productionsLength(Collection<int[]> productions) {

        int grammarLength = 0;
        for (int[] production : productions) {

            if (production == null) {
                continue;
            }

            for (int i = 0; i < production.length; i++) {
                int c = production[i];

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
