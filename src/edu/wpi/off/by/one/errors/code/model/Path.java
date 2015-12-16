package edu.wpi.off.by.one.errors.code.model;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import edu.wpi.off.by.one.errors.code.controller.ControllerSingleton;

public class Path {
	private Id startNode;
	private Id endNode;
	private ArrayList<Id> route;
	private Graph theGraph;
	//private Vector<Node> nodes;
	//private Vector<Edge> edges;
	
	public Path(Id startNodeIn, Id endNodeIn){
		startNode = startNodeIn;
		endNode = endNodeIn;
		route = new ArrayList<Id>();
	}
	
	/**
	 * this method is the bulk of the A* algorithm, it iterates through a list of nodes and chooses the best path from them
	 * @param graphin
	 */
	public void runAStar(Graph graphin){
		theGraph = graphin;
		////System.out.println("started a*");
		ArrayList<Id> visited = new ArrayList<Id>();	//These nodes we have already seen
		ArrayList<Id> open = new ArrayList<Id>();		//These are the next nodes we will visit

		open.add(startNode);
		//this.nodes = nodesIn;	//the list of Node objects to search
		//this.edges = edgesIn;	//the list of the associated edges for the Nodes

		Id current; 		//the ID for the node we are currently examining
		HashMap<Id, Id> cameFrom = new HashMap<Id, Id>();		//The map of current optimum path to a node
		HashMap<Id, Float> gScore = new HashMap<Id, Float>();	//The map of the nodeID to its path finding score, the lower the better
		HashMap<Id, Float> fScore = new HashMap<Id, Float>();	//create a new #map for the f score of the node

		for (Node elem : theGraph.getNodes()){	//sets each node that we could examine, place its ID and the maximum value into the #maps
			if(elem==null)
				continue;
			gScore.put(elem.getId(), Float.MAX_VALUE);
			fScore.put(elem.getId(), Float.MAX_VALUE);
		}
		gScore.put(startNode, (float) 0.0);	//start the algorithm at the first node
		fScore.put(startNode, calcHeuristic(startNode,endNode));	//initialize the fscore with the heuristic of length
	
		while(!open.isEmpty()){	//while we still have nodes to visit
			current = findLowestFInOpen(open, fScore);	//find the closest node to the end in the open list 
			int toRemove = open.indexOf(current);
			open.remove(toRemove);
			//System.out.println("Start printing open" + open.toString() + "finished printing open");
			if (current == endNode){	//we made it!
				//System.out.println("ever found a path");
				route = reconstructPath(cameFrom, current);	//run the helper that goes through and puts the path in order
			}
			//int toRemove = open.indexOf(current);
			//open.remove(toRemove);	//take the current node out of nodes to visit
			visited.add(current);	//then add it to the list of already visited nodes
			/*//System.out.println("start of edgelist");
			//System.out.println(nodes.get(current).getEdgelist().toString());
			//System.out.println("end of edgelist");*/
			////System.out.println("nodes.get(current) "+nodes.get(current)+" end");
			////System.out.println("EdgeListBeforeFor" + nodes.get(current).getEdgelist()+"EdgeListBeforeForDone");
			for(Id elem : theGraph.returnNodeById(current).getEdgelist()){	//find all the edges attached to the node
				if(elem == null) continue;
				//System.out.println("in the for loop");
				Edge neighborEdge = theGraph.returnEdgeById(elem);	//pull one edge from the list at a time
				Id neighborId;	//the ID of the node at the other end of the edge
				if (neighborEdge.getNodeA() == current){	//if we try and get the ID for the node and it's the same ID
					neighborId = neighborEdge.getNodeB();	//then it must be the other node in the edge
				}
				else {
					neighborId = neighborEdge.getNodeA();	//otherwise this one must be the neighbor
				}
				if(neighborId == null){
					theGraph.deleteEdge(elem);
					continue;
				}
				float tentativeGScore = gScore.get(current)+theGraph.returnEdgeById(elem).getLength();	//calculate the distance needed to get to the current point	
				if(visited.contains(neighborId)){				//if we have already been to this neighbor
					if(gScore.get(neighborId)>tentativeGScore){	//and this route is better than the existing one
						gScore.put(neighborId, tentativeGScore);//then make this the current best route to this point
					}
					else continue;	//otherwise we need to do the while loop again
				}
				if(!open.contains(neighborId)){	//if we haven't been to the neighbor yet
					//System.out.println("adding to open");
					//System.out.println("Start printing open" + open.toString() + "finished printing open");
					open.add(neighborId);		//add the neighbor to the list of places to go
				}
				else if(tentativeGScore>gScore.get(neighborId)){	//if this path to this node isn't better than the existing one
					continue;										//loop again
				}
				cameFrom.put(neighborId, current); //if we made it to here it means we found a new best path to this node
				gScore.put(neighborId, tentativeGScore);   //now we just make it say that
				fScore.put(neighborId, gScore.get(neighborId)+calcHeuristic(neighborId  , endNode));
			}
		}
		
		
	}
	
	public void runAccessibleAStar(Graph graphin){
		System.out.println("accessible");
		theGraph = graphin;
		////System.out.println("started a*");
		ArrayList<Id> visited = new ArrayList<Id>();	//These nodes we have already seen
		ArrayList<Id> open = new ArrayList<Id>();		//These are the next nodes we will visit

		open.add(startNode);
		//this.nodes = nodesIn;	//the list of Node objects to search
		//this.edges = edgesIn;	//the list of the associated edges for the Nodes

		Id current; 		//the ID for the node we are currently examining
		HashMap<Id, Id> cameFrom = new HashMap<Id, Id>();		//The map of current optimum path to a node
		HashMap<Id, Float> gScore = new HashMap<Id, Float>();	//The map of the nodeID to its path finding score, the lower the better
		HashMap<Id, Float> fScore = new HashMap<Id, Float>();	//create a new #map for the f score of the node

		for (Node elem : theGraph.getNodes()){	//sets each node that we could examine, place its ID and the maximum value into the #maps
			if(elem==null)
				continue;
			gScore.put(elem.getId(), Float.MAX_VALUE);
			fScore.put(elem.getId(), Float.MAX_VALUE);
		}
		gScore.put(startNode, (float) 0.0);	//start the algorithm at the first node
		fScore.put(startNode, calcHeuristic(startNode,endNode));	//initialize the fscore with the heuristic of length
	
		while(!open.isEmpty()){	//while we still have nodes to visit
			current = findLowestFInOpen(open, fScore);	//find the closest node to the end in the open list 
			int toRemove = open.indexOf(current);
			open.remove(toRemove);
			//System.out.println("Start printing open" + open.toString() + "finished printing open");
			if (current == endNode){	//we made it!
				//System.out.println("ever found a path");
				route = reconstructPath(cameFrom, current);	//run the helper that goes through and puts the path in order
			}
			//int toRemove = open.indexOf(current);
			//open.remove(toRemove);	//take the current node out of nodes to visit
			visited.add(current);	//then add it to the list of already visited nodes
			/*//System.out.println("start of edgelist");
			//System.out.println(nodes.get(current).getEdgelist().toString());
			//System.out.println("end of edgelist");*/
			////System.out.println("nodes.get(current) "+nodes.get(current)+" end");
			////System.out.println("EdgeListBeforeFor" + nodes.get(current).getEdgelist()+"EdgeListBeforeForDone");
			for(Id elem : theGraph.returnNodeById(current).getEdgelist()){	//find all the edges attached to the node
				if(elem == null) continue;
				//System.out.println("in the for loop");
				Edge neighborEdge = theGraph.returnEdgeById(elem);	//pull one edge from the list at a time
				Id neighborId;	//the ID of the node at the other end of the edge
				if (neighborEdge.getNodeA() == current){	//if we try and get the ID for the node and it's the same ID
					neighborId = neighborEdge.getNodeB();	//then it must be the other node in the edge
				}
				else {
					neighborId = neighborEdge.getNodeA();	//otherwise this one must be the neighbor
				}
				if(neighborId == null){
					theGraph.deleteEdge(elem);
					continue;
				}
				float tentativeGScore = gScore.get(current)+theGraph.returnEdgeById(elem).getLength();	//calculate the distance needed to get to the current point	
				if(visited.contains(neighborId)){				//if we have already been to this neighbor
					if(gScore.get(neighborId)>tentativeGScore){	//and this route is better than the existing one
						gScore.put(neighborId, tentativeGScore);//then make this the current best route to this point
					}
					else continue;	//otherwise we need to do the while loop again
				}
				if(!open.contains(neighborId)){	//if we haven't been to the neighbor yet
					//System.out.println("adding to open");
					//System.out.println("Start printing open" + open.toString() + "finished printing open");
					if(theGraph.returnNodeById(neighborId).isAccessible()){
						open.add(neighborId);		//add the neighbor to the list of places to go
					}
					else {
						System.out.println("skipping");
						continue;
					}
					
				}
				else if(tentativeGScore>gScore.get(neighborId)){	//if this path to this node isn't better than the existing one
					continue;										//loop again
				}
				cameFrom.put(neighborId, current); //if we made it to here it means we found a new best path to this node
				gScore.put(neighborId, tentativeGScore);   //now we just make it say that
				fScore.put(neighborId, gScore.get(neighborId)+calcHeuristic(neighborId  , endNode));
			}
		}

		
	}
	/**
	 * this calculates the heuristic (length) between the two nodes
	 * @param current current node
	 * @param destination destination node
	 * @return the distance between the nodes
	 */
	private float calcHeuristic(Id current, Id destination){
		Coordinate coordA = null; //initialize the coordinates
		Coordinate coordB = null;
		/*for(Node elem: nodes){	//for each node in the list of nodes
			if (elem.getId() == current){	//we want to get the IDs of the passed nodes and then get their coordinates
				coordA = elem.getCoordinate();
			}
			if (elem.getId() == destination){
				coordB = elem.getCoordinate();
			}
		}*/
		if(theGraph.returnNodeById(current) != null && theGraph.returnNodeById(destination) != null) {
			coordA = theGraph.returnNodeById(current).getCoordinate();
			coordB = theGraph.returnNodeById(destination).getCoordinate();
			float xDist = coordA.getX()-coordB.getX();
			float yDist = coordA.getY()-coordB.getY();
			return (float) Math.sqrt(xDist*xDist+yDist*yDist); //return the pythagorean length
		}
		return Float.MAX_VALUE;
	}
	
	/**
	 * find the lowest F score in the list of nodes we haven't visited
	 * @param openList
	 * @param fScores
	 * @return	the index of the node with the lowest f score
	 */
	private Id findLowestFInOpen(ArrayList<Id> openList, HashMap<Id, Float> fScores){
		float lowest = Float.MAX_VALUE;
		Id iDLowest = null;
		for(Id elem : openList){	//for each element in the list of unexplored nodes see which has the lowest score
			if( fScores.get(elem)<lowest){
				lowest = fScores.get(elem);
				iDLowest = elem;
			}
		}
		return iDLowest;
	}	
	
	/**
	 * Helper for A* that backtracks and finds the path we took through the nodes
	 * @param parentList
	 * @param currentNode
	 * @return the list of nodes that we visited in order from first to last
	 */
	private ArrayList<Id> reconstructPath(HashMap<Id, Id> parentList, Id currentNode){
		Id currNode = currentNode;
		ArrayList<Id> totalPath = new ArrayList<Id>();	//initialize a variable that will hold the path as we back through it
		//System.out.println(currNode);
		totalPath.add(currNode); //start with the node we ended at
		while(currNode != startNode){	//while we haven't gotten back to the start
			//System.out.println(currNode);

			////System.out.println(Math.round(parentList.get(currNode)));
			currNode= (parentList.get(currNode));
			
			//currNode = Integer.parseInt(jank); //keep adding the parent node of the current node to the path
			totalPath.add(currNode);
		}
		//System.out.println("PreReverse" + totalPath + "prereverse printed");
		Collections.reverse(totalPath);
		//System.out.println("startPath");
		//System.out.println(totalPath.toString());
		//System.out.println("endPath");
		return totalPath;
	}
	
	/**
	 * sets the start Node
	 * @param startNodeIn
	 */
	public void setStartNode(Id startNodeIn){
		startNode = startNodeIn;
	}
	/**
	 * sets the end Node
	 * @param endNodeIn
	 */
	public void setEndNode(Id endNodeIn){
		endNode = endNodeIn;
	}
	/**
	 * gets the start Node
	 * @return starting Node
	 */
	public Id getStartNode(){
		return startNode;
	}
	/**
	 * gets the end Node
	 * @return ending Node
	 */
	public Id getEndNode(){
		return endNode;
	}
	/**
	 * gets the route 
	 * @return the route
	 */
	public ArrayList<Id> getRoute(){
//		s//System.out.println("start of route");
	//	//System.out.println(route.toString());
		////System.out.println("end of route");
		return route;
	}
	/**
	 * 
	 * @param startID the location to find the nearest food to.
	 * @return the nearest location for food by linear distance
	 */
	public Id findNearestFood(Id startID, Graph graphIn){
		theGraph = graphIn;
		Node best = null;
		float bestDist = Float.MAX_VALUE;
		for(Node elem: theGraph.getNodes()){
			if(elem.isFood()){
				float dist = calcHeuristic(startID, elem.getId());
				if(bestDist>dist){
					if(!ControllerSingleton.getInstance().getMapRootPane().isAccessibleMode||elem.isAccessible()){
						best = elem;
						bestDist = dist;
					}
				}
			}
		}
		return best.getId();
	}
	/**
	 * 
	 * @param startID the location to find the nearest mens room to.
	 * @return the nearest mens room by linear distance
	 */
	public Id findNearestMensRoom(Id startID, Graph graphIn){
		theGraph = graphIn;
		Node best = null;
		float bestDist = Float.MAX_VALUE;
		for(Node elem: theGraph.getNodes()){
			if(elem.isMens()){
				float dist = calcHeuristic(startID, elem.getId());
				if(bestDist>dist){
					if(!ControllerSingleton.getInstance().getMapRootPane().isAccessibleMode||elem.isAccessible()){
						best = elem;
						bestDist = dist;
					}
				}
			}
		}
		return best.getId();
	}
	/**
	 * 
	 * @param startID the location to find the nearest womens room to.
	 * @return the nearest womens room by linear distance
	 */
	public Id findNearestWomensRoom(Id startID, Graph graphIn){
		theGraph = graphIn;
		Node best = null;
		float bestDist = Float.MAX_VALUE;
		for(Node elem: theGraph.getNodes()){
			if(elem.isWomens()){
				float dist = calcHeuristic(startID, elem.getId());
				if(bestDist>dist){
					if(!ControllerSingleton.getInstance().getMapRootPane().isAccessibleMode||elem.isAccessible()){
						best = elem;
						bestDist = dist;
					}
				}
			}
		}
		return best.getId();
	}
	/**
	 * 
	 * @param startID the location to find the nearest gender neutral restroom to.
	 * @return the nearest gender neutral restroom by linear distance
	 */
	public Id findNearestGenderNeutralRestroom(Id startID, Graph graphIn){
		theGraph = graphIn;
		Node best = null;
		float bestDist = Float.MAX_VALUE;
		for(Node elem: theGraph.getNodes()){
			if(elem.isGenderNeutral()){
				float dist = calcHeuristic(startID, elem.getId());
				if(bestDist>dist){
					if(!ControllerSingleton.getInstance().getMapRootPane().isAccessibleMode||elem.isAccessible()){
						best = elem;
						bestDist = dist;
					}

				}
			}
		}
		return best.getId();
	}
	/**
	 * gets the textual path
	 * @return the textual path
	 */
	public ArrayList<String> getTextual(){
		ArrayList<String> res = new ArrayList<String>();
		if(route == null || route.isEmpty()) return null;
		int cnt = 0;
		Coordinate lastcoord = null;
		float lastangle = -10000.0f;
		float distFromTurn = 0;
		float distFromTurnMeter = 0;
		
		String unit;
		
		boolean isMeter = ControllerSingleton.getInstance().getSettingsMenuPane().isMeter();
		if (isMeter) {
			unit = " meters";
		}else{
			unit = " feet";
		}
		
		//float lastdist = 0.0f; TODO
		for(Id cur : route){
			Node n = theGraph.returnNodeById(cur);
			if(n == null) continue;
			Coordinate thiscoord = n.getCoordinate();
			if(lastcoord != null){
				float mx = thiscoord.getX() - lastcoord.getX();
				float my = thiscoord.getY() - lastcoord.getY();
				float distsq =  mx * mx + my * my;
				float dist = (float)Math.sqrt((double)distsq);
				float angle = (float) (Math.atan2(mx, my)* 180 / Math.PI);
				
				if (isMeter){
					distFromTurn = (float) (distFromTurnMeter * 0.72);
				}else{
					distFromTurn = (float) (distFromTurnMeter * 0.72 * 3.28);
				}
				
				if(lastangle > -180.0f){
					float dxangle = lastangle - angle;
					float degreedangle = Math.abs(dxangle);
					
					//System.out.println(degreedangle);
					//System.out.println(dxangle);
					if(Math.abs(degreedangle) <= 20){ //determines magnitude of turn
						distFromTurnMeter += dist;
					} else if(degreedangle <= 45){
						if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){

							res.add("Walk for " + Math.round(distFromTurn) + " meters " + (!n.getName().isEmpty() ? "towards " : "") + n.getName());
						}else{ 
							res.add("Walk for " + Math.round(distFromTurn) + " paces "  + (!n.getName().isEmpty() ? "towards " : "") + n.getName());
						}
						res.add("Make a slight " + (dxangle>=0 ? "right" : "left")+ " turn");
						distFromTurnMeter = dist;
					} else if (degreedangle <= 90){
						if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){
							res.add("Walk for " + Math.round(distFromTurn) + unit);
						}else res.add("Walk for " + Math.round(distFromTurn) + " paces");
						res.add("Make a " + (dxangle>=0 ? "right" : "left")+ " turn");
						distFromTurnMeter = dist;
					} else if (degreedangle <= 180){
						if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){
							res.add("Walk for " + Math.round(distFromTurn) + unit);
						}else res.add("Walk for " + Math.round(distFromTurn) + " paces");
						res.add("Make a hard " + (dxangle>=0 ? "right" : "left")+ " turn");
						distFromTurnMeter = dist;
					} else {
						if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){
							res.add("Walk for " + Math.round(distFromTurn) + unit);
						}else res.add("Walk for " + Math.round(distFromTurn) + " paces");
						res.add("Make a sharp " + (dxangle>=0 ? "right" : "left")+ " turn");
						distFromTurnMeter = dist;
					}
				} else {
					if((-45 <= angle && angle < 45)){
						res.add("Face south");
					} else if((45 <= angle && angle < 135)){
						res.add("Face east");
					} else if((-135 <= angle && angle < 135)){
						res.add("Face west");
					} else {
						res.add("Face north");
					}
					distFromTurnMeter = dist;
				}
				lastangle = angle;
			}
			lastcoord = thiscoord;
			cnt++;
		}
		if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){
		res.add("Walk for " + Math.round(distFromTurn) + unit);
		res.add("You have reached your destination");
		}else {
			res.add("Walk for " + Math.round(distFromTurn) + " paces");
			res.add("You have found the booty");
		}

		return res;
	}

public ArrayList<Step> getSteps(){
	ArrayList<Step> steps = new ArrayList<Step>();
	String res = "";
	if(route == null || route.isEmpty()) return null;
	int cnt = 0;
	Coordinate lastcoord = null;
	Node lastNode = null;
	float lastangle = -10000.0f;
	float distFromTurn = 0;
	
	for(Id cur : route){
		Node n = theGraph.returnNodeById(cur);
		
		if(n == null) continue;
		Coordinate thiscoord = n.getCoordinate();
		if(lastcoord != null){
			float mx = thiscoord.getX() - lastcoord.getX();
			float my = thiscoord.getY() - lastcoord.getY();
			float distsq =  mx * mx + my * my;
			float dist = (float)Math.sqrt((double)distsq);
			float angle = (float) (Math.atan2(mx, my)* 180 / Math.PI);
			if(lastangle > -180.0f){
				float dxangle = lastangle - angle;
				float degreedangle = Math.abs(dxangle);
				
				if(Math.abs(degreedangle) <= 20){ //determines magnitude of turn
					distFromTurn += dist;
				} else if(degreedangle <= 45){
					if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){
						res += ("Walk for " + Math.round(distFromTurn) + " meters " + (!n.getName().isEmpty() ? "towards" : "") + n.getName());
					}else{ 
						res += ("Walk for " + Math.round(distFromTurn) + " paces " + (!n.getName().isEmpty() ? "towards" : "") + n.getName());
					}
					res += (" and make a slight " + (dxangle>=0 ? "right" : "left")+ " turn");
					Step step = new Step(n, lastNode);
					step.setInstructions(res);
					steps.add(step);
					lastNode = n;
					res = "";
					distFromTurn = dist;
				} else if (degreedangle <= 90){
					if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){
						res += ("Walk for " + Math.round(distFromTurn) + " meters");
					}else res += ("Walk for " + Math.round(distFromTurn) + " paces");
					res += (" and  make a " + (dxangle>=0 ? "right" : "left")+ " turn");
					distFromTurn = dist;
					Step step = new Step(n, lastNode);
					step.setInstructions(res);
					steps.add(step);
					lastNode = n;
					res = "";
				} else if (degreedangle <= 180){
					if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){
						res += ("Walk for " + Math.round(distFromTurn) + " meters");
					}else res += ("Walk for " + Math.round(distFromTurn) + " paces");
					res += (" and make a hard " + (dxangle>=0 ? "right" : "left")+ " turn");
					distFromTurn = dist;
					Step step = new Step(n, lastNode);
					step.setInstructions(res);
					steps.add(step);
					lastNode = n;
					res = "";
				} else {
					if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){
						res += ("Walk for " + Math.round(distFromTurn) + " meters");
					}else res += ("Walk for " + Math.round(distFromTurn) + " paces");
					res += ("and make a sharp " + (dxangle>=0 ? "right" : "left")+ " turn");
					distFromTurn = dist;
					Step step = new Step(n, lastNode);
					step.setInstructions(res);
					steps.add(step);
					lastNode = n;
				}
			} else {
				if((-45 <= angle && angle < 45)){
					res += ("Face south");
				} else if((45 <= angle && angle < 135)){
					res += ("Face east");
				} else if((-135 <= angle && angle < 135)){
					res += ("Face west");
				} else {
					res += ("Face north");
				}
				Step step = new Step(n, lastNode);
				step.setInstructions(res);
				steps.add(step);
				lastNode = n;
				res = "";
				distFromTurn = dist;
			}
			lastangle = angle;
		}
		lastcoord = thiscoord;
		cnt++;
	}
	res = "";
	if(!ControllerSingleton.getInstance().getMapRootPane().isPirateMode){
		res += ("Walk for " + Math.round(distFromTurn) + " meters");
		res += (" and you have reached your destination");
	}else {
		res += ("Walk for " + Math.round(distFromTurn) + " paces");
		res += (" and you have found the booty");
	}
	
	Step step = new Step(null, lastNode);
	step.setInstructions(res);
	steps.add(step);
	return steps;
}
}
