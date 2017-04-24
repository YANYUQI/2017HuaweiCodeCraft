package com;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

public class GraphAlgo {
	
	public static int bestCost = Integer.MAX_VALUE;
	public static ArrayList<String> bestPathList;
	private int[] aug;
	private int[] disTo;
	private int[] edgeTo;
	LinkedList<Integer> queue;
	private boolean[] onQ;
	
	public GraphAlgo(int nodeNum) {
		aug = new int[nodeNum];
		disTo = new int[nodeNum];
		edgeTo = new int[nodeNum];
		queue = new LinkedList<Integer>();
		bestPathList = new ArrayList<String>();
		onQ = new boolean[nodeNum];
	}
	
	public Path SPFA(NetworkGraph g, int startNd, int endNd) {
		for(int i = 0; i < disTo.length;i++){
			disTo[i] = Integer.MAX_VALUE;
			edgeTo[i] = -1;
			aug[i] = 0;
			onQ[i]=false;
		}
		disTo[startNd] = 0;
		aug[startNd] = Integer.MAX_VALUE;
		queue.clear();
		queue.add(startNd);	
		onQ[startNd]=true;
		while(!queue.isEmpty()) {
			int nodeId = queue.getFirst();
			queue.pop();
			for(Integer edgeId : g.getNode(nodeId).getEdgeIds()) {
				Edge e = g.getEdge(edgeId);
				if(e.capacity > e.flow && disTo[e.endNd] > disTo[nodeId] + e.price) {
					disTo[e.endNd] = disTo[nodeId] + e.price;
					edgeTo[e.endNd] = edgeId;
					aug[e.endNd] = Math.min(aug[nodeId], e.capacity - e.flow);
					if(!onQ[e.endNd]) {
						if(queue.isEmpty()) {
							onQ[e.endNd]=true;
							queue.push(e.endNd);
						}
						else {
							if(disTo[e.endNd] >= disTo[queue.getFirst()]) {
								onQ[e.endNd]=true;
								queue.addLast(e.endNd);
							}
							else {
								onQ[e.endNd]=true;
								queue.addFirst(e.endNd);
							}
						}
					}
				}
			}
			onQ[nodeId]=false;	
		}
			
		if(disTo[endNd] == Integer.MAX_VALUE) return null;
			
		LinkedList<Integer> path = new LinkedList<Integer>();
		int index = endNd;
		while(index != startNd) {
			Edge e = g.getEdge(edgeTo[index]);
			e.flow += aug[endNd];
			g.getEdge(edgeTo[index]^1).flow -= aug[endNd];
			path.addFirst(edgeTo[index]);
			index = e.startNd;

		}
		return new Path(aug[endNd], disTo[endNd], path);
	}
	
		
	private int MincostMaxflow(NetworkGraph g, int startNd, int endNd, int neededFlow) {
		int totalCost = 0;
		int countFlow = neededFlow;
		Path path;
		while((path = SPFA(g, startNd, endNd)) != null) {
			countFlow = countFlow - path.flow;
			totalCost +=  path.flow * path.pathCost;
			if(countFlow <= 0) {
				if(countFlow < 0) {
					path.flow = countFlow + path.flow;
					for(Integer edgeId:path.path) {
						g.getEdge(edgeId).flow += countFlow;
						g.getEdge(edgeId^1).flow -= countFlow;
					}
					totalCost += countFlow * path.pathCost;
				}
			break;
			}
		}
		if(countFlow > 0) return -1;
		
		
		int curCost = totalCost + g.cdnCost * g.superSource.getEdgeIds().size();
		if(curCost < bestCost) {
			ArrayList<String>
			pathList = getPathList(g);
			GraphAlgo.updateBestRecord(g, pathList, curCost);
		}
		
		return totalCost;

	}
	
	public int MincostMaxflowForGA(NetworkGraph g, ArrayList<Integer> nodeIds,Timer t) {
		g.addSuperSourceEdges(nodeIds);
		int cost = this.MincostMaxflow(g, g.superSourceId, g.superSinkId, g.totalReqBandwidth);
		g.cleanSuperSourceEdges();
		g.cleanFlows();
		return cost;
	}
	

	private static void updateBestRecord(NetworkGraph g, ArrayList<String> pathList, int curCost) {
			bestCost = curCost;
			bestPathList.clear();
			bestPathList.addAll(pathList);
	}

	
	private ArrayList<String> getPathList(NetworkGraph g) {
		ArrayList<String> pathList = new ArrayList<String>();

		for(Integer edgeId:g.nodes.get(g.superSourceId).getEdgeIds()) {
			StringBuffer pathString  = new StringBuffer();
			Edge edge = g.edges.get(edgeId);
			pathString.append(edge.endNd);
			this.getPathListByDfs(g, edge, edge.flow, pathList, pathString);
		}
		return pathList;
	}
	
	public int getPathListByDfs(NetworkGraph g, Edge edge, int minFlow, ArrayList<String> pathList, StringBuffer pathString) {
		int newMinFlow = Math.min(minFlow, edge.flow);
		int tmp = newMinFlow;
		if(edge.endNd == g.superSinkId) {
			pathString.append(" ");
			pathString.append(g.users.nodeToUser.get(edge.startNd));
			pathString.append(" ");
			pathString.append(newMinFlow);
			pathList.add(pathString.toString());
			edge.flow -= newMinFlow;
			return newMinFlow;
		} 
		for(Integer edgeId:g.nodes.get(edge.endNd).getEdgeIds()) {
			Edge subEdge = g.edges.get(edgeId);
			if(subEdge.price >= 0 && subEdge.flow > 0) {
				StringBuffer subPathString = new StringBuffer(pathString.toString());
				if(subEdge.endNd != g.superSinkId) {
					subPathString.append(" ");
					subPathString.append(subEdge.endNd);
				}
				int flwCost = getPathListByDfs(g, subEdge, tmp, pathList, subPathString);
				tmp -= flwCost;
				if(tmp == 0) break;
			}
		}
		edge.flow -= newMinFlow;
		return newMinFlow;
	}
	
//	public ArrayList<Integer> revFlow(NetworkGraph g) {
//		ArrayList<Integer> unCertainUsers = new ArrayList<Integer>();
//		for(User user : g.users.userList) {
//			if(!g.nodes.get(user.linkedNodeId).isRealCDN()) {
//				unCertainUsers.add(user.linkedNodeId);
//				this.SPFAforRevFlow(g, user);
//			}
//		}
//		return unCertainUsers;
//	}
//	
//	private void SPFAforRevFlow(NetworkGraph g, User user) {
//		for(int i = 0; i < marked.length; i++) {
//			marked[i] = false;
//		}
//		queue.clear();
//		queue.add(user.linkedNodeId);
//		marked[user.linkedNodeId] = true;
//		int step = 0;
//		while(!queue.isEmpty()) {
//			int nodeId = queue.pop();
//			int edgeLength = g.getNode(nodeId).getEdgeIds().size();
//			for(int i = 0; i < edgeLength; i++) {
//				int edgeId = g.getNode(nodeId).getEdgeIds().get(i);
//				Edge e = g.getEdge(edgeId);
//				if(e.price < 0 || e.capacity < user.reqBandwidth || marked[e.endNd]) continue;
//				queue.push(e.endNd);
//				marked[e.endNd] = true;
//				g.nodes.get(e.endNd).revFlowUsers.add(user.linkedNodeId);
//				step++;
//			}
//			if((step == 0)) {
//				step++;
//				for(Integer edgeId : g.getNode(nodeId).getEdgeIds()) {
//					Edge e = g.getEdge(edgeId);
//					if(e.price < 0 || marked[e.endNd]) continue;
//					g.nodes.get(e.endNd).revFlowUsers.add(user.linkedNodeId);
//					marked[e.endNd] = true;
//				}
//			}
//		}
//	}
//	public ArrayList<Integer> preTreatment(NetworkGraph g) {
//		ArrayList<Integer> fixedCDNIds = new ArrayList<Integer>();
//		for(User user:g.users.userList) {
//			if(user.linkedNodeId == 327){
//				System.out.println(327);
//			}
//			int sumCap = 0;
//			for(Integer edgeId:g.nodes.get(user.linkedNodeId).getEdgeIds()) {
//				if(g.edges.get(edgeId).price > 0){
//					sumCap += g.edges.get(edgeId).capacity;
//				}
//			}
//			if(sumCap < user.reqBandwidth){
//				g.nodes.get(user.linkedNodeId).setRealCDN();
//				fixedCDNIds.add(user.linkedNodeId);
//			}
//		}
//		return fixedCDNIds;
//	}
	
	
	public ArrayList<Integer> selfishness(NetworkGraph g,int tolerance) {
		ArrayList<Integer> fixedCDNIds = new ArrayList<Integer>();
		for(User user:g.users.userList) {	
			TreeMap<Integer,Integer> links = new TreeMap<Integer,Integer>();//key:rent鈥斺��>value:total capacity
			for(Integer edgeId:g.nodes.get(user.linkedNodeId).getEdgeIds()) {
				if(g.edges.get(edgeId).price > 0){
					if(links.containsKey(g.edges.get(edgeId).price)){
						links.put(g.edges.get(edgeId).price,links.get(g.edges.get(edgeId).price) + g.edges.get(edgeId).capacity);
					}else{
						links.put(g.edges.get(edgeId).price, g.edges.get(edgeId).capacity);
					}
				}
			}
			int curUserReq = user.reqBandwidth;
			int leastCost = 0;
			for(Integer rent:links.keySet()){
				curUserReq -= links.get(rent);
				leastCost += links.get(rent) * rent;
				if(curUserReq == 0) break;
				if(curUserReq < 0) {
					leastCost -= (-curUserReq) * rent;
					break;
				}
			}
			

			
			if(leastCost >= g.cdnCost - tolerance){
				g.nodes.get(user.linkedNodeId).setRealCDN();
				fixedCDNIds.add(user.linkedNodeId);
			}
		}
		return fixedCDNIds;
	}

}




