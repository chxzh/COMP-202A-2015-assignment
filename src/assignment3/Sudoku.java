package assignment3;

public class Sudoku {
	public static boolean isSudoku(int[][] puzzle) {
		for (int i = 0; i < 9; i++) {
			if (!uniqueEntries(puzzle[i])){
				System.out.println("row "+i+" is not unique");
				return false;
			}
			if (!uniqueEntries(getColumn(puzzle, i))) {
				System.out.println("Column "+i+" is not unique");
				return false;
			}
			if (!uniqueEntries(flatten(subGrid(puzzle, i/3*3, i%3*3, 3)))) {
				System.out.println("sub grid " + i + "is not unique");
				return false;
			}
		}
		return true;
	}

	public static int[] sort(int[] array) {
		int[] result = array.clone();
		// bubble sort
		for (int i = 0; i < result.length - 1; i++) {
			for (int j = 0; j < result.length - 1 - i; j++) {
				if (result[j] > result[j + 1]) {
					int temp = result[j];
					result[j] = result[j + 1];
					result[j + 1] = temp;
				}
			}
		}
		return result;
	}

	public static boolean uniqueEntries(int[] array) {
		int[] sorted = sort(array);
		for (int i = 1; i < sorted.length; i++) {
			if (sorted[i - 1] == sorted[i])
				return false;
		}
		return true;
	}

	public static int[] getColumn(int[][] square, int j) {
		int[] result = new int[square.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = square[i][j];
		}
		return result;
	}

	public static int[] flatten(int[][] square) {
		if (square == null)
			return null;
		int totalLength = 0;
		for (int i = 0; i < square.length; i++) {
			// presuming no null array inside
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

	public static int[][] subGrid(int[][] square, int x, int y, int size) {
		int[][] subGrid = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				subGrid[i][j] = square[x + i][y + j];
			}
		}
		return subGrid;
	}

	public static void main(String args[]) {
		int[][] good = { 
				{ 5, 3, 4, 6, 7, 8, 9, 1, 2 },
				{ 6, 7, 2, 1, 9, 5, 3, 4, 8 }, 
				{ 1, 9, 8, 3, 4, 2, 5, 6, 7 },
				{ 8, 5, 9, 7, 6, 1, 4, 2, 3 }, 
				{ 4, 2, 6, 8, 5, 3, 7, 9, 1 },
				{ 7, 1, 3, 9, 2, 4, 8, 5, 6 }, 
				{ 9, 6, 1, 5, 3, 7, 2, 8, 4 },
				{ 2, 8, 7, 4, 1, 9, 6, 3, 5 }, 
				{ 3, 4, 5, 2, 8, 6, 1, 7, 9 } };
		int[][] rowError = { 
				{ 5, 3, 4, 6, 8, 5, 9, 1, 2 },
				{ 6, 7, 2, 1, 9, 9, 3, 4, 8 }, 
				{ 1, 9, 8, 3, 4, 2, 5, 6, 7 },
				{ 8, 5, 9, 7, 6, 1, 4, 2, 3 }, 
				{ 4, 2, 3, 8, 5, 3, 7, 9, 1 },
				{ 7, 1, 3, 9, 2, 4, 8, 5, 6 }, 
				{ 9, 6, 1, 5, 3, 7, 2, 8, 4 },
				{ 2, 8, 7, 4, 1, 9, 6, 3, 5 },
				{ 3, 4, 5, 2, 8, 6, 1, 7, 9 } };
		int[][] columnError = { 
				{ 5, 3, 4, 6, 7, 8, 9, 1, 2 },
				{ 6, 7, 2, 1, 9, 5, 3, 4, 8 }, 
				{ 1, 9, 8, 3, 4, 2, 5, 6, 7 },
				{ 8, 5, 9, 7, 6, 1, 4, 2, 3 }, 
				{ 4, 2, 6, 8, 5, 3, 7, 9, 1 },
				{ 7, 1, 3, 9, 2, 4, 8, 5, 6 }, 
				{ 9, 6, 1, 5, 3, 7, 2, 8, 4 },
				{ 5, 8, 7, 4, 1, 9, 6, 3, 5 }, 
				{ 3, 4, 5, 2, 8, 6, 1, 7, 9 } };
		int[][] GridError = { 
				{ 5, 3, 4, 6, 7, 8, 9, 1, 2 },
				{ 6, 5, 2, 1, 9, 5, 3, 4, 8 }, 
				{ 1, 9, 8, 3, 4, 2, 5, 6, 7 },
				{ 8, 5, 9, 7, 6, 1, 4, 2, 3 }, 
				{ 4, 2, 3, 8, 5, 3, 7, 9, 1 },
				{ 7, 1, 3, 9, 2, 4, 8, 5, 6 }, 
				{ 9, 6, 1, 5, 3, 7, 2, 8, 4 },
				{ 2, 8, 7, 4, 1, 9, 6, 3, 5 },
				{ 3, 4, 5, 2, 8, 6, 1, 7, 9 } };
		System.out.println(isSudoku(good));
		System.out.println(isSudoku(rowError));
		System.out.println(isSudoku(columnError));
		System.out.println(isSudoku(GridError));
	}
}
