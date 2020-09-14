package Greedy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Greedy.Greedy_BRP2.Edge;

public class ReadInput {
	public static HashMap<Integer, Integer> readBikeData(int i) throws IOException {
		HashMap<Integer, Integer> stationMap = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader("src/DataSet/bike_data"+i+".txt"));
		int availableBikes = Integer.parseInt(reader.readLine());
		String line = "";
		while((line = reader.readLine()) != null) {
			String[] stations = line.split(" ");
			stationMap.put(Integer.parseInt(stations[0]), Integer.parseInt(stations[1]));
		}
		reader.close();
		return stationMap;
	}
	public static HashMap<Integer, ArrayList<Edge>> readTopology(boolean[] visited, int j) throws IOException {
		HashMap<Integer, ArrayList<Edge>> graph = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader("src/DataSet/topology"+j+".txt"));
		String[] nVL = reader.readLine().split(" ");
		graph = createNodes(Integer.parseInt(nVL[0]));
		visited = new boolean[Integer.parseInt(nVL[0])];
		reader.readLine();
		reader.readLine();
		int nE = Integer.parseInt(reader.readLine());
		for(int i = 0; i < nE; i++) {
			String line = reader.readLine();
			addEdgesFromFile(graph, line);
		}	
		reader.close();
		return graph;
	}
	
	public static HashMap<Integer, ArrayList<Edge>> createNodes(int nV) {
		HashMap<Integer, ArrayList<Edge>> graph = new HashMap<>();
		for(int i = 0; i < nV; i++) {
			graph.put(i, new ArrayList<>());
		}
		return graph;
	}
	
	public static void addEdgesFromFile(HashMap<Integer, ArrayList<Edge>> graph, String line) {
		int count = 0;
		for(int i = 0; i < line.length(); i++) {
			if(!line.substring(i, i+1).equals(" ")) {
				count++;
			}else {
				break;
			}
		}
		int node = Integer.parseInt(line.substring(0, count));
		int j = 0;
		for(int i = 0; i < line.length(); i++) {
			count = 0;
			j = 0;
			if(line.substring(i, i+1).equals("(")) {
				i++;
				j = i;
				while(!line.substring(i, i+1).equals(")")) {
					count++;
					i++;
				}
				if(count == 3) {
					int destination = Integer.parseInt(line.substring(j, j+1));
					int duration = Integer.parseInt(line.substring(j+2, j+3));
					if(!haveNeighbor(graph.get(node), destination)) {
						graph.get(node).add(new Edge(destination, duration));
						graph.get(destination).add(new Edge(node, duration));
					}				
				}else if(count == 4){
					int destination = Integer.parseInt(line.substring(j, j+2));
					int duration = Integer.parseInt(line.substring(j+3, j+4));
					if(!haveNeighbor(graph.get(node), destination)) {
						graph.get(node).add(new Edge(destination, duration));
						graph.get(destination).add(new Edge(node, duration));
					}
				}else if(count == 5) {
					int destination = Integer.parseInt(line.substring(j, j+3));
					int duration = Integer.parseInt(line.substring(j+4, j+5));
					if(!haveNeighbor(graph.get(node), destination)) {
						graph.get(node).add(new Edge(destination, duration));
						graph.get(destination).add(new Edge(node, duration));
					}
				}
			}
		}
	}
	
	public static boolean haveNeighbor(ArrayList<Edge> edges, int destination) {
		if(edges == null) {
			return false;
		}
		for(Edge e : edges) {
			if(e.destination == destination) {
				return true;
			}
		}
		return false;
	}
}
