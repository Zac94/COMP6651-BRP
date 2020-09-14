package Greedy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import Greedy.Greedy_BRP2.Edge;

public class GraphFunctions {
	public static int countArcs(HashMap<Integer, ArrayList<Edge>> graph) {
		int count = 0;
		for(Entry<Integer, ArrayList<Edge>> e : graph.entrySet()) {
			count += e.getValue().size();
		}
		return count;
	}
	
	public static ArrayList<Integer> show_path(HashMap<Integer, Integer> nodeBefore, int destination) {
		ArrayList<Integer> path = new ArrayList<>();
		int node = destination;
		path.add(node);
		while(nodeBefore.get(node) != null) {
			node = nodeBefore.get(node);
			path.add(node);
		}
		Collections.reverse(path);
		return path;
	}
	
	public static HashMap<Integer, Integer> dijkstra(HashMap<Integer, ArrayList<Edge>> graph, int source, boolean[] visited, HashMap<Integer, Integer> nodeBefore) { 
		HashMap<Integer, Integer> distance = new HashMap<>();
		for(int i = 0; i < graph.size(); i++) {
			distance.put(i, Integer.MAX_VALUE);
		}
		nodeBefore.clear();
		distance.put(source, 0);
		for(int i = 0; i < graph.size(); i++) {
			int min_node = getMinNode(distance, visited);
			visited[min_node] = true;
			for(Edge e : graph.get(min_node)) {
				if(!visited[e.getDestination()] && distance.get(e.getDestination()) > distance.get(min_node) + e.getDuration()) {
					distance.put(e.getDestination(), distance.get(min_node) + e.getDuration());
					nodeBefore.put(e.getDestination(), min_node);
				}
			}
		}
		Arrays.fill(visited, false);
		return distance;
	}
	
	public static int getMinNode(HashMap<Integer, Integer> distance, boolean[] visited) {
		int min = Integer.MAX_VALUE;
		int min_node = -1;
		for(int i = 0; i < visited.length; i++) {
			if(!visited[i] && distance.get(i) < min) {
				min = distance.get(i);
				min_node = i;
			}
		}
		return min_node;
	}
	
	public static boolean checkConnected(HashMap<Integer, ArrayList<Edge>> graph, int node1, int node2) {
		boolean[] visited1 = new boolean[graph.size()];
		boolean[] visited2 = new boolean[graph.size()];
		boolean connected1 = true;
		boolean connected2 = true;
		dfs(graph, node1, visited1);
		dfs(graph, node2, visited2);
		for(int i = 0; i < visited1.length; i++) {
			if(visited1[i] == false) {
				connected1 = false;
			}
			if(visited2[i] == false) {
				connected2 = false;
			}
		}
		if(connected1 && connected2) {
			return true;
		}else {
			return false;
		}
	}
	
	public static void dfs(HashMap<Integer, ArrayList<Edge>> graph, int i, boolean[] visited) {
		visited[i] = true;
		for(Edge e : graph.get(i)) {
			if(!visited[e.destination]) {
				dfs(graph, e.destination, visited);
			}
		}
	}
}
