package puzzle8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Puzzle implements Comparable<Puzzle> {

	// Configuration will be a string of numbers
	// Representing the state of the puzzle

	// example: 012345678
	// 0 1 2
	// 3 4 5
	// 6 7 8
	private int emptyIndex;
	private int stepCost;
	private int estimatedCost;
	private String currentState;
	public final String GOAL_STATE = "012345678";
	private final int[] ILLEGAL_UP_INDICES = { 0, 1, 2 };
	private final int[] ILLEGAL_DOWN_INDICES = { 6, 7, 8 };
	private final int[] ILLEGAL_LEFT_INDICES = { 0, 3, 6 };
	private final int[] ILLEGAL_RIGHT_INDICES = { 2, 5, 8 };
	private Puzzle previousPuzzle;

	public Puzzle(String current, int startingCost, int hCost, Puzzle p) {
		currentState = current;
		emptyIndex = current.indexOf('0');
		stepCost = startingCost;
		estimatedCost = hCost;
		previousPuzzle = p;
	}
	
	// Return array of all legal neighbor states via move UP, DOWN, LEFT, RIGHT
	public ArrayList<String> getValidMoves() {
		ArrayList<String> validMoves = new ArrayList<String>();

		// Case: Move Up
		if (!contains(ILLEGAL_UP_INDICES, (emptyIndex))) {
			validMoves.add(swapString(currentState, emptyIndex, emptyIndex - 3));
		}
		// Case: Move Down
		if (!contains(ILLEGAL_DOWN_INDICES, (emptyIndex))) {
			validMoves.add(swapString(currentState, emptyIndex, emptyIndex + 3));
		}
		// Case: Move Left
		if (!contains(ILLEGAL_LEFT_INDICES, (emptyIndex))) {
			validMoves.add(swapString(currentState, emptyIndex, emptyIndex - 1));
		}
		// Case: Move Right
		if (!contains(ILLEGAL_RIGHT_INDICES, (emptyIndex))) {
			validMoves.add(swapString(currentState, emptyIndex, emptyIndex + 1));
		}

		return validMoves;
	}

	// h(n) = # of misplaced tiles
	public int heuristicOne(String state) {
		char[] goal = GOAL_STATE.toCharArray();
		char[] current = state.toCharArray();

		int mismatches = 0;

		for (int i = 0; i < goal.length; i++) {
			if (goal[i] != current[i]) {
				mismatches++;
			}
		}

		return mismatches;
	}

	// h(n) = sum of distances of tiles from their goal positions
	public int heuristicTwo(String state) {
		char[] current = state.toCharArray();

		int sumOfMoves = 0;

		for (int i = 0; i < current.length; i++) {
			sumOfMoves += movesFromSolution(i, Integer.parseInt("" + current[i]));
		}

		return sumOfMoves;
	}

	// Helper method to heuristicTwo. Calculate the coordinate distances.
	public int movesFromSolution(int currentPosition, int value) {
		// Coordinates of value's index in a 3 x 3 grid
		// X and Y are the offset of movements from the goal position
		int goalPosition = GOAL_STATE.indexOf("" + value);

		int x = Math.abs((currentPosition % 3) - (goalPosition % 3));
		int y = Math.abs((currentPosition / 3) - (goalPosition / 3));

		return x + y;
	}

	// A* Search. Returns the first path solution. It is guaranteed to be optimal
	// because the step cost g(n) is uniform for puzzle-8, which means that
	// evaluation function f(n) = g(n) + h(n)
	// In other words, every node is expanded in order of decreasing heuristic cost
	public SolutionData solve(String initialState, int heuristic) {
		PriorityQueue<Puzzle> frontier = new PriorityQueue<Puzzle>();
		HashSet<String> exploredSet = new HashSet<String>();
		
		frontier.add(new Puzzle(initialState, 0, 0, null));
		Puzzle current = null;
		long timeElapsed = 0;
		long timeStart = System.currentTimeMillis();
		
		while (!frontier.isEmpty()) {
			current = frontier.remove();
			exploredSet.add(current.currentState);

			if (!current.currentState.equals(GOAL_STATE)) {
				ArrayList<String> moves = current.getValidMoves();

				// Add neighbors to frontier
				for (String state : moves) {
					// Skip explored nodes
					if (!exploredSet.contains(state)) {
						int heuristicValue;

						// Assign heuristic value to the node
						if (heuristic == 1) {
							heuristicValue = heuristicOne(state);
						} else {
							heuristicValue = heuristicTwo(state);
						}
						
						frontier.add(new Puzzle((state), current.stepCost + 1, heuristicValue, current));
					}
				}
			} else {
				timeElapsed = System.currentTimeMillis() - timeStart;
				frontier.clear();
			}
		}

		// Rebuild the path
		ArrayList<String> path = new ArrayList<String>();
		while (current != null) {
			path.add(current.currentState);
			current = current.previousPuzzle;
		}
		Collections.reverse(path);
		return new SolutionData(path, timeElapsed, exploredSet.size() + frontier.size());
	}
	
	// Check if an integer array contains a given value
	public boolean contains(int[] arr, int value) {
		for (int i : arr) {
			if (i == value) {
				return true;
			}
		}
		return false;
	}

	// Return result of swapping two characters in a string
	public String swapString(String str, int index1, int index2) {
		char[] arr = str.toCharArray();
		char temp = arr[index1];
		arr[index1] = arr[index2];
		arr[index2] = temp;
		return new String(arr);
	}

	public String prettyToString(String configuration) {
		String str = configuration.substring(0, 3).replace("", " ").trim();
		str += "\n";
		str += configuration.substring(3, 6).replace("", " ").trim();
		str += "\n";
		str += configuration.substring(6, 9).replace("", " ").trim();
		str += "\n";
		return str;
	}

	@Override
	public int compareTo(Puzzle other) {
		// Returns the comparison of f(n) from both puzzles.
		// Necessary for implementing the priority queue heuristics.
		int priority1 = stepCost + estimatedCost;
		int priority2 = other.stepCost + other.estimatedCost;
		
		if (priority1 < priority2) {
			return -1;
		} else if (priority1 > priority2) {
			return 1;
		} else {
			return 0;
		}
	}
}
