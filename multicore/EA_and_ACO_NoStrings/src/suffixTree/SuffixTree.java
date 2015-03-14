/**
 * Refactored java-code originally based on Mark Nelson's C++ implementation of Ukkonen's algorithm.
 * http://illya-keeplearning.blogspot.com/search/label/suffix%20tree
 */
package suffixTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import utils.Utils;

public class SuffixTree {

    final int[] text;
    Node root;
    int nodesCount;
    // private final String firstChar_Str = String.valueOf((char) (196));
    // private final String lastChar_Str = String.valueOf((char) (197));
    private static final int firstChar = 0xFFFFFFFF;
    private static final int lastChar = 0xFFFFFFFE;

    public SuffixTree(int[] text) {
        this.text = new int[text.length + 2];
        this.text[0] = firstChar;
        System.arraycopy(text, 0, this.text, 1, text.length);
        this.text[this.text.length-1] = lastChar;

        root = new Node(null);

        Suffix active = new Suffix(root, 0, -1);
        for (int i = 0; i < this.text.length; i++) {
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
                edge = active.getOriginNode().findEdge(text[endIndex]);
                if (edge != null) {
                    break;
                }
            } else {
                // implicit node, a little more complicated
                edge = active.getOriginNode().findEdge(
                        text[active.getBeginIndex()]);
                int span = active.getSpan();
                if (text[edge.getBeginIndex() + span + 1] == text[endIndex]) {
                    break;
                }
                parentNode = edge.splitEdge(active);
            }

            // We didn't find a matching edge, so we create a new one, add it to
            // the tree at the parent node position,
            // and insert it into the hash table. When we create a new node, it
            // also means we need to create
            // a suffix link to the new node from the last node we visited.
            Edge newEdge = new Edge(endIndex, text.length - 1, parentNode);
            newEdge.insert();
            updateSuffixNode(lastParentNode, parentNode);
            lastParentNode = parentNode;

            // This final step is where we move to the next smaller suffix
            if (active.getOriginNode() == root) {
                active.incBeginIndex();
            } else {
                active.changeOriginNode();
            }
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
        Queue<Node> queue = new LinkedList<>();

        queue.add(root);
        dumpEdges(queue);
    }

    private void dumpEdges(Queue<Node> queue) {
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            for (Edge edge : node.getEdges()) {
                Node suffixNode = edge.getEndNode().getSuffixNode();
                System.out.print("\t" + edge + " " + "\t\t"
                        + edge.getStartNode() + " " + "\t\t"
                        + edge.getEndNode() + "("
                        + edge.getEndNode().getIncomingEdge() + ")" + " "
                        + "\t\t" + ((suffixNode == null) ? "-1" : suffixNode)
                        + " " + "\t\t" + edge.getBeginIndex() + " " + "\t\t"
                        + edge.getEndIndex() + " " + "\t\t");
                for (int l = edge.getBeginIndex(); l <= edge.getEndIndex(); l++) {
                    System.out.print(text[l]);
                }
                System.out.println();

                if (edge.getEndNode() != null) {
                    queue.add(edge.getEndNode());
                }
            }
        }
    }

    @Override
    public String toString() {
        String properties = "rankdir=LR; node[shape=box fillcolor=gray95 style=filled]\n";
        return "digraph {\n" + properties + this.root + "}";
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

            this.endNode.setIncomingEdge(this);

        }

        public Node splitEdge(Suffix suffix) {
            remove();
            Edge newEdge = new Edge(beginIndex, beginIndex + suffix.getSpan(),
                    suffix.getOriginNode());
            newEdge.insert();
            newEdge.endNode.setSuffixNode(suffix.getOriginNode());
            beginIndex += suffix.getSpan() + 1;
            startNode = newEdge.getEndNode();
            insert();
            return newEdge.getEndNode();
        }

        public void insert() {
            startNode.addEdge(text[beginIndex], this);
        }

        public void remove() {
            startNode.removeEdge(text[beginIndex]);
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

        // @Override
        // public String toString() {
        // return Integer.toString(endNode.name);
        // }
        public int[] getLabel() {
            return Arrays.copyOfRange(text, beginIndex, endIndex + 1);
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
                Edge edge = originNode.findEdge(text[beginIndex]);

                int edgeSpan = edge.getSpan();
                while (edgeSpan <= getSpan()) {
                    beginIndex += edgeSpan + 1;
                    originNode = edge.getEndNode();
                    if (beginIndex <= endIndex) {
                        edge = edge.getEndNode().findEdge(text[beginIndex]);
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
        private Map<Integer, Edge> edges;
        private int name;
        private Integer stringDepth;
        boolean isRoot = true;
        int nodeDepth = 0;
        private int id;
        private Edge incomingEdge;

        // int stringDepth = 0;
        public Node(Node suffixNode) {
            this.suffixNode = suffixNode;
            edges = new HashMap<Integer, Edge>();

            name = nodesCount++;
        }

        public void addEdge(int ch, Edge edge) {
            edges.put(ch, edge);
        }

        public void removeEdge(int ch) {
            edges.remove(ch);
        }

        public Edge findEdge(int ch) {
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
        // @Override
        // public String toString() {
        // return ((Integer) name).toString();
        // }
        TreeMap<Integer, HashSet<Integer>> annotation = new TreeMap<Integer, HashSet<Integer>>();
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
            if (incomingEdge != null) {
                isRoot = false;
            } else {
                isRoot = true;
            }
            this.incomingEdge = incomingEdge;
            stringDepth = this.incomingEdge.startNode.getStringDepth()
                    + this.incomingEdge.getSpan() + 1;
            nodeDepth = this.incomingEdge.startNode.nodeDepth + 1;
        }

        public int getStringDepth() {
            if (isRoot) {
                return 0;
            } else {
                return stringDepth;
            }
        }

        public String toString() {
            StringBuilder result = new StringBuilder();
            int[] incomingLabel_intArr = isRoot ? new int[0]
                    : this.incomingEdge.getLabel();
            String incomingLabel = Utils.intArrayToString(incomingLabel_intArr);
            if (isRoot) {
                c = 1;
                this.id = 1;
            } else {
                this.id = c;
                // if (this.getNodeDepth() > 1) {
                printTab(result);
                result.append(this.id + "[label=\"" + "(" + printAnnotation()
                        + ")" + "\"];\n");
                // }
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
            for (Integer key : annotation.keySet()) {
                ret += ((char) key.intValue()) + ": ";
                for (int value : annotation.get(key)) {
                    ret += value + " ";
                }
                ret += "\\n";
            }
            return ret;
        }

        private void printTab(StringBuilder result) {
            for (int i = 1; i <= nodeDepth; i++) {
                result.append("\t");
            }
        }
    }
}
