package PA1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Mehrdimensionale Arrays: [Zeilen] [Spalten]

public class JuliaImage {
	private int width, height, colorDepth;
	
	// Array which will store the values for each pixel as a String
	private String[][] pixelArray;		
	private String white = "255 255 255";
	private String black = "0 0 0";

/**
 * Constructor of the JuliaImage. Creates a JuliaImage with the specified width and height
 * and with the specified colordepth	
 * @param width
 * @param height
 * @param colorDepth
 */
	public JuliaImage (int width, int height, int colorDepth){
		this.width = width;
		this.height = height;
		this.colorDepth = colorDepth;
		pixelArray = new String[height][width];
	}
	
	/**
	 * Set the by x and y specified pixel black
	 * @param x
	 * @param y
	 */
	public void setPixelBlack(int x, int y){
		pixelArray[y][x] = black;
		
	}
	
	/**
	 * Set the by x and y specified pixel white
	 * @param x
	 * @param y
	 */
	public void setPixelWhite(int x, int y){
		pixelArray[y][x] = white;
	}
	
	/**
	 * This function will store the computed JuliaImage directly to the hard drive in the users 
	 * home directory. There it creates a directory named "Multicore_PA1" in which it will store
	 * the images with a sequence number.
	 * @param filename
	 * @throws IOException
	 */
	public void storeImage(String filename) throws IOException{
		
		File saveDir = new File((System.getProperty("user.home")+"/Multicore_PA1"));
		if (!saveDir.exists()){
			saveDir.mkdir();
		}
		File saveFile = new File(saveDir+"/"+filename+".ppm");
		int a = 1;
		while (saveFile.exists()){
			saveFile = new File(saveDir+"/"+filename+"_"+a+".ppm");
			a++;
		}
		FileWriter writer = new FileWriter(saveFile);
		writer.write(toString());
		writer.flush();
		writer.close();
	}
	
	/**
	 * Returns the image as a String with the required identifiers for the .ppm image 
	 */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("P3\n");
		buffer.append(width+"\n");
		buffer.append(height+"\n");
		buffer.append(colorDepth+"\n");
		for (int i=0; i< height; i++){
			for (int j=0; j<width; j++){
				buffer.append(pixelArray[i][j] + "\n");
			}			
		}		
		return buffer.toString();		
	}

}
