package representation;

import java.util.LinkedList;
import java.util.List;

import representation.DipoleMove.typeMove;

public class DipoleConf implements Conf {

	/*
	 * private long p1=0; private long p2=0; private long p3=0; private long p4=0;
	 * private long p5=0; private long p6=0; private long p7=0; private long p8=0;
	 * private long p9=0; private long p10=0; private long p11=0; private long
	 * p12=0;
	 */

	private long moves;

	private long frontAttack;
	private long backAttack;
	// private long merge;
	private long death;
	private long quietMove;

	private Status status;
	private long pBlack;
	private long pRed;
	private boolean BLACK;
	private long[] pieces = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	static long blackSquare = 0x55aa55aa55aa55aaL;

	// Configurazione inzio partita
	public DipoleConf(boolean BLACK) {

//		this.pieces[11] = 0x1000000000000008L;
//		this.pRed = 0x8;
//		this.pBlack = 0x1000000000000000L;
		this.pieces[11] = 0x20000810200400L;
		this.pRed = 0x0L;
		this.pBlack = 0x20000810200400L;
		this.BLACK=BLACK;

	}

	// ruota la bitboard di 180 gradi
	public long flip180(long x) {

		// flipping vertically
		long k1 = 0x00FF00FF00FF00FFL;
		long k2 = 0x0000FFFF0000FFFFL;
		x = ((x >>> 8) & k1) | ((x & k1) << 8);
		x = ((x >>> 16) & k2) | ((x & k2) << 16);
		x = (x >>> 32) | (x << 32);

		// mirroring horizontally
		long k3 = 0x5555555555555555L;
		long k4 = 0x3333333333333333L;
		long k5 = 0x0f0f0f0f0f0f0f0fL;
		x = ((x >>> 1) & k3) + 2 * (x & k3);
		x = ((x >>> 2) & k4) + 4 * (x & k4);
		x = ((x >>> 4) & k5) + 16 * (x & k5);

		return x;
	}

	private long rankMask(int sq) {
		return 0xffL << (sq & 56);
	}

	private long fileMask(int sq) {
		return 0x0101010101010101L << (sq & 7);
	}

	private long diagonalMask(int sq) {
		long maindia = 0x8040201008040201L;
		int diag = 8 * (sq & 7) - (sq & 56);
		int nort = -diag & (diag >> 31);
		int sout = diag & (-diag >> 31);
		return (maindia >>> sout) << nort;
	}

	private long antiDiagMask(int sq) {
		long maindia = 0x0102040810204080L;
		int diag = 56 - 8 * (sq & 7) - (sq & 56);
		int nort = -diag & (diag >> 31);
		int sout = diag & (-diag >> 31);
		return (maindia >>> sout) << nort;
	}

	private long getRose2(int sq) {
		return rankMask(sq) | fileMask(sq) | diagonalMask(sq) | antiDiagMask(sq);
	}

	////// la morte si pu� realizzare senza utilizzare una bitboard ma incrementando
	////// un contatore una volta che la linea arriva ai bordi.
	private long removeImpossibleMove(long rose, int sq, long opponent, long mine, int type, long[] pieces) {
		long notFree = opponent;
		long ovest = fileMask(sq);
		long est = ovest;
		long sud = rankMask(sq);
		long nord = sud;
		long tmp = 0;
		int cont = 0;
		while (cont < type) {
			ovest ^= ovest & Board.b_l;
			est ^= est & Board.b_r;
			sud ^= sud & Board.b_d;
			nord ^= nord & Board.b_u;
			ovest <<= 1;
			est >>>= 1;
			sud >>>= 8;
			nord <<= 8;
			notFree ^= pieces[cont] & (~mine);
			tmp |= notFree & ovest;
			tmp |= notFree & est;
			tmp |= notFree & sud;
			tmp |= notFree & nord;
			cont++;
		}
		while (cont < 8) {
			ovest ^= ovest & Board.b_l;
			est ^= est & Board.b_r;
			sud ^= sud & Board.b_d;
			nord ^= nord & Board.b_u;
			ovest <<= 1;
			est >>>= 1;
			sud >>>= 8;
			nord <<= 8;
			tmp |= rose & ovest;
			tmp |= rose & est;
			tmp |= rose & sud;
			tmp |= rose & nord;
			cont++;
		}
		return rose ^ (rose & tmp);
	}
	
	private long removeImpossibleMove2(long rose, int sq, long opponent, long mine, int type, long[] pieces) {
		long notFree = opponent;
		long ovest = fileMask(sq);
		long est = ovest;
		long sud = rankMask(sq);
		long nord = sud;
		long tmp = 0;
		int cont = 0;
		while (cont < type) {
			ovest ^= ovest & Board.b_l;
			est ^= est & Board.b_r;
			sud ^= sud & Board.b_d;
			nord ^= nord & Board.b_u;
			ovest <<= 1;
			est >>>= 1;
			sud >>>= 8;
			nord <<= 8;
			notFree ^= pieces[cont] & (~mine);
			tmp |= notFree & ovest;
			tmp |= notFree & est;
			tmp |= notFree & sud;
			tmp |= notFree & nord;
			cont++;
		}
		return rose ^ (rose & tmp);
	}

	private void allMoves(long x, long opponent, long mines, int type, long[] pieces) {
		int sq = getSquare(x);
		long rose = getRose2(sq);
		rose = removeImpossibleMove(rose & blackSquare ^ x, sq, opponent, mines, type, pieces);
		long backMask = x ^ (x - 1);
		backMask |= rankMask(sq);
		backMask = backMask & rose;
		backAttack = backMask & opponent;
		long frontMask = backMask ^ rose;
		frontAttack = frontMask & opponent;
		quietMove = frontMask ^ frontAttack;
		moves = backAttack | frontAttack | quietMove;
		// backAttack = getBackAttack(rose, pBlack, sq, x);
	}
	
	private void allMoves2(long x, long opponent, long mines, int type, long[] pieces, long[][] possibleMove) {
		if(type>6) type=6;		
		int sq = getSquare(x);
		//System.out.println(sq);
		long rose = possibleMove[type][sq];
		rose = removeImpossibleMove2(rose ^ x, sq, opponent, mines, type, pieces);
		long backMask = x ^ (x - 1);
		backMask |= rankMask(sq);
		backMask = backMask & rose;
		backAttack = backMask & opponent;
		long frontMask = backMask ^ rose;
		frontAttack = frontMask & opponent;
		quietMove = frontMask ^ frontAttack;
		moves = backAttack | frontAttack | quietMove;
		// backAttack = getBackAttack(rose, pBlack, sq, x);
	}
	
//	// precalcolo rosa dell'intera scacchiera
//	public long[][] precalculations() {
//		long rose[][]= new long [7][64];
//		long tmp;
//		long square = blackSquare;
//		/*
//		for (int i = 0; i < 7; i++) {
//			for (int j = 1; j < 64; j += 1) {
//				 tmp= getRose2(j);
//			}
//		}int 
//		*/
//		long lsb;
//		int sq;
//		long aaa[] = {0,0,0,0,0,0,0,0,0,0,0,0};
//		while(square!=0) {
//			lsb = square & -square;
//			square ^= lsb;
//			sq = getSquare(lsb);
//			tmp = getRose2(sq);
//			tmp &= blackSquare;
//			rose[6][sq] = tmp;
//			rose[5][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 6, aaa);
//			rose[4][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 5, aaa);
//			rose[3][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 4, aaa);
//			rose[2][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 3, aaa);
//			rose[1][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 2, aaa);
//			rose[0][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 1, aaa);	
//		}
//		System.out.println("{");
//		for(int i=0; i<7; i++) {
//			System.out.print("{");
//			for(int j=0; j<64; j++){
//				System.out.print(rose[i][j]+"L,");
//			}
//			System.out.print("}\n");
//		}
//		System.out.println("}");
//		
//		return rose;
//	}// precalculations

	
	
//	 // Ritorna la rosa di azione della pedina presa in considerazione private
//	 long getRose(long square, int type, long mine, long opponent) {
//	 
//	 return checkSquareMoves(square, -9, Board.b_r | Board.b_d, 1, mine, opponent,
//	 type) | checkSquareMoves(square, -16, Board.b2_d, 2, mine, opponent, type) |
//	 checkSquareMoves(square, -7, Board.b_l | Board.b_d, 1, mine, opponent, type)
//	 | checkSquareMoves(square, -2, Board.b2_r, 2, mine, opponent, type) |
//	 checkSquareMoves(square, 2, Board.b2_l, 2, mine, opponent, type) |
//	 checkSquareMoves(square, 7, Board.b_r | Board.b_u, 1, mine, opponent, type) |
//	 checkSquareMoves(square, 16, Board.b2_u, 2, mine, opponent, type) |
//	 checkSquareMoves(square, 9, Board.b_l | Board.b_u, 1, mine, opponent, type);
//	 }
//	 
//	 // Ritorna il movimento di una pedina in una sola direzione (ES
//	 N,NO,NE,S,SO...) private long checkSquareMoves(long square, int shift, long
//	 border, int addMove, long mine, long opponent, int type) { long notFreeSquare
//	 = opponent; long ret = 0; long tmp =0; int cont = addMove - 1; while ((square
//	 & border) == 0 && cont < type) { if (shift > 0) { square <<= shift; } else {
//	 square >>>= -shift; } notFreeSquare ^= pieces[cont] & (~mine); cont +=
//	 addMove; tmp =square^(square & notFreeSquare); ret |= tmp; } ret ^= (ret &
//	 notFreeSquare); return ret; }


	// ritorna il numero della casella in cui � posizionato il pedone
	public int getSquare(long position) {

		long b = position ^ (position - 1);
		int fold = (int) (b ^ (b >>> 32));
		return Board.BIT_TABLE[(fold * 0x783a9b23) >>> 26];
	}

	// Ritorna la posizione esatta del pedone sottoforma di stringa (ES A8) a
	// partire da una bitboard
	public String DeBruijn(long position) {

		return Board.SQUARE_NAMES[getSquare(position)];

	}

	// Conta il numero di 1 presenti all'interno di una bitboard
	public int popCount(long x) {
		int count = 0;
		while (x != 0) {
			count++;
			x &= x - 1; // reset LS1B
		}
		return count;
	}

	// SOMMATORIA DEI PEZZI PER IL PROPRIO VALORE VAL
	public int getMaterial() {
		int material = 0;

		if (pieces[0] != 0) {
			material += popCount(pieces[0]) * Board.VAL_ONE;
		}
		if (pieces[1] != 0) {
			material += popCount(pieces[1]) * Board.VAL_TW0;
		}
		if (pieces[2] != 0) {
			material += popCount(pieces[2]) * Board.VAL_THREE;
		}
		if (pieces[3] != 0) {
			material += popCount(pieces[3]) * Board.VAL_FOUR;
		}
		if (pieces[4] != 0) {
			material += popCount(pieces[4]) * Board.VAL_FIVE;
		}
		if (pieces[5] != 0) {
			material += popCount(pieces[5]) * Board.VAL_SIX;
		}
		if (pieces[6] != 0) {
			material += popCount(pieces[6]) * Board.VAL_SEVEN;
		}
		if (pieces[7] != 0) {
			material += popCount(pieces[7]) * Board.VAL_EIGHT;
		}
		if (pieces[8] != 0) {
			material += popCount(pieces[8]) * Board.VAL_NINE;
		}
		if (pieces[9] != 0) {
			material += popCount(pieces[9]) * Board.VAL_TEN;
		}
		if (pieces[10] != 0) {
			material += popCount(pieces[10]) * Board.VAL_ELEVEN;
		}
		if (pieces[11] != 0) {
			material += popCount(pieces[11]) * Board.VAL_TWELVE;
		}
		return material;
	}

	@Override
	public List<Move> getActions() {
		long pawn;
		long mines;
		List<Move> actions = new LinkedList<Move>();
		if (!BLACK) {
			mines = pRed;
			while (mines != 0) {
				pawn = mines & -mines;
				mines ^= pawn;
				int selectType = 0;
				while (selectType < 12) {
					if ((pawn & pieces[selectType]) != 0) {
						break;
					}
					selectType++;
				}
				allMoves2(pawn, pBlack, pRed, selectType, pieces, Board.movingBook);
				//allMoves(pawn, pBlack, pRed, selectType, pieces);
				long temp;
				while (backAttack != 0) {
					temp = backAttack & -backAttack;
					backAttack ^= temp;
					actions.add(new DipoleMove(pawn, temp, selectType, BLACK, typeMove.BACKATTACK));
				}
				while (frontAttack != 0) {
					temp = frontAttack & -frontAttack;
					frontAttack ^= temp;
					actions.add(new DipoleMove(pawn, temp, selectType, BLACK, typeMove.FRONTATTACK));
				}
				while (quietMove != 0) {
					temp = quietMove & -quietMove;
					quietMove ^= temp;
					actions.add(new DipoleMove(pawn, temp, selectType, BLACK, typeMove.QUIETMOVE));
				}
			}
			return actions;
		} else {
			long[] pieces180 = new long[12];
			long pBlack180;
			long pRed180;
			int cont = 0;
			while (cont < 12) {
				pieces180[cont] = flip180(pieces[cont]);
				cont++;
			}
			pBlack180 = flip180(pBlack);
			pRed180 = flip180(pRed);
			mines = pBlack;
			while (mines != 0) {
				pawn = mines & -mines;
				mines ^= pawn;
				int selectType = 0;
				while (selectType < 12) {
					if ((pawn & pieces[selectType]) != 0) {
						break;
					}
					selectType++;
				}
				allMoves2(pawn, pRed, pBlack, selectType, pieces, Board.movingBook);
				//allMoves(pawn, pRed, pBlack, selectType, pieces);
				backAttack = flip180(backAttack);
				frontAttack = flip180(frontAttack);
				quietMove = flip180(quietMove);
				pawn = flip180(pawn);
				long temp;
				while (backAttack != 0) {
					temp = backAttack & -backAttack;
					backAttack ^= temp;
					actions.add(new DipoleMove(pawn, temp, selectType, BLACK, typeMove.BACKATTACK));
				}
				while (frontAttack != 0) {
					temp = frontAttack & -frontAttack;
					frontAttack ^= temp;
					actions.add(new DipoleMove(pawn, temp, selectType, BLACK, typeMove.FRONTATTACK));
				}
				while (quietMove != 0) {
					temp = quietMove & -quietMove;
					quietMove ^= temp;
					actions.add(new DipoleMove(pawn, temp, selectType, BLACK, typeMove.QUIETMOVE));
				}
			}
			return actions;
		}
	}

	@Override
	public float heuristic() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Conf getParentState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float heuristic2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String identifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] getConf() {
		return pieces.clone();
	}

}
