package serverLogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RecommenderSystemMain {

	public static final String DATABASE_LOCATION = "./rosebudDB.sqlite";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		extractingTheDatabase();
		ArrayList<Movie> movieList = DatabaseConnection.getAllMovies();
		RecommenderSystem recommender = new RecommenderSystem();

	}

	/**
	 * if the file ./rosebudDB.sqlite does not exists. The Database is copied to that file
	 */
	private static void extractingTheDatabase() {
		File database = new File(DATABASE_LOCATION);
		if (!database.exists()) {
			InputStream source = RecommenderSystemMain.class.getResourceAsStream("/database/rosebudDB.sqlite");
			FileOutputStream destination;
			try {
				destination = new FileOutputStream(database);
				byte[] buffer = new byte[1048576];
				int len;
				while ((len = source.read(buffer)) != -1) {
					destination.write(buffer, 0, len);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
