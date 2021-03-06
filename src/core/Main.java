package core;

import java.awt.print.PrinterException;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import algorithms.*;
import heuristics.*;
import representation.*;
import converter.ConverterMove;
import converter.SenderReceiver;
import heuristics.*;
import representation.*;
import representation.DipoleMove.typeMove;

import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;

import representation.Conf.Status;

public class Main {

	private static HeuristicInterface hi;
	private static HeuristicInterface hi2;
	private static HeuristicInterface hi3;
	private static HeuristicInterface hi4;
	private static HeuristicInterface hi5;

	private static AlgorithmInterface ai_R;
	private static AlgorithmInterface ai_B;

	private static AlgorithmInterfaceEnc aienc_R;
	private static AlgorithmInterfaceEnc aienc_B;

	private static Conf state;
	private static Move move_R;
	private static Move move_B;

	private static int movenc_R;
	private static int movenc_B;
	private static Move dp;

	public static Semaphore srSem = new Semaphore(0);
	public static Semaphore algSem = new Semaphore(0);
	public static boolean blackPlayer;

	public static void main(String[] args) throws InvalidActionException, CloneNotSupportedException, PrinterException {
		System.out.println("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
		boolean server = false;
		boolean enc = false;

		PrintStream fileOut;

		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
		}
		int port = Integer.parseInt(args[1]);

		// serve per creare un log e non perdere nessuna info.
		// il file deve gi� esistere senno probabilmente da errore!
//		try {
//			fileOut = new PrintStream("C:/Users/loren/Desktop/out.txt");
//			System.setOut(fileOut);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// potrei dividere l'euristica in modo da evitare di splittare gli algoritmi.
		hi = new BBEvaluator();
		hi2 = new BBEvaluator2();
		hi3 = new BBEvaluator3();
		hi4 = new BBEvaluator4();
		BBEvaluator4NewVal hi4N = new BBEvaluator4NewVal();
		hi5 = new BBEvaluator5_4();
		BBEvaluator6 hi6 = new BBEvaluator6();
		BBEvaluator5N hi5N = new BBEvaluator5N();

		// true perch� � maximazer
		ai_R = new ABVisit(hi5, false, 5, 15);

		ai_B = new ABVisit(hi5, true, 5, 15);

		state = new DipoleConf();
//		System.out.println(state);
		long time;
		localPlay();

		if (server) {
			if (enc)
				startServerEnc(ip, port);
			else
				startServer(ip, port);
		} else {

			while (state.getStatus() == Status.Ongoing) {
				System.out.println("\n\n---------------------------------------------------------------");
				System.out.println(state);
				System.out.println("---------------------------------------------------------------\n\n");

				time = System.currentTimeMillis();
				move_R = ai_R.compute(state);
				System.out.println(System.currentTimeMillis() - time);

				System.out.println("\n\n-----------------------------RED----------------------------------");
				System.out.println(move_R);
				System.out.println("----------------------------------RED-----------------------------\n\n");

				state = move_R.applyTo(state);
				System.out.println("\n\n---------------------------------------------------------------");
				System.out.println(state);
				System.out.println("---------------------------------------------------------------\n\n");

				time = System.currentTimeMillis();
				move_B = ai_B.compute(state);
				System.out.println(System.currentTimeMillis() - time);

				System.out.println("\n\n---------------------------------BLACK------------------------------");
				System.out.println(move_B);
				System.out.println("---------------------------------------BLACK------------------------\n\n");

				state = move_B.applyTo(state);

			}

			/**
			 * avviare il server (abbiamo un ogetto converter e si fa c.start) root
			 * istanzionio algoritmo ed euristica while(nextstate.getStatus() != vittorie)
			 * attesa sul semaforo //per capire che � il mio turno(o la mossa) if(� il mio
			 * turno) ai.compute che mi rid� la mossa c1.convert(mossa) //devono essere due
			 * entit� diverse SR2.setmossa(stringa) //1 � proprio un covnertitore l'altro
			 * SR(sender receiver) else(devo codificare la mossa dell'avversario)
			 * SR2.getpacket //che mi rid� la stringa nextMovev= c1.decoder(string) // che
			 * mi ritorna la mossa next state = nextMove.applyto(root)
			 * 
			 * 
			 */

		}

	}

	public static void startServer(InetAddress ip, int port) throws InvalidActionException, CloneNotSupportedException {
		// blackPlayer = false;
		SenderReceiver sr = new SenderReceiver(ip, port);
		sr.start();
		ConverterMove cm = new ConverterMove();
		// int type = 11;
		while (true) {
			try {
				algSem.acquire();

				if (sr.getStatus().equals("MESSAGE All players connected")) {
					if (Main.blackPlayer) {
						System.out.println();
						ai_B.warmUp(15000);
					} else {
						ai_R.warmUp(15000);
					}
				} else if (sr.getStatus().equals("OPPONENT_MOVE")) {
					if (Main.blackPlayer) {
						move_R = cm.unpacking(sr.getMove(), state);
						state = move_R.applyTo(state);
					} else {
						move_B = cm.unpacking(sr.getMove(), state);
						state = move_B.applyTo(state);
					}
				}

				else if (sr.getStatus().equals("YOUR_TURN")) {
					if (Main.blackPlayer) {
						move_B = ai_B.compute(state);
						state = move_B.applyTo(state);
						System.out.println(cm.generatePacket(move_B));
						System.out.println(ai_B.getClass());
						sr.setMove(cm.generatePacket(move_B));
					} else {
						move_R = ai_R.compute(state);
						state = move_R.applyTo(state);
						System.out.println(cm.generatePacket(move_R));
						System.out.println(ai_R.getClass());
						sr.setMove(cm.generatePacket(move_R));
					}

				} else if (sr.getStatus().equals("DEFEAT")) {
					System.out.println("RIP!");
					break;
				} else if (sr.getStatus().equals("VICTORY")) {
					System.out.println("SIUUUUUUUUUUUU");
					break;
				}

				srSem.release();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void startServerEnc(InetAddress ip, int port)
			throws InvalidActionException, CloneNotSupportedException {
		// blackPlayer = false;
		dp = new DipoleMove();
		SenderReceiver sr = new SenderReceiver(ip, port);
		sr.start();
		ConverterMove cm = new ConverterMove();
		// int type = 11;
		while (true) {
			try {
				algSem.acquire();

				if (sr.getStatus().equals("MESSAGE All players connected")) {
					if (Main.blackPlayer) {
						ai_B.warmUp(15000);
					} else {
						ai_R.warmUp(15000);
					}
				}

				else if (sr.getStatus().equals("OPPONENT_MOVE")) {
					if (Main.blackPlayer) {
						movenc_R = cm.unpackingEnc(sr.getMove(), state);
						state = dp.applyToEnc(state, movenc_R);
					} else {
						movenc_B = cm.unpackingEnc(sr.getMove(), state);
						state = dp.applyToEnc(state, movenc_B);
					}
				}

				else if (sr.getStatus().equals("YOUR_TURN")) {
					if (Main.blackPlayer) {
						movenc_B = aienc_B.compute(state);
						state = dp.applyToEnc(state, movenc_B);
						System.out.println(cm.generatePacketEnc(movenc_B));
						System.out.println(aienc_B.getClass());
						sr.setMove(cm.generatePacketEnc(movenc_B));
					} else {
						movenc_R = aienc_R.compute(state);
						state = dp.applyToEnc(state, movenc_R);
						System.out.println(cm.generatePacketEnc(movenc_R));
						System.out.println(aienc_R.getClass());
						sr.setMove(cm.generatePacketEnc(movenc_R));
					}

				} else if (sr.getStatus().equals("DEFEAT")) {
					System.out.println("RIP!");
					break;
				} else if (sr.getStatus().equals("VICTORY")) {
					System.out.println("SIUUUUUUUUUUUU");
					break;
				}

				srSem.release();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void localPlay() throws InvalidActionException, CloneNotSupportedException {
		ConverterMove cm = new ConverterMove();
		System.out.println("Inserisci il colore del giocatore scelto (RED/BLACK): ");
		Scanner scan = new Scanner(System.in);
		String player = scan.nextLine();
		Pattern p = Pattern.compile("[a-hA-H][1-8][,][a-h][1-8]");
		Pattern c = Pattern.compile("[Z][,][a-hA-H][1-8][-][1-9]");
		if (player.equals("RED") || player.equals("red") || player.equals("R") || player.equals("r")) {
			blackPlayer = true;
		} else {
			blackPlayer = false;
		}
		while (true) {
			if (blackPlayer) {
				System.out.println(state.toString());
				String mossa = "a";
				Matcher m = p.matcher(mossa);
				Matcher s = c.matcher(mossa);
				while (!m.matches() && !s.matches()) {
					System.out.println("Inserisci mossa (ES:  H5,N,2)");
					mossa = scan.nextLine();
					m = p.matcher(mossa);
					s = c.matcher(mossa);
					System.out.println(s.matches());
				}
				move_B = cm.unpackingLocal(mossa, state);
				System.out.println(move_B.toString());
				state = move_B.applyTo(state);
				System.out.println(state.toString());
				move_B = ai_B.compute(state);
				state = move_B.applyTo(state);
				System.out.println(cm.generatePacket(move_B));
			} else {
				System.out.println(state.toString());
				move_R = ai_R.compute(state);
				state = move_R.applyTo(state);
				System.out.println(cm.generatePacket(move_R));
				System.out.println(state.toString());
				String mossa = "a";
				Matcher s = c.matcher(mossa);
				Matcher m = p.matcher(mossa);
				while (!m.matches() && !s.matches()) {
					System.out.println("Inserisci mossa (ES:  H5,N,2)");
					mossa = scan.nextLine();
					m = p.matcher(mossa);
					s = c.matcher(mossa);
				}
				move_R = cm.unpackingLocal(mossa, state);
				state = move_R.applyTo(state);
			}
		}
	}
}