package puzzle8;

import java.util.ArrayList;

public class SolutionData {
	protected ArrayList<String> path;
	protected double averageBranchingFactor;
	protected long timeElapsed;
	protected int searchCost;
	protected int depth;
	
	public SolutionData(ArrayList<String> solution, double abf, long time, int nodesGenerated) {
		path = solution;
		averageBranchingFactor = ((int)(abf*100))/(double)100;
		timeElapsed = time;
		searchCost = nodesGenerated;
		depth = path.size() - 1;
	}
}