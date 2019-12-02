package core;

import java.awt.print.PrinterException;
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

import javax.swing.JTextPane;

import representation.Conf.Status;

public class Main {

	private static HeuristicInterface hi;
	private static HeuristicInterface hi2;
	private static AlgorithmInterface ai_R;
	private static AlgorithmInterface ai_B;

	private static Conf state;
	private static Move move_R;
	private static Move move_B;

	public static Semaphore srSem = new Semaphore(0);
	public static Semaphore algSem = new Semaphore(0);
	public static boolean blackPlayer;

	public static void main(String[] args) throws InvalidActionException, CloneNotSupportedException, PrinterException {
		boolean server = true;
		LAVORAMU();

		// potrei dividere l'euristica in modo da evitare di splittare gli algoritmi.
		hi = new BBEvaluator();
		hi2 = new BBEvaluator2();

		ai_R = new ABAgent(hi, false, 30);
		ai_B = new ABAgent(hi2, true, 30);

		state = new DipoleConf();

//		localPlay();

		if (server) {
			startServer();
		} else {

			while (state.getStatus() == Status.Ongoing) {
				System.out.println("\n\n---------------------------------------------------------------");
				System.out.println(state);
				System.out.println("---------------------------------------------------------------\n\n");

				move_R = ai_R.compute(state);
				System.out.println("\n\n---------------------------------------------------------------");
				System.out.println(move_R);
				System.out.println("---------------------------------------------------------------\n\n");

				state = move_R.applyTo(state);
				System.out.println("\n\n---------------------------------------------------------------");
				System.out.println(state);
				System.out.println("---------------------------------------------------------------\n\n");

				move_B = ai_B.compute(state);
				System.out.println("\n\n---------------------------------------------------------------");
				System.out.println(move_B);
				System.out.println("---------------------------------------------------------------\n\n");

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

			System.out.println("FINITA\n");

			LAVORAMU();

		}

	}

	public static void localPlay() throws InvalidActionException, CloneNotSupportedException {
		ConverterMove cm = new ConverterMove();
		System.out.println("Inserisci il colore del giocatore scelto (RED/BLACK): ");
		Scanner scan = new Scanner(System.in);
		String player = scan.nextLine();
		if (player.equals("RED")) {
			blackPlayer = true;
		} else {
			blackPlayer = false;
		}
		while (true) {
			if (blackPlayer) {
				System.out.println(state.toString());
				System.out.println("Inserisci mossa (ES:  H5,N,2)");
				String mossa = scan.nextLine();
				move_B = cm.unpacking(mossa, state);
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

				System.out.println("Inserisci mossa (ES:  H5,N,2)");
				String mossa = scan.nextLine();

				move_R = cm.unpacking(mossa, state);
				state = move_R.applyTo(state);
			}
		}
	}

	public static void startServer() throws InvalidActionException, CloneNotSupportedException {
		// blackPlayer = false;
		SenderReceiver sr = new SenderReceiver();
		sr.start();
		ConverterMove cm = new ConverterMove();
		// int type = 11;
		while (true) {
			try {
				algSem.acquire();

				if (sr.getStatus().equals("OPPONENT_MOVE")) {
					if (Main.blackPlayer) {
						move_R = cm.unpacking(sr.getMove(), state);
						state = move_R.applyTo(state);
					} else {
						move_B = cm.unpacking(sr.getMove(), state);
						state = move_B.applyTo(state);
					}
				}

				if (sr.getStatus().equals("YOUR_TURN")) {
					if (Main.blackPlayer) {
						move_B = ai_B.compute(state);
						state = move_B.applyTo(state);
						System.out.println(cm.generatePacket(move_B));
						sr.setMove(cm.generatePacket(move_B));
					} else {
						move_R = ai_R.compute(state);
						state = move_R.applyTo(state);
						System.out.println(cm.generatePacket(move_R));
						sr.setMove(cm.generatePacket(move_R));
					}

				}

				srSem.release();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void LAVORAMU() throws PrinterException {
		Date date = new Date(2019 - 1900, 9, 18);
		Date now = new Date();
		// System.out.println(date.toString());
		// System.out.println(now.toString());
		long tiempu = now.getTime() - date.getTime();
		System.out.println("Giorni PERSI : " + tiempu / (1000 * 60 * 60 * 24));
		String guarda = ("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\r\n"
				+ "#################%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "(((((((((((((((###%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "##################%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "%%%%%%%%%%%%%%%%%%%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&&&&&%#((%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&@@@@@@@@@@@&%##%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&@@@@@@@@@@@@&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%%%&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&@&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&&&&%#(((////(///(((((((###%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%##(((((/////(((((((#((((((((((((######%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%##((((///////////((((((/////((//(((#####%%&&%######%&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%#((////(((#####(((((((((////(((#((###%%%%##%%&&&&%%%%###%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(///((((((((#(((///(((((((((((##%%#############%%%%%%%#####%%&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(////((((((###(((////(((((((((((#%%%###((((#%%%%%%%%%%%%#######%%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%%#(((((((((((####%#((((((##(###############((((##%%%%%&&&&%%%#######%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&%&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%#(//(###########%%&&&%#####%%%%%%%%%%%%##%%%%%#######%%%%%%%%&&&&%%%####%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&%%%%%%%%%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%#(((#%%###((((##%%&&&&%%%%%%&&&&&&&&&&&%%%%%%%%%#####%%%%%&&%%&&&&&&&&%%%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&%%%%###((#%%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%#(##%%##(((/((((#%%%%%%%%%%%&&&&&&&&&&&&&%%%%%%%%#####%%%%%&&&&&@@@@@@@@&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%%%##(//*//(#%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%#(####((////////(#####%%%%%%%%%%%%%%%%%%%%%%%%%%%######%%%&&&&@@@@@@@@@&@@&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%%#((/*****/(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%#(((((////////////((((((###############################%%%%%&&&&&@@@@@&&&&@&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%%#(//*****/(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%#(((((//////////////////((((((((((((((((((((((#########%%%%%%%&&&&&&&&@@@@&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%##((//////(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(((((//////////////////////////////////////((((((((((((###%%%&&&&&&&&&&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%#((//////(#%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#((((/////////////////////////////////////((((((////////((##%%%%%&&&&&&&&&&&&&@@@@&&&&&&@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%#((//////(%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%#((((/////////////////////((((((((/////////((///////////((###%%%&&&&&&&&&&&&&&&@@@@&&&&&&@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%#(((((((#%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%%#((/////////////////////((((((((((((///////////////////(((###%%%%&&&&&&&&&&&&&@@@@&@&&&&@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&%%#((###%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%#(///////////////////////(((((((((((////////////////////((#####%%&&&%%%&&&&@@@@@@&&@@@&&&@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%#((##%&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%#((////////////////////////((((((((((((//////////////////(((#####%%&&%%%%%&&&@@@@&&&@@@&&&@@@@@@@@@@@@@@@@@@\r\n"
				+ "@&%#(((((#%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%#(///////////////////////(((((((((((((((////////////////(((#######%%%%%%&&&&&@&&&&&@@@&&&&@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@&%#(//((#%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#((///////////////////////(((((((((((((((((//////////////(((########%%%%&&&&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@&&%((/((#%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(////////////////////////(((((((((((((((((((((/////////((((########%%&&&&&&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@&%%#####%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(/////////////////////////((((((((((((((((((((////((((((((#####%%##%%&&&&&&&&&&&@@&&&&&&&&@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@&&%%####%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%#/////////////////////////(((((((((////////(////((((((((((#####%%%%%%%%&&&&&&&&&&@@&&&&&&&&@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@&&&&%%%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%(////////////////////////////(((/////////////((((((((((((######%%%%%%%%&&&&&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%(/////////////////////////////(((/////////////(((((((((((#####%%%%%%%%%&&&&&&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@#(////////////////////////////(((((////////////(((((((((######%%%%%%%%%%%&&&&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@#######(((((////////////////////(((((///////////((((((((#######%%%%%%%%%%%&&&%%&&&&&@@@&&&&@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@###%%%%%%%##(((((((((((((((((((((((((((((((///////((((((((((###%%%%%%%%%%%%%%%%&&&&&&&&@&&&@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@#######%%%%%########((#(##################(((((////(((((((((((##%%%&&%%%%&&&&%&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(((((((##%%%%########%%%%&&&&&&&&&&&&%%%#####(((((((((((/////((#%%&&&&&&&&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&(((((((##%%%%%%#(///(##%&&&&&&&&%%%%%%%%##########((((((//////(((#%&&&&&&&&&&&&&&@@&&&&&&&@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%(((###%%&&&&&%%#/////(#%%&&&&%%%############%%%%####(((((//////((##&&&&&%&&&&&&&&&&@&&&&&@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%%%%%&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%//(((###%%%%##((/**//((#%%%%%%%%%%#######%%%%%%%%%###(((((((///(((#%%%%%%%%&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&%%&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#///////(((((((///*////(########%%%%%%%%%%%%%%%%%%%###(((((((((((((#%%%%%%%%%%&&%%%%%%%&&@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#*******///////////////(((((((######%%%%%%%%%%#######(((((((((((((##%%%%%####%%%%######%&&@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%(*************/////////////(((((((((((((((((((((((((((((((((#######%%%%#####%%%%&&%%&&&&&&@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%(/////*********////////////((((//(((((////////////(((((((#((########%%####%%%%%#####%&&&&@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%(//////*****/////////////(((((///////////////////((((((###################%%&&%##((#%%%&&@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(///////////////////////((((((////////(((((((((((((((########((((((((((((#%&&&&%####%%%&&@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(///////////*///////////(((((((///////((((((((((((########(((((((((((((((#%%&&&%%###%%&&@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(//////(((##((((###%%%##(((((##((((((((((((((############((((((((((((((#####%%%%###%%&&@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%(//(//((#######%%%&&&&&%#########(((((((((((#############(((########((######%%%%##%%&@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(((/((##(//(((#%%&&&&&&%%%######(((((((((##########################((((#####%%##%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%((((((#((////(((#######((((((###(((((##########################################%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&(((((((((//////((((((((((((((((((((((###############################%%%#####%%%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%(((((((///*////////(((((((((((((((((######################%%%###########(###%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(//((///(((((((((((((((####################%%%%%%%%%%%###%%##%%%%##%%%%%&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#((((/((#%&&&&&&%%#########################%%%%%%%%%%%%%#####%%&&%%%%&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%#(((((#%&&@@@@@@@&&&%####################%%%%%%%%%%%%%%%%###%%%&%%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%#(///(#&&%%%%&&@@@@&&%%################%%%%%%%%%%%%%%%%%%#####%##%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%((/((##((((#%%&&@@@@&&%%############%%%%%%%%%%%%%%%%%%%%#######%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%((/(/////((#%%&&&@@@@&&%############%%%%%%%%%%%%%&%%%%########%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%#(/////(((##%%&&&&&@@@&%%###########%%%%%%%%%%%%%%%%%%########%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#//////(((####%%%&&@@@&%############%%%%%%%%%%%%%%%%%%#######%%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%(//////(((#######&&@@&%############%%%%%%%%%%%%%%%%%%#########%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(//////(((####((((%&&&%#############%%%%%%%%%%%%%%%%%%##########%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#///////(((###((((((%&&%%##############%%%%%%%%%#####%%%#########((#&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(////((((((####((((((%%%###############%%%%%%%%%###%%%%%%########((/(#&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@#/////(((((#####((((((#%%################%%%%%%%%%%%%%%%%%#########(/*/(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%////(((((((#####(((((##%%%%%%%#####%#####%%%%%%%%%%%%%%%%%%%%%#####/***/#&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%(////((((###%###(((((##%%%%%%%%%###%%%##%%%%%%%%%%%%%%%%%%%%%%%####(****/(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#(////((((###%%#((((((##%%&&%%%%%%%%#%%%%%%%%%%%%%%%%&&&&%%%%%%%###(//****/(#&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#////(((((###%%#((((((##%&&&&&%%%%%%%%%%%%%%%%%%%%%%&&&&&&%%%%%##((//******/(#&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@&@@@@@@@@@@@&%#(((((#%%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&#/////((((####%%#((((((##%%%%%%%%%%&&&%%%%%%%%%%&&&&&&&&&&&&%%#((///*********/#%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@%############%@@@@@@@@@@@@@@@@@@@@@@@@@&%(/////(((#####%%##(((((###%%%%%%%%&&&&&&&%%&&&&&&&&&&&&&&&&%%#(/////*********/(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@&&%%%%%%####((%&@@@@@@@@@@@@@@@@@@@@@@%#//////((((####%%##((((####%%%&&&&&&&&&&&&&&&&&&&&@@&&&&&&&%##(//////*********/(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@&&&&@@@@@@@@@@@&&&%%%%###((##%&@@@@@@@@@@@@@@@@@@&#(//////(((((#######((((####%%%&&&&&&&&&&&&&&&&@@@@@&&&&&&%##((/////***********/(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@&&&&&@@@@@@@@@@@@@&&%%%####((##%&@@@@@@@@@@@@@&&&%#///////(((((########(((#####%%&&&&&&&&&&&@@@@&&&&&&&&&&&%##((///////**********/(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@&%%%&@@@@@@@@@@@@@@@&%###########%&&&&&&&&&&&&&%#(///(((((((#########(((((#######((((%%%%&&&&&&&&&&&&&%%%##((((//////////*******/#&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@&&%&&@@@@@@@@@@@@@@@@&%#######((##%%&&&&&&&&&%%#(//(((((((##########(((##########(/**/(#%%&&&&&&&&&%%%####(((//////////*******//(&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%#####(((###%%&&&&&&%%#(((//(((############################/****//(#%&&&%#########(((//////////*****////#%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@&&&&&%%####((((#####%%%%###(((((((((###################%%######(/*,**/((#####(((((###((((///////////*****//(#&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@&&&&&&&&&&&%%####(((((##########(((((((####################%%%%%####(/**(%&&&&&%%%%((//((((((///////////*****//(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@&&&&&&&&&&&&&&%####(((((((###################################%%%####(((((#&@@&&&&&&@@&#((/((((/////////////***//(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@&&@@@@@&&&&&&@@@@@@&%##((((((###################################%%%%%######%%%%@@@@@@@@@@@@@&%#(((//////////////*//(#&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@&&&@@@@@@@@&&%##(((####################################%%%%%%%%%%%%%%%%%&@@@@@@@@@@@@@&%((/////////////*//(#&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%##################################%%%%%%%%%%%%%%%%%%%%%%%#%&@@@@@@@@@@@@@&%#((/////////////(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%########%%%%#####%%##########%%%%%%%%%%%%%%%%%%%%%%%%%%%&@@@@@@@@@@@@@@&%##(((/////////(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%####%%%%%%%%%%%%%%#########%%%%%%%%%%%%%%%%%%%%%%%%%%&&@@@@@@@@@@@@@@@@&%#(((///////(%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&%%%%%%%%%%%%%%%%%%%%########%%%%%%%%%%%%%%%%%%%%%%%%%%&&@@@@@@@@@@@@@@@@@@&%#(((/////(%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&%%%%%%%%%%%%%%%%########%%%%%%%%%%%%%%%%%%%%%%%%%%&@@@@@@@@@@@@@@@@@@@@@@&%#(((((%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&%%%%%%%%%%%%%#########%%%%%%%%%%%%%%%%%%%%%%%%&&&&@@@@@@@@@@@@@@@@@@@@@@@&%#((#%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&%%%%%%%%%%%##########%%%%%%%%%%%%%%%%%%%%%%%%&&&&@@@@@@@@@@@@@@@@@@@@@@@@&%##%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&%%%%%%%%%%########%%%%%%%%%%%%%%%%%&&%%%%%%&&&&@@@@@@@@@@@@@@@&&&&@@@@@@&&%&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&%%%%%%%#####%%%%%%%%%%%%%%%%%%%&&%%%%%%%%&&&@@@@@@@@@@@@@@@&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%##%%%####%%%%%%%%%%%%%%%&&&%&&&&&&%&&&&&&@@@@@@@@@@@@@@@@&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%%%%%%%%%%%%%%%%%%%%%%%&&&&&%&&&&%%&&&%&&@@@@@@@@@@@@@@@&&&%%%%%&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%%%%%%%%%%%%%%%%%%%%%%%&%%%%%%%&&&&&%%%&@@@@@@@@@@@@@@@@&&%%%%%&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%&&&&&%%&&@@@@@@@@@@@@@@&&&%%%%%%&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%&&&&&&&&@@@@@@@@@@@@@@@@&&%%%%%&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%&&&&&&&&&@@@@@@@@@@@@@@@@@&&%%%%&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%%%%%%%%%%%%%%%%%%%%%%%%%%&&&&@@&&&&&@@@@@@@@@@@@@@@@@@&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%%%%%%%%%%&&&&%%%%%%%%%%&&&&&@@@@&&&&&@@@@@@@@@@@@@@@@@&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%%%%%%%&&&&&&&&&%&&%%%%&&&&&&@@@@@&&&&@@@@@@@@@@@@@@@@@@&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%%&&&&&&&&&&&&&&&&&&&&&&&&@@@@@@@@&&&&@@@@@@@@@@@@@@@@@&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%%%%&&&&&&&&&&&&&&&&&&&&&&&&@@@@@@@@@&&&@@@@@@@@@@@@@@@@@@&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&%%&&&&&&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&&&&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&%&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%%&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&%%%&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&&&&&&@@@@@@@&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
				+ "");

	}

}
