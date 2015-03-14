package suffixTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Main_Does_IRR_MC {

//	// replacements
//	public static final String firstChar = String.valueOf((char) (196));
//	public static final String lastChar = String.valueOf((char) (197));
//	public static final String nonTerminal = String.valueOf((char) (198));
//	public static final String delimiter = String.valueOf((char) (199));
//	private static final int nonTerminalCounterStart = 200;

	// last char = 197
	// Nonterminal = 198
	// delimiter = 199

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		String sequence = readFile("/dev/shm/xargs.1");
		ArrayList<String> productions = IRR_MC.compress(sequence);
		IRR_MC.printGrammarToFile(productions,"/dev/shm/xargs1_compressed");
		
		
		
//		Thread.sleep(5000);
//		AnnotatedTree tree;// = new AnnotatedTree("xggcgcygcgccz");
//		// String properties =
//		// "rankdir=LR; node[shape=box fillcolor=gray95 style=filled]\n";
//		// System.out.println("digraph {\n" + properties + tree.root + "}");
//		// tree.dumpEdges();
//
//		String sequence = // "a rose is a rose is a rose";
//		readFile("/home/baatz/Uni.lu/Master/MSemester1/Multicore/PA4/Corpus/cantrbry/aliceUnix.txt");// "üa rose is a rose is a roseä";
//		// ArrayList<String> productions = new ArrayList<String>();
//
//		sequence = AnnotatedTree.firstChar + sequence + AnnotatedTree.lastChar;
//
//		ArrayList<String> productions = new ArrayList<String>();
//		productions.add(sequence);
//
//		short counter = AnnotatedTree.nonTerminalCounterStart + 1;
//		int oldSequenceLength = productions.get(0).length();
//		// AnnotatedTree tree;
//		int filecounter = 0;
//
//		int oldLength, newLength;
//
//		do {
//			// System.out.println("Check: " + sequence);
//			
//			//TODO prrof what is better now only tree of first production or of all productions
//			String allProds = productions.get(0);
////			for (String prod : productions){
////				allProds += prod;
////			}			
//			tree = new AnnotatedTree(allProds);
//			
//			String bestRepetition = tree.bestRepetition;
//
//			if (bestRepetition != null && tree.bestScore > 1) {
//				// oldSequenceLength = sequence.length();
//				oldLength = productionsLength(productions);
//
//				// sequence = sequence.replace(bestRepetition, "ö" +
//				// counterFirstByte + counterSecondByte);
//				for (int i = 0; i < productions.size(); i++) {
//					productions.set(i, productions.get(i).replace(bestRepetition, AnnotatedTree.nonTerminal + ((char) (counter))));
//				}
//
//				newLength = productionsLength(productions);
//				productions.add(bestRepetition);
//			} else {
//				break;
//			}
//
//			System.out.println("Best repetition: \"" + bestRepetition + "\": " + tree.bestScore + " " + tree.occurence
//			+ " (ö" + counter + ")");
//
//			PrintWriter out = new PrintWriter("/dev/shm/testfile" + filecounter + ".txt");
//			for (int i = 0; i < productions.size(); i++) {
//				out.print(AnnotatedTree.delimiter + productions.get(i));
//			}
//			out.close();
//			filecounter++;
//
//			counter++;
//			// if (counter == 100){
//			// break;
//			// }
//
//			// System.out.println(oldSequenceLength + "  " + sequence.length());
//		} while (oldLength > newLength);
//
//		System.out.println("\nResult:");
//		System.out.println(productions.get(0));
//
//		System.out.println(productionsLength(productions));
//
//		printGrammarToFile(productions);
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

	private static int productionsLength(ArrayList<String> productions) {
		int length = 0;
		for (String s : productions) {
			length += s.length();
		}
		return length;
	}

//	private static void printGrammarToFile(ArrayList<String> productions) throws FileNotFoundException {
//
//		String outputString = "";
//		int grammarLength = 0;
//		short nonTerminalCounter = AnnotatedTree.nonTerminalCounterStart;
//		
//		for (String production : productions) {
//			
//			outputString += "#"+(char)nonTerminalCounter;
//			nonTerminalCounter++;
//			
//			for (int i = 0; i < production.length(); i++) {
//				char c = production.charAt(i);
//				if (c == AnnotatedTree.nonTerminal.charAt(0)) {
//					outputString += "N" + production.charAt(i + 1);
//					i++;
//					grammarLength++;
//				} else if (c == AnnotatedTree.firstChar.charAt(0)) {
//					continue;
//				} else if (c == AnnotatedTree.lastChar.charAt(0)) {
//					continue;
//				} else if (c == AnnotatedTree.delimiter.charAt(0)) {
//					outputString += "#"+((char) (nonTerminalCounter));
//					nonTerminalCounter++;
//				} else if (c == 'N') {
//					outputString += "\\N";
//					grammarLength++;
//				} else if (c == '#') {
//					outputString += "\\#";
//					grammarLength++;
//				} else if (c == '\\') {
//					outputString += "\\";
//					grammarLength++;
//				} else {
//					outputString += c;
//					grammarLength++;
//				}
//			}
//		}
//		grammarLength += productions.size();
//
//		System.out.println("Grammar length: " + grammarLength);
//		PrintWriter out = new PrintWriter("/dev/shm/grammar.txt");
//		out.print(outputString);
//		out.close();
//
//	}

}
