package PA1;

public class Supervisor extends Worker {
	
	int initialBlocksize;
	
	int height8 = height / 8;
	int height4 = height / 4;
	int height3_8 = height * 3 / 8;
	int height5_8 = height * 5 / 8;
	int height3_4 = height * 3 / 4;
	int height7_8 = height * 7 / 8;
	
	/*
	 * The supervisor is a attempt for better load balancing.
	 * The Julia fractal has more white pixels the more we get from the top to the middle of the picture,
	 * and the amount of white pixels is decreasing the more we get to the end of the picture.
	 * Because of this, we want that the threads compute a block of pixels at once. This blocksize will
	 * decrease the more we get to the middle and again increase the more we get to the end.
	 */

	public Supervisor(JuliaImage julia, int width, int height, int initialBlocksize) {
		super(julia, width, height);
		lock.lock();
		Worker.block = initialBlocksize;
		this.initialBlocksize = initialBlocksize;
		lock.unlock();
	}
	
	/*
	 * Check the class variable y, which will be increased by the threads.
	 * Depending on the value of y the size of a block, 
	 * which should be computed by the threads, is increased or decreased
	 *    
	 */
	private void readjustBlockSize() {
		int temp;
		if (y > height8 && y < height4) {
			temp = initialBlocksize >> 1; //= div 2 
		} else if (y > height4 && y < height3_8) {
			temp = initialBlocksize >> 2; //= div 4
		} else if (y > height3_8 && y < height5_8) {
			temp = initialBlocksize >> 3; //= div 8
		} else if (y > height5_8 && y < height3_4) {
			temp = initialBlocksize >> 2;
		} else if (y > height3_4 && y < height7_8) {
			temp = initialBlocksize >> 1;
		} else { // > 3/4
			temp = initialBlocksize;
		}
		
		if (temp != Worker.block) {
			lock.lock();
			Worker.block = temp;
			lock.unlock();
		}
	}

	public void run() {
		while (getPixelsAndCompute()) {
			readjustBlockSize();
		}
	}

}
