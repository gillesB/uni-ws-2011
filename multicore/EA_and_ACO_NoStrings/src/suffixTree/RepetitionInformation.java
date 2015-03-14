package suffixTree;

import java.util.BitSet;

public class RepetitionInformation implements Comparable<RepetitionInformation> {

    public int[] repetition;
    public int occurences = 0;
    public Integer score = 0;
    public BitSet alreadyOccuredOnPosition = new BitSet();
    private IRR_States state;

    public RepetitionInformation(IRR_States state) {
        this.state = state;
    }
    
    @Override
    public int compareTo(RepetitionInformation arg0) {
        // -1 to invert the sorting order
        switch (state) {
            case MC:
                return this.score.compareTo(arg0.score) * -1;
            case ML:
                return ((Integer) this.repetition.length).compareTo((Integer) arg0.repetition.length) - 1;
            case MO:
                return ((Integer) this.occurences).compareTo((Integer) arg0.occurences) - 1;
            default:    //default return MC
                return this.score.compareTo(arg0.score) * -1;
        }
    }
}
