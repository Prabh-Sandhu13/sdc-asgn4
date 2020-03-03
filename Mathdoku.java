/*
  Mathdoku: It is a public class used to solved a mathematics based puzzle knows as mathdoku.
  This puzzle has N*N puzzle where value of a cell is an integer in range of [1-N]. Same value
  cannot be repeated in the row and column of the cell. Each cell is part of a grouping which is
  formed to perform mathematical operation(+,-,*,/,=) and equivalent value of the operation is 
  given for each group. The complexity of the puzzle increases with the size of the grid.
*/

/*---------------------------import statements------------------------------*/
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Mathdoku {
	
	/*---------------------------Global variables------------------------------*/
	
	// size of the puzzle used to build puzzle board
	public static int puzzleSize = 0;

	// counter for number of choices made before reaching the final solution
	public static int choiceCounter = 0;
	
	// boolean to check is all constraints to solve puzzle are fulfilled
	public static boolean isPuzzleReady = false;
	
	// boolean to check if n*n values are available to form the grid
	public static boolean isGridComplete = false;
	
	// list of operators allowed for the puzzle
	public static final List<String> operators = Arrays.asList("+","-","*","/","=");
	
	// Set of strings used to represent the groupings
	public static Set<String> grpVarSet = new HashSet<String>();
	
	// list of strings used to read and store puzzle
	public static List<String> puzzleInput = new ArrayList<String>();
	
	// Map of grouping cells, key: group variable, value: location of cells of that grouping
	public static Map<String, List<int []>> groupCells = new HashMap<String, List<int []>>();
	
	// Map of grouping operators, key: group variable, value: operator for that group
	public static Map<String, String> groupOperators = new HashMap<String, String>();
	
	// Map of grouping results, key: group variable, value: result of operation for that group
	public static Map<String, Integer > groupEquals = new HashMap<String, Integer >();
	
	// 2D array to store grouping variable for each cell
	public static String[][] groupArr;
	
	// 2D array which is used to finally solve the puzzle
	public static int[][] puzzleBoard;
	
	/*-----------------------------Public methods------------------------------*/
	
	/*
	 loadPuzzle method:
	  * input: BufferedReader stream
	  * output: boolean
	  * functionality: This method is used to read the puzzle and store the 
	  * data in different objects which can be used through out the class
	  * by other methods.
	 */
	public static boolean loadPuzzle(BufferedReader stream) {
		
		// reset all the global variables every time load puzzle is called
		grpVarSet = new HashSet<String>(); 
		puzzleInput = new ArrayList<String>();
		groupCells = new HashMap<String, List<int []>>();
		groupOperators = new HashMap<String, String>();
		groupEquals = new HashMap<String, Integer >();
		puzzleSize = 0;
		choiceCounter = 0;
		isPuzzleReady = false;
		isGridComplete = false;
		
		// if stream is null return false
		if (stream == null) {
			return false;
		}
		
		// handle IOException while reading the input in stream
		try {
			String line = null;
			// read the input line by line and store it in puzzleInput
			while ((line = stream.readLine()) != null) {
	            puzzleInput.add(line);
	        } 
		} catch (IOException e) {
			// return false if exception is caught
			System.out.println(e.getStackTrace());
			return false;
		}
		// return false if file was empty or else call initializeDS and 
		// return true
		if (puzzleInput.size() != 0) {
			if(initializeDS()) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 readyToSolve method:
	  * output: boolean isPuzzleReady
	  * functionality: This method is used to check if all the constraints
	  * needed to solve the puzzle are met. If it returns true, solve can be 
	  * called to get the final solution.
	 */
	public static boolean readyToSolve() {
		
		// get the set of keys from groupOperators, groupEquals and groupCells
		Set<String> opKeys = groupOperators.keySet();
		Set<String> eqKeys = groupEquals.keySet();
		Set<String> cellKeys = groupCells.keySet();
		
		// all the key sets from above set should contain all grouping variables
		if (opKeys.containsAll(grpVarSet) && eqKeys.containsAll(grpVarSet) && 
				cellKeys.containsAll(grpVarSet)) {
			boolean shouldContinue = true;
			
			// check if all the operators are with in the defined set i.e. +,-,/,*,=
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
					// only one cell should be allocated to the "=" operator
					if(op.equals("=")) {
						if (cells.size() != 1) {
							shouldContinue = false;
							break;
						}
					} 
					// only two cells should be allocated to the "-" and "/" operator
					else if(op.equals("-") || op.equals("/")) {
						if (cells.size() != 2) {
							shouldContinue = false;
							break;
						}
					}
				}
				// if all the above conditions are true set isPuzzleReady to true
				if (shouldContinue) {
					isPuzzleReady = true;
				}
			}			
		}
		return isPuzzleReady && isGridComplete;
	}
	
	/*
	 solve method:
	  * output: boolean
	  * functionality: This method is used to solve the puzzle. Returns true if 
	  * puzzle is solved and returns false otherwise.
	 */
	public static boolean solve() {
		
		// check if puzzle is ready to be solved
		if(isPuzzleReady && isGridComplete) {
			
			// get the location of next empty cell
			int[] emptyCell = nextEmptyCell();
			
			// if there is no empty cell that means puzzle is solve 
			// return true in that case or else iterate over all 
			// possible values [1-N] for the empty cell
			if (emptyCell != null) {
				for (int i=1; i<=puzzleSize; i++) {
					// call value validation for each cell value if it returns true
					// use recursion to solve the next cells and backtrack if it is
					// false and set cell value to 0
					if(valueValidation(i, emptyCell[0], emptyCell[1])) {
						puzzleBoard[emptyCell[0]][emptyCell[1]] = i;
						if (solve()) {
							return true;
						}
						puzzleBoard[emptyCell[0]][emptyCell[1]] = 0;
					}
				}
				// in case the value for variable fails call choicesIncrement
				// and return false
				choicesIncrement();
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	/*
	 print method:
	  * output: String printPuzzle
	  * functionality: This method is used to print the current state of the 
	  * puzzle. It returns grouping variable for the cell that is not solved 
	  * yet.
	 */
	public static String print() {
		
		// initialize printPuzzle to empty string
		String printPuzzle = "";
		
		// check if grid is complete and iterate over the puzzle board
		if(isGridComplete) {
			for (int i=0; i < puzzleSize; i++) {
				for (int j=0; j < puzzleSize; j++) {
					// if cell value is zero add grouping variable for that
					if (puzzleBoard[i][j] == 0) {
						printPuzzle += groupArr[i][j];
					} else {
						printPuzzle += puzzleBoard[i][j];
					}
				}
				// add new line at the end of each row and return printPuzzle
				printPuzzle += "\n";
			}
		}
		return printPuzzle;	
	}
	
	/*
	 choices method:
	  * output: int choiceCounter
	  * functionality: This method is returns the number of wrong choices
	  * made before returning the output. 
	 */
	public static int choices() {
		// its just the wrapper method, computation of choices is done 
		// in choicesIncrement
		return choiceCounter;
	}
	
	/*---------------------------Private methods-------------------------------*/
	
	/*
	 initializeDS method:
	  * functionality: This method is called by load puzzle if file has some data.
	  * This method checks if data loaded is valid to form puzzle grouping and 
	  * initializes all the global variables
	 */
	private static boolean initializeDS() {
		// get the number of characters in first row of the puzzleInput
		String FirstLine = puzzleInput.get(0);
		int n = FirstLine.length();
		puzzleSize = FirstLine.length();
		// if number of rows in puzzleInput is greater than n and then iterate 
		// over next n rows and check if they also have n characters and set 
		// isGridComplete to true
		if(puzzleInput.size() >= n) {
			isGridComplete = true;
			for (int k=0; k < n; k++) {
				if(puzzleInput.get(k).length() != n) {
					isGridComplete = false;
					break;
				}
			}
			// if n rows have n elements then update the groupArr with 
			// grouping variables and initialize puzzleBoard to 0.
			if(isGridComplete) {
				groupArr = new String[n][n];
				puzzleBoard = new int[n][n];
				// use nested for loops to update values of groupArr,groupCells 
				// puzzleBoard and grpVarSet
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
				
				// iterate over the rest of the rows to create map of
				// groupOperators and groupEquals
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
			}
		} else {
			// if number of rows in puzzleInput are less than set isPuzzleReady to false
			isPuzzleReady = false;
		}
		return isGridComplete;
	}
	
	/*
	 rowValidation method:
	  * input: int value, int rowNo, int colNo
	  * output: boolean
	  * functionality: This method gets the prospective value and location
	  * for the empty cell and returns true if the value is not already 
	  * in the row of the cell, otherwise returns false
	 */
	private static boolean rowValidation(int value, int rowNo, int colNo) {
		// iterate over the row and check if value already exists
		for (int i = 0;i < puzzleSize; i++) {
			if(puzzleBoard[rowNo][i] == value && colNo != i) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 colValidation method:
	  * input: int value, int rowNo, int colNo
	  * output: boolean
	  * functionality: This method gets the prospective value and location
	  * for the empty cell and returns true if the value is not already 
	  * in the column of the cell, otherwise returns false
	 */
	private static boolean colValidation(int value, int rowNo, int colNo) {
		// iterate over the column and check if value already exists
		for (int i = 0;i < puzzleSize; i++) {
			if(puzzleBoard[i][colNo] == value && rowNo != i) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 groupValidation method:
	  * input: int value, int rowNo, int colNo
	  * output: boolean
	  * functionality: This method gets the prospective value and location
	  * for the empty cell and applies rules for the grouping of the cell
	  * if it satisfies the rule method returns true or returns false otherwise
	 */
	private static boolean groupValidation(int value, int rowNo, int colNo) {
		
		// get group variable, operator and result for the given cell
		String groupVar = groupArr[rowNo][colNo];
		String groupOp = groupOperators.get(groupVar);
		int groupResult = groupEquals.get(groupVar);
		List<int []> groupLocs = groupCells.get(groupVar);
		
		// if the operator is +, check if the value after adding to other 
		// cell location doesn't exceed the result
		if(groupOp.equals("+")) {
			int total = value;
			for (int[] loc: groupLocs) {
				total += puzzleBoard[loc[0]][loc[1]];
			}
			if(total <= groupResult) {
				return true;
			}
		} 
		// if the operator is -, returns true if both cells are empty or
		// checks if value - other cell or vice versa is equal to the result
		else if(groupOp.equals("-")) {
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
		} 
		// if the operator is *, check if the value after multiplying 
		// to other cell location doesn't exceed the result 
		else if(groupOp.equals("*")) {
			int total = value;
			for (int[] loc: groupLocs) {
				total *= puzzleBoard[loc[0]][loc[1]];
			}
			if(total <= groupResult) {
				return true;
			}
			
		}
		// if the operator is /, returns true if both cells are empty or
		// checks if value / other cell or vice versa is equal to the result
		else if(groupOp.equals("/")) {
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
		} 
		// if the operator is =, returns true if value = result
		else if(groupOp.equals("=")) {
			if(value == groupResult) {
				return true;
			}
		}
		
		// returns false if either one of the above conditions is not met
		return false;
	}
		
	/*
	 valueValidation method:
	  * input: int value, int rowNo, int colNo
	  * output: boolean
	  * functionality: This method is used to validate the given value and 
	  * its cell location
	 */
	private static boolean valueValidation(int value, int rowNo, int colNo) {
		
		// it calls rowValidation, colValidation, groupValidation and 
		// returns true if each function validates the new value
		boolean rowCheck = rowValidation(value, rowNo, colNo);
		boolean colCheck = colValidation(value, rowNo, colNo);
	    boolean groupCheck = groupValidation(value, rowNo, colNo);
	    if(rowCheck && colCheck && groupCheck) {
	    	return true;
	    }
		return false;	
	}
	
	/*
	 nextEmptyCell method:
	  * output: int[] emptyLoc
	  * functionality: This method is used to find the next empty
	  * cell in the puzzle board.
	 */
	private static int[] nextEmptyCell() {
		int[] emptyLoc = {0,0};
		
		// it iterates over all values in puzzle board and returns
		// the location of cell which has value = 0 
		for (int i=0; i < puzzleSize; i++) {
			for (int j=0; j < puzzleSize; j++) {
				if (puzzleBoard[i][j]==0) {
					emptyLoc[0]=i;
					emptyLoc[1]=j;
					return emptyLoc;
				}
			}
		}
		// returns null if there is no empty cell or puzzle is solved
		return null;
	}
	
	/*
	 choicesIncrement method:
	  * functionality: This method is used to update the choiceCounter
	  * for the number of choices being made before reaching the final
	  * solution for the puzzle.
	 */
	private static void choicesIncrement() {
		
		// find the empty cells in the puzzle when the choice 
		// was dropped
		int emptyCells = 0;
		for (int i=0; i < puzzleSize; i++) {
			for (int j=0; j < puzzleSize; j++) {
				if (puzzleBoard[i][j] == 0) {
					emptyCells += 1;
				} 
			}
		}
		
		// if puzzle is less than 4*4 and half of the cells 
		// were empty we increment choiceCounter
		if (puzzleSize <= 4) {
			if (emptyCells <= puzzleSize*2) {
				choiceCounter += 1;
			}
		} 
		// if puzzle is more than 4*4 and n cells 
		// were empty we increment choiceCounter
		else {
			if (emptyCells <= puzzleSize) {
				choiceCounter += 1;
			}
		}

	}

}
