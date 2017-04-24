package com;

import java.util.ArrayList;
import java.util.List;




public class Deploy {
    public static String[] deployServer(String[] graphContent)
    {
    	Timer timer = new Timer();
    	timer.begin();

        NetworkGraph networkGraph;
     
        String[] tmp = graphContent[0].split(" ");
        int[] graphInfo = new int[tmp.length];
        for(int i = 0; i < 3; i++)
        {
            graphInfo[i] = Integer.parseInt(tmp[i]);
        }

        networkGraph = new NetworkGraph(graphInfo[0], graphInfo[1], graphInfo[2]);

        int cdnCost = Integer.parseInt(graphContent[2]);

        networkGraph.setCDNcost(cdnCost);

        int count = 4;
        for(int i = 1; i <= networkGraph.edgeNum; i++) 
        {
            String[] edgeTmp = graphContent[count++].split(" ");
            int startNd = Integer.parseInt(edgeTmp[0]);
            int endNd = Integer.parseInt(edgeTmp[1]);
            int capacity = Integer.parseInt(edgeTmp[2]);
            int price = Integer.parseInt(edgeTmp[3]);
            boolean added = networkGraph.addEdge(startNd, endNd, capacity, price);

        }
        
        count++;

        for(int j = 1; j <= networkGraph.users.userNum; j++) 
        {
            String[] userTmp = graphContent[count++].split(" ");
            int id = Integer.parseInt(userTmp[0]);
            int linkedNodeID = Integer.parseInt(userTmp[1]);
            int reqBandwidth = Integer.parseInt(userTmp[2]);

            networkGraph.users.addUser(id, linkedNodeID, reqBandwidth);
            networkGraph.nodes.get(linkedNodeID).userId = id;
        }

        networkGraph.setAllNeighbors();
        
        GraphAlgo graphAlgo = new GraphAlgo(networkGraph.nodeNum + 2);
 
        
        
        
        if(networkGraph.nodeNum < 200 ){
        	
        	timer.setThreshold(88500);
        	
        	int toleranceForCandidates = 600;
            int toleranceForFixed = 0;
            ArrayList<Integer> cdnNodeIds;
            
            networkGraph.addSuperSinkEdges();
            while(true){
            	cdnNodeIds = graphAlgo.selfishness(networkGraph,toleranceForCandidates);
            	int tmpCost = graphAlgo.MincostMaxflowForGA(networkGraph, cdnNodeIds,timer);
            	if(tmpCost < 0 ) {
            		System.out.println("初始解不可行，tolerance += 50,重新产生中》》》");
            		toleranceForCandidates += 50;
            		continue;
            	}else{
            		System.out.println("初始解可行！！！开始退火！");
            		break;
            	}
            }
            ArrayList<Integer> fixedCdnIds = graphAlgo.selfishness(networkGraph,toleranceForFixed);
            
           System.out.println("fixedCdnIds: "+fixedCdnIds.size()+" 个  "+fixedCdnIds.toString());
           System.out.println("cdnNodeIds: "+cdnNodeIds.size()+" 个   "+cdnNodeIds.toString());
        	

           Fire fire = new Fire(2,0.997,50,100,0.00001);
           fire.fireAlg(cdnNodeIds,fixedCdnIds,networkGraph, graphAlgo,timer,1,0,1,0.75);
        	
        }else if(networkGraph.nodeNum < 400){
        	
        	timer.setThreshold(88500);
        	
        	
        	int toleranceForCandidates = 600;
            int toleranceForFixed = 0;
            ArrayList<Integer> cdnNodeIds;
            
            networkGraph.addSuperSinkEdges();
            while(true){
            	cdnNodeIds = graphAlgo.selfishness(networkGraph,toleranceForCandidates);
            	System.out.println("cdnNodeIds: "+cdnNodeIds.size()+" 个  "+cdnNodeIds.toString());
         	
            	int tmpCost = graphAlgo.MincostMaxflowForGA(networkGraph, cdnNodeIds,timer);
            	if(tmpCost < 0 ) {
            		System.out.println("初始解不可行，tolerance += 50,重新产生中》》》");
            		toleranceForCandidates += 50;
            		continue;
            	}else{
            		System.out.println("初始解可行！！！开始退火！");
            		break;
            	}
            }
            ArrayList<Integer> fixedCdnIds = graphAlgo.selfishness(networkGraph,toleranceForFixed);
            
        	Fire fire = new Fire(5,1,50,100,0.1);
            fire.fireAlg(cdnNodeIds,fixedCdnIds,networkGraph, graphAlgo,timer,1,1,1,0.75);
        	
        }else{
        	
        	timer.setThreshold(87500);  	

        	int toleranceForCandidates = 900;
            int toleranceForFixed = 750;
            ArrayList<Integer> cdnNodeIds;

            networkGraph.addSuperSinkEdges();
            while(true){
            	cdnNodeIds = graphAlgo.selfishness(networkGraph,toleranceForCandidates);
            	System.out.println("cdnNodeIds: "+cdnNodeIds.size()+" 个   "+cdnNodeIds.toString());
            	int tmpCost = graphAlgo.MincostMaxflowForGA(networkGraph, cdnNodeIds,timer);
            	if(tmpCost < 0 ) {
            		System.out.println("初始解不可行，tolerance += 50,重新产生中》》》");
            		toleranceForCandidates += 50;

            		continue;
            	}else{
            		System.out.println("初始解可行！！！开始退火！");
            		break;
            	}
            }
            ArrayList<Integer> fixedCdnIds = graphAlgo.selfishness(networkGraph,toleranceForFixed);
            System.out.println("fixedCdnIds: "+fixedCdnIds.size()+" 个 "+fixedCdnIds.toString());
            

        	Fire fire = new Fire(10,0.997,40,100,0.1);
        	
        	int countLess = 0;
        	if(cdnNodeIds.size()<200){
        		countLess = 6;
        	}else{
        		countLess = 9;
        	}
            fire.fireAlg(cdnNodeIds,fixedCdnIds,networkGraph, graphAlgo,timer,countLess,2,1,0.99);//2,2,1

        }
        

		System.out.println(GraphAlgo.bestCost);
		System.out.println(GraphAlgo.bestPathList.size());
		System.out.println(GraphAlgo.bestPathList.toString());
       

		List<String> pathsInfo = GraphAlgo.bestPathList;
		String[] resultContents = new String[pathsInfo.size() + 2];
		resultContents[0] = String.valueOf(pathsInfo.size());
		resultContents[1] = "";
    	for (int i = 0; i < pathsInfo.size(); i++) {
    		resultContents[i + 2] = pathsInfo.get(i);
    	}
    	
    	timer.print();
    	
        return resultContents;
   
    }
    
    
    
public static void main(String[] args) {
    	
    	String graphFilePath = "D:/casesNew/low/case0.txt";
    	String resultFilePath = "D:/answers/answer.txt";
    	
    	String[] graphContent = FileUtil.read(graphFilePath, null);
    	String[] resultContents = Deploy.deployServer(graphContent);
    	
    	if (hasResults(resultContents))
        {
            FileUtil.write(resultFilePath, resultContents, false);
        }
        else
        {
            FileUtil.write(resultFilePath, new String[] { "NA" }, false);
        }
        LogUtil.printLog("End");
    }
    
    private static boolean hasResults(String[] resultContents) 
    {
        if(resultContents==null)
        {
            return false;
        }
        for (String contents : resultContents)
        {
            if (contents != null && !contents.trim().isEmpty())
            {
                return true;
            }
        }
        return false;
    }
   
}
