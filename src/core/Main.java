package core;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import algorithms.*;
import heuristics.*;
import representation.DipoleConf;
import representation.DipoleMove;
import representation.Move;
<<<<<<< HEAD
import representation.DipoleMove.typeMove;
=======
import representation.*;

>>>>>>> branch 'master' of https://github.com/Lory999555/AI-Project

public class Main {

	public static void main(String[] args) {
		/**
		 * // long a = 263172;
		 * 
		 * // System.out.println(a); // long b = flipVertical(a); //
		 * System.out.println(b); long aa = 4; long[] ciccio = { aa }; //
		 * System.out.println(aa); // System.out.println(ciccio[0]); // aa=7;
		 * 
		 * // long now=System.currentTimeMillis(); long now = new Date().getTime();
		 * 
		 * for (int i = 0; i < 100000000; i++) { ciccio[0] ^= 1; } //
		 * System.out.println(ciccio[0]);
		 * 
		 * // long after= System.currentTimeMillis()-now; long after = new
		 * Date().getTime() - now; System.out.println(after);
		 **/
<<<<<<< HEAD
		long now = System.nanoTime();
=======
		//LAVORAMU();

		long now = System.currentTimeMillis();
>>>>>>> branch 'master' of https://github.com/Lory999555/AI-Project
		DipoleConf prova = new DipoleConf(true);
<<<<<<< HEAD
		DipoleMove move = new DipoleMove();
//		List<Move> mosse = prova.getActions();
		List<Integer> mosse = prova.getActions2(move);
		for (int i=0;i< mosse.size();i++) {
			System.out.println(mosse.get(i).toString());
		}
		
		System.out.println(System.nanoTime()-now);
=======
		//System.out.println(prova);
		List<Move> mosse = prova.getActions();
		//System.out.println(mosse.size());
//		for (int i=0;i< mosse.size();i++) {
//			System.out.println(mosse.get(i).toString());
//		}
//		System.out.println(System.currentTimeMillis()-now);
>>>>>>> branch 'master' of https://github.com/Lory999555/AI-Project
		LAVORAMU();
		
		HeuristicInterface hi = new BBEvaluator();
		
		AlgorithmInterface ai = new MTDFAgent(hi);
		
		Conf root = new DipoleConf(false);
		
		Move choise = ai.compute(root);
		System.out.println(choise);
		
	}

	public static long flipVertical(long x) {
		long k1 = 0x00FF00FF00FF00FFL;
		long k2 = 0x0000FFFF0000FFFFL;
		x = ((x >>> 8) & k1) | ((x & k1) << 8);
		x = ((x >>> 16) & k2) | ((x & k2) << 16);
		x = (x >>> 32) | (x << 32);
		return x;
	}

	public static void LAVORAMU() {
		Date date = new Date(2019 - 1900, 9, 18);
		Date now = new Date();
		//System.out.println(date.toString());
		//System.out.println(now.toString());
		long tiempu = now.getTime() - date.getTime();
		System.out.println("Giorni PERSI : " + tiempu/(1000*60*60*24) +"\n https://gfycat.com/reasonabledismalkinglet-stinson-barney!!!");
	}
	
	
}
