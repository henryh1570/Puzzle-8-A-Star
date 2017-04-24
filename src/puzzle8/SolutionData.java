package puzzle8;

import java.util.ArrayList;

public class SolutionData {
	protected ArrayList<String> path;
	protected double averageBranchingFactor;
	protected long timeElapsed;
	protected int searchCost;
	protected int depth;
	
	public SolutionData(ArrayList<String> solution, double abf, long time, int nodesGenerated, int steps) {
		path = solution;
		averageBranchingFactor = abf;
		timeElapsed = time;
		searchCost = nodesGenerated;
		depth = steps;
	}
}