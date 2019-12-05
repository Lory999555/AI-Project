package heuristics;

import java.util.LinkedList;

import representation.Board;
import representation.Conf;
import representation.Conf.Status;
import representation.DipoleConf;

public class BBEvaluator implements HeuristicInterface {
	private int val[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }; // valore delle pedine
//	private double valPositionR[] = { 2, 1.75, 1.50, 1.25, 1, 0.6, 0.4, 0.2 }; // valore della posizione in base alla riga
//	private double valPositionB[] = { 0.2, 0.4, 0.6, 1, 1.25, 1.50, 1.75, 2 };

//	private double valPositionR[] = { 2, 2, 4, 5, 4, 2, 1, 0 }; // valore della posizione in base alla riga
//	private double valPositionB[] = { 0, 1, 2, 4, 5, 4, 2, 2 };

	private double valPositionR[] = { 2, 2, 3, 3.5, 2.5, 1.5, 1, 0 }; // valore della posizione in base alla riga
	private double valPositionB[] = { 0, 1, 1.5, 2.5, 3.5, 3, 2, 2 };

	private int mobilityB;
	private int backAttackB;
	private int frontAttackB;
	private int mobilityR;
	private int backAttackR;
	private int frontAttackR;
	private long pRed;
	private long pBlack;
	private double mat; /////// si pu� eliminare
	// sarebbe utile sapere che pedina � sotto attacco
	//
//	public int evaluate_R(Conf c) {
//		DipoleConf dc = (DipoleConf) c;
//		pRed = dc.getpRed();
//		pBlack = dc.getpBlack();
//		numberMovesBlack(dc);
//		numberMovesRed(dc);
//		
//		
//		double eval = (materialR(dc) + mobilityR + 1.5 * frontAttackR + 2 * backAttackR)
//				- (materialB(dc) + mobilityB + 1.5 * frontAttackB + 2 * backAttackB);
//		return (int) Math.round(eval);
//	}
//	
//	public int evaluate_B(Conf c) {
//		DipoleConf dc = (DipoleConf) c;
//		pRed = dc.getpRed();
//		pBlack = dc.getpBlack();
//		numberMovesBlack(dc);
//		numberMovesRed(dc);
//		
//		
//		double eval = - (materialR(dc) + mobilityR + 1.5 * frontAttackR + 2 * backAttackR)
//				+ (materialB(dc) + mobilityB + 1.5 * frontAttackB + 2 * backAttackB);
//		return (int) Math.round(eval);
//	}

	public int evaluate_R(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		pRed = dc.getpRed();
		pBlack = dc.getpBlack();
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval;
		if (c.isBlack()) {
			eval = (materialR(dc) + mobilityR + 1.5 * frontAttackR + 2 * backAttackR)
					- (materialB(dc) + mobilityB + frontAttackB + 1.2 * backAttackB);
		} else {
			eval = (materialR(dc) + mobilityR + frontAttackR + 1.2 * backAttackR)
					- (materialB(dc) + mobilityB + 1.5 * frontAttackB + 2 * backAttackB);
		}
//		System.out.println(
//				"mat=" + mat + " evalR_____mobR=" + mobilityR + " fronR= " + frontAttackR + " backR= " + backAttackR
//						+ "\n" + "mobB=" + mobilityB + " fronB= " + frontAttackB + " backB= " + backAttackB + "\n");
//		System.out.println(c.toString());
		return (int) Math.round(eval);
	}

	public int evaluate_B(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		pRed = dc.getpRed();
		pBlack = dc.getpBlack();
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval;
		if (c.isBlack()) {
			eval = (materialB(dc) + mobilityB + 1.5 * frontAttackB + 2 * backAttackB)
					- (materialR(dc) + mobilityR + frontAttackR + 1.2 * backAttackR);
		} else {
			eval = (materialB(dc) + mobilityB + frontAttackB + 1.2 * backAttackB)
					- (materialR(dc) + mobilityR + 1.5 * frontAttackR + 2 * backAttackR);
		}
//		System.out.println(
//				"mat=" + mat + " evalB_____mobR=" + mobilityR + " fronR= " + frontAttackR + " backR= " + backAttackR
//						+ "\n" + "mobB=" + mobilityB + " fronB= " + frontAttackB + " backB= " + backAttackB + "\n");
//		System.out.println(c.toString());
		return (int) Math.round(eval);
	}

	public double evaluateMob(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval = mobilityR - mobilityB;
		return eval;
	}

	public double evaluateMat(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval = materialR(dc) - materialB(dc);
		return eval;
	}

	public double evaluateAtt(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval = frontAttackR + 2 * backAttackR - frontAttackB - 2 * backAttackB;
		return eval;
	}

	/**
	 * @param DipoleConf
	 * @return number of mobility (quiet move), number of backAttack and number of
	 *         frontAttack
	 */
//	private void numberMovesRed(DipoleConf c) {
//		long pB = pBlack;
//		long pR = pRed;
//		mobilityR = 0;
//		backAttackR = 0;
//		frontAttackR = 0;
//		long pawn;
//		while (pR != 0) {
//			pawn = pR & -pR;
//			pR ^= pawn;
//			c.allMoves2(pawn, pB, pR, c.getType(pawn), c.getPieces(), Board.movingBook);
//			mobilityR += c.popCount(c.getQuietMove() | c.getMerge());
//			backAttackR += c.popCount(c.getBackAttack());
//			frontAttackR += c.popCount(c.getFrontAttack());
//		}
//	}
//
//	/**
//	 * @param DipoleConf
//	 * @return number of mobility (quiet move), number of backAttack and number of
//	 *         frontAttack
//	 */
//	private int numberMovesBlack(DipoleConf c) {
//		long pB = Board.flip180(pBlack);
//		long pR = Board.flip180(pRed);
//		mobilityB = 0;
//		backAttackB = 0;
//		frontAttackB = 0;
//		long pawn;
//		while (pB != 0) {
//			pawn = pB & -pB;
//			pB ^= pawn;
//			c.allMoves2(pawn, pR, pB, c.getType180(pawn), c.getPieces180(), Board.movingBook);
//			mobilityB += c.popCount(c.getQuietMove() | c.getMerge());
//			backAttackB += c.popCount(c.getBackAttack());
//			frontAttackB += c.popCount(c.getFrontAttack());
//		}
//		return 0;
//	}

	private void numberMovesRed(DipoleConf c) {
		long pB = pBlack;
		long pR = pRed;
		mobilityR = 0;
		backAttackR = 0;
		frontAttackR = 0;
		long pawn;
		while (pR != 0) {
			pawn = pR & -pR;
			pR ^= pawn;
			c.allMoves2(pawn, pB, pR, c.getType(pawn), c.getPieces(), Board.movingBook);
			mobilityR += c.popCount(c.getQuietMove() | c.getMerge());
			frontAttackR += c.evalFA();
			backAttackR += c.evalBA();
		}
	}

	/**
	 * @param DipoleConf
	 * @return number of mobility (quiet move), number of backAttack and number of
	 *         frontAttack
	 */
	private void numberMovesBlack(DipoleConf c) {
		long pB = Board.flip180(pBlack);
		long pR = Board.flip180(pRed);
		mobilityB = 0;
		backAttackB = 0;
		frontAttackB = 0;
		long pawn;
		while (pB != 0) {
			pawn = pB & -pB;
			pB ^= pawn;
			c.allMoves2(pawn, pR, pB, c.getType180(pawn), c.getPieces180(), Board.movingBook);
			mobilityB += c.popCount(c.getQuietMove() | c.getMerge());
			c.setBackAttack(Board.flip180(c.getBackAttack()));
			c.setFrontAttack(Board.flip180(c.getFrontAttack()));
			frontAttackB += c.evalFA();
			backAttackB += c.evalBA();
		}
	}

	/**
	 * @param DipoleConf
	 * @return returns the value of the pieces in relation to their position
	 */
	private double materialB(DipoleConf c) { // N.B. se l'euristica rimane cos�, posso richiamare il numberMove
		// direttamente nel while presente in questa classe
		double material = 0;
		long pieces = pBlack;
		long pawn;
		int square;
		int type;
		while (pieces != 0) {
			pawn = pieces & -pieces;
			pieces ^= pawn;
			square = Board.getSquare(pawn);
			type = c.getType(pawn);
			assert (type < 12);
			material += (val[type] * valPositionB[square >>> 3]);
		} // while
		mat = material;//////// si pu� eliminare
		return material;
	}

	private double materialR(DipoleConf c) { // N.B. se l'euristica rimane cos�, posso richiamare il numberMove
		// direttamente nel while presente in questa classe
		double material = 0;
		long pieces = pRed;
		long pawn;
		int square;
		int type;
		while (pieces != 0) {
			pawn = pieces & -pieces;
			pieces ^= pawn;
			square = Board.getSquare(pawn);
			type = c.getType(pawn);
			assert (type < 12);
			material += (val[type] * valPositionR[square >>> 3]);
		} // while
		mat = material; ///// si pu� eliminae
		return material;
	}
	
	public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }

}