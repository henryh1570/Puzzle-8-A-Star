package puzzle8;

import java.util.ArrayList;

public class Puzzle {
	
	// Configuration will be a string of numbers
	// Representing the state of the puzzle
	
	// example: 012345678
	// 0 1 2
	// 3 4 5
	// 6 7 8
	private int position;
	private String configuration;
	public final String GOAL_STATE = "012345678";
	private final int[] ILLEGAL_UP_POSITIONS = {0, 1, 2};
	private final int[] ILLEGAL_DOWN_POSITIONS = {6, 7, 8};
	private final int[] ILLEGAL_LEFT_POSITIONS = {0, 3, 6};
	private final int[] ILLEGAL_RIGHT_POSITIONS = {2, 5, 8};
	
	public Puzzle (String current){
		setConfiguration(current);
		position = current.indexOf('0');
	}

	public String toString() {
		return configuration;
	}

	// Set configuration to String state
	public void setConfiguration(String state) {
		configuration = state;
	}
	
	// Return array of all legal configurations via move UP, DOWN, LEFT, and RIGHT
	public String[] getValidMoves() {
		ArrayList<String> validMoves = new ArrayList<String>();
		
		// Case: Move Up
		if (!contains(ILLEGAL_UP_POSITIONS, (position - 3))) {
			validMoves.add(swapString(configuration, position, position - 3));
		}
		// Case: Move Down
		if (!contains(ILLEGAL_DOWN_POSITIONS, (position + 3))) {
			validMoves.add(swapString(configuration, position, position + 3));
		}
		// Case: Move Left
		if (!contains(ILLEGAL_LEFT_POSITIONS, (position - 1))) {
			validMoves.add(swapString(configuration, position, position - 1));
		}
		// Case: Move Right
		if (!contains(ILLEGAL_RIGHT_POSITIONS, (position + 1))) {
			validMoves.add(swapString(configuration, position, position + 1));
		}
		
		return validMoves.toArray(new String[validMoves.size()]);
	}
	
	// Check if an int array contains a given value
	public boolean contains(int[] arr, int value) {
		for (int i: arr) {
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
	
	// h(n) = # of misplaced tiles
	public int heuristicOne(String state) {
		char[] goal = GOAL_STATE.toCharArray();
		char[] current = state.toCharArray();
		
		int matches = 0;
		
		for (int i = 0; i < goal.length; i++) {
			if (goal[i] == current[i]) {
				matches++;
			}
		}
		
		return matches;
	}
	
	// h(n) =  sum of distances of tiles from their goal positions
	public int heuristicTwo(String state) {
		char[] current = state.toCharArray();
		
		int sumOfMoves = 0;
		
		for (int i = 0; i < current.length; i++) {
			sumOfMoves += movesFromSolution(i, Integer.parseInt(""+ current[i]));
		}
		
		return sumOfMoves;
	}
	
	//TODO: A* Search. Returns path of all transitioned states to GOAL_STATE.
	public ArrayList<String> solve(String initialState, int heuristic) {
		ArrayList<String> path = new ArrayList<String>();
		String currentState = initialState;
		
		while (!currentState.equals(GOAL_STATE)) {
			String[] frontier = getValidMoves();
			int value = Integer.MAX_VALUE;
			String next = "";
			
			// Check the frontier
			for (String state: frontier) {
				int heuristicValue;
				
				// Assign heuristic value to the specific node
				if (heuristic == 1) {
					heuristicValue = heuristicOne(state);
				} else {
					heuristicValue = heuristicTwo(state);
				}
				
				// Pick the lower state.
				if (heuristicValue < value) {
					value = heuristicValue;
					next = state;
				}
			}
			setConfiguration(next);
			path.add(next);
		}
		return path;
	}
	
	public int movesFromSolution(int currentPosition, int value) {
		// Coordinates of value's index in a 3 x 3 grid
		// X and Y are the offset of movements from the goal position
		int goalPosition = GOAL_STATE.indexOf(""+value);
		
		int x = Math.abs((currentPosition % 3) - (goalPosition % 3));
		int y = Math.abs((currentPosition / 3) - (goalPosition / 3));
		
		return x + y;
	}
}
