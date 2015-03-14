package evolutionaryAlgorithm;

import utils.NumberAndProduction;
import java.util.ArrayList;
import java.util.List;

import suffixTree.AnnotatedTree;
import utils.BoyerMoore;

@SuppressWarnings("serial")
public class ProductionHierarchy extends ArrayList<NumberAndProduction> {

    public void replaceIndex(int oldIndex, int newIndex) {
        int[] oldIndexArray = shortToIntegerArray((short) oldIndex);
        int[] newIndexArray = shortToIntegerArray((short) newIndex);

        int[] searchPattern = new int[]{AnnotatedTree.nonTerminal, oldIndexArray[0], oldIndexArray[1]};


        for (NumberAndProduction n : this) {
            if (n.number == oldIndex) {
                n.number = newIndex;
            }


            List<Integer> occurences = new BoyerMoore().match(searchPattern, n.production.data);

            for(int i : occurences){
                if(i == 0 || n.production.data[i - 1] != AnnotatedTree.escape){
                    n.production.data[i + 1] = newIndexArray[0];
                    n.production.data[i + 2] = newIndexArray[1];
                }
            }

        }
    }

    public String toString() {
        String ret = "";
        for (NumberAndProduction n : this) {
            ret += n.number + " | ";
            for (int i : n.production.data) {
                ret += i + " ";
            }
            ret += "\n";
        }


        return ret;
    }

   private int[] shortToIntegerArray(short s) {
        int[] ret = new int[2];
        ret[0] = (int) (s & 0xff);
        ret[1] = (int) ((s >> 8) & 0xff);

        return ret;
    }
}
