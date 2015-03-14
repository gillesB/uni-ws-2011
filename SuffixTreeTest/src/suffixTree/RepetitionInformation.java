package suffixTree;

import java.util.BitSet;

public class RepetitionInformation implements Comparable<RepetitionInformation> {
	
	//TODO try somehow to remove this one
	public String repetition;
	
	public int occurences = 0;
	public Integer score = 0;
	public double pheromone = 0;
	public BitSet alreadyOccuredOnPosition = new BitSet();
	
	
	@Override
	public int compareTo(RepetitionInformation arg0) {
		return this.score.compareTo(arg0.score) * -1;
	}
}
