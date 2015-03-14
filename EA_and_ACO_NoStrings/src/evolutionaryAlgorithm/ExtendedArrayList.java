package evolutionaryAlgorithm;

import java.util.ArrayList;

public class ExtendedArrayList<E> extends ArrayList<E> {

	public E addAt(int index, E element) {
		for (int i = this.size(); i < index + 1; i++) {
			this.add(null);
		}
		return this.set(index, element);
	}

}
