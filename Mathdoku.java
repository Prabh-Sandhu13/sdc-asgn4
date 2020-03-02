/*
Mathdoku
*/

/*---------------------------import statements------------------------------*/
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Mathdoku {
	
	/*---------------------------Global variables------------------------------*/
	public static final List<String> operators = Arrays.asList("+","-","*","/","=");
	public static Set<String> grpVarSet = new HashSet<String>(); 
	public static List<String> puzzleInput = new ArrayList<String>();
	public static Map<String, List<int []>> groupCells = new HashMap<String, List<int []>>();
	public static Map<String, String> groupOperators = new HashMap<String, String>();
	public static Map<String, Integer > groupEquals = new HashMap<String, Integer >();
	public static String[][] groupArr;
	public static int[][] puzzleBoard;
	public static int puzzleSize = 0;
	public static int groupCount = 0;
	public static int choiceCounter = 0;
	public static boolean isPuzzleReady = false;
	public static boolean isGridComplete = false;
	
	/*-----------------------------Public methods------------------------------*/
	public static boolean loadPuzzle(BufferedReader stream) {
		grpVarSet = new HashSet<String>(); 
		puzzleInput = new ArrayList<String>();
		groupCells = new HashMap<String, List<int []>>();
		groupOperators = new HashMap<String, String>();
		groupEquals = new HashMap<String, Integer >();
		puzzleSize = 0;
		groupCount = 0;
		choiceCounter = 0;
		isPuzzleReady = false;
		isGridComplete = false;
		if (stream == null) {
			return false;
		}
		try {
			String line = null;
			while ((line = stream.readLine()) != null) {
	            System.out.println(line);
	            puzzleInput.add(line);
	        } 
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			return false;
		}
		if (puzzleInput.size() != 0) {
			initializeDS();
			return true;
		}
		return false;
	}
	
	public static  boolean readyToSolve() {
		Set<String> opKeys = groupOperators.keySet();
		Set<String> eqKeys = groupEquals.keySet();
		Set<String> cellKeys = groupCells.keySet();
		if (opKeys.containsAll(grpVarSet) && eqKeys.containsAll(grpVarSet) && 
				cellKeys.containsAll(grpVarSet)) {
			boolean shouldContinue = true;
			for(String op: groupOperators.values()) {
				if (!operators.contains(op)) {
					shouldContinue = false;
					break;
				}
			}
			if(shouldContinue) {
				for (String grpVar: grpVarSet) {
					String op = groupOperators.get(grpVar);
					List<int []> cells = groupCells.get(grpVar);
					if(op.equals("=")) {
						if (cells.size() != 1) {
							shouldContinue = false;
							break;
						}
					} else if(op.equals("-") || op.equals("/")) {
						if (cells.size() != 2) {
							shouldContinue = false;
							break;
						}
					}
				}
				if (shouldContinue) {
					isPuzzleReady = true;
				}
			}			
		}
		return isPuzzleReady;
	}
	
	public static boolean solve() {
		if(isPuzzleReady) {
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
				choicesIncrement();
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public static String print() {
		String printPuzzle = "";
		if(isGridComplete) {
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
		}
		return printPuzzle;	
	}
	
	public static int choices() {
		return choiceCounter;
	}
	
	/*---------------------------Private methods-------------------------------*/
	private static void initializeDS() {
		String FirstLine = puzzleInput.get(0);
		int n = FirstLine.length();
		puzzleSize = FirstLine.length();
		if(puzzleInput.size() >= n) {
			isGridComplete = true;
			for (int k=0; k < n; k++) {
				if(puzzleInput.get(k).length() != n) {
					isGridComplete = false;
					break;
				}
			}
			if(isGridComplete) {
				groupCount = puzzleInput.size() - puzzleSize;
				groupArr = new String[n][n];
				puzzleBoard = new int[n][n];
				for (int i=0; i < n; i++) {
					for (int j=0; j < n; j++) {
						groupArr[i][j] = puzzleInput.get(i).split("")[j];
						puzzleBoard[i][j] = 0;
						grpVarSet.add(groupArr[i][j]);
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
						try {
							groupEquals.put(operator[0], Integer.parseInt(operator[1]));
						} catch(NumberFormatException e) {
							System.out.print(e.getMessage());
						}
					}
				}
				System.out.println(groupCells);
				System.out.println(groupOperators);
				System.out.println(groupEquals);
				System.out.println(grpVarSet);
			}
		} else {
			isPuzzleReady = false;
		}
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
	

	private static void choicesIncrement() {
		int emptyCells = 0;
		for (int i=0; i < puzzleSize; i++) {
			for (int j=0; j < puzzleSize; j++) {
				if (puzzleBoard[i][j] == 0) {
					emptyCells += 1;
				} 
			}
		}
		if (puzzleSize <= 4) {
			if (emptyCells <= puzzleSize*2) {
				choiceCounter += 1;
			}
		} else {
			if (emptyCells <= puzzleSize) {
				choiceCounter += 1;
			}
		}

	}

	public static void main(String[] args) throws IOException {
		System.out.println("print:"+ print());
		FileInputStream fis = new FileInputStream("/Users/prabhjotkaur/Documents/MathdocuFiles/empty.txt");
		FileInputStream fis2 = new FileInputStream("/Users/prabhjotkaur/Documents/MathdocuFiles/testFile2.txt");
		FileInputStream fis3 = new FileInputStream("/Users/prabhjotkaur/Documents/MathdocuFiles/mathdoku.txt");
		FileInputStream fis4 = new FileInputStream("/Users/prabhjotkaur/Documents/MathdocuFiles/badData.txt");
		InputStreamReader r = new InputStreamReader(fis3);
		BufferedReader br = new BufferedReader(r);
		System.out.println(loadPuzzle(null));
		System.out.println(loadPuzzle(br));
//		System.out.println(loadPuzzle(br));
		System.out.println("---------string-------");
		System.out.print(print());
		System.out.println("---------string-------");
		System.out.println("is Puzzle ready to solve:" + readyToSolve());
		solve();
		System.out.println("---------string-------");
		System.out.print(print());
		System.out.println("choices: " + choices());
		System.out.println("---------string-------");
		InputStreamReader r2 = new InputStreamReader(fis2);
		BufferedReader br2 = new BufferedReader(r2);
		System.out.println(loadPuzzle(br2));
		System.out.println("is Puzzle ready to solve:" + readyToSolve());
		solve();
		System.out.println("print:"+ print());
		System.out.println("-----------end------------");
		
	}

}
