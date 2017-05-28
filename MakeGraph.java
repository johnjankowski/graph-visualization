/** Notes:
*** in process of updating to use graphstream ***
This program uses the graphstream library
Just include the jar files in classpath
for example: javac -classpath "gs-algo-1.0/*:gs-core-1.0/*:gs-ui-1.0/*:lib/*" MakeGraph.java
*/


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/** making a graph and max flow*/

public class MakeGraph {

	private int vertices;
	private HashMap<Integer, ArrayList<Integer>> list;
	private int s;
	private int t;
	private Integer[][] caps;
	private int maxflow;

	public MakeGraph(int n, int maxCap) {
		vertices = n * n;
		list = makeDirectedList(n);
		caps = initializeCaps(vertices, maxCap);
		/* Display d = new Display(); */
		s = 0;
		t = vertices - 1;
		/** test case 
		s = 0;
		t = 3;
		list = new HashMap<Integer, ArrayList<Integer>>();
		for (int i = 0; i < 4; i++) {
			list.put(i, new ArrayList<Integer>());
		}
		list.get(0).add(1);
		list.get(0).add(2);
		list.get(1).add(2);
		list.get(1).add(3);
		list.get(2).add(3);
		caps = new Integer[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (list.containsKey(i)) {
					if (list.get(i).contains(j)) {
						caps[i][j] = 1;
					}
					else {
						caps[i][j] = 0;
					}
				}
				else {
					caps[i][j] = 0;
				}
			}
		} */
		maxflow = 0;
	}

	public ArrayList<ArrayList<Integer>> findMaxFlow() {
		ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> path = findPath();
		while (path != null) {
			paths.add(path);
			int currflow = findFlow(path);
			this.maxflow += currflow;
			createResidual(currflow, path);
			path = findPath();
		}
		return paths;
	}

	public int getMaxFlow() {
		return maxflow;
	}

	public HashMap<Integer, ArrayList<Integer>> getList() {
		return list;
	}

	private ArrayList<Integer> findPath() {
		return bfs(list);
	}

	private int findFlow(ArrayList<Integer> path) {
		int flow = 100000;
		for (int i = 0; i < path.size() - 1; i++) {
			int j = path.get(i);
			int k = path.get(i+1);
			if (caps[j][k] < flow) {
				flow = caps[j][k];
			}
		}
		return flow;
	}

	public Integer[][] getCaps() {
		return caps;
	}

	/* make a method that assigns cpacities to all the edges*/
	private Integer[][] initializeCaps(int vertices, int maxCap) {
		Integer[][] newcaps = new Integer[vertices][vertices];
		Random thisRando = new Random();
		
		for (int i = 0; i < vertices; i++) {
			for (int j = 0; j < vertices; j++) {
				if (list.containsKey(i)) {
					if (list.get(i).contains(j)) {
						newcaps[i][j] = thisRando.nextInt(maxCap) + 1;
					}
					else {
						newcaps[i][j] = 0;
					}
				}
				else {
					newcaps[i][j] = 0;
				}
			}
		}
		return newcaps;
	}
	

	/* make a method that creates redidual graph.... changes the capacities and edges*/
	private void createResidual(int flow, ArrayList<Integer> path) {
		Set<Integer> keys = list.keySet();
		for (int i = 0; i < path.size() - 1; i++) {
			int j = path.get(i);
			int k = path.get(i+1);
			this.caps[j][k] -= flow;
		}
		for (int i = path.size() - 1; i > 0; i--) {
			int j = path.get(i);
			int k = path.get(i-1);
			this.list.get(j).add(k);
			this.caps[j][k] += flow;
		}

	}

	private ArrayList<Integer> bfs(HashMap graph) {
		LinkedList<Node> myqueue = new LinkedList<Node>();
		HashSet<Integer> visited = new HashSet<Integer>();
		Node first = new Node(null, s);
		myqueue.add(first);
		while (!myqueue.isEmpty()) {
			Node curr = myqueue.remove();
			if (curr.val == t) {
				ArrayList<Integer> path = new ArrayList<Integer>();
				while (curr.prev != null) {
					path.add(curr.val);
					curr = curr.prev;
				}
				path.add(curr.val);
				Collections.reverse(path);
				return path;
			}
			visited.add(curr.val);
			for (int i : list.get(curr.val)) {
				if (!visited.contains(i) && caps[curr.val][i] > 0) {
					myqueue.add(new Node(curr, i));
				}
			}
		}
		return null;
	}

	private class Node {

		private Node prev;
		private int val;

		private Node(Node prev, int val) {
			this.prev = prev;
			this.val = val;
		}
	}

	/** Return an adjacency list so it forms an n x n grid with no two vertices having a forward
		and backward edge between them */
	private HashMap<Integer, ArrayList<Integer>> makeDirectedList(Integer n) {
		HashMap<Integer, ArrayList<Integer>> thisList = new HashMap<Integer, ArrayList<Integer>>();
		Random thisRando = new Random();
		for (int i = 0; i < n*n; i++) {
			thisList.put(i, new ArrayList<Integer>());
		}
		LinkedList<Integer> myqueue = new LinkedList<Integer>();
		myqueue.add(0);
		while (!myqueue.isEmpty()) {
			int v = myqueue.remove();
			ArrayList<Integer> edgeChoices = new ArrayList<Integer>();
			int possible_edges = 0;
			if ((((v - 1) / n) == (v / n)) && (v - 1 >= 0)) {
				edgeChoices.add((v - 1));
				possible_edges += 1;
			}
			if (((v + 1) / n) == (v / n)) {
				edgeChoices.add((v + 1));
				possible_edges += 1;
			}
			if (v - n >= 0) {
				edgeChoices.add((v - n));
				possible_edges += 1;
			}
			if (v + n < n*n) {
				edgeChoices.add((v + n));
				possible_edges += 1;
			}
			int num_edges = thisRando.nextInt(possible_edges - 1) + 1;
			int i = 0;
			int deadend = 0;
			while (i < num_edges) {
				int edge_index = thisRando.nextInt(possible_edges);
				int edge = edgeChoices.get(edge_index);
				if (!(thisList.get(v).contains(edge) || thisList.get(edge).contains(v))) {
					//adds edge from v to e
					thisList.get(v).add(edge);
					//adds edge to queue to build rest of graph
					myqueue.add(edge);
					i += 1;
				}
				deadend += 1;
				if (deadend > 16) {
					i = num_edges;
				}
			}
		}
		
		return thisList;
	}


	/** Before using graphstream library 

	public class Display extends JFrame {

		public Display() {
			Panel myPanel = new Panel();
			myPanel.setBackground(java.awt.Color.white);
			myPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			this.setContentPane(myPanel);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	pack();
        	setVisible(true);
		


		public class Panel extends JPanel {

			public Panel() {
				setPreferredSize(new Dimension(10 * MakeGraph.this.vertices, 10 * MakeGraph.this.vertices));
			}

			@Override
        	public void paintComponent(Graphics g) {
        		Graphics2D g2 = (Graphics2D) g;
            	super.paintComponent(g2);
				g2.setColor(Color.blue);
				for (Integer i : MakeGraph.this.list.keySet()) {
					int x_pos = (int) ((i % Math.sqrt(MakeGraph.this.list.size())));
					int y_pos = (int) ((i - (x_pos)) / Math.sqrt(MakeGraph.this.list.size()));
					int scale = (int) (Math.sqrt(MakeGraph.this.vertices) * 10);
					g2.drawOval(x_pos * scale + 10, y_pos * scale + 10, 30, 30);
					g2.fillOval(x_pos * scale + 10, y_pos * scale + 10, 30, 30);
					ArrayList<Integer> edges = MakeGraph.this.list.get(i);
					for (Integer v : edges) {
						//different cases for direction of edge
						int edge_x_pos;
						int edge_y_pos;
						int to_x_pos = (int) ((v % Math.sqrt(MakeGraph.this.list.size())));
						int to_y_pos = (int) ((v - (to_x_pos)) / Math.sqrt(MakeGraph.this.list.size()));
						int width = (Math.max(to_x_pos, x_pos) - Math.min(to_x_pos, x_pos)) * scale;
						int height = (Math.max(to_y_pos, y_pos) - Math.min(to_y_pos, y_pos)) * scale;
						
						if (width == 0) {
							edge_x_pos = x_pos * scale + 10;
							edge_y_pos = (int) ((to_y_pos + y_pos) / 2.0 * scale);
							width = 30;
						}
						else {
							edge_x_pos = (int) ((to_x_pos + x_pos) / 2.0 * scale);
							edge_y_pos = y_pos * scale + 10;
							height = 30;
						}
						g2.drawRect(edge_x_pos, edge_y_pos, width, height);
					}
				}
        	}
		}
	} */
}

