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
		for (char c : GOAL_STATE.toCharArray()) {
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
			for (Character c : list) {
				puzzle += c;
			}
		} while (!isSolvable(puzzle));
		return puzzle;
	}

	// Do some operation given user-input
	public static void performOperations(int choice) {
		Puzzle puzzle;
		int input = -1;

		switch (choice) {
		case 1:
			puzzle = new Puzzle(getRandomPuzzle(), 0, 0, null);
			System.out.println("Random Puzzle: " + puzzle.toString());
			singleSolve(input, puzzle);
			break;
		case 2:
			System.out.println("Enter a Puzzle-8 String: ");
			String data = k.nextLine();
			if (!isSolvable(data)) {
				System.out.println("Invalid puzzle");
			} else {
				puzzle = new Puzzle(data, 0, 0, null);
				System.out.println("User Puzzle: " + puzzle.toString());
				singleSolve(input, puzzle);
			}
			break;
		case 3:
			System.out.println("Goodbye.");
			break;
		case 4:
			System.out.println("Enter # of tests: ");
			int iterations = getInput();
			for (int i = 0; i < iterations; i++) {
				puzzle = new Puzzle(getRandomPuzzle(), 0, 0, null);
				long startTime = System.currentTimeMillis();
				ArrayList<String> solution = solve(1, puzzle);
				long elapsedTime = System.currentTimeMillis() - startTime;
				String output = ("Puzzle " + i + ": " + puzzle.toString() + " | Steps(h1): " + (solution.size() - 1)
						+ " | Time(h1.ms): " + elapsedTime);

				startTime = System.currentTimeMillis();
				solution = solve(2, puzzle);
				elapsedTime = System.currentTimeMillis() - startTime;
				output += (" | Steps(h2): " + (solution.size() - 1) + " | Time(h2.ms): " + elapsedTime);
				System.out.println(output);
			}
			break;
		default:
			System.out.println("Invalid Input");
			break;
		}
	}

	public static ArrayList<String> solve(int input, Puzzle puzzle) {
		ArrayList<String> solution = new ArrayList<String>();

		if (input == 1) { // Heuristic 1
			solution = puzzle.solve(puzzle.toString(), 1);
		} else { // Heuristic 2
			solution = puzzle.solve(puzzle.toString(), 1);
		}
		return solution;
	}

	public static void singleSolve(int input, Puzzle puzzle) {
		promptSolution();
		while (input == -1) {
			input = getInput();
		}

		ArrayList<String> solution = solve(input, puzzle);

		for (String state : solution) {
			System.out.println(puzzle.prettyToString(state));
		}
		System.out.println("Number of steps :" + (solution.size() - 1));
	}

	public static int getInput() {
		try {
			return Integer.parseInt(k.nextLine());
		} catch (Exception e) {
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

	public static void promptSolution() {
		System.out.println("(1) Solve via heuristic 1 (# of misplaced tiles)");
		System.out.println("(2) Solve via heuristic 2 (sum of distances of tiles from goal positions)");
	}

	public static void promptPuzzle() {
		System.out.println("(1) Solve a randomly genereated Puzzle");
		System.out.println("(2) Solve a user-input Puzzle");
		System.out.println("(3) Terminate Program");
		System.out.println("(4) Large Random Testing");
	}
}
