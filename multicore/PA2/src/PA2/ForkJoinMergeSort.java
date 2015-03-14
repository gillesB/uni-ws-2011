package PA2;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
@SuppressWarnings("serial")
public class ForkJoinMergeSort extends RecursiveAction {
	private int[] list;
	private int start, stop;
	private int blockSize = 5000;

	public ForkJoinMergeSort(int[] list, int start, int stop) {
		this.list = list;
		this.start = start;
		this.stop = stop;
	}
	
	private void seqentialMergeSort(int[] list, int low, int high) {
		if (low < high) {
			int middle = (low + high) / 2;
			seqentialMergeSort(list, low, middle);
			seqentialMergeSort(list, middle+1, high);
			sequentialMerge(list, low, middle, high);
		}
	}

	private void sequentialMerge(int[] list, int low, int middle, int high) {
		int i, j, k;
		int[] temp = new int[middle+1-low];

		//copy the last half of the list to the temporary array
		System.arraycopy(list, low, temp, 0, middle+1-low);
		
		//copy the numbers back and reorder them, while copying
		i = 0;
		j= middle+1;
		k = low;
		while (k < j && j <= high)
			if (temp[i] <= list[j])
				list[k++] = temp[i++];
			else
				list[k++] = list[j++];
		
		//copy the remaining numbers to the list
		System.arraycopy(temp, i, list, k, j-k);
	}
	

	protected void compute() {
		if ((stop-start) < blockSize) {
			// Do work directly (sequential)
			seqentialMergeSort(list, start, stop);
		} else {
			int middle = (start+stop) / 2;
			ForkJoinMergeSort left = new ForkJoinMergeSort(list, start, middle);
			ForkJoinMergeSort right = new ForkJoinMergeSort(list, middle+1, stop);
			invokeAll(left, right);
			sequentialMerge(list, start, middle, stop);
		}
	}	
	
	public static void main(final String[] argv) {
		int p, n, printflag;

		p = Integer.parseInt(argv[0]);
		n = Integer.parseInt(argv[1]);
		printflag = Integer.parseInt(argv[2]);
		int[] arrayToSort = new int[n];
		
		ForkJoinPool pool = new ForkJoinPool();
		
		//fill the array in different threads
		int blocksize= 5000;
		int start = 0;
		int stop = blocksize;
		while(stop < n){
			new FillArray(arrayToSort, start, stop, p, n).compute();
			start += blocksize;
			stop += blocksize;
		}

		if (start < n){
			new FillArray(arrayToSort, start, n, p, n).compute();
		}
		
		ForkJoinMergeSort mergesort = new ForkJoinMergeSort(arrayToSort,0,arrayToSort.length-1);
		
		pool.invoke(mergesort);
		if (printflag == 1) {
			int j = 0;
			for (Integer i : arrayToSort) {
				System.out.println(j + " " + i + ' ' + (i - j++));
			}
		}
	}
}
