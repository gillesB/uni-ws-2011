package PA1;

import java.io.IOException;

public class Starter {
	

	/**
	 * The Starter first checks if all the input arguments are correct, if this is so,
	 * it will create a new JuliaImage, start a Supervisor and start the specified 
	 * number of Worker threads.  
	 * @param args
	 */
	public static void main(String[] args) {

		int width = 50, height = 50, nThreads = 1, outputFlag = 1;
		JuliaImage julia;
		Thread[] threads;

		if (args.length < 4) {
			System.err.println("Missing Argument(s)");
			System.exit(1);
		} else if (args.length > 4) {
			System.err.println("To many Arguments");
			System.exit(1);
		} else {
			// Reading Argument 1 (width) and checking if it is a number
			try {
				width = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Error at the first argument, wrong Format");
				System.exit(1);
			}
			// Reading Argument 2 (height) and checking if it is a number
			try {
				height = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("Error at the second argument, wrong Format");
				System.exit(1);
			}
			// Reading Argument 3 (number of threads) and checking if it is a number
			try {
				nThreads = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("Error at the third argument, wrong Format");
				System.exit(1);
			}
			// Reading Argument 4 (flag) and checking if it is a number
			try {
				outputFlag = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				System.err.println("Error at the fourth argument, wrong Format");
				System.exit(1);
			}
			
			// Checking the parameter values: number of threads and output flags 
			if (nThreads == 0) {
				System.err.println("Number of Threads should be at least 1");
				System.exit(1);
			}
			
			/* 	outputflag = 0: no output
			 	outputflag = 1: print on STDOUT
			 	outputflag = 2: write directly to hard drive (additional, to make testing easier)
			 */
			if ((outputFlag != 0) && (outputFlag != 1) && (outputFlag != 2)) {
				System.err.println("Outputflag should be 0 or 1 or 2");
				System.exit(1);
			}

			// If program call OK: create new juliaImage, Supervisor and n number of threads
			// All workers will work on the same juliaImage
			julia = new JuliaImage(width, height, 256); // Color depth of 256
			threads = new Thread[nThreads];
			threads[0] = new Supervisor(julia, width, height, width);
			threads[0].start();
			for (int i = 1; i < nThreads; i++) {
				threads[i] = new Worker(julia, width, height);
				threads[i].start();
			}
			
			//Wait for one thread after the other to finish 
			for (int i = 0; i < nThreads; i++) {
				try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// If all threads terminated write juliaset to Harddrive or to stout
			if (outputFlag == 0) {
				// Do nothing
			} else if (outputFlag == 1) {
				// Print to STDOUT
				System.out.println(julia.toString());
			} else {
				// Save in the User's home directory
				try {
					julia.storeImage("julia_fractal");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.exit(0);
		}
	}
}
