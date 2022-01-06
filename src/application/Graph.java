package application;

import java.util.ArrayList;
import java.util.HashMap;


//class for a graph representing the connection of nodes - graph class does not actually contain nodes, rather it tracks and stores
//the connections between nodes (ie edges between nodes) using node IDs
public class Graph {
	
	private HashMap<Integer, ArrayList<NodeData>> graph;
	private HashMap<Integer, Node> map;
	
	public Graph() {
		graph = new HashMap<Integer, ArrayList<NodeData>>();
		map = new HashMap<Integer, Node>();
	}
	
	public HashMap<Integer, Node> getNodeMap() {
		return map;
	}
	
	public Node getNode(int id) {
		return map.get(id);
	}
	
	public void insertEdge(Node n1, Node n2, double weight) { //inserts edge from n1 to n2 into graph - if edge is already in graph, it updates graph with the new edge
		int id1 = n1.getid(); //n1 is always the start of the edge, and n2 is the end of the edge
		int id2 = n2.getid();
		if(this.graph.get(id1)==null){
			this.graph.put(id1, new ArrayList<NodeData>()); //if node 1 has no adjacent nodes (ie the arrayList has not been allocated in memory), then allocate the arraylist first then add the data of the adjacent node to the list
		}
		for(NodeData nd: this.graph.get(id1)) {
			if(nd.id==id2)
			{
				nd.weight = weight;
				return;
			}
		}
		this.graph.get(id1).add(new NodeData(weight, id2)); // adds a NodeData object for n2 to the linked list for n1 - this.graph.get(id1) is an ArrayList for nodes adjacent to node with ID of id1
	}
	
	public boolean exists(Node n1, Node n2) { //checks if edge exists from n1 to n2
		int id1 = n1.getid(); 
		int id2 = n2.getid();
		for(NodeData nd: this.graph.get(id1)) {
			if(nd.id==id2)
			{
				return true;
			}
		}
		return false;
	}
	
	public void removeEdge(Node n1, Node n2) { //removes edge is edge exists
		int id1 = n1.getid();
		int id2 = n2.getid();
		int index = 0;
		for(NodeData nd: this.graph.get(id1)) {
			if(nd.id==id2)
			{
				this.graph.get(id1).remove(index);
				return;
			}
			index++;
		}
	}
	
	public double getWeight(Node n1, Node n2) {
		//first checks if edge exists between n1 and n2, and if it does, return the weight, else return -1 to indicate no edge exists between the 2 - since all weights must be positive for dijkstras, returning -1 is a suitable flag for no edge existing between n1 and n2
		int id1 = n1.getid();
		int id2 = n2.getid();
		for(NodeData nd: this.graph.get(id1)) {
			if(nd.id==id2)
			{
				return nd.weight;
			}
		}
		return -1;
	}
	
	public ArrayList<Integer> adjacentNodes(Node n) {
		int id = n.getid();
		ArrayList<Integer> rtnlist = new ArrayList<Integer>(); //list being returned.
		ArrayList<NodeData> nodeList = this.graph.get(id);
		for(NodeData nd: nodeList) {
			rtnlist.add(nd.id);
		}
		return rtnlist;
	}
}


