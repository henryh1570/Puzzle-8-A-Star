package puzzle8;

import java.util.ArrayList;

public class SolutionData {
	protected ArrayList<String> path;
	protected long timeElapsed;
	protected int searchCost;
	protected int depth;
	
	public SolutionData(ArrayList<String> solution, long time, int nodesGenerated) {
		path = solution;
		timeElapsed = time;
		searchCost = nodesGenerated;
		depth = path.size() - 1;
	}
}