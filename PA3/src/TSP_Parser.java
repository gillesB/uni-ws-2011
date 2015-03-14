import java.io.BufferedReader;
import java.io.FileReader;

//found the parser at http://dev.heuristiclab.com/svn/hl/core/tags/3.3.5/HeuristicLab.Problems.TravelingSalesman/3.3/TSPLIBParser.cs
public class TSP_Parser {

	public enum TSPLIBEdgeWeightType {
		UNDEFINED, EUC_2D, GEO
	};

	private final int EOF = 0;
	private final int NAME = 1;
	private final int TYPE = 2;
	private final int COMMENT = 3;
	private final int DIM = 4;
	private final int WEIGHTTYPE = 5;
	private final int NODETYPE = 6;
	private final int NODESECTION = 7;

	private BufferedReader source;

	private String name;

	// / <summary>
	// / Gets the name of the parsed TSP.
	// / </summary>
	public String getName() {
		return name;
	}

	private String comment;

	// / <summary>
	// / Gets the comment of the parsed TSP.
	// / </summary>
	public String getComment() {
		return comment;
	}

	private double[][] vertices;

	// / <summary>
	// / Gets the vertices of the parsed TSP.
	// / </summary>
	public double[][] getVertices() {
		return vertices;
	}

	private TSPLIBEdgeWeightType weightType;

	// / <summary>
	// / Gets the weight type of the parsed TSP.
	// / </summary>
	public TSPLIBEdgeWeightType getWeightType() {
		return weightType;
	}

	// / <summary>
	// / Initializes a new instance of <see cref="TSPLIBParser"/> with the given
	// <paramref name="path"/>.
	// / </summary>
	// / <exception cref="ArgumentException">Thrown if the input file is not a
	// TSPLIB TSP file (*.tsp)
	// / </exception>
	// / <param name="path">The path where the TSP is stored.</param>
	public TSP_Parser(String path) throws Exception {
		if (!path.endsWith(".tsp"))
			throw new Exception("Input file has to be a TSPLIB TSP file (*.tsp).");

		source = new BufferedReader(new FileReader(path));
		name = path;
		comment = "";
		vertices = null;
		weightType = TSPLIBEdgeWeightType.UNDEFINED;
	}

	// / <summary>
	// / Reads the TSPLIB TSP file and parses the elements.
	// / </summary>
	// / <exception cref="InvalidDataException">Thrown if the file has an
	// invalid format or contains invalid data.</exception>
	public void Parse() throws Exception {
		int section = -1;
		String str = null;
		boolean typeIsChecked = false;
		boolean weightTypeIsChecked = false;

		do {
			str = source.readLine();
			section = GetSection(str);

			if (section != -1) {
				switch (section) {
				case NAME:
					ReadName(str);
					break;
				case TYPE:
					CheckType(str);
					typeIsChecked = true;
					break;
				case COMMENT:
					ReadComment(str);
					break;
				case DIM:
					InitVerticesArray(str);
					break;
				case WEIGHTTYPE:
					ReadWeightType(str);
					weightTypeIsChecked = true;
					break;
				case NODETYPE:
					CheckNodeType(str);
					break;
				case NODESECTION:
					ReadVertices();
					break;
				}
			}
		} while (!((section == EOF) || (str == null)));

		if (!(typeIsChecked && weightTypeIsChecked))
			throw new Exception("Input file does not contain type or edge weight type information.");
	}

	private int GetSection(String str) {
		if (str == null)
			return EOF;

		String[] tokens = str.split(":");
		if (tokens.length == 0)
			return -1;

		String token = tokens[0].trim();
		if (token.compareToIgnoreCase("eof") == 0)
			return EOF;
		if (token.compareToIgnoreCase("name") == 0)
			return NAME;
		if (token.compareToIgnoreCase("type") == 0)
			return TYPE;
		if (token.compareToIgnoreCase("comment") == 0)
			return COMMENT;
		if (token.compareToIgnoreCase("dimension") == 0)
			return DIM;
		if (token.compareToIgnoreCase("edge_weight_type") == 0)
			return WEIGHTTYPE;
		if (token.compareToIgnoreCase("node_coord_type") == 0)
			return NODETYPE;
		if (token.compareToIgnoreCase("node_coord_section") == 0)
			return NODESECTION;

		return -1;
	}

	private void ReadName(String str) {
		String[] tokens = str.split(":");
		name = tokens[tokens.length - 1].trim();
	}

	private void CheckType(String str) throws Exception {
		String[] tokens = str.split(":");

		String type = tokens[tokens.length - 1].trim();
		if (!(type.compareToIgnoreCase("tsp") == 0))
			throw new Exception("Input file type is not \"TSP\"");
	}

	private void ReadComment(String str) {
		String[] tokens = str.split(":");
		comment = tokens[tokens.length - 1].trim();
	}

	private void InitVerticesArray(String str) {
		String[] tokens = str.split(":");
		String dimension = tokens[tokens.length - 1].trim();

		int dim = Integer.parseInt(dimension);
		vertices = new double[dim][2];
	}

	private void ReadWeightType(String str) throws Exception {
		String[] tokens = str.split(":");
		String type = tokens[tokens.length - 1].trim();

		if (type.compareToIgnoreCase("euc_2d") == 0)
			weightType = TSPLIBEdgeWeightType.EUC_2D;
		else if (type.compareToIgnoreCase("geo") == 0)
			weightType = TSPLIBEdgeWeightType.GEO;
		else
			throw new Exception("Input file contains an unsupported edge weight type (only \"EUC_2D\" and \"GEO\" are supported).");
	}

	private void CheckNodeType(String str) throws Exception {
		String[] tokens = str.split(":");
		String type = tokens[tokens.length - 1].trim();

		if (!(type.compareToIgnoreCase("twod_coords") == 0))
			throw new Exception("Input file contains an unsupported node coordinates type (only \"TWOD_COORDS\" is supported).");
	}

	private void ReadVertices() throws Exception {
		if (vertices == null)
			throw new Exception("Input file does not contain dimension information.");

		for (int i = 0; i < vertices.length; i++) {
			String str = source.readLine();
			str = str.replaceAll("\\s+", " ");
			str = str.trim();
			String[] tokens = str.split(" ");

			if (tokens.length != 3)
				throw new Exception("Input file contains invalid node coordinates.");

			// CultureInfo culture = new CultureInfo("en-US");
			vertices[i][0] = Double.parseDouble(tokens[1]);
			vertices[i][1] = Double.parseDouble(tokens[2]);
		}
	}
}
