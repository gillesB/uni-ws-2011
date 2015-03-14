package utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import suffixTree.AnnotatedTree;

public class Utils {

    public static int[] readFile(String path) {
        try {
            int len = (int) (new File(path).length());
            FileInputStream fis = new FileInputStream(path);
            byte buf[] = new byte[len];

            fis.read(buf);
            fis.close();

            int[] intBuf = new int[len];
            for (int i = 0; i < len; i++) {
                intBuf[i] = buf[i] & 0xff;
            }

            return intBuf;

        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }

    public static void writeFile(String path, ArrayList<Integer> list)
            throws IOException {

        OutputStream stream = new FileOutputStream(path);
        BufferedOutputStream bufferedStream = new BufferedOutputStream(stream);

        for (int b : list) {
            bufferedStream.write(b);
        }
        bufferedStream.flush();
        bufferedStream.close();
    }

    public static void writeFile(String path, int[] arr) throws IOException {

        OutputStream stream = new FileOutputStream(path);
        BufferedOutputStream bufferedStream = new BufferedOutputStream(stream);

        for (int b : arr) {
            bufferedStream.write(b);
        }
        bufferedStream.flush();
        bufferedStream.close();
    }

    public static void printGrammarToFile(ArrayList<int[]> productions,
            String filename) throws FileNotFoundException {

        ArrayList<Integer> outputString = new ArrayList<Integer>();
        int grammarLength = 0;
        // short nonTerminalCounter = 0;//
        // AnnotatedTree.nonTerminalCounterStart;

        // for (short nonTerminalCounter = 0; nonTerminalCounter <
        // productions.size(); nonTerminalCounter++) {
        short nonTerminalCounter = 0;
        for (int[] production : productions) {

            if (production == null) {
                continue;
            }

            int[] nonTerminal;
            do {
                nonTerminal = shortToIntegerArray(nonTerminalCounter);
                nonTerminalCounter++;
            } while (nonTerminal[0] == AnnotatedTree.delimiter
                    || nonTerminal[1] == AnnotatedTree.delimiter
                    || nonTerminal[0] == AnnotatedTree.escape
                    || nonTerminal[1] == AnnotatedTree.escape
                    || nonTerminal[0] == AnnotatedTree.nonTerminal
                    || nonTerminal[1] == AnnotatedTree.nonTerminal
                    || (nonTerminal[0] == 0xd && nonTerminal[1] == 0xa)
                    || nonTerminal[1] == 0xd);

            outputString.add(AnnotatedTree.delimiter);
            outputString.add(nonTerminal[0]);
            outputString.add(nonTerminal[1]);

            // int[] production = productions.get(nonTerminalCounter);

            for (int i = 0; i < production.length; i++) {
                int c = production[i];

                if (c == AnnotatedTree.escape) {
                    outputString.add(AnnotatedTree.escape);
                    outputString.add(production[i + 1]);

                    i++;
                    grammarLength++;
                } else if (c == AnnotatedTree.nonTerminal) {
                    outputString.add(AnnotatedTree.nonTerminal);
                    outputString.add(production[i + 1]);
                    outputString.add(production[i + 2]);
                    i += 2;
                    grammarLength++;

                } else {
                    outputString.add(c);
                    grammarLength++;
                }
            }
        }
        grammarLength += productions.size();
        System.out.println("Grammar length: " + grammarLength);
        System.out.println("Amount of rules: " + productions.size());

        try {
            writeFile(filename, outputString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printGrammarToFile(ListSet<IntegerArrayWrapper> productions,
            String filename) throws FileNotFoundException {

        ArrayList<Integer> outputString = new ArrayList<Integer>();
        int grammarLength = 0;
        // short nonTerminalCounter = 0;//
        // AnnotatedTree.nonTerminalCounterStart;

        // for (short nonTerminalCounter = 0; nonTerminalCounter <
        // productions.size(); nonTerminalCounter++) {
        short nonTerminalCounter = 0;
        for (IntegerArrayWrapper production : productions) {

            if (production == null) {
                continue;
            }

            int[] nonTerminal;
            do {
                nonTerminal = shortToIntegerArray(nonTerminalCounter);
                nonTerminalCounter++;
            } while (nonTerminal[0] == AnnotatedTree.delimiter
                    || nonTerminal[1] == AnnotatedTree.delimiter
                    || nonTerminal[0] == AnnotatedTree.escape
                    || nonTerminal[1] == AnnotatedTree.escape
                    || nonTerminal[0] == AnnotatedTree.nonTerminal
                    || nonTerminal[1] == AnnotatedTree.nonTerminal
                    || (nonTerminal[0] == 0xd && nonTerminal[1] == 0xa)
                    || nonTerminal[1] == 0xd);

            outputString.add(AnnotatedTree.delimiter);
            outputString.add(nonTerminal[0]);
            outputString.add(nonTerminal[1]);

            // int[] production = productions.get(nonTerminalCounter);

            for (int i = 0; i < production.data.length; i++) {
                int c = production.data[i];

                if (c == AnnotatedTree.escape) {
                    outputString.add(AnnotatedTree.escape);
                    outputString.add(production.data[i + 1]);

                    i++;
                    grammarLength++;
                } else if (c == AnnotatedTree.nonTerminal) {
                    outputString.add(AnnotatedTree.nonTerminal);
                    outputString.add(production.data[i + 1]);
                    outputString.add(production.data[i + 2]);
                    i += 2;
                    grammarLength++;

                } else {
                    outputString.add(c);
                    grammarLength++;
                }
            }
        }
        grammarLength += productions.size();
        System.out.println("Grammar length: " + grammarLength);
        System.out.println("Amount of rules: " + productions.size());

        try {
            writeFile(filename, outputString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public static int[] concatAll(int[] first, int[]... rest) {
        int totalLength = first.length;
        for (int[] array : rest) {
            totalLength += array.length;
        }
        int[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (int[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    } 

    //only used in SuffixTree toString()
    public static String intArrayToString(int[] arr) {
        String ret = "";
        if (arr.length == 0) {
            return "";
        }

        for (int b : arr) {
            ret += (char) b;
        }
        return ret;
    }

    //only used in this class
    public static int[] shortToIntegerArray(short s) {
        int[] ret = new int[2];
        ret[0] = (int) (s & 0xff);
        ret[1] = (int) ((s >> 8) & 0xff);

        return ret;
    }

    //only used in DecompressGrammar
    public static int intsToShort(int lowInteger, int highInteger) {

        int ret = Math.abs(lowInteger);
        ret += (Math.abs(highInteger) << 8);
        return ret;
    }
   
}
