
package com;

import java.util.ArrayList;
import java.util.Collections;
//import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class NetworkGraph {
	public final int nodeNum;
	public final int edgeNum;
	public ArrayList<Node> nodes;
	public ArrayList<Edge> edges;
	public int cdnCost;
	public Users users;
	public int totalReqBandwidth;
	public Node superSource;
	private Node superSink;
	public final int superSourceId;
	public final int superSinkId;
	
	

	public NetworkGraph(int nodeNum, int edgeNum, int userNum) {
		this.nodeNum = nodeNum;
		this.edgeNum = edgeNum;
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
		for(int i = 0; i < nodeNum; i++) {
			Node node = new Node(i);
	 		nodes.add(node);
		}
		superSourceId = nodeNum;
		superSource = new Node(superSourceId);
		nodes.add(superSource);
		
		superSinkId = nodeNum + 1;
		superSink = new Node(superSinkId);
		nodes.add(superSink);
		
		users = new Users(userNum);
		totalReqBandwidth = 0;
	}


	public boolean addEdge(int Nd1, int Nd2, int capacity, int price) {
		if(Nd1 < 0 || Nd1 >= nodeNum || Nd2 < 0 || Nd2 >= nodeNum) return false;

//		if(!Edge.isLegalCapacity(capacity)) return false;
//		if(!Edge.isLegalPrice(price)) return false;
//		if(!Edge.isLegalLink(nodes.get(Nd1).getOutDegree(), nodes.get(Nd2).getOutDegree())) return false;
		
		edges.add(new Edge(Nd1, Nd2, capacity, price));
		edges.add(new Edge(Nd2, Nd1, 0, -price));
		int m = edges.size();
		nodes.get(Nd1).addEdgeId(m - 2);
		nodes.get(Nd2).addEdgeId(m - 1);
		nodes.get(Nd1).addOutDegree();
		
		edges.add(new Edge(Nd2, Nd1, capacity, price));
		edges.add(new Edge(Nd1, Nd2, 0, -price));
		m = edges.size();
		nodes.get(Nd2).addEdgeId(m - 2);
		nodes.get(Nd1).addEdgeId(m - 1);
		nodes.get(Nd2).addOutDegree();
		
		return true;
	}
	
	
//	public ArrayList<Integer> getCandidateNds(int threshold) {
//		ArrayList<Node> result = new ArrayList<Node>();
//		ArrayList<Integer> potentialCDN = new ArrayList<Integer>();
//		
//		for(Node node: this.nodes) {
//			if(node.revFlowUsers.size() >= threshold) {
//				result.add(node);
//				potentialCDN.add(node.nodeId);
//			
//			}
//		}
//
//		Collections.sort(result, new Node.revFlowOrder());

//		return potentialCDN;
//	}

	
	public void addSuperSourceEdges(ArrayList<Integer> targetNds) {
		for(Integer targetNd:targetNds) {
			edges.add(new Edge(superSourceId, targetNd, Integer.MAX_VALUE, 0));
			edges.add(new Edge(targetNd, superSourceId, 0, 0));
			int m = edges.size();
			superSource.addEdgeId(m - 2);
			nodes.get(targetNd).addEdgeId(m - 1);
		}
	}
	
	public void cleanSuperSourceEdges() {
		for(Integer edgeId:superSource.getEdgeIds()) {
			
			int nodeId = edges.get(edgeId).endNd;
			nodes.get(nodeId).removeEdgeId(edgeId + 1);
		}
		int edgeId = superSource.getEdgeIds().get(0);
		for(int i = edges.size()-1;i >= edgeId;i--){
			edges.remove(i);
		}
		superSource.cleanEdgeIds();
	}
	
	public void addSuperSinkEdges() {
		for(User user:this.users.userList) {
			totalReqBandwidth += user.reqBandwidth;
			edges.add(new Edge(user.linkedNodeId, superSinkId, user.reqBandwidth, 0));
			edges.add(new Edge(superSinkId, user.linkedNodeId, 0, 0));
			int m = edges.size();
			nodes.get(user.linkedNodeId).addEdgeId(m - 2);
			superSink.addEdgeId(m - 1);
		}
	}
	
	public void cleanSuperSinkEdges() {
		for(Integer edgeId:superSink.getEdgeIds()) {
			nodes.get(edges.get(edgeId).endNd).removeEdgeId(edgeId - 1);
			edges.remove(edgeId - 1);
			edges.remove(edgeId);
		}
		superSink.cleanEdgeIds();
	}
	
	
	public Node getNode(int nodeID) {
		return this.nodes.get(nodeID);
	}
	
	public Edge getEdge(int edgeId) {
		return this.edges.get(edgeId);
	}
	
	public void cleanFlows() {
		for(Edge edge: edges) {
			edge.cleanFlow();
		}
	}
	
	public void setCDNcost(int cdnCost) {
		this.cdnCost = cdnCost;
	}

//	public static boolean isLegalNodeNum(int nodeNum) {
//		if (nodeNum > 0 && nodeNum <= ConUtil.MAX_NODE_NUM) 
//			return true;
//		return false;
//	}

//	public static boolean isLegalCDNCost(int cdnCost) {
//		if (cdnCost >= 0 && cdnCost <= ConUtil.MAX_CDN_COST) 
//			return true;
//		return false;
//	}
	

	public void setAllNeighbors(){
		for(Node node: nodes){
			node.setNeighbors(this.edges,this.users);
		}
	}
	
	
	public ArrayList<Integer> getGoodNodes(int threshold){
		ArrayList<Integer> goodNodes = new ArrayList<Integer>();
        for(Node node:this.nodes){
        	int nodeNeiContain = 0;
        	for(Integer edgeId:node.getEdgeIds()){
    			if(this.edges.get(edgeId).price > 0){
    				if(this.users.nodeToUser.keySet().contains(this.edges.get(edgeId).endNd)){
    					nodeNeiContain++;
    				}
    			}
    		}
        	if(nodeNeiContain >= threshold){
        		goodNodes.add(node.nodeId);
        	}

        }
        return goodNodes;
        
	}
	
	
}
class Edge {
	public final int startNd;
	public final int endNd;
	public final int capacity;
	public final int price;
	public int flow;
	
	public Edge(int startNd, int endNd, int capacity, int price) 
	{
		this.startNd = startNd;
		this.endNd = endNd;
		this.capacity = capacity;
		this.price = price;
		flow = 0;
	}
	
	public void cleanFlow() {
		this.flow = 0;
	}

//	static boolean isLegalPrice(int Price) 
//	{
//		if (Price >= 0 && Price <= ConUtil.MAX_PRICE) 
//			return true;
//		return false;
//	}

//	static boolean isLegalCapacity(int capacity) 
//	{
//		if (capacity >= 0 && capacity <= ConUtil.MAX_CAPCITY)
//			return true;
//		return false;
//	}
	
//	static boolean isLegalLink(int startDegree, int endDegree)//閿熸枻鎷疯閿熺潾闈╂嫹
//	{
//		if(startDegree > ConUtil.MAX_DEGREE_NUM || endDegree > ConUtil.MAX_DEGREE_NUM)
//			return false;
//		return true;
//	}
}
class Node {
	public int nodeId;
	private int outDegree;
	HashSet<Integer> edgeIds;
	private boolean realCDN;
	public int userId; 
	
	
	public ArrayList<Integer> revFlowUsers;
	
	public ArrayList<Integer> neighbors;
	public ArrayList<Integer> userNeighbors;
	
	public void setNeighbors(ArrayList<Edge> edges,Users users){
		for(Integer edgeId:this.edgeIds){
			if(edges.get(edgeId).price > 0){
				this.neighbors.add(edges.get(edgeId).endNd);
				if(users.nodeToUser.keySet().contains(edges.get(edgeId).endNd)){
					this.userNeighbors.add(edges.get(edgeId).endNd);
				}
			}
		}
	}

	
	public Node(int id) {
		this.nodeId = id;
		edgeIds = new HashSet<Integer>();
		realCDN = false;
		userId = -1;
		
		revFlowUsers = new ArrayList<Integer>();
		
		
		 neighbors= new ArrayList<Integer>();
		 userNeighbors = new ArrayList<Integer>();
		
	}	
	
	void setRealCDN() {
		this.realCDN = true;
	}

	
	boolean isRealCDN() {
		return realCDN;
	}

	void addOutDegree() {
		this.outDegree++;
	}
	
	int getOutDegree() {
		return this.outDegree;
	}
	
	void addEdgeId(int edgeId) {
		this.edgeIds.add(edgeId);
	}
	
	void cleanEdgeIds() {
		this.edgeIds.clear();
	}
	
	ArrayList<Integer> getEdgeIds() {
		ArrayList<Integer> edgeList = new ArrayList<Integer>(this.edgeIds);
		Collections.sort(edgeList);
		return edgeList;
	}
	
	void removeEdgeId(int edgeId) {
		this.edgeIds.remove(edgeId);
				
	}

	
//	public static class revFlowOrder implements Comparator<Node>{
//		public int compare(Node a,Node b) {
//			if(a.revFlowUsers.size() > b.revFlowUsers.size()) return -1;
//			if(a.revFlowUsers.size() < b.revFlowUsers.size()) return +1;
//		
//			return 0;
//		}
//	}
	
	public String toString() {
		return "nodeId: " + this.nodeId;
	}
}
class Path {
	public int flow;
	public int pathCost;
	public LinkedList<Integer> path;
	
	public Path(int flow, int pathCost, LinkedList<Integer> path) {
		this.flow = flow;
		this.pathCost = pathCost;
		this.path = path;
	}
	
	public String toString(NetworkGraph g) {
		StringBuffer pathString  = new StringBuffer();;
		int LastEdge = 0;
		for(Integer edgeId:this.path) {
			LastEdge = edgeId;
			if(g.getEdge(edgeId).endNd == g.nodeNum + 1) break;
			pathString.append(g.getEdge(edgeId).endNd + " ");
			LastEdge = edgeId;
		}
		pathString.append(g.users.nodeToUser.get(g.getEdge(LastEdge).startNd)+" ");
		pathString.append(this.flow);
		return pathString.toString();
	}
}

class Users {
	public final int userNum; 
	public ArrayList<User> userList;
	private boolean[] linkedNodeHash;
	public HashMap<Integer,Integer> nodeToUser;
	
	public Users(int userNum) {
		this.userNum = userNum;
		userList = new ArrayList<User>();
		linkedNodeHash = new boolean[ConUtil.MAX_NODE_NUM];
		nodeToUser = new HashMap<Integer,Integer>();
	}

	public boolean isLegalLinkedNode(int linkedNodeID) {
		if(this.linkedNodeHash[linkedNodeID]) return false;
		return true;
	}

	public static boolean isLegalUserNum(int userNum) {
		if (userNum > 0 && userNum <= ConUtil.MAX_USER_NUM) 
			return true;
		return false;
	}

	public void addUser(int id, int linkedNodeID, int reqBandwidth) {
		User user = new User(id, linkedNodeID, reqBandwidth);
		this.linkedNodeHash[linkedNodeID] = true;
		userList.add(user);
		nodeToUser.put(linkedNodeID, id);
	}
	
}

class User {
	public final int id;
	public final int linkedNodeId;
	public final int reqBandwidth;


	public User(int id, int linkedNodeID, int reqBandwidth) {
		this.id = id;
		this.linkedNodeId = linkedNodeID;
		this.reqBandwidth = reqBandwidth;
	}

	static boolean isLegalReq(int reqBandwidth) {
		if (reqBandwidth >= 0 && reqBandwidth <= ConUtil.MAX_REQ)
			return true;
		return false; 
	}
}