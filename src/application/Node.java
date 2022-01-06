package application;

import javafx.scene.control.Button;
//these nodes are intended to be in a graph (hence the unique IDs and prev attributes)
public class Node extends Button{
	
	private double shortestDist;
	private double heuristic;
	private boolean isWall;
	private int id; //unique id
	 //so you can search for any node in any graph by its ID
	private static int maxID = 0; //used in case of duplicate ID inputted
	
	public Node(boolean isWall, int id, Graph g) {
		super();
		this.isWall = isWall;
		this.setid(id, g);
		this.shortestDist = -1;
		this.heuristic = 0;
	}
	public Node(int id, Graph g) { //when a node is created, it must have an id and must be part of a graph
		super();
		this.isWall = false;
		this.setid(id, g);
		this.shortestDist = -1;
		this.heuristic = 0;
	}
	
	private void setid(int id, Graph g) { //once id is set for a node, we don't want people changing it to avoid duplicate IDs
		if(!g.getNodeMap().containsKey(id)) {
			this.id = id;
			g.getNodeMap().put(id, this);
			if(id>maxID) {
				maxID = id;
			}
			return;
		}
		this.id = ++maxID; //in the case that the ID inputed already belongs to another node, assign the maxID + 1 to this node because maxID + 1 is guaranteed to not be used already since its 1 + the max used ID value
	}

	public int getid() {
		return this.id;
	}
	
	public boolean getIsWall() {
		return this.isWall;
	}
	
	public void setIsWall(boolean wall) {
		this.isWall = wall;
	}	
	
	public double getShortestDistance() {
		return this.shortestDist;
	}
	
	public void setShortestDistance(double sd) {
		if(sd>-1) {
			this.shortestDist = sd;
		}else {
			System.out.println("error setting shortest distance");
		}
	}	
	
	public double getHeuristic() {
		return this.heuristic;
	}
	
	public void setHeuristic(double h) {
		if(h>-1) {
			this.heuristic = h;
		}else {
			System.out.println("error setting heuristic value");
		}
	}	
	
	public double getCombinedWeight() {
		return this.heuristic+this.shortestDist;
	}
}

