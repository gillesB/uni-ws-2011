package evolutionaryAlgorithm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import suffixTree.AnnotatedTree;
import suffixTree.IRR;
import suffixTree.IRR_States;
import utils.BoyerMoore;
import utils.Utils;

public class MasterIsland extends Island {

    private static String filenameIn;
    private static String filenameOut;
    private int threadnumber = 2;
    private IRR irrMC = new IRR(IRR_States.MC);
    private IRR irrML = new IRR(IRR_States.ML);
    private IRR irrMO = new IRR(IRR_States.MO);
    Island[] archipelago = new Island[threadnumber];
    /**
     * contains the productions of the IRR compressions
     * [0]: IRR-MC
     * [1]: IRR-ML
     * [2]: IRR-MO
     */
    ArrayList[] irrProductions = new ArrayList[3];

    public static void main(String[] args) throws Exception {
        filenameIn = args[0];

        java.io.File inFile = new java.io.File(filenameIn);
        String fileName = inFile.getName();
        if (fileName.contains(".")) {
            int index = fileName.indexOf('.');
            filenameOut = "./" + fileName.substring(0, index) + ".out";
        } else {
            filenameOut = "./" + fileName + ".out";
        }
        new MasterIsland().start();
    }

    public MasterIsland() {
        super();
        initCountdown();
        try {
            initSmallestGrammar();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        createFirstGeneration();

        for (int i = 0; i < threadnumber; i++) {
            archipelago[i] = new Island(initialSequence, irrProductions[(i + 1) % 3]);
            archipelago[i].start();
            System.out.println(archipelago[i] + " started");
        }
        bestIndividual = new Individual(productions);
        try {
            int iterationCounter = 0;
            while (true) {
                select();
                //performCrossover();
                try {
                    performMutation();
                } catch (Exception e) {
                    System.err.println(e.toString());//e.printStackTrace();
                    break;
                }

                for (Individual i : population) {
                    if (i.length < bestIndividual.length) {
                        bestIndividual = (Individual) i.clone();
                    }
                }
                System.out.println("MasterIsland: " + iterationCounter + ": " + bestIndividual.length);

                if (iterationCounter != 0 && iterationCounter % 50 == 0) {
                    performRemoveWorst();
                }

                iterationCounter++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initSmallestGrammar() throws Exception {
        loadSequence();
        initHeuritsics();
    }

    private void loadSequence() throws IOException {
        int[] sequence = Utils.readFile(filenameIn);
        //add the needed escape charcters to the sequence
        sequence = replaceInArray(sequence, new int[]{'\\'}, new int[]{'\\', '\\'}, false);
        sequence = replaceInArray(sequence, new int[]{'N'}, new int[]{'\\', 'N'}, false);
        sequence = replaceInArray(sequence, new int[]{'#'}, new int[]{'\\', '#'}, false);
        initialSequence = sequence;
    }

    private void initHeuritsics() throws FileNotFoundException {
        Thread mc = new Thread() {

            @Override
            public void run() {
                try {
                    irrProductions[0] = irrMC.compress(initialSequence);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        };
        Thread ml = new Thread() {

            @Override
            public void run() {
                try {
                    irrProductions[1] = irrML.compress(initialSequence);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        };
        Thread mo = new Thread() {

            @Override
            public void run() {
                try {
                    irrProductions[2] = irrMO.compress(initialSequence);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        };
        mc.start();
        ml.start();
        mo.start();
        try {
            mc.join();
            ml.join();
            mo.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        productions = irrProductions[0];
    }

    private void performRemoveWorst() {
        Individual bestIndividual = this.bestIndividual;
        Island islandWithWorst_BestIndividual = this;

        //threadnumber-1 as MasterIsland is not in the archipelago array
        for (int i = 0; i < threadnumber; i++) {
            if (bestIndividual.length > archipelago[i].bestIndividual.length) {
                bestIndividual = archipelago[i].bestIndividual;
            }
            if (islandWithWorst_BestIndividual.bestIndividual.length < archipelago[i].bestIndividual.length) {
                islandWithWorst_BestIndividual = archipelago[i];
            }
        }
        islandWithWorst_BestIndividual.addIndivdual(bestIndividual);
    }

    private void initCountdown() {
        new Thread() {

            @Override
            public void run() {
                try {
                    sleep(180000); //570000 ms = 9:30 min
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (irrMC.finished.get() == true) { //IRR finished
                    Island bestIsland = MasterIsland.this;
                    for (Island is : MasterIsland.this.archipelago) {
                        if (is.bestIndividual.length < bestIsland.bestIndividual.length) {
                            bestIsland = is;
                        }
                    }
                    try {
                        Utils.printGrammarToFile(bestIsland.bestIndividual.getProductions(), filenameOut);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                } else { //IRR did not finish
                    try {
                        Utils.printGrammarToFile(irrMC.stop(), filenameOut);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                System.exit(0);
            }
        }.start();
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
