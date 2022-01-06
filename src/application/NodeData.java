package application;



//used in graph class: contains data about what node the node of the list is connected too, and the weight of that edge
public class NodeData {
	public double weight;
	public int id;
	//used in graph class
	public NodeData(double weight, int id) {
		this.weight = weight;
		this.id = id;
	}
}
