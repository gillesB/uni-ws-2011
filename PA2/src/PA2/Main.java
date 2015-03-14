package PA2;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

	// static ExecutorService service;
	static ThreadPoolExecutor service;
	static int p, n, printflag;

	public static void main(final String[] argv) {

		p = Integer.parseInt(argv[0]);
		n = Integer.parseInt(argv[1]);
		printflag = Integer.parseInt(argv[2]);

		// service = Executors.newCachedThreadPool();
		// int amountThreads = 128;

		// TODO *****************************************
		int recursiveDepth = Integer.parseInt(argv[3]);

		service = (ThreadPoolExecutor) Executors.newCachedThreadPool();

		int[] testList = new int[n];
		for (int i = 1; i <= n; i++) {
			testList[i-1] = (i * p % n);
		}

		try {
			testList = service.submit(new MergeSortIntArr(testList, n, recursiveDepth)).get();
		} catch (final InterruptedException ex) {
			ex.printStackTrace();
		} catch (final ExecutionException ex) {
			ex.printStackTrace();
		}

		if (printflag == 1) {
			int j = 0;

			for (Integer i : testList) {
				System.out.println(j + " " + i + ' ' + (i - j++));
			}
		}

		System.out.println(service.getTaskCount());
		service.shutdownNow();

	}
}

class MergeSort implements Callable<LinkedList<Integer>> {

	LinkedList<Integer> list;
	int depth;

	public MergeSort(LinkedList<Integer> list, int listSize, int depth) {
		super();
		this.list = list;
		// this.listSize = listSize;
	}

	public LinkedList<Integer> call() {
		if (list.size() <= 1) {
			return list;
		}
		if (depth <= 0) {
			return seqentialMergeSort(list);
		}

		int middle = list.size() / 2;
		LinkedList<Integer> left = new LinkedList<Integer>(list.subList(0, middle));
		LinkedList<Integer> right = new LinkedList<Integer>(list.subList(middle, list.size()));

		try {
			left = Main.service.submit(new MergeSort(left, middle, depth - 1)).get();
			right = Main.service.submit(new MergeSort(right, middle, depth - 1)).get();
			list = Main.service.submit(new ParallelMerge(left, right, depth - 1)).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	private static LinkedList<Integer> seqentialMergeSort(LinkedList<Integer> list) {
		if (list.size() <= 1) {
			return list;
		}
		int middle = list.size() / 2;
		LinkedList<Integer> left = new LinkedList<Integer>(list.subList(0, middle));
		LinkedList<Integer> right = new LinkedList<Integer>(list.subList(middle, list.size()));
		left = seqentialMergeSort(left);
		right = seqentialMergeSort(right);
		list = ParallelMerge.sequentialMerge(left, right);
		return list;

	}
}

class MergeSortIntArr implements Callable<int[]> {

	public int[] list;
	private int low, high;

	public MergeSortIntArr(int[] list, int low, int high) {
		super();
		this.list = list;
		this.low = low;
		this.high = high;
	}

	public int[] call() {
		if (list.length <= 1 || low == high) {
			return list;
		}
		// if (depth <= 0){
		// return seqentialMergeSort(list);
		// }

		int middle = (low + high) / 2;

		try {
			Main.service.submit(new MergeSortIntArr(list, low, middle)).get();
			Main.service.submit(new MergeSortIntArr(list, middle, high)).get();
			Main.service.submit(new ParallelMergeIntArr(list, low, middle, high)).get();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

	private static LinkedList<Integer> seqentialMergeSort(LinkedList<Integer> list) {
		if (list.size() <= 1) {
			return list;
		}
		int middle = list.size() / 2;
		LinkedList<Integer> left = new LinkedList<Integer>(list.subList(0, middle));
		LinkedList<Integer> right = new LinkedList<Integer>(list.subList(middle, list.size()));
		left = seqentialMergeSort(left);
		right = seqentialMergeSort(right);
		list = ParallelMerge.sequentialMerge(left, right);
		return list;

	}
}

/*
 * class Merge implements Callable<LinkedList<Integer>> {
 * 
 * LinkedList<Integer> left, right;
 * 
 * public Merge(LinkedList<Integer> left, LinkedList<Integer> right) { super();
 * this.left = left; this.right = right; }
 * 
 * public LinkedList<Integer> call() { LinkedList<Integer> newList = new
 * LinkedList<Integer>(); while (!left.isEmpty() && !right.isEmpty()) { if
 * (left.getFirst() <= right.getFirst()) { newList.add(left.pop()); } else {
 * newList.add(right.pop()); } } if (left.size() > 0) newList.addAll(left); else
 * newList.addAll(right);
 * 
 * return newList; }
 * 
 * 
 * }
 */

class ParallelMerge implements Callable<LinkedList<Integer>> {

	LinkedList<Integer> left, right;
	int depth;

	public ParallelMerge(LinkedList<Integer> left, LinkedList<Integer> right, int depth) {
		super();
		this.left = left;
		this.right = right;
		this.depth = depth;
	}

	public LinkedList<Integer> call() {
		if (depth <= 0 || left.size() <= 10 || right.size() <= 10) {
			// System.out.println(Main.service.getActiveCount());
			return sequentialMerge(left, right);

		}

		if (left.size() > right.size()) {
			try {
				return Main.service.submit(new ParallelMerge(right, left, depth - 1)).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		LinkedList<Integer> newList = new LinkedList<Integer>();

		int middleLeft = left.size() / 2;
		int middleRight = binarySearch(left.get(middleLeft), right);

		LinkedList<Integer> leftLeft = new LinkedList<Integer>(left.subList(0, middleLeft));
		LinkedList<Integer> leftRight = new LinkedList<Integer>(left.subList(middleLeft, left.size()));
		LinkedList<Integer> rightLeft = new LinkedList<Integer>(right.subList(0, middleRight));
		LinkedList<Integer> rightRight = new LinkedList<Integer>(right.subList(middleRight, right.size()));

		try {
			LinkedList<Integer> mergedPart1 = Main.service.submit(new ParallelMerge(leftLeft, rightLeft, depth - 1)).get();
			LinkedList<Integer> mergedPart2 = Main.service.submit(new ParallelMerge(leftRight, rightRight, depth - 1)).get();
			newList.addAll(mergedPart1);
			newList.addAll(mergedPart2);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newList;
	}

	public static LinkedList<Integer> sequentialMerge(LinkedList<Integer> left, LinkedList<Integer> right) {
		LinkedList<Integer> newList = new LinkedList<Integer>();
		while (!left.isEmpty() && !right.isEmpty()) {
			if (left.getFirst() <= right.getFirst()) {
				newList.add(left.pop());
			} else {
				newList.add(right.pop());
			}
		}
		if (left.size() > 0)
			newList.addAll(left);
		else
			newList.addAll(right);

		return newList;
	}

	private int binarySearch(int search, LinkedList<Integer> list) {
		int min = 0;
		int max = list.size();
		int mid = (min + max) / 2;
		while (list.get(mid) != search && min < max) {
			mid = (min + max) / 2;
			if (search >= list.get(mid)) {
				min = mid + 1;
			} else {
				max = mid - 1;
			}
		}
		if (min == list.size() || search <= list.get(min)) {
			return min;
		} else {
			return min + 1;
		}

	}

}

class ParallelMergeIntArr implements Callable<int[]> {

	private int[] list, auxiliaryArray;
	private int low, middle, high;

	public ParallelMergeIntArr(int[] list, int low, int middle, int high) {
		super();
		this.auxiliaryArray = new int[list.length];
		this.list = list;
		this.low = low;
		this.middle = middle;
		this.high = high;
	}

	public int[] call() {
		/*
		 * if (depth <= 0 || left.size() <= 10 || right.size() <= 10) {
		 * //System.out.println(Main.service.getActiveCount()); return
		 * sequentialMerge(left, right);
		 * 
		 * }
		 */
		int i = low, j = high, k = low;

		while (i <= middle)
			auxiliaryArray[k++] = list[i++];

		while (j > middle)
			auxiliaryArray[k++] = list[j--];

		i = low;
		j = high;
		k = low;

		while (i <= j)
			if (auxiliaryArray[i] <= auxiliaryArray[j])
				list[k++] = auxiliaryArray[i++];
			else
				list[k++] = auxiliaryArray[j--];
return list;
		/*
		 * if (left.size() > right.size()) { try { return
		 * Main.service.submit(new ParallelMerge(right, left, depth - 1)).get();
		 * } catch (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (ExecutionException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 * 
		 * LinkedList<Integer> newList = new LinkedList<Integer>();
		 * 
		 * int middleLeft = left.size() / 2; int middleRight =
		 * binarySearch(left.get(middleLeft), right);
		 * 
		 * LinkedList<Integer> leftLeft = new
		 * LinkedList<Integer>(left.subList(0, middleLeft)); LinkedList<Integer>
		 * leftRight = new LinkedList<Integer>(left.subList(middleLeft,
		 * left.size())); LinkedList<Integer> rightLeft = new
		 * LinkedList<Integer>(right.subList(0, middleRight));
		 * LinkedList<Integer> rightRight = new
		 * LinkedList<Integer>(right.subList(middleRight, right.size()));
		 * 
		 * try { LinkedList<Integer> mergedPart1 = Main.service.submit(new
		 * ParallelMerge(leftLeft, rightLeft, depth - 1)).get();
		 * LinkedList<Integer> mergedPart2 = Main.service.submit(new
		 * ParallelMerge(leftRight, rightRight, depth - 1)).get();
		 * newList.addAll(mergedPart1); newList.addAll(mergedPart2);
		 * 
		 * } catch (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (ExecutionException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } return newList;
		 */
	}

	public static LinkedList<Integer> sequentialMerge(LinkedList<Integer> left, LinkedList<Integer> right) {
		LinkedList<Integer> newList = new LinkedList<Integer>();
		while (!left.isEmpty() && !right.isEmpty()) {
			if (left.getFirst() <= right.getFirst()) {
				newList.add(left.pop());
			} else {
				newList.add(right.pop());
			}
		}
		if (left.size() > 0)
			newList.addAll(left);
		else
			newList.addAll(right);

		return newList;
	}

	private int binarySearch(int search, LinkedList<Integer> list) {
		int min = 0;
		int max = list.size();
		int mid = (min + max) / 2;
		while (list.get(mid) != search && min < max) {
			mid = (min + max) / 2;
			if (search >= list.get(mid)) {
				min = mid + 1;
			} else {
				max = mid - 1;
			}
		}
		if (min == list.size() || search <= list.get(min)) {
			return min;
		} else {
			return min + 1;
		}

	}

}
