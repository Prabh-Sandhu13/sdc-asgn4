import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mathdoku {
	public static List<String> puzzleInput = new ArrayList<String>();
	public static Map<String, List<int []>> groupCells = new HashMap<String, List<int []>>();
	public static Map<String, String> groupOperators = new HashMap<String, String>();
	public static Map<String, Integer > groupEquals = new HashMap<String, Integer >();
	public static String[][] groupArr;
	public static int[][] puzzleBoard;
	public static int puzzleSize = 0;
	public static int groupCount = 0;
	public static int choiceCounter = 0;
	
	public static boolean loadPuzzle(BufferedReader stream) throws IOException  {
		String thisLine = null;
		while ((thisLine = stream.readLine()) != null) {
            System.out.println(thisLine);
            puzzleInput.add(thisLine);
        } 
		String FirstLine = puzzleInput.get(0);
		int n = FirstLine.length();
		puzzleSize = FirstLine.length();
		groupCount = puzzleInput.size() - puzzleSize;
		groupArr = new String[n][n];
		puzzleBoard = new int[n][n];
		for (int i=0; i < n; i++) {
			for (int j=0; j < n; j++) {
				groupArr[i][j] = puzzleInput.get(i).split("")[j];
				puzzleBoard[i][j] = 0;
				List<int[]> groupCell = new ArrayList<int[]>();
				int[] cellLoc = {i,j};
				if (groupCells.containsKey(groupArr[i][j])) {
					groupCell= groupCells.get(groupArr[i][j]);
					groupCell.add(cellLoc);
				} else {
					groupCell.add(cellLoc);
					groupCells.put(groupArr[i][j], groupCell );
				}
			}
		}
		for (int i=n; i < puzzleInput.size(); i++) {
			String[] operator = puzzleInput.get(i).split(" ");
			if (operator.length == 3) {
				groupOperators.put(operator[0], operator[2]);
				groupEquals.put(operator[0], Integer.parseInt(operator[1]));
			}
		}
		System.out.println(groupCells);
		System.out.println(groupOperators);
		System.out.println(groupEquals);
		return false;
	}
		
	private static boolean rowValidation(int value, int rowNo, int colNo) {
		for (int i = 0;i < puzzleSize; i++) {
			if(puzzleBoard[rowNo][i] == value && colNo != i) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean colValidation(int value, int rowNo, int colNo) {
		for (int i = 0;i < puzzleSize; i++) {
			if(puzzleBoard[i][colNo] == value && rowNo != i) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean groupValidation(int value, int rowNo, int colNo) {
		String groupVar = groupArr[rowNo][colNo];
		String groupOp = groupOperators.get(groupVar);
		int groupResult = groupEquals.get(groupVar);
		List<int []> groupLocs = groupCells.get(groupVar);
		if(groupOp.equals("+")) {
			int total = value;
			for (int[] loc: groupLocs) {
				total += puzzleBoard[loc[0]][loc[1]];
			}
			if(total <= groupResult) {
				return true;
			}
		} else if(groupOp.equals("-")) {
			List<Integer> cellVals = new ArrayList<Integer>();
			for (int[] loc: groupLocs) {
				cellVals.add(puzzleBoard[loc[0]][loc[1]]);
			}
			int num1 = cellVals.get(0);
			int num2 = cellVals.get(1);
			if(num1==0) {
				if(num2 == 0) {
					return true;
				} else if(num2-value == groupResult) {
					return true;
				} else if(value-num2 == groupResult) {
					return true;
				}
			} else if(num1 - value == groupResult) {
				return true;
			} else if (value-num1 == groupResult) {
				return true;
			}
		} else if(groupOp.equals("*")) {
			int total = value;
			for (int[] loc: groupLocs) {
				total *= puzzleBoard[loc[0]][loc[1]];
			}
			if(total <= groupResult) {
				return true;
			}
			
		} else if(groupOp.equals("/")) {
			List<Integer> cellVals = new ArrayList<Integer>();
			for (int[] loc: groupLocs) {
				cellVals.add(puzzleBoard[loc[0]][loc[1]]);
			}
			int num1 = cellVals.get(0);
			int num2 = cellVals.get(1);
			if(num1==0) {
				if(num2 == 0) {
					return true;
				} else if(num2/value == groupResult) {
					return true;
				} else if(value/num2 == groupResult) {
					return true;
				}
			} else if(num1/value == groupResult) {
				return true;
			} else if (value/num1 == groupResult) {
				return true;
			}
		} else if(groupOp.equals("=")) {
			if(value == groupResult) {
				return true;
			}
		}
		
		return false;
	}
	
	private static int[] nextEmptyCell() {
		int[] emptyLoc = {0,0};
		for (int i=0; i < puzzleSize; i++) {
			for (int j=0; j < puzzleSize; j++) {
				if (puzzleBoard[i][j]==0) {
					emptyLoc[0]=i;
					emptyLoc[1]=j;
					return emptyLoc;
				}
			}
		}
		return null;
	}
	
	private static boolean valueValidation(int value, int rowNo, int colNo) {
		boolean rowCheck = rowValidation(value, rowNo, colNo);
		boolean colCheck = colValidation(value, rowNo, colNo);
	    boolean groupCheck = groupValidation(value, rowNo, colNo);
	    if(rowCheck && colCheck && groupCheck) {
	    	return true;
	    }
		return false;	
	}
	
	public static boolean solve() {
		int[] emptyCell = nextEmptyCell();
		if (emptyCell != null) {
			for (int i=1; i<=puzzleSize; i++) {
				if(valueValidation(i, emptyCell[0], emptyCell[1])) {
					puzzleBoard[emptyCell[0]][emptyCell[1]] = i;
					if (solve()) {
						return true;
					}
					puzzleBoard[emptyCell[0]][emptyCell[1]] = 0;
				}
			}
			choiceCounter += 1;
			return false;
		} else {
			return true;
		}
	}
	
	private static void printGrid() {
		System.out.println("**********************");
		for (int i=0; i < puzzleSize; i++) {
			for (int j=0; j < puzzleSize; j++) {
				System.out.print(puzzleBoard[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println("**********************");
	}
	
	public static String print() {
		String printPuzzle = "";
		for (int i=0; i < puzzleSize; i++) {
			for (int j=0; j < puzzleSize; j++) {
				if (puzzleBoard[i][j] == 0) {
					printPuzzle += groupArr[i][j];
				} else {
					printPuzzle += puzzleBoard[i][j];
				}
			}
			printPuzzle += "\n";
		}
		return printPuzzle;	
	}
	
	public static int choices() {
		return choiceCounter;
	}

	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream("/Users/prabhjotkaur/Documents/MathdocuFiles/testFile3.txt");
//		FileInputStream fis = new FileInputStream("/Users/prabhjotkaur/Documents/MathdocuFiles/mathdoku.txt");
		InputStreamReader r = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(r);
		loadPuzzle(null);
//		loadPuzzle(br);
		System.out.println("---------string-------");
		System.out.print(print());
		System.out.println("---------string-------");
		printGrid();
		solve();
		printGrid();
		System.out.println("---------string-------");
		System.out.print(print());
		System.out.println("choices: " + choices());
		System.out.println("---------string-------");
	}

}
