import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BrickPopSolver {

	public static void main(String[] args) {

		int[][] brickboard = new int[10][10];

		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(args[0]));
		} catch (IOException e) {
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("ERROR: Must run with command-line argument");
			System.exit(0);
		}

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				int xcoord = (j * 64) + 30;
				int ycoord = (i * 64) + 313;
				int color = image.getRGB(xcoord, ycoord);

				if (color == -37518) {
					brickboard[i][j] = 1;
				}
				if (color == -15814244) {
					brickboard[i][j] = 2;
				}
				if (color == -11561230) {
					brickboard[i][j] = 3;
				}
				if (color == -150986) {
					brickboard[i][j] = 4;
				}
				if (color == -5017618) {
					brickboard[i][j] = 5;
				}
				if (color == -6846348) {
					brickboard[i][j] = 6;
				}
			}
		}
		System.out.println("BOARD TO SOLVE:");
		printBoard(brickboard);
		printBoard(findShortest(10000, brickboard));
	}

	public static int[][] applyGravity(int[][] brickboard) {
		for (int i = 8; i >= 0; i--) {
			for (int j = 0; j < 10; j++) {
				if (brickboard[i][j] != 0 && findDropPoint(i, j, brickboard) > i) {

					brickboard[findDropPoint(i, j, brickboard)][j] = brickboard[i][j];
					brickboard[i][j] = 0;
				}
			}
		}
		return brickboard;
	}

	public static int[][] applyLeftCollapse(int[][] brickboard) {
		while (findGap(brickboard) != -1) {
			for (int i = 0; i < 10; i++) {
				for (int j = findGap(brickboard) + 1; j < 10; j++) {
					brickboard[i][j - 1] = brickboard[i][j];
					brickboard[i][j] = 0;
				}
			}
		}
		return brickboard;

	}

	public static int findDropPoint(int dropPoint, int j, int[][] brickboard) {
		while (dropPoint < 9 && brickboard[dropPoint + 1][j] == 0) {
			dropPoint++;
		}
		return dropPoint;
	}

	public static int findGap(int[][] brickboard) {
		for (int i = 0; i < farthestBlock(brickboard); i++) {
			if (brickboard[9][i] == 0) {
				return i;
			}
		}
		return -1;

	}

	public static int farthestBlock(int[][] brickboard) {
		int farthestblock = -1;
		for (int i = 0; i < 10; i++) {
			if (brickboard[9][i] != 0) {
				farthestblock = i;
			}
		}
		return farthestblock;

	}

	public static int[][] getGroupings(int[][] brickboardactual) {
		int[][] brickboard = new int[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				brickboard[i][j] = brickboardactual[i][j];
			}
		}
		int[][] tempgroupings = new int[2][50];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 50; j++) {
				tempgroupings[i][j] = -1;
			}
		}

		int counter = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (brickboard[i][j] != -1 && brickboard[i][j] != 0) {
					int type = brickboard[i][j];
					brickboard = findGrouping(1, i, j, brickboard);
					if (brickboard[i][j] == -1) {
						tempgroupings[0][counter] = i;
						tempgroupings[1][counter] = j;
						counter++;

					} else {
						brickboard[i][j] = type;
					}
				}
			}
		}

		if (counter == 0) {
			return null;
		}
		int[][] groupings = new int[2][counter];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < counter; j++) {
				groupings[i][j] = tempgroupings[i][j];
			}
		}
		return groupings;
	}

	public static int[][] findGrouping(int size, int i, int j, int[][] brickboard) {
		int type = brickboard[i][j];
		brickboard[i][j] = -1;
		if (i > 0 && brickboard[i - 1][j] == type) {
			size++;
			brickboard = findGrouping(size, i - 1, j, brickboard);
		}
		if (j < 9 && brickboard[i][j + 1] == type) {
			size++;
			brickboard = findGrouping(size, i, j + 1, brickboard);

		}
		if (i < 9 && brickboard[i + 1][j] == type) {
			size++;
			brickboard = findGrouping(size, i + 1, j, brickboard);
		}
		if (j > 0 && brickboard[i][j - 1] == type) {
			size++;
			brickboard = findGrouping(size, i, j - 1, brickboard);
		}
		if (size == 1) {
			brickboard[i][j] = type;
		}
		return brickboard;
	}

	public static boolean checkSolved(int[][] brickboard) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (brickboard[i][j] != 0)
					return false;
			}
		}
		return true;
	}

	public static int[][] solve(int[][] brickboard) {
		int[][] tempmoves = new int[2][50];
		int counter = 0;
		while (getGroupings(brickboard) != null) {
			int movechoice = (int) Math.floor(Math.random() * (getGroupings(brickboard)[0].length));
			tempmoves[0][counter] = getGroupings(brickboard)[0][movechoice];
			tempmoves[1][counter] = getGroupings(brickboard)[1][movechoice];
			brickboard = findGrouping(1, tempmoves[0][counter], tempmoves[1][counter], brickboard);
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					if (brickboard[i][j] == -1) {
						brickboard[i][j] = 0;
					}
				}
			}
			applyGravity(brickboard);
			applyLeftCollapse(brickboard);
			counter++;
		}
		if (checkSolved(brickboard)) {
			int[][] moves = new int[2][counter];
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < counter; j++) {
					moves[i][j] = tempmoves[i][j];
				}
			}
			return moves;
		} else {
			return null;

		}

	}

	public static void printBoard(int[][] brickboard) {
		for (int i = 0; i < brickboard.length; i++) {
			for (int j = 0; j < brickboard[0].length; j++) {
				System.out.print(brickboard[i][j] + " ");
			}
			System.out.println("");
		}
	}

	public static int[][] findSolution(int[][] brickboardactual) {
		int[][] brickboard = new int[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				brickboard[i][j] = brickboardactual[i][j];
			}
		}
		int[][] solution = solve(brickboard);
		if (checkSolved(brickboard)) {
			return solution;
		} else {
			return findSolution(brickboardactual);
		}
	}

	public static int[][] findShortest(int iterations, int[][] brickboard) {
		int length = 50;
		int[][] solution = null;
		int counter = 0;
		System.out.println("0%...");
		while (counter <= iterations) {
			
			if (counter % 100 == 0) {
		
			System.out.println((double) counter / (double) iterations * 100.0 + "%");
			}

			int[][] tempsolution = findSolution(brickboard);
			if (tempsolution[0].length < length) {
				solution = tempsolution;
				length = tempsolution[0].length;
			}
			counter++;
		}
		System.out.println("--------");
		System.out.println("COMPLETE");
		System.out.println("--------");
		System.out.println("(" + length + " moves)");
		return solution;
	}
}
