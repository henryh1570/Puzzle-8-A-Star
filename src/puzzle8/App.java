package puzzle8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class App {

	public static Scanner k = new Scanner(System.in);
	public static ArrayList<Character> list = new ArrayList<Character>();
	public static final String GOAL_STATE = "012345678";
	
	public static void main(String[] args) {
		// Preemptively prepare random puzzle generation
		for (char c: GOAL_STATE.toCharArray()) {
			list.add(c);
		}

		int choice = -1;
		while (choice != 3) {
			promptPuzzle();
			choice = getInput();
			performOperations(choice);
		}
	}
	
	// Generate a random solvable puzzle.
	public static String getRandomPuzzle() {
		String puzzle = "";
		do {
			Collections.shuffle(list);
			puzzle = "";
			for (Character c: list) {
				puzzle += c;
			}
		} while (!isSolvable(puzzle));
		return puzzle;
	}
	
	// Do some operation given user-input
	public static void performOperations(int choice) {
		String puzzle = "";
		int input = -1;
		switch (choice) {
		case 1:
			puzzle = getRandomPuzzle();
			System.out.println("Random Puzzle: " + puzzle);
			promptSolution();
			while (input == -1) {
				input = getInput();
			}
			
			if (input == 1) { // Heuristic 1
				
			} else {
				
			}
			// TODO: Solving Stuff
			break;
		case 2:
			System.out.println("Enter a Puzzle-8 String: ");
			puzzle = k.nextLine();
			System.out.println("P: " + puzzle);
			if (!isSolvable(puzzle)) {
				System.out.println("Invalid puzzle");
			} else {
				promptSolution();
				while (input == -1) {
					input = getInput();
				}
				
				if (input == 1) { // Heuristic 1
					
				} else {
					
				}
				//TODO: Solving stuff
			}			
			break;
		case 3:
			System.out.println("Goodbye.");
			break;
		default:
			System.out.println("Invalid Input");
			break;
		}
	}
	
	public static int getInput() {
		try {
			return Integer.parseInt(k.nextLine());
		} catch(Exception e) {
			return -1;
		}
	}
	
	public static boolean isSolvable(String state) {
		// First, Check size and only for digits
		if (state.length() != 9 || !state.matches("[0-9]+")) {
			return false;
		}
		
		// Second, Check if only unique digits, starting with '0'
		// Value is the next character
		// tracker records all characters that occur into its bits
		int tracker = 0;
		for (int a = 0; a < state.length(); a++) {
			int value = state.charAt(a) - '0';
			if ((tracker & (1 << value)) > 0) {
				return false;
			} else {
				tracker |= (1 << value);
			}
		}
		
		// Third, Check Inversions
		int inversions = 0;
		char[] arr = state.replace("0", "").toCharArray();
		
		// For all numbers in the puzzle
		for (int i = 0; i < arr.length; i++) {
			// For other numbers preceding the i-th one
			for (int k = i + 1; k < arr.length; k++) {
				// If the k-th number is less than the i-th one
				if (arr[i] < arr[k]) {
					inversions++;
				}
			}
		}
		
		// Odd # of inversions is unsolvable
		if (inversions % 2 == 1) {
			return false;
		}
		
		return true;
	}
	
	// h(n) = # of misplaced tiles
	public static int heuristicOne(String state) {
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
	public static int heuristicTwo(String state) {
		char[] current = state.toCharArray();
		
		int sumOfMoves = 0;
		
		for (int i = 0; i < current.length; i++) {
			sumOfMoves += movesFromSolution(i, Integer.parseInt(""+ current[i]));
		}
		
		return sumOfMoves;
	}
	
	//TODO: 
	public static ArrayList<String> solve(String initialState) {
		ArrayList<String> path = new ArrayList<String>();
		String currentState = initialState;
		while (!currentState.equals(GOAL_STATE)) {
			
		}
		return path;
	}
	
	public static int movesFromSolution(int currentPosition, int value) {
		// Coordinates of value's index in a 3 x 3 grid
		// X and Y are the offset of movements from the goal position
		int goalPosition = GOAL_STATE.indexOf(""+value);
		
		int x = Math.abs((currentPosition % 3) - (goalPosition % 3));
		int y = Math.abs((currentPosition / 3) - (goalPosition / 3));
		
		return x + y;
	}
	
	public static void promptSolution() {
		System.out.println("(1) Solve via heuristic 1 (# of misplaced tiles)");
		System.out.println("(2) Solve via heuristic 2 (sum of distances of tiles from goal positions)");
	}
	
	public static void promptPuzzle() {
		System.out.println("(1) Solve a randomly genereated Puzzle");
		System.out.println("(2) Solve a user-input Puzzle");		
		System.out.println("(3) Terminate Program");
	}
}
