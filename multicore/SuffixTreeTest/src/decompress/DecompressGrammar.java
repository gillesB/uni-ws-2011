package decompress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class DecompressGrammar {

	private static ArrayList<String> productions, decompressedProductions;

	public static void main(String[] args) throws IOException {
		String compressed = readFile("/dev/shm/grammar.txt");
		compressed = compressed.substring(2);
		// String[] productions = compressed.split("(?m)(?s)[^\\\\]#");
		productions = new ArrayList<String>();
		decompressedProductions = new ArrayList<String>();

		// split the compressed string
		String newProduction = "";
		for (int i = 0; i < compressed.length(); i++) {
			char c = compressed.charAt(i);
			if (c == '#' && compressed.charAt(i - 1) != '\\') {
				productions.add(newProduction);
				decompressedProductions.add(null);
				newProduction = "";
				// ignore the productionID
				i++;
			} else {
				newProduction += c;
			}
		}
		productions.add(newProduction);
		decompressedProductions.add(null);

		// replace the non terminals
		decompressProduction(0);
		// for (int i = productions.size() - 1; i >= 0; i--) {
		// String production = productions.get(i);
		//
		// String decompressedProduction = "";
		// for (int j = 0; j < production.length(); j++) {
		// char c = production.charAt(j);
		// if (c == 'N') {
		// char productionID = production.charAt(j + 1);
		// j++;
		// int productionIndex = ((int) productionID) - 200;
		// decompressedProduction += productions.get(productionIndex);
		// } else if (c == '\\') {
		// decompressedProduction += production.charAt(j + 1);
		// j++;
		// } else {
		// decompressedProduction += production.charAt(j);
		// }
		// }
		// productions.set(i,decompressedProduction);
		// }

		PrintWriter out = new PrintWriter("/dev/shm/decompressed.txt");
		out.print(productions.get(0));
		out.close();

	}

	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	public static String decompressProduction(int index) {
		
		if(index == -4){
			System.out.println("bla");
		}

		String production = decompressedProductions.get(index);
		if (production != null) {
			return production;
		}

		production = productions.get(index);

		String decompressedProduction = "";
		for (int j = 0; j < production.length(); j++) {
			char c = production.charAt(j);
			if (c == 'N') {
				char productionID = production.charAt(j + 1);
				j++;
				int productionIndex = ((int) productionID) - 200;
				decompressedProduction += decompressProduction(productionIndex);
			} else if (c == '\\') {
				decompressedProduction += production.charAt(j + 1);
				j++;
			} else {
				decompressedProduction += production.charAt(j);
			}
		}
		productions.set(index, decompressedProduction);
		decompressedProductions.set(index, decompressedProduction);
		return productions.get(index);
	}
}
