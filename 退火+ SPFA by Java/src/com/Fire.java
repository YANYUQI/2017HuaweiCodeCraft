package com;

import java.util.ArrayList;

public class Fire {
	
	private double curT;
	private double deCold;
	private int interTimes;
	private int outerTimes;
	private double p;
	private ArrayList<Integer> newCdnNodeIds;

	
	public Fire(double curT1,double deCold1,int interTimes1,int outerTimes1,double p1){
		this.curT = curT1;
		this.deCold = deCold1;
		this.interTimes = interTimes1;
		this.outerTimes = outerTimes1;
		this.p = p1;
		this.newCdnNodeIds = new ArrayList<Integer>();
	}
	
	public void fireAlg(ArrayList<Integer> cdnNodeIds,ArrayList<Integer> fixedCdnIds,NetworkGraph g, GraphAlgo graphAlgo,Timer t,int countForLess,int caseKind,int countForMore,double lessOrMore){
		
		int countForLessTMP = countForLess;
		double lessOrMoreTMP = lessOrMore;
		
		ArrayList<Integer> lastCdnNodeIds = new ArrayList<Integer>();
		lastCdnNodeIds.addAll(cdnNodeIds);
		int lastCost  = GraphAlgo.bestCost;

		ArrayList<Integer> curCdnNodeIds = new ArrayList<Integer>();
		int curCost;

		double ran;
		double metropolis;
		
		
		boolean tmpB = true;
		
		int countTimes = 0;
		int unacTimes = 0;
		for(int curOuterTimes = 0;curOuterTimes < this.outerTimes;curOuterTimes++){
			
			for(int curInterTimes = 0;curInterTimes < this.interTimes;curInterTimes++){

				while(true){

					if(t.overtime()) return;
					
					if(caseKind == 2 ) {

						
						if(lastCdnNodeIds.size()<150){
							countForLessTMP = 1;

							
							ran = Math.random();
							if(ran < 0.6 ){
								curCdnNodeIds.clear();
								this.nearSolutionLESS(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForLessTMP,caseKind);
								curCdnNodeIds.addAll(this.newCdnNodeIds);
							}
							else if (ran < 0.8) {
								curCdnNodeIds.clear();
								this.nearSolutionMORE(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForMore);
								curCdnNodeIds.addAll(this.newCdnNodeIds);
							}else{
								curCdnNodeIds.clear();
								this.nearSolutionKeep(lastCdnNodeIds,fixedCdnIds,g,1);
								curCdnNodeIds.addAll(this.newCdnNodeIds);
							}
						}else{
							ran = Math.random();
							if(ran < lessOrMoreTMP ){
								curCdnNodeIds.clear();
								this.nearSolutionLESS(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForLessTMP,caseKind);
								curCdnNodeIds.addAll(this.newCdnNodeIds);
							}
							else {
								curCdnNodeIds.clear();
								this.nearSolutionMORE(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForMore);
								curCdnNodeIds.addAll(this.newCdnNodeIds);
							}
						}

					}
					else if(caseKind == 1){
		
							if(lastCdnNodeIds.size()<60){
								countForLessTMP = 1;
								
								ran = Math.random();
								if(ran < 0.6 ){
									curCdnNodeIds.clear();
									this.nearSolutionLESS(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForLessTMP,caseKind);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
								}
								else if (ran < 0.8) {
									curCdnNodeIds.clear();
									this.nearSolutionMORE(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForMore);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
								}else{
									curCdnNodeIds.clear();
									this.nearSolutionKeep(lastCdnNodeIds,fixedCdnIds,g,1);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
								}
							}else{
								ran = Math.random();
								if(ran < lessOrMoreTMP ){
									curCdnNodeIds.clear();
									this.nearSolutionLESS(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForLessTMP,caseKind);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
								}
								else{
									curCdnNodeIds.clear();
									this.nearSolutionMORE(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForMore);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
									
								}
							}

						
						
						
					}
					else if(caseKind == 0){
						
						if(GraphAlgo.bestCost <= 28876){
							tmpB = false;
						}
						if(tmpB){

							if(lastCdnNodeIds.size()<40){
								countForLessTMP = 1;
								ran = Math.random();
								if(ran < 0.6 ){
									curCdnNodeIds.clear();
									this.nearSolutionLESS(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForLessTMP,caseKind);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
								}
								else if (ran < 0.8) {
									curCdnNodeIds.clear();
									this.nearSolutionMORE(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForMore);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
								}else{
									curCdnNodeIds.clear();
									this.nearSolutionKeep(lastCdnNodeIds,fixedCdnIds,g,1);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
								}
							}else{
								ran = Math.random();
								if(ran > lessOrMore ){
									curCdnNodeIds.clear();
									this.nearSolutionMORE(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForMore);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
								}
								else{
									curCdnNodeIds.clear();
									this.nearSolutionLESS(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForLessTMP,caseKind);
									curCdnNodeIds.addAll(this.newCdnNodeIds);
								}
							}

						}else{

							ran = Math.random();
							if(ran < 0.5 ){
								curCdnNodeIds.clear();
								this.nearSolutionLESS(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForLessTMP,caseKind);
								curCdnNodeIds.addAll(this.newCdnNodeIds);
							}
							else if (ran < 0.75) {
								curCdnNodeIds.clear();
								this.nearSolutionMOREforLow(lastCdnNodeIds,fixedCdnIds,g,graphAlgo,countForMore);
								curCdnNodeIds.addAll(this.newCdnNodeIds);
							}else{
								curCdnNodeIds.clear();
								this.nearSolutionKeepforLow(lastCdnNodeIds,fixedCdnIds,g,1);
								curCdnNodeIds.addAll(this.newCdnNodeIds);
							}
						}
					}


					countTimes++;
					System.out.println("***************以下是第："+ countTimes +"***************");
					
					
					curCost = graphAlgo.MincostMaxflowForGA(g, curCdnNodeIds,t);
					if(curCost == -1) {
						unacTimes++;
						System.out.println("Not available! Continue.");
						continue;
					}

					curCost += curCdnNodeIds.size() * g.cdnCost;
					if(curCost < lastCost){
						System.out.println("----------------------------------");
						System.out.println("curCost < lastCost:  "+curCost +"  <   "+lastCost);
						lastCost = curCost;
						lastCdnNodeIds.clear();
						lastCdnNodeIds.addAll(curCdnNodeIds);
						System.out.println("更优！！！接受！！！当前cost:  "+lastCost );
						System.out.println("当前cdn数目:  "+lastCdnNodeIds.size() );
						System.out.println("----------------------------------");
						unacTimes = 0;
						break;
					}
					
					metropolis = Math.exp( -(double)(curCost - lastCost) / this.curT); 
					System.out.println("----------------------------------");
					System.out.println("(double)(curCost - lastCost) :"+(double)(curCost - lastCost) );
					System.out.println("metropolis :"+metropolis  );
					if(metropolis >= this.p){
						lastCost = curCost;
						lastCdnNodeIds.clear();
						lastCdnNodeIds.addAll(curCdnNodeIds);
						System.out.print("只差了一点。接受！！！ "  );
						System.out.println("当前cost :"+lastCost  );
						System.out.println("当前cdn数目:  "+lastCdnNodeIds.size() );
						System.out.println("----------------------------------");
						unacTimes = 0;
						break;
					}
					unacTimes ++;
					System.out.println("差太多，不接受！！！"+"当前维持cost :"+lastCost  );//
					System.out.println("当前维持cdn数目:  "+lastCdnNodeIds.size() );
					System.out.println("----------------------------------");
					break;
				}
			}
			this.curT *= deCold ;
		}
	}
	

	private void nearSolutionLESS(ArrayList<Integer> lastCdnNodeIds,ArrayList<Integer> fixedCdnIds,NetworkGraph g, GraphAlgo graphAlgo,int countForLess,int caseKind){

		newCdnNodeIds.clear();
		newCdnNodeIds.addAll(lastCdnNodeIds) ;

		int ran,ranSelectedCdnId;
 
		for(int i = 0; i < countForLess; i++){
			while(true){
				ran = (int)(Math.random() * newCdnNodeIds.size()) ; 
				ranSelectedCdnId = newCdnNodeIds.get(ran);
				if(!fixedCdnIds.contains(ranSelectedCdnId)){
					break;
				}
			}
			newCdnNodeIds.remove(ran);
		}
		return ;	
	}
	
	
	private void nearSolutionKeep(ArrayList<Integer> lastCdnNodeIds,ArrayList<Integer> fixedCdnIds,NetworkGraph g,int countForKeep){

		newCdnNodeIds.clear();
		newCdnNodeIds.addAll(lastCdnNodeIds) ;

		int ran,ranSelectedCdnId;
 
		for(int i = 0; i < countForKeep; i++){
			while(true){
				ran = (int)(Math.random() * newCdnNodeIds.size()) ; 
				ranSelectedCdnId = newCdnNodeIds.get(ran);
				if(fixedCdnIds.contains(ranSelectedCdnId)){
					continue;
				}
				if(g.getNode(ranSelectedCdnId).userNeighbors.size()<1){
					continue;
				}
				int ran2 = (int)(Math.random() * g.getNode(ranSelectedCdnId).userNeighbors.size());
				int ranSelectedCdnId2 = g.getNode(ranSelectedCdnId).userNeighbors.get(ran2);
				if(newCdnNodeIds.contains(ranSelectedCdnId2)){
					continue;
				}
				newCdnNodeIds.remove(ran);
				newCdnNodeIds.add(ranSelectedCdnId2);
				break;

			}
	
		}
		return ;	
	}
	
	private void nearSolutionMORE(ArrayList<Integer> lastCdnNodeIds,ArrayList<Integer> fixedCdnIds,NetworkGraph g, GraphAlgo graphAlgo,int countForMore){

		newCdnNodeIds.clear();
		newCdnNodeIds.addAll(lastCdnNodeIds) ;
		
		int ran;
		int ranSelectedCdnId;
		for(int i = 0; i < countForMore; i++){
			while(true){
				if(lastCdnNodeIds.size() >= g.users.userList.size()){
					System.out.println("加满了,目前个数："+lastCdnNodeIds.size());
					return ;
				}
				ran = (int)(Math.random() * g.users.userList.size()) ; 
				ranSelectedCdnId = g.users.userList.get(ran).linkedNodeId;
				if(newCdnNodeIds.contains(ranSelectedCdnId)){
					continue;
				}else{
					newCdnNodeIds.add(ranSelectedCdnId);
					break ;
				}
			}
		}
		return ;
	}

	private void nearSolutionMOREforLow(ArrayList<Integer> lastCdnNodeIds,ArrayList<Integer> fixedCdnIds,NetworkGraph g, GraphAlgo graphAlgo,int countForMore){

		newCdnNodeIds.clear();
		newCdnNodeIds.addAll(lastCdnNodeIds) ;
		
		int ran;
		int ranSelectedCdnId;
		for(int i = 0; i < countForMore; i++){
			while(true){
				if(lastCdnNodeIds.size() >= g.nodeNum){
					System.out.println("forLLL加满了,目前个数："+lastCdnNodeIds.size());
					return ;
				}
				ran = (int)(Math.random() * g.nodes.size()) ; 
				ranSelectedCdnId = g.nodes.get(ran).nodeId;
				if(ranSelectedCdnId >= g.nodeNum){
					continue;
				}
				if(newCdnNodeIds.contains(ranSelectedCdnId)){
					continue;
				}else{
					newCdnNodeIds.add(ranSelectedCdnId);
					break ;
				}
			}
		}
		return ;
	}

	private void nearSolutionKeepforLow(ArrayList<Integer> lastCdnNodeIds,ArrayList<Integer> fixedCdnIds,NetworkGraph g,int countForKeep){

		newCdnNodeIds.clear();
		newCdnNodeIds.addAll(lastCdnNodeIds) ;

		int ran,ranSelectedCdnId;
 
		for(int i = 0; i < countForKeep; i++){
			while(true){
				ran = (int)(Math.random() * g.nodes.size()) ; 
				ranSelectedCdnId = g.nodes.get(ran).nodeId;
				if(fixedCdnIds.contains(ranSelectedCdnId) ||  ranSelectedCdnId>=g.nodeNum || g.getNode(ranSelectedCdnId).neighbors.size()<1){
					continue;
				}
				
				int ran2 = (int)(Math.random() * g.getNode(ranSelectedCdnId).neighbors.size());
				int ranSelectedCdnId2 = g.getNode(ranSelectedCdnId).neighbors.get(ran2);
				if(newCdnNodeIds.contains(ranSelectedCdnId2)){
					continue;
				}
				for(int dn = 0;dn<newCdnNodeIds.size();dn++){
					if(newCdnNodeIds.get(dn)==ranSelectedCdnId){
						newCdnNodeIds.remove(dn);
					}
				}
				newCdnNodeIds.add(ranSelectedCdnId2);
				break;

			}
	
		}
		return ;	
	}
	
	
}

	
