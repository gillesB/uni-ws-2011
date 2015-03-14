public class TSP_WeightCalculator {

	int[][] weights;

	public TSP_WeightCalculator(String path) throws Exception {
		super();
		TSP_Parser parser = new TSP_Parser(path);
		parser.Parse();
		weights = new int[parser.getVertices().length][parser.getVertices().length];
		calculateWeights(parser);

	}

	public int[][] getWeights() {
		return weights;
	}

	private void calculateWeights(TSP_Parser parser) throws Exception {
		switch (parser.getWeightType()) {
		case EUC_2D:
			calculateWeight_EUC_2D(parser.getVertices());
			break;
		case GEO:
			calculateWeight_GEO(parser.getVertices());
			break;
		default:
			throw new Exception("WeightType not known");
		}
	}

	private void calculateWeight_EUC_2D(double[][] vertices) {
		for (int i = 0; i < vertices.length; i++) {
			for (int j = i; j < vertices.length; j++) {
				// coordinates from first node - coordinates from second node
				// xd = x[i] - x[j]
				double xd = vertices[i][0] - vertices[j][0];
				// yd = y[i] - y[j]
				double yd = vertices[i][1] - vertices[j][1];
				weights[i][j] = (int) (Math.sqrt(xd * xd + yd * yd) + 0.5);
			}
		}
	}

	private void calculateWeight_GEO(double[][] vertices) {
		double RRR = 6378.388;

		for (int i = 0; i < vertices.length; i++) {
			double[] node1_latitude_longitude = getLatitudeAndLongitude(vertices[i]);
			for (int j = i; j < vertices.length; j++) {
				double[] node2_latitude_longitude = getLatitudeAndLongitude(vertices[j]);

				double q1 = Math.cos(node1_latitude_longitude[1] - node2_latitude_longitude[1]);
				double q2 = Math.cos(node1_latitude_longitude[0] - node2_latitude_longitude[0]);
				double q3 = Math.cos(node1_latitude_longitude[0] + node2_latitude_longitude[0]);
				
				double temp =  (RRR * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);

				weights[i][j] = (int) (RRR * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);

			}
		}
	}

	private double[] getLatitudeAndLongitude(double[] node) {
		int deg = (int) (node[0] + 0.5);
		double min = node[0] - deg;
		double latitude = Math.PI * (deg + 0.5 * min / 3.0) / 180.0;
		deg = (int) (node[1] + 0.5);
		min = node[1] - deg;
		double longitude = Math.PI * (deg + 0.5 * min / 3.0) / 180.0;

		return new double[] { latitude, longitude };

	}

}
