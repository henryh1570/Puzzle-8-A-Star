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
	private int[] configuration;
	private final int[] ILLEGAL_UP_POSITIONS = {0, 1, 2};
	private final int[] ILLEGAL_DOWN_POSITIONS = {6, 7, 8};
	private final int[] ILLEGAL_LEFT_POSITIONS = {0, 3, 6};
	private final int[] ILLEGAL_RIGHT_POSITIONS = {2, 5, 8};
	
	public Puzzle (String current){
		setConfiguration(current);
		position = current.indexOf('0');
	}

	public String toString() {
		String str = "";
		for (int i: configuration) {
			str += i;
		}
		return str;
	}

	// Set configuration to String state
	public void setConfiguration(String state) {
		char[] arr = state.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			configuration[i] = Integer.parseInt(""+arr[i]);
		}
	}
	
	// Return array of all legal configurations via move UP, DOWN, LEFT, and RIGHT
	public String[] getValidMoves() {
		ArrayList<String> validMoves = new ArrayList<String>();
		String state = toString();
		
		// Case: Move Up
		if (!contains(ILLEGAL_UP_POSITIONS, (position - 3))) {
			validMoves.add(swapString(state, position, position - 3));
		}
		// Case: Move Down
		if (!contains(ILLEGAL_DOWN_POSITIONS, (position + 3))) {
			validMoves.add(swapString(state, position, position + 3));
		}
		// Case: Move Left
		if (!contains(ILLEGAL_LEFT_POSITIONS, (position - 1))) {
			validMoves.add(swapString(state, position, position - 1));
		}
		// Case: Move Right
		if (!contains(ILLEGAL_RIGHT_POSITIONS, (position + 1))) {
			validMoves.add(swapString(state, position, position + 1));
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
}
