# COMP6651-BRP
A course project for bike repositioning problem using a Greedy algorithm in Java

The Greedy algorithm relies heavily on Djikstra algorithm to find the shortest path from one bike stations to all others node in the graph

The goal is to redistribute the bikes in each station so that all stations become "balanced" (no stations have less than 5 bikes)

An data generator is written to randomly generate the graph with some conditions:
- The resulting graph must be connected and will be undirected at first
- Randomly insert a bike station between two nodes in the graph
- Randomly remove some links between nodes to produce one-way streets such that 40% of the streets are one-way
- There will be 90 bike stations and 50% of them will have less than 5 bikes at the beginning
