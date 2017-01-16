import java.util.*;
import java.awt.*;

public class Test {
	
	public static void main(String[] args) {
		int n = Integer.parseInt(args[0]);
		int cap = Integer.parseInt(args[1]);
		MakeGraph graph = new MakeGraph(n, cap);
		ArrayList<ArrayList<Integer>> paths = graph.findMaxFlow();
		for (ArrayList<Integer> path : paths) {
			for (int i : path) {
				System.out.print(i);
				System.out.print(" -> ");
			}
			System.out.println(":)");
		}
		System.out.println(graph.getMaxFlow());
		while (true) {
			int i = 0;
		}
	}
}