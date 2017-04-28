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
	public static final String FILENAME = "samples.txt";
	public static final String OUTFILE = "output.txt";

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
		Puzzle puzzle = null;
		int input = -1;

		switch (choice) {
		case 1: // Single Random Solve
			puzzle = new Puzzle(getRandomPuzzle(), 0, 0, null);
			System.out.println("Random Puzzle: " + puzzle.toString());
			singleSolve(input, puzzle);
			break;
		case 2: // Single User-Input Solve
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
		case 3: // Terminate
			System.out.println("Goodbye.");
			break;
		case 4: // Multi-Random, Dual-Heuristics Solving
			System.out.println("Enter # of tests: ");
			int iterations = getInput();
			multiSolve(puzzle, iterations);
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

	public static SolutionData solve(int input, Puzzle puzzle) {
		SolutionData solution = null;

		if (input == 1) { // Heuristic 1
			solution = puzzle.solve(puzzle.toString(), 1);
		} else { // Heuristic 2
			solution = puzzle.solve(puzzle.toString(), 2);
		}
		return solution;
	}

	public static void singleSolve(int input, Puzzle puzzle) {
		promptSolution();
		while (input == -1) {
			input = getInput();
		}

		SolutionData solution = solve(input, puzzle);

		for (String state : solution.path) {
			System.out.println(puzzle.prettyToString(state));
		}
		System.out.println("Number of steps/depth: " + (solution.depth));
		System.out.println("Time(ms) taken: " + (solution.timeElapsed));
		System.out.println("Average Branching Factor: " + (solution.averageBranchingFactor));
		System.out.println("Search Cost: " + (solution.searchCost));
		System.out.println("Effective Branching Factor: " + Math.pow(solution.searchCost, 1 / (double) solution.depth));
	}

	public static void multiSolve(HashMap<Integer, ArrayList<String>> samples) {
		try {
			File f = new File(OUTFILE);
			PrintWriter p = new PrintWriter(new FileOutputStream(f, false));

			Integer[] keys = samples.keySet().toArray(new Integer[samples.keySet().size()]);
			Arrays.sort(keys);

			// For every DEPTH level
			for (Integer layer : keys) {
				p.write(("DEPTH " + layer + "\n"));
				double aggregateABFH1 = 0.0;
				double averageDepthH1 = 0.0;
				double averageSearchCostH1 = 0.0;
				double averageTimeCostH1 = 0.0;
				double aggregateABFH2 = 0.0;
				double averageDepthH2 = 0.0;
				double averageSearchCostH2 = 0.0;
				double averageTimeCostH2 = 0.0;
				
				// For every puzzle in that DEPTH level
				for (String configuration : samples.get(layer)) {
					SolutionData sol = null;
					Puzzle puzzle = new Puzzle(configuration, 0, 0, null);
					sol = puzzle.solve(configuration, 1);

					String output = "Puzzle " + ": " + puzzle.toString() + " | depth(h1): " + sol.depth + " | ABF(h1): "
							+ sol.averageBranchingFactor + " | time(h1.ms): " + sol.timeElapsed + " | cost(h1): "
							+ sol.searchCost + " | EBF(h1): " + Math.pow(sol.searchCost, 1 / (double) sol.depth);
					aggregateABFH1 += sol.averageBranchingFactor;
					averageDepthH1 += sol.depth;
					averageSearchCostH1 += sol.searchCost;
					averageTimeCostH1 += sol.timeElapsed;

					sol = puzzle.solve(configuration, 2);
					output += " | depth(h2): " + sol.depth + " | ABF(h2): " + sol.averageBranchingFactor
							+ " | time(h2.ms): " + sol.timeElapsed + " | cost(h2): " + sol.searchCost + " | EBF(h2): "
							+ Math.pow(sol.searchCost, 1 / (double) sol.depth);
					aggregateABFH2 += sol.averageBranchingFactor;
					averageDepthH2 += sol.depth;
					averageSearchCostH2 += sol.searchCost;
					averageTimeCostH2 += sol.timeElapsed;

					p.write(output + "\n");
				}

				double iterations = samples.get(layer).size();
				// Average the sums and print the averaged data
				aggregateABFH1 = aggregateABFH1 / iterations;
				averageDepthH1 = averageDepthH1 / iterations;
				averageSearchCostH1 = averageSearchCostH1 / iterations;
				averageTimeCostH1 = averageTimeCostH1 / iterations;

				aggregateABFH2 = aggregateABFH2 / iterations;
				averageDepthH2 = averageDepthH2 / iterations;
				averageSearchCostH2 = averageSearchCostH2 / iterations;
				averageTimeCostH2 = averageTimeCostH2 / iterations;

				p.write("Average Summary of DEPTH " + layer + "\n");
				p.write("H1| ABF: " + aggregateABFH1 + " | Avg Depth: " + averageDepthH1
						+ " | Avg Search Cost: " + averageSearchCostH1 + " | Avg time: " + averageTimeCostH1
						+ " | Avg EBF: " + Math.pow(averageSearchCostH1, 1 / averageDepthH1) + "\n");
				p.write("H2| ABF: " + aggregateABFH2 + " | Avg Depth: " + averageDepthH2
						+ " | Avg Search Cost: " + averageSearchCostH2 + " | Avg time: " + averageTimeCostH2
						+ " | Avg EBF: " + Math.pow(averageSearchCostH2, 1 / averageDepthH2) + "\n");
			}
			p.close();
		} catch (Exception e) {
			System.out.println("Error writing to file");
		}
		System.out.println("Job finished. Check " + OUTFILE);
	}

	public static void multiSolve(Puzzle puzzle, int iterations) {
		double aggregateABFH1 = 0.0;
		double averageDepthH1 = 0.0;
		double averageSearchCostH1 = 0.0;
		double averageTimeCostH1 = 0.0;
		double aggregateABFH2 = 0.0;
		double averageDepthH2 = 0.0;
		double averageSearchCostH2 = 0.0;
		double averageTimeCostH2 = 0.0;

		// Sum the data of the solving iterations, print individual runs
		for (int i = 0; i < iterations; i++) {
			SolutionData sol = null;
			puzzle = new Puzzle(getRandomPuzzle(), 0, 0, null);
			sol = puzzle.solve(puzzle.toString(), 1);

			String output = "Puzzle " + i + ": " + puzzle.toString() + " | depth(h1): " + sol.depth + " | ABF(h1): "
					+ sol.averageBranchingFactor + " | time(h1.ms): " + sol.timeElapsed + " | cost(h1): "
					+ sol.searchCost + " | EBF(h1): " + Math.pow(sol.searchCost, 1 / (double) sol.depth);
			aggregateABFH1 += sol.averageBranchingFactor;
			averageDepthH1 += sol.depth;
			averageSearchCostH1 += sol.searchCost;
			averageTimeCostH1 += sol.timeElapsed;

			sol = puzzle.solve(puzzle.toString(), 2);
			output += " | depth(h2): " + sol.depth + " | ABF(h2): " + sol.averageBranchingFactor + " | time(h2.ms): "
					+ sol.timeElapsed + " | cost(h2): " + sol.searchCost + " | EBF(h2): "
					+ Math.pow(sol.searchCost, 1 / (double) sol.depth);
			aggregateABFH2 += sol.averageBranchingFactor;
			averageDepthH2 += sol.depth;
			averageSearchCostH2 += sol.searchCost;
			averageTimeCostH2 += sol.timeElapsed;

			System.out.println(output);
		}

		// Average the sums and print the averaged data
		aggregateABFH1 = aggregateABFH1 / iterations;
		averageDepthH1 = averageDepthH1 / iterations;
		averageSearchCostH1 = averageSearchCostH1 / iterations;
		averageTimeCostH1 = averageTimeCostH1 / iterations;

		aggregateABFH2 = aggregateABFH2 / iterations;
		averageDepthH2 = averageDepthH2 / iterations;
		averageSearchCostH2 = averageSearchCostH2 / iterations;
		averageTimeCostH2 = averageTimeCostH2 / iterations;

		System.out.println("Average Summary");
		System.out.println("H1| ABF: " + aggregateABFH1 + " | Avg Depth: " + averageDepthH1 + " | Avg Search Cost: "
				+ averageSearchCostH1 + " | Avg time: " + averageTimeCostH1 + " | Avg EBF: "
				+ Math.pow(averageSearchCostH1, 1 / averageDepthH1));
		System.out.println("H2| ABF: " + aggregateABFH2 + " | Avg Depth: " + averageDepthH2 + " | Avg Search Cost: "
				+ averageSearchCostH2 + " | Avg time: " + averageTimeCostH2 + " | Avg EBF: "
				+ Math.pow(averageSearchCostH2, 1 / averageDepthH2));
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

	public static HashMap<Integer, ArrayList<String>> readSamples() {
		HashMap<Integer, ArrayList<String>> samples = new HashMap<Integer, ArrayList<String>>();
		try {
			File f = new File(FILENAME);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			// Skip the first header
			int level =  Integer.parseInt(br.readLine().replaceAll("[^\\d]", ""));
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
