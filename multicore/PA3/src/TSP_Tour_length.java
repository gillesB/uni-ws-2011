import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TSP_Tour_length {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String pathNodes = "/home/baatz/Uni.lu/Master/MSemester1/Multicore/PA3/tours/eil51.tsp.conv";
		BufferedReader source;
		int nodeAmount = 0;
		int[][] weights = null;
		try {
			source = new BufferedReader(new FileReader(pathNodes));
			String str = source.readLine();
			nodeAmount = Integer.parseInt(str);
			weights = new int[nodeAmount][nodeAmount];
			// trails = new double[nodeAmount][nodeAmount];
			str = source.readLine();
			int i = 0;
			while (str != null) {
				String[] line = str.split(" ");
				for (int j = 0; j < line.length; j++) {
					weights[i][j] = Integer.parseInt(line[j]);
				}
				i++;
				str = source.readLine();
			}
			source.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String pathTour = "/home/baatz/Uni.lu/Master/MSemester1/Multicore/PA3/calculated_tours/eil51_our_429";
		int[] tour = null;
		try {
			source = new BufferedReader(new FileReader(pathTour));
			String str = source.readLine();
			tour = new int[nodeAmount+1];
			// trails = new double[nodeAmount][nodeAmount];;
			int i = 0;
			while (str != null) {
				//String node = str.trim();
				String node = str;
				tour[i] = Integer.parseInt(node)-1;

				i++;
				str = source.readLine();
			}
			source.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		int distance = 0;
		for(int i= 1; i <= nodeAmount; i++){
			System.out.println(tour[i-1]+" - "+tour[i]+": " + weights[ tour[i-1] ][ tour[i] ]);
			distance += weights[ tour[i-1] ][ tour[i] ];
		}
		System.out.println(distance);

	}

}
