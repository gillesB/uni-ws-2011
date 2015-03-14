package PA2;

import java.util.concurrent.RecursiveAction;

@SuppressWarnings("serial")
public class FillArray extends RecursiveAction {
	
	private int[] list;
	private int start, stop, p, n;	

	public FillArray(int[] list, int start, int stop, int p, int n) {
		super();
		this.list = list;
		this.start = start;
		this.stop = stop;
		this.p = p;
		this.n = n;
	}

	@Override
	protected void compute() {		
		for (int i = start; i < stop; i++) {
			list[i]=((i+1) * p % n);
		}		
	}
	


}
