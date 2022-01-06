package application;
	

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//Other features
//user can add dimensions of graph, display shortest path distance and number of nodes visited, reset graph

public class Main extends Application {
	
	public enum State { //state of the board
		off, //applications just starts and no buttons have been pressed
		wall, //state when user can place walls on board
		searching, //state when algorithm is in play (board cannot be interacted with by user)
		startNode, //state when user selects start and end node
		endNode
	}
	
	State state = State.off;
	
	//for user input, ROWS and COLS must both be > 2
	final int ROWS = 40;//40, 150
	final int COLS = 80;//80, 380
	final int NODE_DIM = 20;//20, 5
	
	Node startingNode = null;
	Node endingNode = null;
	
	public enum Algorithm{
		dijkstras,
		aStar
	}
	
	Algorithm algo;
	
	@Override
	public void start(Stage primaryStage) {
		final int WIDTH = 1000;
		final int HEIGHT = 600;
		
		StackPane root = new StackPane();
		Scene scene = new Scene(root, WIDTH, HEIGHT); //width, height
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		
		setUp(root);
		
		primaryStage.setTitle("Pathfinding");
		primaryStage.setMaximized(true);
		primaryStage.show();
	}
	
	public Graph graphSetUp(StackPane root, Text mssg, Text mssg2, Button startSearch, Button pre, Button rand) {
		Graph g = new Graph();
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				int id = row*COLS + col;
				Node newNode = new Node(id, g);
				insertIntoGraph(id, g);
				/*
				if(id%2==0) {
					newNode.setIsWall(true);
					newNode.setStyle("-fx-background-color: Black");
				}
				*/
				newNode.setPrefHeight(NODE_DIM+1);
				newNode.setPrefWidth(NODE_DIM+1);
				newNode.setTranslateX(NODE_DIM*col-COLS/2.0*NODE_DIM+NODE_DIM/2.0);
				newNode.setTranslateY(NODE_DIM*row-300);
				newNode.getStyleClass().add("node-style");
				root.getChildren().add(newNode);
				newNode.setOnAction(event -> {
					switch(state) {
						case startNode:
							if(!newNode.getIsWall()) {
								startingNode = newNode;
								startingNode.setStyle("-fx-background-color: Green");
								state = State.endNode;
								mssg.setText("Select end node");
								root.getChildren().remove(pre);
								root.getChildren().remove(rand);
							}
							
							break;
						case endNode:
							if(!startingNode.equals(newNode)&&!newNode.getIsWall()) {
								endingNode = newNode;
								endingNode.setStyle("-fx-background-color: Purple");
								state = State.wall;
								mssg.setText("Select Walls");
								root.getChildren().add(startSearch);
							}
							break;
						case wall:
							if(!startingNode.equals(newNode)&&!endingNode.equals(newNode)) {
								newNode.setIsWall(true);
								newNode.setStyle("-fx-background-color: Black");
							}
							break;
						case searching: //need searching and off because they are in enum (but in both cases graph nodes are disabled for user interaction, hence there is nothing)
						case off:
					}
				});
			}
		}
		startSearch.setOnAction(event -> {
			state = State.searching;
			root.getChildren().remove(startSearch);
			root.getChildren().add(mssg2);
			Stack<Integer> shortestPath = new Stack<Integer>();
			Queue<Integer> visitedNodes = new LinkedList<Integer>();
			switch(algo) {
				case dijkstras:
					Pathfinding.dijkstras(g, startingNode.getid(), endingNode.getid(), shortestPath, visitedNodes);
				break;
				case aStar:
					//Pathfinding.dijkstras(g, startingNode.getid(), endingNode.getid(), shortestPath, visitedNodes);
					Pathfinding.aStar(g, startingNode.getid(), endingNode.getid(), shortestPath, visitedNodes, NODE_DIM);
				break;
			}
			
			displayAlgorithm(visitedNodes, shortestPath, g, mssg, mssg2);
		});
		return g;
	}
	
	public void displayAlgorithm(Queue<Integer> nodes, Stack<Integer> path, Graph g, Text mssg, Text mssg2) {
		int nodesSearched = nodes.size();
		Timeline t1 = new Timeline(
			new KeyFrame(Duration.seconds(0.004), //0.004 is good speed 
            new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					int index = nodes.remove();
		    	    Node curr = g.getNode(index);
		    	    if(index!=startingNode.getid()) {
		    	    	curr.setStyle("-fx-background-color: Blue; -fx-opacity: 1.0;");
		    	    	FadeTransition ft = new FadeTransition(Duration.millis(3000), curr);
		    	    	ft.setFromValue(0.1);
						ft.setToValue(1.0);
						ft.setCycleCount(1);
						ft.setAutoReverse(true);
						ft.play();	
		    	    }
				}
		}));
		t1.setCycleCount(nodes.size());
		t1.play();
		if(!path.isEmpty()) {
			displayPath(path, g, t1, mssg, mssg2, nodesSearched);	
		}
		else {
			t1.setOnFinished(e -> mssg.setText("No Path Found :("));
		}
		
	}
	public double distBetweenAdjNodes(Node n1, Node n2) {
		double dx = n1.getTranslateX()-n2.getTranslateX();
		double dy = n1.getTranslateY()-n2.getTranslateY();
		return Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2))/NODE_DIM*10;
	}
	double size = 0;
	public void displayPath(Stack<Integer> path, Graph g, Timeline t1, Text mssg, Text mssg2, int nodesSearched) {
		
		double speed = 0.05;
		if(path.size()>70) {
			speed = 0.02;
		}
		else if(path.size()>20) {
			speed = 0.035;
		}
		Timeline t2 = new Timeline(
			new KeyFrame(Duration.seconds(speed), 
            new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					int index = path.pop();
		    	    Node curr = g.getNode(index);
		    	    if(!path.isEmpty()) {
		    	    	Node next = g.getNode(path.peek());
		    	    	size += distBetweenAdjNodes(curr, next);
		    	    }
	    	    	curr.setStyle("-fx-background-color: Yellow");
	    	    	FadeTransition ft = new FadeTransition(Duration.millis(500), curr);
	    	    	ft.setFromValue(0.4);
					ft.setToValue(1.0);
					ft.setCycleCount(1);
					ft.setAutoReverse(true);
					ft.play();	 
				}
		}));
		t2.setCycleCount(path.size());
		t1.setOnFinished(e -> t2.play());
		t2.setOnFinished(e -> {
			mssg.setText("Path Found! :)");
			mssg2.setText("Path is " + roundDec(size/10.0, 2) +" units long\n" + nodesSearched +" nodes searched");
			
		});
	}
	
	public double roundDec(double n, int r) {
		return Math.round(n*Math.pow(10, r))/100.0;
	}
	
	public void setUp(StackPane root) {
		final int bH = 40;
		final int bW = 250;
		final int bX = 250;
		final int bY = -360;
		
		Button dijkstrasBttn = new Button("Dijkstras");
		dijkstrasBttn.setPrefHeight(bH);
		dijkstrasBttn.setPrefWidth(bW);
		dijkstrasBttn.setTranslateX(-bX);
		dijkstrasBttn.setTranslateY(bY);
		dijkstrasBttn.getStyleClass().add("button-style");
		root.getChildren().add(dijkstrasBttn);
		
		Button aStarBttn = new Button("A* Algorithm");
		aStarBttn.setPrefHeight(bH);
		aStarBttn.setPrefWidth(bW);
		aStarBttn.setTranslateX(bX);
		aStarBttn.setTranslateY(bY);
		aStarBttn.getStyleClass().add("button-style");
		root.getChildren().add(aStarBttn);
		
		Button startSearch = new Button("Start Search");
		startSearch.setPrefHeight(bH);
		startSearch.setPrefWidth(bW);
		startSearch.setTranslateX(0);
		startSearch.setTranslateY(bY);
		startSearch.getStyleClass().add("button-style");
		
		Button presetLayout = new Button("Preset Wall Layout");
		presetLayout.setPrefHeight(bH);
		presetLayout.setPrefWidth(bW*1.3);
		presetLayout.setTranslateX(-bX);
		presetLayout.setTranslateY(bY);
		presetLayout.getStyleClass().add("button-style");
		
		Button randomLayout = new Button("Random Wall Layout");
		randomLayout.setPrefHeight(bH);
		randomLayout.setPrefWidth(bW*1.3);
		randomLayout.setTranslateX(bX);
		randomLayout.setTranslateY(bY);
		randomLayout.getStyleClass().add("button-style");
		
		Text title = new Text("Pathfinding Program");
		title.setTranslateX(0); 
		title.setTranslateY(-470);
		title.setFont(Font.font ("Verdana",FontWeight.BOLD, 50));
		title.setFill(javafx.scene.paint.Color.rgb(130, 0, 0));
		root.getChildren().add(title);
		
		Text mssg = new Text("Choose an Algorithm!");
		mssg.setTranslateX(0); 
		mssg.setTranslateY(-420);
		mssg.setFont(Font.font ("Verdana",FontWeight.BOLD, 30));
		mssg.setFill(javafx.scene.paint.Color.DARKRED);
		root.getChildren().add(mssg);
		
		Text mssg2 = new Text();
		mssg2.setTranslateX(0); 
		mssg2.setTranslateY(-350);
		mssg2.setFont(Font.font ("Verdana",FontWeight.BOLD, 30));
		mssg2.setFill(javafx.scene.paint.Color.DARKRED);
		
		//algorithm button actions:
		dijkstrasBttn.setOnAction(event -> {
			root.getChildren().remove(aStarBttn);
			root.getChildren().remove(dijkstrasBttn);
			root.getChildren().add(presetLayout);
			root.getChildren().add(randomLayout);
			mssg.setText("Select start node or a preset/random layout");
			state = State.startNode;
			algo = Algorithm.dijkstras;
			//setWalls(mssg);
		});
		aStarBttn.setOnAction(event -> {
			root.getChildren().remove(aStarBttn);
			root.getChildren().remove(dijkstrasBttn);
			root.getChildren().add(presetLayout);
			root.getChildren().add(randomLayout);
			mssg.setText("Select start node or a preset/random layout");
			state = State.startNode;
			algo = Algorithm.aStar;
			//implement after dijkstras
		});
		
		Graph g = graphSetUp(root, mssg, mssg2, startSearch, presetLayout, randomLayout);
		
		presetLayout.setOnAction(event -> {
			state = State.searching;
			root.getChildren().add(startSearch);
			root.getChildren().remove(presetLayout);
			root.getChildren().remove(randomLayout);
			mssg.setText("");
			// set up start, end , and walls
		});
		
		randomLayout.setOnAction(event -> {
			root.getChildren().remove(presetLayout);
			root.getChildren().remove(randomLayout);
			mssg.setText("");
			for (Map.Entry<Integer, Node> mapElement : g.getNodeMap().entrySet()) {
				double wall = Math.random();
				int wallInt = 0;
				if(wall<(1.0/2)) {
					wallInt = 1;
				}
				//wall = Math.round(wall);
				//int wallInt = (int)wall;
				boolean wallBool = false;
				if(wallInt==1) {
					wallBool = true;
					mapElement.getValue().setStyle("-fx-background-color: Black");
				}
				mapElement.getValue().setIsWall(wallBool);
			}
			state = State.startNode;
			mssg.setText("Select Start Node");
		});
	}
	
	public void insertIntoGraph(int id, Graph g) {
		final double PERP = 10; //actually is 1, but multiply by 10 to keep in same ratio as DIAG
		final double DIAG = Math.sqrt(2)*10; //actually is 1.4, but multiply by 10 so we can use ints
		//comment out all inserts with DIAG if you dont want diagonal travelling to be allowed
		if(id<COLS) { //if in first row
			if(id%COLS!=0) { //if not top left node
				g.insertEdge(g.getNode(id), g.getNode(id-1), PERP); //connect to node to left of it
				g.insertEdge(g.getNode(id-1), g.getNode(id), PERP);
			}
		} else { //if not in first row
			if(id%COLS!=0) { //if not in left most col
				g.insertEdge(g.getNode(id), g.getNode(id-1), PERP); //connect to node to left of it
				g.insertEdge(g.getNode(id-1), g.getNode(id), PERP);
				g.insertEdge(g.getNode(id), g.getNode(id-COLS-1), DIAG); //connect to node to top left diag node
				g.insertEdge(g.getNode(id-COLS-1), g.getNode(id), DIAG);
			}
			if((id-COLS+1)%COLS!=0) { //if not in right most col
				g.insertEdge(g.getNode(id), g.getNode(id-COLS+1), DIAG); //connect to node to top right diag node
				g.insertEdge(g.getNode(id-COLS+1), g.getNode(id), DIAG);
			}
			g.insertEdge(g.getNode(id), g.getNode(id-COLS), PERP); //connects to node above it
			g.insertEdge(g.getNode(id-COLS), g.getNode(id), PERP);
		}	
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
