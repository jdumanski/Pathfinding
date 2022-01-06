package application;
import java.util.Comparator;
import java.util.HashMap;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;


//class of static functions that perform pathfinding algorithms
//expolored = red, final pat = light blue
public class Pathfinding{
	public static void dijkstras(Graph g, int startID, int endID, Stack<Integer> shortestPath, Queue<Integer> visitedNodes) {
		Comparator<Node> comparator = new NodeComparator();
		PriorityQueue<Node> pq = new PriorityQueue<Node>(150, comparator); //link this to shortest distance
		HashMap<Integer, Integer> visited = new HashMap<Integer, Integer>(); //container containing IDs of visited nodes
		HashMap<Integer, Integer> prevNode = new HashMap<Integer, Integer>(); //container containing IDs of nodes and their previous nodes
		//HashMap<Integer, Integer> shortestDist = new HashMap<Integer, Integer>(); //container containing IDs of nodes and their current shortest distance to the start node
		//also have to worry about walls (do after)
		g.getNode(startID).setShortestDistance(0);
		pq.add(g.getNode(startID));
		boolean path = true;
		//if you reach a node in the pq with no neighbours that are unvisited 
		while(pq.peek().getid()!=endID) { //ends when end node is at top of queue - then follow path pack to get path - because when you visit a node, you determine its perminanshortest path
			int curr = pq.poll().getid();
			visitedNodes.add(curr);
			for(int id : g.adjacentNodes(g.getNode(curr))){
				if(!visited.containsKey(id)&&!g.getNode(id).getIsWall()) { //if node has not been visited and is not a wall
				 //if node has been visited, its been through and out the pq, meaning its shortest distance has been finalized
					if(g.getNode(id).getShortestDistance()!=-1) { //if distance is not infinity (ie it has been set to a previous distance - so now we are checking if we need to update the distance or not depending if the new distance is shorter
						if((g.getNode(curr).getShortestDistance()+g.getWeight(g.getNode(curr), g.getNode(id))) < g.getNode(id).getShortestDistance()){
							g.getNode(id).setShortestDistance(g.getNode(curr).getShortestDistance()+g.getWeight(g.getNode(curr), g.getNode(id)));
							prevNode.put(id, curr);
						}
					}
					else {
						g.getNode(id).setShortestDistance(g.getNode(curr).getShortestDistance()+g.getWeight(g.getNode(curr), g.getNode(id)));
						prevNode.put(id, curr);
						pq.add(g.getNode(id)); //only add node to PQ once (ie if its being set a distance for the first time)
					}
					
				}
			}
			visited.put(curr, curr);
			if(pq.isEmpty()){
				System.out.println("No Path Possible");
				path = false;
				break;
			}
		}
		if(path) {
			int currID = endID;
			while(currID!=startID) {
				shortestPath.push(currID);
				currID = prevNode.get(currID);
			}
			shortestPath.push(startID);
		}
		
		//declare and fill up queue with node IDs by packing tracking prev nodes, starting from the last node, then the prev node of the last node, etc until the start node
		//then return this queue
		//add start to PQ with a distance of zero
	}
	public static void aStar(Graph g, int startID, int endID, Stack<Integer> shortestPath, Queue<Integer> visitedNodes, int NODE_DIM) {
		//astar is the same a dijkstras, except we also factor in the heuristic value to the priority queue - the only thing the the heuristic does is affect the placement of nodes in the priority queue
		Comparator<Node> comparator = new NodeComparator();
		PriorityQueue<Node> pq = new PriorityQueue<Node>(150, comparator); //link this to shortest distance
		HashMap<Integer, Integer> visited = new HashMap<Integer, Integer>(); //container containing IDs of visited nodes
		HashMap<Integer, Integer> prevNode = new HashMap<Integer, Integer>(); //container containing IDs of nodes and their previous nodes
		//HashMap<Integer, Integer> shortestDist = new HashMap<Integer, Integer>(); //container containing IDs of nodes and their current shortest distance to the start node
		g.getNode(startID).setShortestDistance(0);
		//heuristic set to zero by default
		pq.add(g.getNode(startID));
		boolean path = true;
		//if you reach a node in the pq with no neighbours that are unvisited 
		while(pq.peek().getid()!=endID) { //ends when end node is at top of queue - then follow path pack to get path - because when you visit a node, you determine its perminanshortest path
			int curr = pq.poll().getid();
			visitedNodes.add(curr);
			for(int id : g.adjacentNodes(g.getNode(curr))){
				if(!visited.containsKey(id)&&!g.getNode(id).getIsWall()) { //if node has not been visited and is not a wall
				 //if node has been visited, its been through and out the pq, meaning its shortest distance has been finalized
					if(g.getNode(id).getShortestDistance()!=-1) { //if distance is not infinity (ie it has been set to a previous distance - so now we are checking if we need to update the distance or not depending if the new distance is shorter
						if((g.getNode(curr).getShortestDistance()+g.getWeight(g.getNode(curr), g.getNode(id))) < g.getNode(id).getShortestDistance()){
							g.getNode(id).setShortestDistance(g.getNode(curr).getShortestDistance()+g.getWeight(g.getNode(curr), g.getNode(id)));
							prevNode.put(id, curr);
						}
					}
					else {
						g.getNode(id).setShortestDistance(g.getNode(curr).getShortestDistance()+g.getWeight(g.getNode(curr), g.getNode(id)));
						prevNode.put(id, curr);
						double h = Pathfinding.calcEuclideanHeuristic(id, endID, g, NODE_DIM);
						g.getNode(id).setHeuristic(h);
						pq.add(g.getNode(id)); //only add node to PQ once (ie if its being set a distance for the first time)
					}	
				}
			}
			visited.put(curr, curr);
			if(pq.isEmpty()){
				System.out.println("No Path Possible");
				path = false;
				break;
			}
		}
		if(path) {
			int currID = endID;
			while(currID!=startID) {
				shortestPath.push(currID);
				currID = prevNode.get(currID);
			}
			shortestPath.push(startID);
		}
	}
	//heuristic must not be an overestimate of the distance to ensure shortest path is calculated.
	private static double calcEuclideanHeuristic(int id, int endID, Graph g, int NODE_DIM) {
		double distScale = 10; //scale for dijkstra distances
		double transScale = NODE_DIM; //scale for getTranslateX and getTranslateY distances
		double dx = Math.abs(g.getNode(id).getTranslateX()-g.getNode(endID).getTranslateX())/transScale*distScale;
		double dy = Math.abs(g.getNode(id).getTranslateY()-g.getNode(endID).getTranslateY())/transScale*distScale;
		//double D = 1*distScale;
		//double D2 = Math.sqrt(2)*distScale; 
		//double dist = Math.sqrt(2)*5 * (dx + dy);
		//7 * (deltaX + deltaY)
		//double dist = D*(dx+dy)+(D2-2*D)*Math.min(dx, dy);
		//double dist = 7 * Math.min(dx,dy) + 10*(Math.max(dx,dy) - Math.min(dx,dy));
		double dist = Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2)); //always an underestimate of the actual length of shortest path to node
		//double dist = Math.max(dx, dy);
		return dist;
		//return 0;
	}
}


//next steps are to put this in main so I can do some more things with animation (ie see the algorithm live) and show the algorithm working and shortest path at the same time
//and make it so you can just hold down mouse and spawn walls
//and put condition for no path found