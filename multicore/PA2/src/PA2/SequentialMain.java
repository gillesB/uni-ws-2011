package PA2;

public class SequentialMain {

	static int p, n, printflag;

	public static void main(final String[] argv) {

		p = Integer.parseInt(argv[0]); // 1, -1 or large prime
		n = Integer.parseInt(argv[1]); // length of the random array
		printflag = Integer.parseInt(argv[2]); // 0: print nothing; 1: print to
		// STDOUT

		int[] testList = new int[n];

		for (int i = 1; i <= n; i++) {
			testList[i - 1] = i * p % n;
		}

		SequentialMain bla = new SequentialMain();
		testList = bla.seqentialMergeSort(testList, 0, n - 1);

		if (printflag == 1) {
			int j = 0;
			for (Integer i : testList) {
				System.out.println(j + " " + i + ' ' + (i - j++));
			}
		}
	}

	private int[] seqentialMergeSort(int[] list, int low, int high) {
		if (low < high) {
			int middle = (low + high) / 2;
			seqentialMergeSort(list, low, middle);
			seqentialMergeSort(list, middle+1, high);
			return sequentialMerge(list, low, middle, high);
		}
		return list;
	}

	private int[] sequentialMerge(int[] list, int low, int middle, int high) {
		int i, j, k;
		int[] temp = new int[high];

		i = 0;

		System.arraycopy(list, low, temp, 0, middle+1-low);
		
		i = 0;
		j=middle+1;
		k = low;

		while (k < j && j <= high)
			if (temp[i] <= list[j])
				list[k++] = temp[i++];
			else
				list[k++] = list[j++];


		while (k < j)
			list[k++] = temp[i++];

		return list;
	}

}