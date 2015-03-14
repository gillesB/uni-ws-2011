package utils;

public class NumberAndProduction {

	public int number;
	public IntegerArrayWrapper production;
        public boolean alreadyContained;

	public NumberAndProduction(int number, IntegerArrayWrapper production) {
		super();
		this.number = number;
		this.production = production;
                alreadyContained = false;
	}

}
