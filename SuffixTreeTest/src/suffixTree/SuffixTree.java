/**
 * Refactored java-code originally based on Mark Nelson's C++ implementation of Ukkonen's algorithm.
 * http://illya-keeplearning.blogspot.com/search/label/suffix%20tree
 */
package suffixTree;

import java.util.*;

public class SuffixTree {
	String text;
	Node root;
	int nodesCount;
	
	private final String firstChar_Str = String.valueOf((char) (196));
	private final String lastChar_Str = String.valueOf((char) (197));
	private static final char firstChar = (char) (196);
	private static final char lastChar = (char) (197);

	public SuffixTree(String text) {
		this.text= firstChar + text +lastChar;
		root = new Node(null);

		Suffix active = new Suffix(root, 0, -1);
		for (int i = 0; i < text.length(); i++) {
			addPrefix(active, i);
		}
	}

	private void addPrefix(Suffix active, int endIndex) {
		Node lastParentNode = null;
		Node parentNode;

		while (true) {
			Edge edge;
			parentNode = active.getOriginNode();

			// Step 1 is to try and find a matching edge for the given node.
			// If a matching edge exists, we are done adding edges, so we break
			// out of this big loop.
			if (active.isExplicit()) {
				edge = active.getOriginNode().findEdge(text.charAt(endIndex));
				if (edge != null)
					break;
			} else {
				// implicit node, a little more complicated
				edge = active.getOriginNode().findEdge(text.charAt(active.getBeginIndex()));
				int span = active.getSpan();
				if (text.charAt(edge.getBeginIndex() + span + 1) == text.charAt(endIndex))
					break;
				parentNode = edge.splitEdge(active);
			}

			// We didn't find a matching edge, so we create a new one, add it to
			// the tree at the parent node position,
			// and insert it into the hash table. When we create a new node, it
			// also means we need to create
			// a suffix link to the new node from the last node we visited.
			Edge newEdge = new Edge(endIndex, text.length() - 1, parentNode);
			newEdge.insert();
			updateSuffixNode(lastParentNode, parentNode);
			lastParentNode = parentNode;

			// This final step is where we move to the next smaller suffix
			if (active.getOriginNode() == root)
				active.incBeginIndex();
			else
				active.changeOriginNode();
			active.canonize();
		}
		updateSuffixNode(lastParentNode, parentNode);
		active.incEndIndex(); // Now the endpoint is the next active point
		active.canonize();
	}

	private void updateSuffixNode(Node node, Node suffixNode) {
		if ((node != null) && (node != root)) {
			node.setSuffixNode(suffixNode);
		}
	}

	public void dumpEdges() {
		System.out.println("\tEdge \t\tStart \t\tEnd \t\tSuf \t\tFirst \t\tLast \t\tString");
		Queue<Node> queue = new LinkedList<Node>();

		queue.add(root);
		dumpEdges(queue);
	}

	private void dumpEdges(Queue<Node> queue) {
		while (!queue.isEmpty()) {
			Node node = queue.remove();
			for (Edge edge : node.getEdges()) {
				Node suffixNode = edge.getEndNode().getSuffixNode();
				System.out.print("\t" + edge + " " + "\t\t" + edge.getStartNode() + " " + "\t\t" + edge.getEndNode()
				+ "(" + edge.getEndNode().getIncomingEdge() + ")" +" " + "\t\t"
				+ ((suffixNode == null) ? "-1" : suffixNode) + " " + "\t\t" + edge.getBeginIndex() + " " + "\t\t"
				+ edge.getEndIndex() + " " + "\t\t");
				for (int l = edge.getBeginIndex(); l <= edge.getEndIndex(); l++) {
					System.out.print(text.charAt(l));
				}
				System.out.println();

				if (edge.getEndNode() != null)
					queue.add(edge.getEndNode());
			}
		}
	}
	
	//TODO get rid of these two just to check that firstChar and lastChar is used nowhere else
	public static boolean isFirstChar(char c){
		return c == firstChar;
	}
	
	public static boolean isLastChar(char c){
		return c == lastChar;
	}

	class Edge {
		private int beginIndex; // can't be changed
		private int endIndex;
		private Node startNode;
		private Node endNode; // can't be changed, could be used as edge id

		// each time edge is created, a new end node is created
		public Edge(int beginIndex, int endIndex, Node startNode) {
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.startNode = startNode;
			this.endNode = new Node(null);

			// TODO
			this.endNode.setIncomingEdge(this);

		}

		public Node splitEdge(Suffix suffix) {
			remove();
			Edge newEdge = new Edge(beginIndex, beginIndex + suffix.getSpan(), suffix.getOriginNode());
			newEdge.insert();
			newEdge.endNode.setSuffixNode(suffix.getOriginNode());
			beginIndex += suffix.getSpan() + 1;
			startNode = newEdge.getEndNode();
			insert();
			return newEdge.getEndNode();
		}

		public void insert() {
			startNode.addEdge(text.charAt(beginIndex), this);
		}

		public void remove() {
			startNode.removeEdge(text.charAt(beginIndex));
		}

		public int getSpan() {
			return endIndex - beginIndex;
		}

		public int getBeginIndex() {
			return beginIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}

		public void setEndIndex(int endIndex) {
			this.endIndex = endIndex;
		}

		public Node getStartNode() {
			return startNode;
		}

		public void setStartNode(Node startNode) {
			this.startNode = startNode;
		}

		public Node getEndNode() {
			return endNode;
		}

		@Override
		public String toString() {
			return Integer.toString(endNode.name);
		}
		
		public String getLabel(){
			return text.substring(beginIndex, endIndex+1);
		}
		
		
	}

	private class Suffix {
		private Node originNode;
		private int beginIndex;
		private int endIndex;

		public Suffix(Node originNode, int beginIndex, int endIndex) {
			this.originNode = originNode;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
		}

		public boolean isExplicit() {
			return beginIndex > endIndex;
		}

		public boolean isImplicit() {
			return endIndex >= beginIndex;
		}

		public void canonize() {
			if (!isExplicit()) {
				Edge edge = originNode.findEdge(text.charAt(beginIndex));

				int edgeSpan = edge.getSpan();
				while (edgeSpan <= getSpan()) {
					beginIndex += edgeSpan + 1;
					originNode = edge.getEndNode();
					if (beginIndex <= endIndex) {
						edge = edge.getEndNode().findEdge(text.charAt(beginIndex));
						edgeSpan = edge.getSpan();
					}
				}
			}
		}

		public int getSpan() {
			return endIndex - beginIndex;
		}

		public Node getOriginNode() {
			return originNode;
		}

		public int getBeginIndex() {
			return beginIndex;
		}

		public void incBeginIndex() {
			beginIndex++;
		}

		public void changeOriginNode() {
			originNode = originNode.getSuffixNode();
		}

		public int getEndIndex() {
			return endIndex;
		}

		public void incEndIndex() {
			endIndex++;
		}
	}

	public static int c;
	class Node {
		private Node suffixNode;
		private Map<Character, Edge> edges;
		private int name;
		private Integer stringDepth, nodeDepth;
		
		
		private int id;

		private Edge incomingEdge;
		//int stringDepth = 0;

		public Node(Node suffixNode) {
			this.suffixNode = suffixNode;
			edges = new HashMap<Character, Edge>();

			name = nodesCount++;
		}

		public void addEdge(char ch, Edge edge) {
			edges.put(ch, edge);
		}

		public void removeEdge(char ch) {
			edges.remove(ch);
		}

		public Edge findEdge(char ch) {
			return edges.get(ch);
		}

		public Node getSuffixNode() {
			return suffixNode;
		}

		public void setSuffixNode(Node suffixNode) {
			this.suffixNode = suffixNode;
		}

		public Collection<Edge> getEdges() {
			return edges.values();
		}

//		@Override
//		public String toString() {
//			return ((Integer) name).toString();
//		}

		TreeMap<String, HashSet<Integer>> annotation = new TreeMap<String, HashSet<Integer>>();
		private ArrayList<Node> children;

		public ArrayList<Node> getChildren() {
			if (children != null) {
				return children;
			} else {
				ArrayList<Node> children = new ArrayList<Node>();
				for (Edge e : edges.values()) {
					children.add(e.endNode);
				}
				return children;
			}

		}

		public boolean isLeaf() {
			return edges.values().isEmpty();
		}

		public Edge getIncomingEdge() {
			return incomingEdge;
		}

		public void setIncomingEdge(Edge incomingEdge) {
			this.incomingEdge = incomingEdge;
		}

		public boolean isRoot() {
			return incomingEdge == null;
		}
		
		public int getNodeDepth(){
			if(nodeDepth != null){
				return nodeDepth;
			}
			if(this.isRoot()){
				return 0;
			} else {
				return this.incomingEdge.startNode.getNodeDepth() + 1;
			}
		}
		
		public int getStringDepth(){
			if(stringDepth != null){
				return stringDepth;
			}
			if(this.isRoot()){
				return 0;
			} else {
				return this.incomingEdge.startNode.getStringDepth() + this.incomingEdge.getSpan()+1;
			}
		}
		
		public String toString() {
			StringBuilder result = new StringBuilder();
			String incomingLabel = this.isRoot() ? "" : this.incomingEdge.getLabel();
			if (this.isRoot()) {
				c = 1;
				this.id = 1;
			} else {
				this.id = c;
				//if (this.getNodeDepth() > 1) {
					printTab(result);
					result.append(this.id + "[label=\"" + "(" + printAnnotation() + ")" + "\"];\n");
				//}
				printTab(result);
				result.append(this.incomingEdge.startNode.id + " -> ");
				result.append(this.id + "[label=\"" + incomingLabel + "\"];\n");
			}
			for (Node child : getChildren()) {
				c++;
				child.id = c;
				result.append(child.toString());
			}
			return result.toString();
		}
		
		private String printAnnotation() {
			String ret = "";
			for (String key : annotation.keySet()) {
				ret += key + ": ";
				for (int value : annotation.get(key)) {
					ret += value + " ";
				}
				ret += "\\n";
			}
			return ret;
		}
		
		private void printTab(StringBuilder result) {
			for (int i = 1; i <= this.getNodeDepth(); i++)
				result.append("\t");
		}
		

	}
}
