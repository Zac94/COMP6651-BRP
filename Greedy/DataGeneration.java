package Greedy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import Greedy.Greedy_BRP2.Edge;

public class DataGeneration {
	public static void graphGeneration(HashMap<Integer, ArrayList<Edge>> graph, int row, int column) {
		int currentV = 0;
		graph.put(0, new ArrayList<>());
		currentV++;
		for(int i = 1; i < column+1; i++) {
			graph.put(i, new ArrayList<>());
			currentV++;
			addRandomEdge(graph, i-1, i);
		}
		for(int i = 1; i < row+1; i++) {
			for(int j = 0; j < column+1; j++) {
				graph.put(currentV, new ArrayList<>());
				addRandomEdge(graph, currentV-13, currentV);
				if(j != 0) {
					addRandomEdge(graph, currentV-1, currentV);
				}
				currentV++;
			}
		}
		int arcCount = 524;
		while(arcCount > 200) {
			boolean isRemoved = removeRandomLink(graph);
			if(isRemoved) {
				arcCount--;
			}
		}
	}
	
	public static boolean removeRandomLink(HashMap<Integer, ArrayList<Edge>> graph) {
		int node1 = new Random().nextInt(143);
		int node2 = new Random().nextInt(graph.get(node1).size());
		node2 = graph.get(node1).get(node2).destination;
//		if(haveNeighbor(graph.get(node1), node2)) {
//			boolean isRemoved = removeEdge(graph, node1, node2);	
//			if(isRemoved) {
//				return true;
//			}
//		}
		boolean isRemoved = removeLink(graph, node1, node2);	
		if(isRemoved) {
			return true;
		}
		return false;
	}
	
	public static boolean removeLink(HashMap<Integer, ArrayList<Edge>> graph, int node1, int node2) {
		int index = 0;
		ArrayList<Edge> temp = graph.get(node1);
		for(int i = 0; i < temp.size(); i++) {
			if(temp.get(i).destination == node2) {
				index = i;
			}
		}
		Edge e = new Edge(temp.get(index).destination, temp.get(index).duration);
		if(temp.size() > 1) {
			temp.remove(index);
		}else {
			return false;
		}
		if(!GraphFunctions.checkConnected(graph, node1, node2)) {
			graph.get(node1).add(e);
			return false;
		}
		return true;
	}
	
	public static void addRandomEdge(HashMap<Integer, ArrayList<Edge>> graph, int currentNode, int nextNode) {
		int randomDuration = new Random().nextInt(3)+5;
//		if(!haveNeighbor(graph.get(currentNode), nextNode)){
//			graph.get(currentNode).add(new Edge(nextNode, randomDuration));
//			graph.get(nextNode).add(new Edge(currentNode, randomDuration));
//		}
		graph.get(currentNode).add(new Edge(nextNode, randomDuration));
		graph.get(nextNode).add(new Edge(currentNode, randomDuration));
	}
	
	private static boolean haveNeighbor(ArrayList<Edge> edges, int destination) {
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
	
	public static void outputStations(HashMap<Integer, Integer> stationMap, int i) throws IOException {
		int countLine = 0;
		BufferedWriter write = null;
		String fileName = "src/DataSet/bike_data"+i+".txt";
		write = new BufferedWriter(new FileWriter(fileName, false));
		write.write("540");
		write.newLine();
		for(Entry<Integer, Integer> e : stationMap.entrySet()) {
			write.write(e.getKey() + " " + e.getValue());
			countLine++;
			if(countLine < 90) {
				write.newLine();
			}
		}
		write.close();
	}

	public static void outputGraph(HashMap<Integer, ArrayList<Edge>> graph, HashMap<Integer, Integer> stationMap, int i) throws IOException {
		int arcCount = GraphFunctions.countArcs(graph);
		int lineCount = 0;
		String s = "";
		BufferedWriter write = null;
		String fileName = "src/DataSet/topology"+i+".txt";
		write = new BufferedWriter(new FileWriter(fileName, false));
		write.write("233 " + arcCount);
		write.newLine();
		write.write("90");
		write.newLine();
		for(Entry<Integer, Integer> e : stationMap.entrySet()) {
			s = s + e.getKey() + " ";
		}
		write.write(s.trim());
		write.newLine();
		for(Entry<Integer, ArrayList<Edge>> e : graph.entrySet()) {
			ArrayList<Edge> temp = e.getValue();
			if(temp.size() > 0) {
				lineCount++;
			}
		}
		write.write(Integer.toString(lineCount));
		lineCount = 0;
		write.newLine();
		for(Entry<Integer, ArrayList<Edge>> e : graph.entrySet()) {
			s = "";
			ArrayList<Edge> temp = e.getValue();
			s = s + e.getKey() + " ";
			for(Edge edge : temp) {
				s = s + "(" + edge.destination + " " + edge.duration + ")" + " ";
			}
			write.write(s.trim());
			lineCount++;
			if(lineCount < 233) {
				write.newLine();
			}
		}
		write.close();
	}

	public static HashMap<Integer, Integer> stationGeneration(HashMap<Integer, ArrayList<Edge>> graph) {
		HashMap<Integer, Integer> stationMap = new HashMap<>();
		int decifit = 45;
		int bikeAvailable = 500;
		int node = 143;
		for(int i = 0; i < 45; i++) {
			int randomBike = new Random().nextInt(5);
			stationMap.put(node, randomBike);
			bikeAvailable = bikeAvailable - randomBike;
			node++;
		}
		for(int i = 0; i < 45; i++) {
			int randomBike = new Random().nextInt(11);
			while(randomBike > bikeAvailable) {
				randomBike = new Random().nextInt(11);
			}
			if(randomBike < 5) {
				decifit++;
			}			
			stationMap.put(node, randomBike);
			bikeAvailable = bikeAvailable - randomBike;
			node++;
		}
		while(bikeAvailable > 0) {
			for(Entry<Integer, Integer> entry : stationMap.entrySet()) {
				int bikes = entry.getValue() + 1;
				if(bikes == 5 && decifit - 1 >= 45) {
					decifit--;
					stationMap.put(entry.getKey(), bikes);
					bikeAvailable--;
				}else if(bikes > 5 && bikes <= 10) {	
					stationMap.put(entry.getKey(), bikes);
					bikeAvailable--;
				}else if(bikes < 5){
					stationMap.put(entry.getKey(), bikes);
					bikeAvailable--;
				}
				if(bikeAvailable == 0) {
					break;
				}
			}
		}
		
		insertStations(graph, stationMap);
		
		return stationMap;
	}

	private static void insertStations(HashMap<Integer, ArrayList<Edge>> graph, HashMap<Integer, Integer> stationMap) {
		for(Entry<Integer, Integer> e : stationMap.entrySet()) {
			int node1 = new Random().nextInt(143);
			ArrayList<Edge> temp1 = graph.get(node1);
			int node2Index = new Random().nextInt(temp1.size());
			int node2 = temp1.get(node2Index).destination;
			while((temp1.size() == 1 && stationMap.containsKey(node2)) || stationMap.containsKey(node2)) {
				node1 = new Random().nextInt(143);
				temp1 = graph.get(node1);
				node2Index = new Random().nextInt(temp1.size());
				node2 = temp1.get(node2Index).destination;	
			}
			int twoWay = checkEdge(graph, node1, node2);
			ArrayList<Edge> temp2 = graph.get(node2);
			int node1Index = -1;
			if(twoWay == 0) {
				for(int i = 0; i < temp2.size(); i++) {
					if(temp2.get(i).destination == node1) {
						node1Index = i;
					}
				}
			}
			int duration = temp1.get(node2Index).duration;
			graph.put(e.getKey(), new ArrayList<>());
			if(twoWay == 0) {
				graph.get(node1).add(new Edge(e.getKey(), duration/2));
				graph.get(node2).add(new Edge(e.getKey(), duration/2));
				graph.get(e.getKey()).add(new Edge(node1, duration/2));
				graph.get(e.getKey()).add(new Edge(node2, duration/2));
				graph.get(node1).remove(node2Index);
				graph.get(node2).remove(node1Index);
			}else if(twoWay == 1) {
				graph.get(node1).add(new Edge(e.getKey(), duration/2));
				graph.get(e.getKey()).add(new Edge(node2, duration/2));
				graph.get(node1).remove(node2Index);
			}
		}
	}

	private static boolean checkBetween(HashMap<Integer, ArrayList<Integer>> stationBetween, int node1, int node2) {
		ArrayList<Integer> temp = stationBetween.get(node1);
		for(int i = 0; i < temp.size(); i++) {
			if(temp.get(i) == node2) {
				return true;
			}
		}
		return false;
	}

	private static int checkEdge(HashMap<Integer, ArrayList<Edge>> graph, int node1, int node2) {
		boolean found1 = false;
		boolean found2 = false;
		ArrayList<Edge> temp1 = graph.get(node1);
		ArrayList<Edge> temp2 = graph.get(node2);
		for(int i = 0; i < temp1.size(); i++) {
			if(temp1.get(i).destination == node2) {
				found1 = true;
			}
		}
		
		for(int i = 0; i < temp2.size(); i++) {
			if(temp2.get(i).destination == node1) {
				found2 = true;
			}
		}
		
		if(found1 && found2) {
			return 0;
		}else {
			return 1;
		}
	}
}
