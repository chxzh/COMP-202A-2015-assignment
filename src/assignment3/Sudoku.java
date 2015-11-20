package assignment3;

public class Sudoku {
	public static boolean isSudoku(int[][] puzzle) {
		return true;
	}
	
	public static int[] sort(int[] array) {
		if(array == null)return null;
		int[] result = new int[array.length];
		return result;
	}
	
	public static boolean uniqueEntries(int[] array) {
		return true;
	}
	
	public static int[] getColumn(int[][] square) {
		int[] result = null;
		return result;
	}
	
	public static int[] flatten(int[][] square) {
		if(square == null) return null;
		int totalLength = 0;
		for(int i = 0; i < square.length; i++) {
			//presuming no null array inside
			totalLength += square[i].length;
		}
		int[] result = new int[totalLength];
		for (int i = 0, t = 0; i < square.length; i++) {
			for (int j = 0; j < square[i].length; j++, t++) {
				result[t] = square[i][j];
			}
		}
		return result;
	}
	
	public static int[][] subGrid(int[][] square, int i, int j, int size) {
		int[][] subGrid = null;
		return subGrid;
	}
	public static void main() {
		int[][] sudoku = {
				{5,3,4,6,8,9,9,1,2},
				{6,7,2,1,9,5,3,4,8},
				{1,9,8,3,4,2,5,6,7},
				{8,5,9,7,6,1,4,2,3},
				{4,2,3,8,5,3,7,9,1},
				{7,1,3,9,2,4,8,5,6},
				{9,6,1,5,3,7,2,8,4},
				{2,8,7,4,1,9,6,3,5},
				{3,4,5,2,8,6,1,7,9}
		};
		System.out.println(isSudoku(sudoku));
	}
}
