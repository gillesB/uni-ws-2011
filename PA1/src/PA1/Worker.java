package PA1;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.math.complex.*;

public class Worker extends Thread {
	private JuliaImage julia;
	int width, height;
	private Complex c = new Complex(1.0, 0.3);

	static int x, y = 0;
	static Lock lock = new ReentrantLock();
	static int block;

	private static final double piTimes4 = 4 * Math.PI;
	private static final double piTimes2 = 2 * Math.PI;

	/**
	 * Constructor for the worker.
	 * Worker receive a juliaImage on which it will work. 
	 * @param julia
	 * @param width
	 * @param height
	 */
	public Worker(JuliaImage julia, int width, int height) {
		this.julia = julia;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Compute the julia serie of a pixel specified by x and y  
	 * @param x
	 * @param y
	 */
	public void compute(int x, int y) {
		Double real = (double) x / width * piTimes4 - piTimes2;
		Double imaginary = (double) y / height * piTimes4 - piTimes2;
		Complex z = new Complex(real, imaginary);
		for (int i = 1; i < 50; i++) {
			z = c.multiply(z.sin());
			if (Math.abs(z.getImaginary()) > 50.0) {
				julia.setPixelBlack(x, y);
				return;
			}
		}
		julia.setPixelWhite(x, y);
	}

	/**
	 * Calculate the start and stop pixels, and compute all pixels between these pixels
	 */
	boolean getPixelsAndCompute(){
		int from_x, to_x, from_y, to_y;
		
		// Lock needed, because read/write access on a class variable
		lock.lock();
			from_x = to_x = x;
			from_y = to_y = y;
	
			to_x += Worker.block;
			while (to_x > width) {	// to_x is out of image borders
				to_x -= width;	
				to_y += 1;			
			}
			if (to_x == width) {
				to_x--;
			}
			x = to_x;
			y = to_y;
		lock.unlock();

		int x = from_x;
		int y = from_y;

		if (y >= height) {
			return false;
		}
		// compute all pixels of this block
		while (!(x == to_x && y == to_y)) {
			compute(x, y);
			x++;
			if (x >= width) {
				x = 0;
				y++;
				if (y >= height) {
					return false;
				}
			}
		}		
		return true;
	}
	
	public void run() {
		while (getPixelsAndCompute()) {
		}
	}
}
