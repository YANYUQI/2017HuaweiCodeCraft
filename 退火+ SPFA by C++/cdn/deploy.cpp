#include "deploy.h"
#include <stdio.h>

int nodeNum, edgeNum, userNum;
vector<int> nodes[MAX_NODE_NUM];
bool realCDN[MAX_NODE_NUM];

vector<Edge> edges;
int cdnCost;
int totalReq;

int superSourceId;
int superSinkId;

int map_NdToUser[MAX_NODE_NUM];
int map_UserToNd[MAX_USER_NUM];
int user_req[MAX_USER_NUM];
int MAX_Int = numeric_limits<int>::max();

int bestCost;
vector<string> bestPaths;

//SPFA相关变量
int aug[MAX_NODE_NUM];
int disTo[MAX_NODE_NUM];
int edgeTo[MAX_NODE_NUM];
int inQ[MAX_NODE_NUM];

char * result_file;
string topo_file;

//SA参量
double curT;
double deCold;
int interTimes;
int outerTimes;
double p;
vector<int> newCDNNdIds;
vector<int> neighbors[MAX_NODE_NUM];
vector<int> userNeighbors[MAX_NODE_NUM];

bool run = true;

//你要完成的功能总入口
void deploy_server(char * topo[MAX_EDGE_NUM], int line_num, char * filename)
{
	signal(SIGALRM, handler);
	result_file = filename;
	topo_file = "";
	//初始化
	nodeNum = 0;
	edgeNum = 0;
	userNum = 0;
	cdnCost = 0;
	totalReq = 0;
	superSourceId = 0;
	superSinkId = 0;
	bestCost = MAX_Int;
	
	for(int i = 0; i < MAX_NODE_NUM; i++)
	{
		nodes[i].clear();
		neighbors[i].clear();
		userNeighbors[i].clear();
	}

	edges.clear();
	bestPaths.clear();
	newCDNNdIds.clear();

	memset(map_NdToUser, -1, sizeof(map_NdToUser));
	memset(map_UserToNd, -1, sizeof(map_UserToNd));
	memset(user_req, 0, sizeof(user_req));
	memset(realCDN, 0, sizeof(realCDN));

	memset(aug, 0, sizeof(aug));
	memset(disTo, MAX_Int, sizeof(disTo));
	memset(edgeTo, -1, sizeof(edgeTo));
	memset(inQ, 0, sizeof(inQ));

	//读取基本数据
	char *charIndex = NULL;
	charIndex = charToNum(topo[0], nodeNum);
	charIndex = charToNum(charIndex, edgeNum);
	charIndex = charToNum(charIndex, userNum);
	charIndex = charToNum(topo[2], cdnCost);

	// printf("%d ", nodeNum);
	// printf("%d ", edgeNum);
	// printf("%d ", userNum);
	// printf("%d\n", cdnCost);

	//设置超级原点
	superSourceId = nodeNum;
	//设置超级聚点
	superSinkId = nodeNum + 1;


	//记录边信息
	int countEdges = 4 + edgeNum;
	for(int i = 4; i < countEdges; i++)
	{
		int Nd1;
		int Nd2;
		int capacity;
		int cost;

		charIndex = charToNum(topo[i], Nd1);
		charIndex = charToNum(charIndex, Nd2);
		charIndex = charToNum(charIndex, capacity);
		charIndex = charToNum(charIndex, cost);

		// printf("%d ", Nd1);
		// printf("%d ", Nd2);
		// printf("%d ", capacity);
		// printf("%d\n", cost);

		edges.push_back(Edge(Nd1, Nd2, capacity, cost, 0));
		edges.push_back(Edge(Nd2, Nd1, 0, -cost, 0));
		int m = edges.size();
		nodes[Nd1].push_back(m - 2);
		nodes[Nd2].push_back(m - 1);

		edges.push_back(Edge(Nd2, Nd1, capacity, cost, 0));
		edges.push_back(Edge(Nd1, Nd2, 0, -cost, 0));
		m = edges.size();
		nodes[Nd2].push_back(m - 2);
		nodes[Nd1].push_back(m - 1);
	}

	// printf("\n");
	// printf("%d\n", edges.size());
	// printf("\n");

	//记录用户信息
	int countUsers = countEdges + 1 + userNum;
	for(int i = countEdges + 1; i < countUsers; i++)
	{
		int userId;
		int linkedNd;
		int req;

		charIndex = charToNum(topo[i], userId);
		charIndex = charToNum(charIndex, linkedNd);
		charIndex = charToNum(charIndex, req);

		// printf("%d ", userId);
		// printf("%d ", linkedNd);
		// printf("%d\n", req);

		map_UserToNd[userId] = linkedNd;
		map_NdToUser[linkedNd] = userId;
		user_req[userId] = req;
		totalReq += req;
	}

	setAllNeighbors();
	
	if(nodeNum < 200)
	{	
		alarm(2);
		int toleranceForCandidates = 600;
		int toleranceForFixed = 0;
		vector<int> cdnNdIds;
		addSuperSinkEdges();
		//printf("加边没问题\n");
		while(true)
		{

			selfishness(cdnNdIds, toleranceForCandidates);
			printf("固定点没问题\n");
			int tmpCost = MinCostMaxFlowSA(cdnNdIds);
			if(tmpCost < 0)
			{
				printf("初始解不可行！！！！！\n");
				toleranceForCandidates += 50;
				continue;
			}
			else
			{
				printf("初始解可行，开始退火！！！！！\n");
				break;
			}
		}
		vector<int> fixedCdnIds;
		selfishness(fixedCdnIds, toleranceForFixed);
		printf("fixedCdnIds:   %d 个\n", fixedCdnIds.size());
		printf("cdnNdIds:   %d 个\n", cdnNdIds.size());
		Fire(2, 1, 5000, 10000, 0.00001);
		fireAlgo(cdnNdIds, fixedCdnIds, 1, 0, 1, 0.75);
	}
	else if(nodeNum < 400)
	{
		alarm(88);
		int toleranceForCandidates = 600;
		int toleranceForFixed = 0;
		vector<int> cdnNdIds;
		addSuperSinkEdges();
		while(true)
		{
			selfishness(cdnNdIds, toleranceForCandidates);
			int tmpCost = MinCostMaxFlowSA(cdnNdIds);
			if(tmpCost < 0)
			{
				printf("初始解不可行！！！！！\n");
				toleranceForCandidates += 50;
				continue;
			}
			else
			{
				printf("初始解可行，开始退火！！！！！\n");
				break;
			}
		}
		vector<int> fixedCdnIds;
		selfishness(fixedCdnIds, toleranceForFixed);
		printf("fixedCdnIds:   %d 个\n", fixedCdnIds.size());
		printf("cdnNdIds:   %d 个\n", cdnNdIds.size());
		Fire(5, 1, 5000, 10000, 0.1);
		fireAlgo(cdnNdIds, fixedCdnIds, 1, 1, 1, 0.75);
	}
	else
	{
		alarm(88);
		int toleranceForCandidates = 800;
		int toleranceForFixed = 600;
		vector<int> cdnNdIds;
		addSuperSinkEdges();
		while(true)
		{
			selfishness(cdnNdIds, toleranceForCandidates);
			int tmpCost = MinCostMaxFlowSA(cdnNdIds);
			if(tmpCost < 0)
			{
				printf("初始解不可行！！！！！\n");
				toleranceForCandidates += 50;
				continue;
			}
			else
			{
				printf("初始解可行，开始退火！！！！！\n");
				break;
			}
		}
		vector<int> fixedCdnIds;
		selfishness(fixedCdnIds, toleranceForFixed);
		printf("fixedCdnIds:   %d 个\n", fixedCdnIds.size());
		printf("cdnNdIds:   %d 个\n", cdnNdIds.size());
		Fire(10, 1, 5000, 10000, 0.1);
		fireAlgo(cdnNdIds, fixedCdnIds, 8, 2, 1, 0.99);
	}

	printf("bestCost: %d\n", bestCost);
	printf("bestPathsSize: %d\n", bestPaths.size());



	// addSuperSinkEdges();
	// vector<int> fixedCdnIds;
	// selfishness(fixedCdnIds);
	// vector<int> a;
	// a.push_back(0);
	// a.push_back(3);
	// a.push_back(22);
	// int cost = MinCostMaxFlowGA(a);
	// printf("\n");
	// printf("%d\n", cost);
	// printf("\n");
	// 需要输出的内容
	//char * topo_file = (char *)"17\n\n0 8 0 20\n21 8 0 20\n9 11 1 13\n21 22 2 20\n23 22 2 8\n1 3 3 11\n24 3 3 17\n27 3 3 26\n24 3 3 10\n18 17 4 11\n1 19 5 26\n1 16 6 15\n15 13 7 13\n4 5 8 18\n2 25 9 15\n0 7 10 10\n23 24 11 23";
	//char * topox = buildTopoResult();
	//string topox;
	buildTopoResult(topo_file);
	// 直接调用输出文件的方法输出到指定文件中(ps请注意格式的正确性，如果有解，第一行只有一个数据；第二行为空；第三行开始才是具体的数据，数据之间用一个空格分隔开)
	write_result((char *)topo_file.data(), result_file);
}

void Fire(double cT, double dC, int inter, int outer, double ratio)
{
	curT = cT;
	deCold = dC;
	interTimes = inter;
	outerTimes = outer;
	p = ratio;
}

void fireAlgo(vector<int>& cdnNdIds, vector<int>& fixedCdnIds, int countForLess, int caseKind, int countForMore, double lessOrMore)
{
	int countForLessTMP = countForLess;
	double lessOrMoreTMP = lessOrMore;
	
	vector<int> lastCdnNdIds;
	lastCdnNdIds.reserve(cdnNdIds.size());
	lastCdnNdIds.insert(lastCdnNdIds.end(), cdnNdIds.begin(), cdnNdIds.end());
	int lastCost = bestCost;

	vector<int> curCdnNdIds;
	int curCost;

	double ran;
	double metropolis;

	int countTimes = 0;
	//根据规模(caseKind)初始化！
	int threshold;

	if(caseKind == 2)
	{
		threshold = 155;
	}
	else if(caseKind == 1)
	{
		threshold = 60;
	}
	else if(caseKind == 0)
	{
		threshold = 40;
	}

	for(int curOuter = 0; curOuter < outerTimes; curOuter++)
	{
		for(int curInter = 0; curInter < interTimes; curInter++)
		{
			while(true)
			{
				if(!run) return;
				if(lastCdnNdIds.size() < threshold)
				{
					countForLessTMP = 1;
					ran = rand() / double(RAND_MAX);
					double lowToMid = 0.6;
					double midToHigh = 0.8;
					if(caseKind == 2)
					{
						lowToMid = 0.4;
						midToHigh = 0.7;
					}
					while(ran == 1)
					{
						ran = rand() / double(RAND_MAX);
					}
					if(ran < lowToMid)
					{
						curCdnNdIds.clear();
						nearSolusionLESS(lastCdnNdIds, fixedCdnIds, countForLessTMP, caseKind);
						curCdnNdIds.reserve(newCDNNdIds.size());
						curCdnNdIds.insert(curCdnNdIds.end(), newCDNNdIds.begin(), newCDNNdIds.end());
					}
					else if(ran < midToHigh)
					{
						curCdnNdIds.clear();
						nearSolusionMORE(lastCdnNdIds, fixedCdnIds, countForMore);
						curCdnNdIds.reserve(newCDNNdIds.size());
						curCdnNdIds.insert(curCdnNdIds.end(), newCDNNdIds.begin(), newCDNNdIds.end());
					}
					else
					{
						curCdnNdIds.clear();
						nearSolusionKEEP(lastCdnNdIds, fixedCdnIds, 1);
						curCdnNdIds.reserve(newCDNNdIds.size());
						curCdnNdIds.insert(curCdnNdIds.end(), newCDNNdIds.begin(), newCDNNdIds.end());
					}

				}
				else
				{
					ran = rand() / double(RAND_MAX);
					while(ran == 1)
					{
						ran = rand() / double(RAND_MAX);
					}
					if(ran > lessOrMoreTMP)
					{
						curCdnNdIds.clear();
						nearSolusionMORE(lastCdnNdIds, fixedCdnIds, countForMore);
						curCdnNdIds.reserve(newCDNNdIds.size());
						curCdnNdIds.insert(curCdnNdIds.end(), newCDNNdIds.begin(), newCDNNdIds.end());
					}
					else
					{
						curCdnNdIds.clear();
						nearSolusionLESS(lastCdnNdIds, fixedCdnIds, countForLess, caseKind);
						curCdnNdIds.reserve(newCDNNdIds.size());
						curCdnNdIds.insert(curCdnNdIds.end(), newCDNNdIds.begin(), newCDNNdIds.end());
					}
				}
				countTimes++;
				printf("********this is %d diedai********\n", countTimes);
				curCost = MinCostMaxFlowSA(curCdnNdIds);
				if(curCost == -1)
				{
					printf("Not available! Continue.\n");
					continue;
				}
				if(curCost < lastCost)
				{
					printf("---------------------------------------------\n");
					printf("curCost < lastCost: %d  <  %d\n", curCost, lastCost);
					lastCost = curCost;
					lastCdnNdIds.clear();
					lastCdnNdIds.reserve(curCdnNdIds.size());
					lastCdnNdIds.insert(lastCdnNdIds.end(), curCdnNdIds.begin(), curCdnNdIds.end());
					printf("better!!!!! received !!!!! curCost: %d\n", lastCost);
					printf("curCDNNum is %d\n", lastCdnNdIds.size());
					printf("---------------------------------------------\n");
					break;
				}
				metropolis = exp(-(double)(curCost - lastCost)/curT);
				printf("---------------------------------------------\n");
				printf("(double) (curCost - lastCost) : %d\n", (curCost - lastCost));
				printf("metropolis : %10f\n", metropolis);
				if(metropolis >= p)
				{
					lastCost = curCost;
					lastCdnNdIds.clear();
					lastCdnNdIds.reserve(curCdnNdIds.size());
					lastCdnNdIds.insert(lastCdnNdIds.end(), curCdnNdIds.begin(), curCdnNdIds.end());
					printf("worst a little \n");
					printf("lastCost:  %d\n", lastCost);
					printf("curCDNNum is %d\n", lastCdnNdIds.size());
					printf("---------------------------------------------\n");
					break;
				}
				printf("too worst!!! \n");
				printf("lastCost:  %d\n", lastCost);
				printf("curCDNNum is %d\n", lastCdnNdIds.size());
				printf("---------------------------------------------\n");
				break;
			}
		}
		curT *= deCold;
	}
}

void nearSolusionLESS(vector<int>& lastCdnNdIds, vector<int>& fixedCdnIds, int countForLess, int caseKind)
{
	newCDNNdIds.clear();
	newCDNNdIds.reserve(lastCdnNdIds.size());
	newCDNNdIds.insert(newCDNNdIds.end(), lastCdnNdIds.begin(), lastCdnNdIds.end());
	int ran, ranSelectedCdnId;
	double ranTMP;
	for(int i = 0; i < countForLess; i++)
	{
		while(true)
		{
			//printf("-------1--------\n");
			ranTMP = rand() / double(RAND_MAX);
			while(ranTMP == 1)
			{	
				//printf("-------2--------\n");
				ranTMP = rand() / double(RAND_MAX);
			}
			ran = (int)(ranTMP * newCDNNdIds.size());
			ranSelectedCdnId = newCDNNdIds[ran];
			if(!realCDN[ranSelectedCdnId])
			{
				break;
			}
		}
		newCDNNdIds.erase(ran + newCDNNdIds.begin());
	}
}
void nearSolusionKEEP(vector<int>& lastCdnNdIds, vector<int>& fixedCdnIds, int countForKeep)
{
	newCDNNdIds.clear();
	newCDNNdIds.reserve(lastCdnNdIds.size());
	newCDNNdIds.insert(newCDNNdIds.end(), lastCdnNdIds.begin(), lastCdnNdIds.end());
	int ran, ranSelectedCdnId;
	double ranTMP;
	for(int i = 0; i < countForKeep; i++)
	{
		while(true)
		{
			ranTMP = rand() / double(RAND_MAX);
			while(ranTMP == 1)
			{
				ranTMP = rand() / double(RAND_MAX);
			}
			ran = (int)(ranTMP * newCDNNdIds.size());
			ranSelectedCdnId = newCDNNdIds[ran];
			if(realCDN[ranSelectedCdnId])
			{
				continue;
			}
			if(userNeighbors[ranSelectedCdnId].size() < 1)
			{
				continue;
			}
			ranTMP = rand() / double(RAND_MAX);
			while(ranTMP == 1)
			{
				ranTMP = rand() / double(RAND_MAX);
			}
			int ran2 = (int)(ranTMP * userNeighbors[ranSelectedCdnId].size());
			int ranSelectedCdnId2 = userNeighbors[ranSelectedCdnId][ran2];
			vector<int>::iterator it = find(newCDNNdIds.begin(), newCDNNdIds.end(), ranSelectedCdnId2);
			if(it != newCDNNdIds.end())
			{
				continue;
			}
			newCDNNdIds.erase(ran + newCDNNdIds.begin());
			newCDNNdIds.push_back(ranSelectedCdnId2);
			break;
		}
	}
}
void nearSolusionMORE(vector<int>& lastCdnNdIds, vector<int>& fixedCdnIds, int countForMore)
{
	newCDNNdIds.clear();
	newCDNNdIds.reserve(lastCdnNdIds.size());
	newCDNNdIds.insert(newCDNNdIds.end(), lastCdnNdIds.begin(), lastCdnNdIds.end());
	int ran, ranSelectedCdnId;
	double ranTMP;
	for(int i = 0; i < countForMore; i++)
	{
		while(true)
		{
			if(lastCdnNdIds.size() >= userNum)
			{
				printf("It's Fulllllll!!! \n");
				return;
			}
			ranTMP = rand() / double(RAND_MAX);
			while(ranTMP == 1)
			{
				ranTMP = rand() / double(RAND_MAX);
			}
			ran = (int)(ranTMP * userNum);
			ranSelectedCdnId = map_UserToNd[ran];
			vector<int>::iterator it = find(newCDNNdIds.begin(), newCDNNdIds.end(), ranSelectedCdnId);
			if(it != newCDNNdIds.end())
			{
				continue;
			}
			else
			{
				newCDNNdIds.push_back(ranSelectedCdnId);
				break;
			}
		}
	}
}

void setAllNeighbors()
{
	for(int i = 0; i < nodeNum; i++)
	{
		for(int j = 0; j < nodes[i].size(); j++)
		{
			Edge& e = edges[nodes[i][j]];
			if(e.cost > 0)
			{
				neighbors[i].push_back(e.endNd);
				if(map_NdToUser[e.endNd] != -1)
				{
					userNeighbors[i].push_back(e.endNd);
				}
			}
		}
	}
}

void handler(int sig)
{	
	//printf("正在输出结果！！！！");
	//buildTopoResult(topo_file);
	//write_result((char *)topo_file.data(), result_file);
	//exit(0);
	run = false;
}

void selfishness(vector<int>& nodeIds, int tolerance)
{
	for(int i = 0; i < userNum; i++)
	{
		map<int, int> links;
		vector<int>& node = nodes[map_UserToNd[i]];
		
		for(int j = 0; j < node.size(); j++)
		{
			Edge& edge = edges[node[j]];
			if(edge.cost > 0)
			{
				map<int,int>::iterator it = links.find(edge.cost);
				if(it != links.end())
				{
					links.insert(pair<int, int>(edge.cost, links.at(edge.cost) + edge.capacity));
				}
				else
				{
					links.insert(pair<int, int>(edge.cost, edge.capacity));
				}
			}
		}
		
		// map<int,int>::iterator x = links.begin();
		// while(x != links.end())
		// {
		// 	printf("-------first %d---second %d---\n", x->first, x->second);
		// 	x++;
		// }
		// printf("---------------------------------\n");
		int curUserReq = user_req[i];
		int leastCost = 0;
		map<int,int>::iterator itor = links.begin();
		while(itor != links.end())
		{	
			//printf("--4-----second %d---first %d---\n", itor->second, itor->first);
			curUserReq -= itor->second;
			leastCost += itor->first * itor->second;
			if(curUserReq == 0) break;
			if(curUserReq < 0)
			{
				leastCost -= (-curUserReq) * (itor->first);
				break;
			}
			itor++;
		}
		
		if(leastCost >= cdnCost - tolerance)
		{	
			if(tolerance == 0)
			{
				realCDN[map_UserToNd[i]] = true;
				printf("\n---realCDN---%d------\n\n", realCDN[map_UserToNd[i]]);
			}	
			nodeIds.push_back(map_UserToNd[i]);
		}

	}
}

bool SPFA(int startNd, int endNd, int& cost, int& flow, vector<int>& paths)
{
	int end = nodeNum + 2;
	for(int i = 0; i < end; i++)
	{
		disTo[i] = MAX_Int;
		edgeTo[i] = -1;
		aug[i] = 0;
		inQ[i] = 0;
	}

	disTo[startNd] = 0;
	aug[startNd] = MAX_Int;
	inQ[startNd] = 1;
	deque<int> Q;
	Q.push_back(startNd);
	while(!Q.empty())
	{
		int nodeId = Q.front();
		Q.pop_front();
		inQ[nodeId]--;
		for(int i = 0; i < nodes[nodeId].size(); i++)
		{
			Edge& e = edges[nodes[nodeId][i]];
			if(e.capacity > e.flow && (disTo[e.endNd] > disTo[nodeId] + e.cost))
			{
				disTo[e.endNd] = disTo[nodeId] + e.cost;
				edgeTo[e.endNd] = nodes[nodeId][i];
				aug[e.endNd] = min(aug[nodeId], e.capacity - e.flow);
				if(!inQ[e.endNd])
				{	
					inQ[e.endNd]++;
					if(Q.empty())
					{
						Q.push_back(e.endNd);
					}
					else
					{
						if(disTo[e.endNd] >= disTo[Q.front()])
						{
							Q.push_back(e.endNd);
						}
						else
						{
							Q.push_front(e.endNd);
						}
					}
				}
			}
		}
	}
	if(disTo[endNd] == MAX_Int) return false;
	flow = aug[endNd];
	cost = disTo[endNd];
	int indexNd = endNd;

	while(indexNd != startNd)
	{	
		edges[edgeTo[indexNd]].flow += aug[endNd];
		edges[edgeTo[indexNd]^1].flow -= aug[endNd];
		paths.push_back(edgeTo[indexNd]);
		indexNd = edges[edgeTo[indexNd]].startNd;
	}
	return true;
}

int MinCostMaxFlowSA(vector<int>& nodeIds)
{
	addSuperSourceEdges(nodeIds);
	// printf("\n");
	// printf("%d\n", totalReq);
	// printf("\n");
	int totalCost = MinCostMaxFlow(superSourceId, superSinkId, totalReq);
	cleanSuperSourceEdges(nodeIds);

	int end = edges.size();
	for(int i = 0; i < end; i++)
	{
		edges[i].flow = 0;
	}

	return totalCost;
}

int MinCostMaxFlow(int startNd, int endNd, int neededFlow)
{
	int flowCost = 0;
	int countFlow = neededFlow;
	int cost = 0, flow = 0;
	vector<int> paths;
	while(SPFA(startNd, endNd, cost, flow, paths))
	{	
		flowCost += cost * flow;
		countFlow -= flow;
		// printf("%d ", flowCost);
		// printf("%d \n", countFlow);

		if(countFlow < 0)
		{	
			int end = paths.size();
			for(int i = 0; i < end; i++)
			{
				edges[paths[i]].flow += countFlow;
				edges[paths[i]^1].flow -= countFlow;
			}
			flowCost += countFlow * cost;
			break;
		}
		if(countFlow == 0)
		{
			break;
		}
		cost = 0;
		flow = 0;
		paths.clear();
	}
	if(countFlow > 0) return -1;
	int totalCost = flowCost + cdnCost * nodes[superSourceId].size();

	if(totalCost < bestCost)
	{	
		bestCost = totalCost;
		getBestPaths();
		// printf("%d\n", bestPaths.size());
		// for(int i = 0; i < bestPaths.size(); i++)
		// {
		// 	printf("%s\n", bestPaths[i].data());
		// }
	}
	return totalCost;
}

void getBestPaths()
{	
	bestPaths.clear();
	int end = nodes[superSourceId].size();
	for(int i = 0; i < end; i++)
	{	
		Edge& edge = edges[nodes[superSourceId][i]];
		string pathString = to_string(edge.endNd);
		getBestPathByDFS(edge, edge.flow, pathString);
	}
}

int getBestPathByDFS(Edge& edge, int minFlow, string& pathString)
{
	int newMinFlow = min(minFlow, edge.flow);
	int tmp = newMinFlow;
	if(edge.endNd == superSinkId)
	{
		pathString += " ";
		pathString += to_string(map_NdToUser[edge.startNd]);
		pathString += " ";
		pathString += to_string(newMinFlow);
		bestPaths.push_back(pathString);
		return newMinFlow;
	}
	int end = nodes[edge.endNd].size();
	for(int i = 0; i < end; i++)
	{
		Edge& subEdge = edges[nodes[edge.endNd][i]];
		if(subEdge.cost >= 0 && subEdge.flow > 0)
		{
			string subParhString = pathString;
			if(subEdge.endNd != superSinkId)
			{
				subParhString += " ";
				subParhString += to_string(subEdge.endNd);
			}
			int flwCost = getBestPathByDFS(subEdge, tmp, subParhString);
			tmp -= flwCost;
			if(tmp == 0) break;
		}
	}
	edge.flow -= newMinFlow;
	return newMinFlow;
}

void addSuperSinkEdges()
{
	for(int i = 0; i < userNum; i++)
	{
		edges.push_back(Edge(map_UserToNd[i], superSinkId, user_req[i], 0, 0));
		edges.push_back(Edge(superSinkId, map_UserToNd[i], 0, 0, 0));
		int m = edges.size();
		nodes[map_UserToNd[i]].push_back(m - 2);
		nodes[superSinkId].push_back(m - 1);
	}
}

void addSuperSourceEdges(vector<int>& CDNs)
{	
	int end = CDNs.size();
	for(int i = 0; i < end; i++)
	{
		edges.push_back(Edge(superSourceId, CDNs[i], MAX_Int, 0, 0));
		edges.push_back(Edge(CDNs[i], superSourceId, 0, 0, 0));
		int m = edges.size();
		nodes[superSourceId].push_back(m - 2);
		nodes[CDNs[i]].push_back(m - 1);
	}
}

void cleanSuperSourceEdges(vector<int>& CDNs)
{
	int end = CDNs.size();
	for(int i = 0; i < end; i++)
	{
		nodes[CDNs[i]].pop_back();
	}
	int begId = nodes[superSourceId][0];
	edges.erase(edges.begin() + begId, edges.end());
	nodes[superSourceId].clear();
}

char * charToNum(char * str, int& target)
{
	int sum = 0;
	while(((*str) != 32) && ((*str) != 13) && ((*str) != 0))
	{	
		sum = sum * 10 + ((*str) - '0');
		str++;
	}
	target = sum;
	return ++str;
}

void buildTopoResult(string& result)
{	
	int end = bestPaths.size();
	result = to_string(end);
	result += "\n\n";
	for(int i = 0; i < end; i++)
	{
		result += bestPaths[i];
		if(i != (end - 1)) result += "\n";
	}
}

