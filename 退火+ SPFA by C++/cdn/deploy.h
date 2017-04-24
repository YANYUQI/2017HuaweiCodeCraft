#ifndef __ROUTE_H__
#define __ROUTE_H__

#include <iostream>
#include <vector>
#include <cstring>
#include <limits>
#include <deque>
#include <algorithm>
#include <map>
#include <cstdlib>
#include <math.h>
#include <unistd.h>
#include <signal.h>
#include <stdlib.h>
#include "lib_io.h"

#define MAX_NODE_NUM 1000
#define MAX_DEGREE_NUM 20
#define MAX_USER_NUM 500
#define MAX_PRICE 100
#define MAX_CAPACITY 100
#define MAX_CDN_COST 5000
#define MAX_REQ 5000

using namespace std;

struct Edge
{
	int startNd;
	int endNd;
	int capacity;
	int cost;
	int flow;
	Edge(int u, int v, int ca, int cst, int fl):startNd(u), endNd(v), capacity(ca), cost(cst), flow(fl){};
};

void deploy_server(char * graph[MAX_EDGE_NUM], int edge_num, char * filename);
void handler(int sig);

void Fire(double cT, double dC, int inter, int outer, double ratio);
void fireAlgo(vector<int>& cdnNdIds, vector<int>& fixedCdnIds, int countForLess, int caseKind, int countFormore, double lessOrMore);
void nearSolusionLESS(vector<int>& lastCdnNdIds, vector<int>& fixedCdnIds, int countForLess, int caseKind);
void nearSolusionKEEP(vector<int>& lastCdnNdIds, vector<int>& fixedCdnIds, int countForKeep);
void nearSolusionMORE(vector<int>& lastCdnNdIds, vector<int>& fixedCdnIds, int countForMore);

void setAllNeighbors();

bool SPFA(int startNd, int endNd, int& cost, int& flow);
int MinCostMaxFlow(int startNd, int endNd, int neededFlow);
int MinCostMaxFlowSA(vector<int>& nodeIds);
void selfishness(vector<int>& nodeIds, int tolerance);

void getBestPaths();
int getBestPathByDFS(Edge& edge, int minFlow, string& pathString);

void addSuperSinkEdges();
void addSuperSourceEdges(vector<int>& CDNs);
void cleanSuperSourceEdges(vector<int>& CDNs);

char * charToNum(char * str, int& target);
void buildTopoResult(string& result);

#endif
