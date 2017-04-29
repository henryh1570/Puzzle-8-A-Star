package puzzle8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class App {

	public static Scanner k = new Scanner(System.in);
	public static ArrayList<Character> list = new ArrayList<Character>();
	public static final String GOAL_STATE = "012345678";
	public static final String INPUT_FILE = "samples.txt";
	public static final String OUTPUT_FILE = "output.txt";
	public static final Puzzle PUZZLE_SOLVER = new Puzzle(GOAL_STATE, 0, 0, null);

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

	// Do some operation given user-input
	public static void performOperations(int choice) {
		String configuration = "";
		int input = -1;

		switch (choice) {
		case 1: // Single Random Solve
			configuration = getRandomPuzzle();
			System.out.println("Random Puzzle: " + configuration);
			singleSolve(input, configuration);
			break;
		case 2: // Single User-Input Solve
			System.out.println("Enter a Puzzle-8 String: ");
			configuration = k.nextLine();
			if (!isSolvable(configuration)) {
				System.out.println("Invalid puzzle");
			} else {
				System.out.println("User Puzzle: " + configuration);
				singleSolve(input, configuration);
			}
			break;
		case 3: // Terminate
			System.out.println("Goodbye.");
			break;
		case 4: // Multi-Random, Dual-Heuristics Solving
			System.out.println("Enter # of tests: ");
			int iterations = getInput();
			multiSolve(iterations);
			break;
		case 5: // Test the sample files
			HashMap<Integer, ArrayList<String>> samples = readSamples();
			multiSolve(samples);
			break;
		default:
			System.out.println("Invalid Input");
			break;
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

	// Generate a random solvable puzzle.
	public static String getRandomPuzzle() {
		String configuration = "";
		do {
			Collections.shuffle(list);
			configuration = "";
			for (Character c : list) {
				configuration += c;
			}
		} while (!isSolvable(configuration));
		return configuration;
	}

	// Choose between the two heuristics for a solution.
	public static SolutionData solve(int input, String configuration) {
		SolutionData solution = null;
		
		if (input == 1) {
			// Heuristic 1
			solution = PUZZLE_SOLVER.solve(configuration, 1);
		} else {
			// Heuristic 2
			solution = PUZZLE_SOLVER.solve(configuration, 2);
		}
		return solution;
	}

	// Produce a single solution result with statistics.
	public static void singleSolve(int input, String configuration) {
		promptSolution();
		while (input == -1) {
			input = getInput();
		}

		SolutionData solution = solve(input, configuration);

		// Visually display each change in state from initial to final
		for (String state : solution.path) {
			System.out.println(PUZZLE_SOLVER.prettyToString(state));
		}
		System.out.println("Number of steps/depth: " + (solution.depth));
		System.out.println("Time(ms) taken: " + (solution.timeElapsed));
		System.out.println("Search Cost: " + (solution.searchCost));
	}

	// Solve many puzzles from an input sample file to an output file.
	// Samples is a hashmap of arraylists of 200 puzzle configurations keyed by depth
	public static void multiSolve(HashMap<Integer, ArrayList<String>> samples) {
		try {
			File f = new File(OUTPUT_FILE);
			PrintWriter pw = new PrintWriter(new FileOutputStream(f, false));
			Integer[] keys = samples.keySet().toArray(new Integer[samples.keySet().size()]);
			Arrays.sort(keys);

			// For every DEPTH level
			for (Integer level : keys) {
				pw.write(("DEPTH " + level + "\n"));
				double avgSearchCostH1 = 0.0;
				double avgTimeH1 = 0.0;
				double avgSearchCostH2 = 0.0;
				double avgTimeH2 = 0.0;

				// For every individual puzzle at the current depth level.
				for (String configuration : samples.get(level)) {
					SolutionData sol = null;
					
					// Solve via heuristic one first
					sol = PUZZLE_SOLVER.solve(configuration, 1);
					String output = "Puzzle " + ": " + configuration + " | time(h1.ms): " + sol.timeElapsed
							+ " | cost(h1): " + sol.searchCost;
					avgSearchCostH1 += sol.searchCost;
					avgTimeH1 += sol.timeElapsed;
					
					// Solve via heuristic two
					sol = PUZZLE_SOLVER.solve(configuration, 2);
					output += " | time(h2.ms): " + sol.timeElapsed + " | cost(h2): " + sol.searchCost;
					avgSearchCostH2 += sol.searchCost;
					avgTimeH2 += sol.timeElapsed;

					pw.write(output + "\n");
				}

				double iterations = samples.get(level).size();
				// Average the sums and print the averaged data
				avgSearchCostH1 = avgSearchCostH1 / iterations;
				avgTimeH1 = avgTimeH1 / iterations;

				avgSearchCostH2 = avgSearchCostH2 / iterations;
				avgTimeH2 = avgTimeH2 / iterations;

				pw.write("Average Summary of DEPTH " + level + "\n");
				pw.write("H1| Avg Search Cost: " + avgSearchCostH1 + " | Avg time: " + avgTimeH1 + "\n");
				pw.write("H2| Avg Search Cost: " + avgSearchCostH2 + " | Avg time: " + avgTimeH2 + "\n");
			}
			pw.close();
		} catch (Exception e) {
			System.out.println("Error writing to file");
		}
		System.out.println("Job finished. Check " + OUTPUT_FILE);
	}

	// Solve many randomly generated puzzles.
	public static void multiSolve(int iterations) {
		double avgDepthH1 = 0.0;
		double avgSearchCostH1 = 0.0;
		double avgTimeH1 = 0.0;
		double avgDepthH2 = 0.0;
		double avgSearchCostH2 = 0.0;
		double avgTimeH2 = 0.0;

		// Sum the data of the solving iterations, print individual runs
		for (int i = 0; i < iterations; i++) {
			SolutionData sol = null;
			String configuration = getRandomPuzzle();
			sol = PUZZLE_SOLVER.solve(configuration, 1);

			String output = "Puzzle " + i + ": " + configuration + " | depth(h1): " + sol.depth + " | time(h1.ms): "
					+ sol.timeElapsed + " | cost(h1): " + sol.searchCost;
			avgDepthH1 += sol.depth;
			avgSearchCostH1 += sol.searchCost;
			avgTimeH1 += sol.timeElapsed;

			sol = PUZZLE_SOLVER.solve(configuration, 2);
			output += " | depth(h2): " + sol.depth + " | time(h2.ms): " + sol.timeElapsed + " | cost(h2): "
					+ sol.searchCost;
			avgDepthH2 += sol.depth;
			avgSearchCostH2 += sol.searchCost;
			avgTimeH2 += sol.timeElapsed;

			System.out.println(output);
		}

		// Average the sums and print the averaged data
		avgDepthH1 = avgDepthH1 / iterations;
		avgSearchCostH1 = avgSearchCostH1 / iterations;
		avgTimeH1 = avgTimeH1 / iterations;

		avgDepthH2 = avgDepthH2 / iterations;
		avgSearchCostH2 = avgSearchCostH2 / iterations;
		avgTimeH2 = avgTimeH2 / iterations;

		System.out.println("Average Summary");
		System.out.println("H1| Avg Depth: " + avgDepthH1 + " | Avg Search Cost: " + avgSearchCostH1 + " | Avg time: "
				+ avgTimeH1);
		System.out.println("H2| Avg Depth: " + avgDepthH2 + " | Avg Search Cost: " + avgSearchCostH2 + " | Avg time: "
				+ avgTimeH2);
	}

	public static int getInput() {
		try {
			return Integer.parseInt(k.nextLine());
		} catch (Exception e) {
			return -1;
		}
	}

	// Read samples.txt input file of 200 puzzle 8 problems
	public static HashMap<Integer, ArrayList<String>> readSamples() {
		HashMap<Integer, ArrayList<String>> samples = new HashMap<Integer, ArrayList<String>>();
		try {
			File f = new File(INPUT_FILE);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			// Skip the first header
			int level = Integer.parseInt(br.readLine().replaceAll("[^\\d]", ""));
			ArrayList<String> depth = new ArrayList<String>();

			String next = br.readLine();
			while (next != null) {
				if (next.toLowerCase().contains("depth")) {
					// Put the previous samples into hashmap. Clear data for
					// next set.
					samples.put(level, depth);
					level = Integer.parseInt(next.replaceAll("[^\\d]", ""));
					depth = new ArrayList<String>();
				} else {
					depth.add(next);
				}
				next = br.readLine();
			}
			// Last iteration
			samples.put(level, depth);
			br.close();
			fr.close();
			return samples;
		} catch (Exception e) {
			System.out.println("The sample file is unavailable.");
			return null;
		}
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
		System.out.println("(5) Test sample cases.");
	}
}
