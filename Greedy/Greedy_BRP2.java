package Greedy;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;

public class Greedy_BRP2 {
	static class Edge{
		int destination;
		int duration;
		public Edge(int destination, int duration) {
			this.destination = destination;
			this.duration = duration;
		}
		
		public int getDuration() {
			return this.duration;
		}
		
		public int getDestination() {
			return this.destination;
		}
	}
	
	static class Node {
		boolean isStation;
		int label;
		ArrayList<Edge> edges;
		public Node(int label) {
			this.label = label;
			edges = new ArrayList<>();
			isStation = false;
		}
		public void assignStation(boolean isStation) {
			this.isStation = isStation;
		}
		
		public boolean containNeighbor(int node) {
			for(Edge e : edges) {
				if(e.destination == node) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	static HashMap<Integer, HashMap<Integer, Integer>> shortestPaths = new HashMap<>();
	static HashMap<Integer, Integer> shortestToDepot = new HashMap<>();
	static int vehicleCount = 0;
	static HashMap<Integer, ArrayList<Integer>> stationVisited = new HashMap<>();
	static HashMap<Integer, Integer> demandSatisfy = new HashMap<>();
	static int shift = 480;
	static int numBikeToSatisfy = 0;
	static HashMap<Integer, Integer> nodeBefore = new HashMap<>();
	static HashMap<Integer, ArrayList<Edge>> graph = new HashMap<>();
	static HashMap<Integer, Integer> stationMap = new HashMap<>();
	static boolean[] visited = new boolean[233];
	static HashMap<Integer, Integer> distance = new HashMap<>();
	
	public static void main(String[] args) throws IOException {
//		for(int i = 1; i <= 10; i++) {
//			DataGeneration.graphGeneration(graph, 10, 12);
//			stationMap = DataGeneration.stationGeneration(graph);
//			DataGeneration.outputGraph(graph, stationMap, i);
//			DataGeneration.outputStations(stationMap, i);
//			graph.clear();
//			stationMap.clear();
//		}
		
		for(int i = 1; i <= 10; i++) {
			double start = System.currentTimeMillis();
			graph = ReadInput.readTopology(visited, i);
			stationMap = ReadInput.readBikeData(i);
			Greedy2();
			System.out.println("Data set " + i + ": ");
			System.out.println("Number of Vehicles: " + vehicleCount);
			for(Entry<Integer, Integer> e : demandSatisfy.entrySet()) {
				System.out.println("Vehicle " + e.getKey() + " visited " + stationVisited.get(e.getKey()).size() + " stations");
				System.out.println("Vehicle " + e.getKey() + " satisfies: " + e.getValue() + " stations");
			}
			double end = System.currentTimeMillis();
			System.out.println("Computational time: " + (end - start) / 1000 + " seconds");
			System.out.println();
			demandSatisfy.clear();
			vehicleCount = 0;
			graph.clear();
			stationMap.clear();
		}
	}

	private static void Greedy3() {
		for(Entry<Integer, Integer> e : stationMap.entrySet()) {
			HashMap<Integer, Integer> d = new HashMap<>();
			d = GraphFunctions.dijkstra(graph, e.getKey(), visited, nodeBefore);
			shortestPaths.put(e.getKey(), new HashMap<>());
			shortestPaths.put(e.getKey(), d);
		}
		ArrayList<Integer> path = new ArrayList<>();
		int currentNode = 0;
		int bikesOnVehicle = 0;
		int duration = 0;
		int maxStation = 0;
		numBikeToSatisfy = getNumBikeToSatify();
		while(checkDemand() == false) {
			currentNode = 0;
			int satisfyCount = 0;
			vehicleCount++;
			stationVisited.put(vehicleCount, new ArrayList<>());
			duration = 0;
			HashMap<Integer, Integer> decifit = getDecifitStations();
			if(bikesOnVehicle < numBikeToSatisfy) {
				path.clear();
				path.add(0);
				distance = GraphFunctions.dijkstra(graph, currentNode, visited, nodeBefore);
				maxStation = closestStationWithMostBike();
				duration += distance.get(maxStation);
				bikesOnVehicle += Math.abs(5 - stationMap.get(maxStation));
				if(bikesOnVehicle > numBikeToSatisfy) {
					bikesOnVehicle = numBikeToSatisfy;
				}
				stationMap.put(maxStation, stationMap.get(maxStation) - (Math.abs(5 - stationMap.get(maxStation))));
				path.add(maxStation);
				updateVisitedStation(maxStation);
				distance = GraphFunctions.dijkstra(graph, maxStation, visited, nodeBefore);
				currentNode = maxStation;
			}else {
				path.clear();
				path.add(0);
				distance = GraphFunctions.dijkstra(graph, 0, visited, nodeBefore);
			}
			while(bikesOnVehicle < numBikeToSatisfy) {
				distance = GraphFunctions.dijkstra(graph, currentNode, visited, nodeBefore);
				int difference = numBikeToSatisfy - bikesOnVehicle;
				int nextStation = closestStationWithMostBike();
				duration += shortestPaths.get(currentNode).get(nextStation);
				currentNode = nextStation;
				if(stationMap.get(nextStation) - difference >= 5) {
					if(bikesOnVehicle + difference <= 10) {
						bikesOnVehicle += difference;
						stationMap.put(nextStation, stationMap.get(nextStation) - difference);
					}else {
						stationMap.put(nextStation, stationMap.get(nextStation) - (10 - bikesOnVehicle));
						bikesOnVehicle += 10 - bikesOnVehicle;
					}
					
				}else {
					if(bikesOnVehicle + Math.abs(5 - stationMap.get(nextStation)) <= 10) {
						bikesOnVehicle += Math.abs(5 - stationMap.get(nextStation));
						stationMap.put(nextStation, stationMap.get(nextStation) - Math.abs(5 - stationMap.get(nextStation)));
					}else {
						stationMap.put(nextStation, stationMap.get(nextStation) - (10 - bikesOnVehicle));
						bikesOnVehicle += (10 - bikesOnVehicle);         
					}
				}
				path.add(nextStation);
				updateVisitedStation(nextStation);
				if(bikesOnVehicle == 10) {
					break;
				}
			}
			while(decifit.size() > 0) {
				int closestDecifit = getClosestDecifitStation(decifit, currentNode);
				duration += shortestPaths.get(currentNode).get(closestDecifit);
				path.add(closestDecifit);
				updateVisitedStation(closestDecifit);
				currentNode = closestDecifit;
			}
			path.add(0);
			duration += shortestPaths.get(currentNode).get(0);
			if(duration > 480) {
				decifit = getDecifitStations();
				ArrayList<Integer> newPath = reducePath(path, duration, 3);
				removeVisitedStation(newPath);
				for(int i = 1; i < newPath.size() - 1; i++) {
					if(decifit.containsKey(newPath.get(i))) {
						int needed = 5 - decifit.get(newPath.get(i));
						if(needed <= bikesOnVehicle) {
							stationMap.put(newPath.get(i), stationMap.get(newPath.get(i)) + needed);
							bikesOnVehicle -= needed;
							numBikeToSatisfy -= needed;
							satisfyCount++;
							demandSatisfy.put(vehicleCount, satisfyCount);
						}else {
							stationMap.put(newPath.get(i), stationMap.get(newPath.get(i)) + bikesOnVehicle);
							numBikeToSatisfy -= bikesOnVehicle;
							bikesOnVehicle = 0;
							demandSatisfy.put(vehicleCount, satisfyCount);
						}
						if(bikesOnVehicle == 0) {
							break;
						}
					}
				}
			}else {
				decifit = getDecifitStations();
				for(int i = 1; i < path.size() - 1; i++) {
					if(decifit.containsKey(path.get(i))) {
						int needed = 5 - decifit.get(path.get(i));
						if(bikesOnVehicle < needed) {
							stationMap.put(path.get(i), stationMap.get(path.get(i)) + bikesOnVehicle);
							numBikeToSatisfy -= bikesOnVehicle;
							bikesOnVehicle = 0;
						}else {
							stationMap.put(path.get(i), stationMap.get(path.get(i)) + needed);
							bikesOnVehicle -= needed;
							numBikeToSatisfy -= needed;
						}										
						if(stationMap.get(path.get(i)) >= 5) {
							satisfyCount++;
						}
						demandSatisfy.put(vehicleCount, satisfyCount);
						if(bikesOnVehicle == 0) {
							break;
						}
					}
				}
			}
		}
	}
	
	
	private static void Greedy2() {
		for(Entry<Integer, Integer> e : stationMap.entrySet()) {
			distance = GraphFunctions.dijkstra(graph, e.getKey(), visited, nodeBefore);
			shortestToDepot.put(e.getKey(), distance.get(0));
		}
		ArrayList<Integer> path = new ArrayList<>();
		int currentNode = 0;
		int bikesOnVehicle = 0;
		int duration = 0;
		int maxStation = 0;
		numBikeToSatisfy = getNumBikeToSatify();
		while(checkDemand() == false) {
			currentNode = 0;
			int satisfyCount = 0;
			vehicleCount++;
			stationVisited.put(vehicleCount, new ArrayList<>());
			duration = 0;
			HashMap<Integer, Integer> decifit = getDecifitStations();
			if(bikesOnVehicle < numBikeToSatisfy) {
				path.clear();
				path.add(0);
				distance = GraphFunctions.dijkstra(graph, currentNode, visited, nodeBefore);
				maxStation = closestStationWithMostBike();
				duration += distance.get(maxStation);
				bikesOnVehicle += Math.abs(5 - stationMap.get(maxStation));
				if(bikesOnVehicle > numBikeToSatisfy) {
					bikesOnVehicle = numBikeToSatisfy;
				}
				stationMap.put(maxStation, stationMap.get(maxStation) - (Math.abs(5 - stationMap.get(maxStation))));
				path.add(maxStation);
				updateVisitedStation(maxStation);
				distance = GraphFunctions.dijkstra(graph, maxStation, visited, nodeBefore);
				currentNode = maxStation;
			}else {
				path.clear();
				path.add(0);
				distance = GraphFunctions.dijkstra(graph, 0, visited, nodeBefore);
			}
			while(bikesOnVehicle < numBikeToSatisfy) {
				distance = GraphFunctions.dijkstra(graph, currentNode, visited, nodeBefore);
				int difference = numBikeToSatisfy - bikesOnVehicle;
				int nextStation = closestStationWithMostBike();
				currentNode = nextStation;
				duration += distance.get(nextStation);
				if(stationMap.get(nextStation) - difference >= 5) {
					if(bikesOnVehicle + difference <= 10) {
						bikesOnVehicle += difference;
						stationMap.put(nextStation, stationMap.get(nextStation) - difference);
					}else {
						stationMap.put(nextStation, stationMap.get(nextStation) - (10 - bikesOnVehicle));
						bikesOnVehicle += 10 - bikesOnVehicle;
					}
					
				}else {
					if(bikesOnVehicle + Math.abs(5 - stationMap.get(nextStation)) <= 10) {
						bikesOnVehicle += Math.abs(5 - stationMap.get(nextStation));
						stationMap.put(nextStation, stationMap.get(nextStation) - Math.abs(5 - stationMap.get(nextStation)));
					}else {
						stationMap.put(nextStation, stationMap.get(nextStation) - (10 - bikesOnVehicle));
						bikesOnVehicle += (10 - bikesOnVehicle);         
					}
				}
				path.add(nextStation);
				updateVisitedStation(nextStation);
				distance = GraphFunctions.dijkstra(graph, nextStation, visited, nodeBefore);
				if(bikesOnVehicle == 10) {
					break;
				}
			}
			while(decifit.size() > 0) {
				int closestDecifit = getClosestDecifitStation(decifit, 2);
				duration += distance.get(closestDecifit);
				path.add(closestDecifit);
				updateVisitedStation(closestDecifit);
				distance = GraphFunctions.dijkstra(graph, closestDecifit, visited, nodeBefore);
			}
			path.add(0);
			duration += distance.get(0);
			if(duration > 480) {
				decifit = getDecifitStations();
				ArrayList<Integer> newPath = reducePath(path, duration, 2);
				removeVisitedStation(newPath);
				for(int i = 1; i < newPath.size() - 1; i++) {
					if(decifit.containsKey(newPath.get(i))) {
						int needed = 5 - decifit.get(newPath.get(i));
						if(needed <= bikesOnVehicle) {
							stationMap.put(newPath.get(i), stationMap.get(newPath.get(i)) + needed);
							bikesOnVehicle -= needed;
							numBikeToSatisfy -= needed;
							satisfyCount++;
							demandSatisfy.put(vehicleCount, satisfyCount);
						}else {
							stationMap.put(newPath.get(i), stationMap.get(newPath.get(i)) + bikesOnVehicle);
							numBikeToSatisfy -= bikesOnVehicle;
							bikesOnVehicle = 0;
							demandSatisfy.put(vehicleCount, satisfyCount);
						}
						if(bikesOnVehicle == 0) {
							break;
						}
					}
				}
			}else {
				decifit = getDecifitStations();
				for(int i = 1; i < path.size() - 1; i++) {
					if(decifit.containsKey(path.get(i))) {
						int needed = 5 - decifit.get(path.get(i));
						if(bikesOnVehicle < needed) {
							stationMap.put(path.get(i), stationMap.get(path.get(i)) + bikesOnVehicle);
							numBikeToSatisfy -= bikesOnVehicle;
							bikesOnVehicle = 0;
						}else {
							stationMap.put(path.get(i), stationMap.get(path.get(i)) + needed);
							bikesOnVehicle -= needed;
							numBikeToSatisfy -= needed;
						}										
						if(stationMap.get(path.get(i)) >= 5) {
							satisfyCount++;
						}
						demandSatisfy.put(vehicleCount, satisfyCount);
						if(bikesOnVehicle == 0) {
							break;
						}
					}
				}
			}
		}
	}
	
	private static void Greedy1() {
		ArrayList<Integer> path = new ArrayList<>();
		int currentNode = 0;
		int bikesOnVehicle = 0;
		int duration = 0;
		int maxStation = 0;
		numBikeToSatisfy = getNumBikeToSatify();
		while(checkDemand() == false) {
			currentNode = 0;
			int satisfyCount = 0;
			vehicleCount++;
			stationVisited.put(vehicleCount, new ArrayList<>());
			duration = 0;
			HashMap<Integer, Integer> decifit = getDecifitStations();
			if(bikesOnVehicle < numBikeToSatisfy) {
				path.clear();
				path.add(0);
				distance = GraphFunctions.dijkstra(graph, currentNode, visited, nodeBefore);
				maxStation = closestStationWithMostBike();
				duration += distance.get(maxStation);
				bikesOnVehicle += Math.abs(5 - stationMap.get(maxStation));
				if(bikesOnVehicle > numBikeToSatisfy) {
					bikesOnVehicle = numBikeToSatisfy;
				}
				stationMap.put(maxStation, stationMap.get(maxStation) - (Math.abs(5 - stationMap.get(maxStation))));
				path.add(maxStation);
				updateVisitedStation(maxStation);
				distance = GraphFunctions.dijkstra(graph, maxStation, visited, nodeBefore);
				currentNode = maxStation;
			}else {
				path.clear();
				path.add(0);
				distance = GraphFunctions.dijkstra(graph, 0, visited, nodeBefore);
			}
			while(bikesOnVehicle < numBikeToSatisfy) {
				distance = GraphFunctions.dijkstra(graph, currentNode, visited, nodeBefore);
				int difference = numBikeToSatisfy - bikesOnVehicle;
				int nextStation = closestStationWithMostBike();
				currentNode = nextStation;
				duration += distance.get(nextStation);
				if(stationMap.get(nextStation) - difference >= 5) {
					if(bikesOnVehicle + difference <= 10) {
						bikesOnVehicle += difference;
						stationMap.put(nextStation, stationMap.get(nextStation) - difference);
					}else {
						stationMap.put(nextStation, stationMap.get(nextStation) - (10 - bikesOnVehicle));
						bikesOnVehicle += 10 - bikesOnVehicle;
					}
					
				}else {
					if(bikesOnVehicle + Math.abs(5 - stationMap.get(nextStation)) <= 10) {
						bikesOnVehicle += Math.abs(5 - stationMap.get(nextStation));
						stationMap.put(nextStation, stationMap.get(nextStation) - Math.abs(5 - stationMap.get(nextStation)));
					}else {
						stationMap.put(nextStation, stationMap.get(nextStation) - (10 - bikesOnVehicle));
						bikesOnVehicle += (10 - bikesOnVehicle);         
					}
				}
				path.add(nextStation);
				updateVisitedStation(nextStation);
				distance = GraphFunctions.dijkstra(graph, nextStation, visited, nodeBefore);
				if(bikesOnVehicle == 10) {
					break;
				}
			}
			while(decifit.size() > 0) {
				int closestDecifit = getClosestDecifitStation(decifit, 1);
				duration += distance.get(closestDecifit);
				path.add(closestDecifit);
				updateVisitedStation(closestDecifit);
				distance = GraphFunctions.dijkstra(graph, closestDecifit, visited, nodeBefore);
			}
			path.add(0);
			duration += distance.get(0);
			if(duration > 480) {
				decifit = getDecifitStations();
				ArrayList<Integer> newPath = reducePath(path, duration, 1);
				removeVisitedStation(newPath);
				for(int i = 1; i < newPath.size() - 1; i++) {
					if(decifit.containsKey(newPath.get(i))) {
						int needed = 5 - decifit.get(newPath.get(i));
						if(needed <= bikesOnVehicle) {
							stationMap.put(newPath.get(i), stationMap.get(newPath.get(i)) + needed);
							bikesOnVehicle -= needed;
							numBikeToSatisfy -= needed;
							satisfyCount++;
							demandSatisfy.put(vehicleCount, satisfyCount);
						}else {
							stationMap.put(newPath.get(i), stationMap.get(newPath.get(i)) + bikesOnVehicle);
							numBikeToSatisfy -= bikesOnVehicle;
							bikesOnVehicle = 0;
							demandSatisfy.put(vehicleCount, satisfyCount);
						}
						if(bikesOnVehicle == 0) {
							break;
						}
					}
				}
			}else {
				decifit = getDecifitStations();
				for(int i = 1; i < path.size() - 1; i++) {
					if(decifit.containsKey(path.get(i))) {
						int needed = 5 - decifit.get(path.get(i));
						if(bikesOnVehicle < needed) {
							stationMap.put(path.get(i), stationMap.get(path.get(i)) + bikesOnVehicle);
							numBikeToSatisfy -= bikesOnVehicle;
							bikesOnVehicle = 0;
						}else {
							stationMap.put(path.get(i), stationMap.get(path.get(i)) + needed);
							bikesOnVehicle -= needed;
							numBikeToSatisfy -= needed;
						}										
						if(stationMap.get(path.get(i)) >= 5) {
							satisfyCount++;
						}
						demandSatisfy.put(vehicleCount, satisfyCount);
						if(bikesOnVehicle == 0) {
							break;
						}
					}
				}
			}
		}
	}

	private static int getCurrentNeededDemand(ArrayList<Integer> newPath, HashMap<Integer, Integer> decifit) {
		int bikes = 0;
		for(int i = 1; i < newPath.size() - 1; i++) {
			if(decifit.containsKey(newPath.get(i))) {
				int currentStationBikes = decifit.get(newPath.get(i));
				bikes = bikes + (5 - currentStationBikes);
			}
		}
		return bikes;
	}

	private static void removeVisitedStation(ArrayList<Integer> newPath) {
		ArrayList<Integer> temp = stationVisited.get(vehicleCount);
		temp.removeIf(s -> (!newPath.contains(s)));
	}

	private static void updateVisitedStation(int nextStation) {
		stationVisited.get(vehicleCount).add(nextStation);
	}

	private static ArrayList<Integer> reducePath(ArrayList<Integer> path, int duration, int greedy) {
		int count = 1;
		int stop = 0;
		ArrayList<Integer> newPath = new ArrayList<>();
		newPath.clear();
		int newDuration = 0;
		if(greedy == 1 || greedy == 2) {
			for(int i = 0; i < path.size() - 1; i++) {
				newPath.add(path.get(i));
				distance = GraphFunctions.dijkstra(graph, path.get(i), visited, nodeBefore);
				int nextNode = path.get(i+1);
				newDuration += distance.get(nextNode);
				if(newDuration > 480) {
					newDuration -= distance.get(nextNode);
					if(greedy == 1) {
						newDuration += distance.get(0);
					}else {
						newDuration += shortestToDepot.get(path.get(i));
					}
					stop = i;
					break;
				}
			}
			while(newDuration > 480) {
				newDuration -= distance.get(0);
				distance = GraphFunctions.dijkstra(graph, path.get(stop-count), visited, nodeBefore);
				newDuration -= distance.get(newPath.get(newPath.size()-1));
				if(greedy == 1) {
					newDuration += distance.get(0);
				}else {
					newDuration += shortestToDepot.get(path.get(stop-count));
				}
				count++;
				newPath.remove(newPath.size()-1);
			}
		}else {
			distance = GraphFunctions.dijkstra(graph, 0, visited, nodeBefore);
			for(int i = 0; i < path.size() - 1; i++) {
				newPath.add(path.get(i));
				int nextNode = path.get(i+1);
				if(path.get(i) == 0) {
					newDuration += distance.get(nextNode);
				}else {
					newDuration += shortestPaths.get(path.get(i)).get(nextNode);
				}
				if(newDuration > 480) {
					newDuration -= shortestPaths.get(path.get(i)).get(nextNode);
					newDuration += shortestPaths.get(path.get(i)).get(0);
					stop = i;
					break;
				}
			}
			while(newDuration > 480) {
				newDuration -= shortestPaths.get(path.get(stop-count+1)).get(0);
				newDuration -= shortestPaths.get(newPath.get(newPath.size()-1)).get(path.get(stop-count));
				newDuration += shortestPaths.get(path.get(stop-count)).get(0);
				count++;
				newPath.remove(newPath.size()-1);
			}
		}	
		newPath.add(0);
		return newPath;
	}

	private static int getClosestDecifitStation(HashMap<Integer, Integer> decifit, int greedy) {
		int min = Integer.MAX_VALUE;
		int closestDecifit = 0;
		if(greedy == 1 || greedy == 2) {
			for(Entry<Integer, Integer> e : decifit.entrySet()) {
				if(distance.get(e.getKey()) < min) {
					min = distance.get(e.getKey());
					closestDecifit = e.getKey();
				}
			}
		}else {
			for(Entry<Integer, Integer> e : decifit.entrySet()) {
				if(shortestPaths.get(greedy).get(e.getKey()) < min) {
					min = shortestPaths.get(greedy).get(e.getKey());
					closestDecifit = e.getKey();
				}
			}
		}		
		decifit.remove(closestDecifit);
		return closestDecifit;
	}

	private static int getNumBikeToSatify() {
		int bikes = 0;
		for(Entry<Integer, Integer> e : stationMap.entrySet()) {
			if(e.getValue() < 5) {
				bikes += 5 - e.getValue();
			}
		}
		return bikes;
	}

	private static int closestStationWithMostBike() {
		int currentMax = getMaxBike();
		int minDistance = Integer.MAX_VALUE;
		int max = 0;
		int maxStation = 0;
		for(Entry<Integer, Integer> e : stationMap.entrySet()){
			if(e.getValue() > max) {
				max = e.getValue();
			}
		}
		for(Entry<Integer, Integer> e : distance.entrySet()) {
			if(stationMap.containsKey(e.getKey()) && stationMap.get(e.getKey()) == max) {
				if(distance.get(e.getKey()) < minDistance) {
					max = stationMap.get(e.getKey());
					maxStation = e.getKey();
					minDistance = distance.get(e.getKey());
				}		
			}
		}
		return maxStation;
	}

	private static int getMaxBike() {
		int max = 0;
		for(Entry<Integer, Integer> e : stationMap.entrySet()) {
			if(e.getValue() > max) {
				max = e.getValue();
			}
		}
		return max;
	}

	private static HashMap<Integer, Integer> getDecifitStations() {
		HashMap<Integer, Integer> decifit = new HashMap<>();
		for(Entry<Integer, Integer> e : stationMap.entrySet()) {
			if(e.getValue() < 5) {
				decifit.put(e.getKey(), e.getValue());
			}
		}
		return decifit;
	}

	private static boolean checkDemand() {
		for(Entry<Integer, Integer> e : stationMap.entrySet()) {
			if(e.getValue() < 5) {
				return false;
			}
		}
		return true;
	}
}
