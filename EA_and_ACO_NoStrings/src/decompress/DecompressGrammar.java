package decompress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import suffixTree.AnnotatedTree;
import utils.Utils;

public class DecompressGrammar {

    private static ArrayList<int[]> productions, decompressedProductions;

    public static void main(String[] args) throws IOException {
        int[] compressed = Utils.readFile("/dev/shm/grammar.out");
        // cut off the first delimiter sign
        compressed = Arrays.copyOfRange(compressed, 1, compressed.length);

        int lastProduction = getLastProduction(compressed);

        productions = new ArrayList<>();
        decompressedProductions = new ArrayList<>();
        growArrayLists(lastProduction);

        // split the compressed string
        int[] newProduction = new int[0];

        int productionNumber = 0;

        for (int i = 0; i < compressed.length; i++) {
            int c = compressed[i];

            if (c == AnnotatedTree.delimiter && compressed[i - 1] != AnnotatedTree.escape) {

                productions.set(productionNumber, newProduction);

                productionNumber = compressed[i + 2] * 256 + compressed[i + 1];

                // ignore the productionID
                i++;
                newProduction = new int[]{compressed[i]};
            } else {
                newProduction = Utils.concatAll(newProduction, new int[]{c});
            }
        }
        productions.add(productionNumber, newProduction);
        decompressedProductions.add(null);

        // replace the non terminals
        decompressProduction(0);

        Utils.writeFile("/dev/shm/decompressed.txt", productions.get(0));

    }

    private static int[] decompressProduction(int index) {
        int[] production = decompressedProductions.get(index);

        if (production != null) {
            return production;
        }

        production = productions.get(index);

        // cut off the first two ints of the reference to the nonterminal
        production = Arrays.copyOfRange(production, 2, production.length);

        int[] decompressedProduction = new int[0];
        int escCharCounter = 0;
        for (int j = 0; j < production.length; j++) {
            int c = production[j];
            if (c == AnnotatedTree.escape) {
                escCharCounter++;
                int c2 = production[j + 1];
                if (c2 == AnnotatedTree.escape || production[j + 2] == AnnotatedTree.nonTerminal) {
                    escCharCounter++;
                }

                decompressedProduction = Utils.concatAll(decompressedProduction, new int[]{c2});
                j++;
            } else if (escCharCounter % 2 == 0 && c == AnnotatedTree.nonTerminal) {
                escCharCounter = 0;
                int productionID = Utils.intsToShort(production[j + 1], production[j + 2]);
                j += 2;
                int productionIndex = ((int) productionID);
                decompressedProduction = Utils.concatAll(decompressedProduction, decompressProduction(productionIndex));
            } else {
                escCharCounter = 0;
                decompressedProduction = Utils.concatAll(decompressedProduction, new int[]{production[j]});
            }
        }
        productions.set(index, decompressedProduction);
        decompressedProductions.set(index, decompressedProduction);
        return productions.get(index);
    }

    private static int getLastProduction(int[] compressed) {
        for (int i = compressed.length - 1; i > 0; i--) {
            int c = compressed[i];
            if (c == AnnotatedTree.delimiter
                    && compressed[i - 1] != AnnotatedTree.escape) {
                return compressed[i + 2] * 256 + compressed[i + 1]; // highbyte*2^8+lowbyte
            }
        }
        return 0;
    }

    private static void growArrayLists(int lastProduction) {
        for (int i = 0; i < lastProduction + 1; i++) {
            productions.add(null);
            decompressedProductions.add(null);
        }

    }
}
