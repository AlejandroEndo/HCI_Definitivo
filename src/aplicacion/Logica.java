package aplicacion;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import serializables.Mensaje;

public class Logica implements Observer {

	private PImage compa;
	private PImage cops;

	private Conexion con;
	private Interfaz interfaz;
	private Ladron ladron;

	private PApplet app;
	private int turno = 1;

	private int s = 30;

	private int dineroPropio = 500;
	private int dineroOtro = 500;

	private boolean enviarIndicacion = false;
	private boolean enviado = false;
	private boolean recibido = false;

	private int[][] matrix = new int[8][8];
	private int[][] position = new int[8][8];

	// Mapa conocido por 1 con la unicacion de 2
	private int[][] matrixA = { { 0, 0, 5, 0, 5, 0, 1, 4 }, { 5, 2, 2, 1, 2, 1, 2, 0 }, { 0, 1, 0, 0, 1, 0, 2, 1 },
			{ 5, 2, 2, 1, 2, 0, 1, 0 }, { 0, 0, 1, 0, 2, 1, 2, 1 }, { 1, 1, 0, 0, 1, 0, 0, 0 },
			{ 1, 0, 1, 1, 2, 1, 2, 1 }, { 3, 1, 1, 0, 5, 0, 5, 0 } };

	// Mapa conocido por 2 con la unicacion de 1
	private int[][] matrixB = { { 4, 0, 0, 5, 0, 0, 5, 0 }, { 1, 1, 0, 1, 0, 0, 1, 0 }, { 0, 2, 1, 2, 1, 1, 2, 5 },
			{ 1, 2, 0, 1, 0, 0, 1, 0 }, { 0, 1, 0, 2, 1, 2, 2, 5 }, { 1, 2, 0, 1, 0, 1, 0, 0 },
			{ 0, 2, 1, 2, 1, 2, 0, 1 }, { 0, 5, 0, 5, 0, 1, 3, 1 } };

	private int[][] matrixAimg = { { 15, 15, 13, 15, 13, 15, 3, 14 }, { 12, 8, 6, 1, 10, 1, 9, 15 },
			{ 15, 0, 15, 15, 0, 15, 7, 14 }, { 12, 6, 8, 1, 9, 15, 0, 15 }, { 15, 15, 11, 15, 7, 1, 6, 14 },
			{ 3, 14, 15, 15, 0, 15, 15, 15 }, { 0, 15, 3, 1, 10, 1, 8, 14 }, { 2, 1, 5, 15, 11, 15, 11, 15 } };

	private int[][] matrixBimg = { { 4, 15, 15, 13, 15, 15, 13, 15 }, { 2, 4, 15, 0, 15, 15, 0, 15 },
			{ 15, 7, 1, 10, 1, 1, 10, 14 }, { 12, 9, 15, 0, 15, 15, 0, 15 }, { 15, 0, 15, 7, 1, 8, 6, 14 },
			{ 12, 9, 15, 0, 15, 0, 15, 15 }, { 15, 7, 1, 10, 1, 9, 15, 13 }, { 15, 11, 15, 11, 15, 2, 1, 5 } };

	// para saber que le voya a enviar a el jugador diferente a mi y no
	// escucharme a mi mismo.
	// el primer parametro de todos los objetos tipo mensaje, debe ser enviarA
	private int enviarA;
	private int x, y;
	private String indicacion = null;

	// esta bloquea a el otro en caso de que ya haya jugado y su compa�ero no
	private String textoInterfaz;

	public Logica(PApplet app) {
		this.app = app;
		con = Conexion.getInstance();
		con.addObserver(this);
		ladron = new Ladron(app, 0, 0);
		textoInterfaz = " ";
		interfaz = new Interfaz(app);

		compa = app.loadImage("../data/compa.png");
		cops = app.loadImage("../data/cops.png");

		// dependiendo de el id, se inicia algo diferente
		switch (con.getId()) {
		case 1:
			// variables que se necesitan inicializar solo para el 1
			enviarA = 2;
			interfaz.setLaberintator(matrixBimg[7][6]);
			break;
		case 2:
			// variables que se necesitan inicializar solo para el 2
			enviarA = 1;
			interfaz.setLaberintator(matrixAimg[7][0]);
			// try {
			// con.enviar("empezar");
			// } catch (IOException e) {
			// System.out.println("no envio :(");
			// }
			break;
		}

	}

	public void draw() {
		interfaz.draw();

		if (app.mouseX > 432 && app.mouseX < 769 && app.mouseY > 572 && app.mouseY < 632) {
			interfaz.setOver(true);
		} else {
			interfaz.setOver(false);
		}

		if (interfaz.getPantalla() >= 2) {

			matrix();
			// Texto turnos--------------------------------
			app.textAlign(PConstants.CENTER, PConstants.CENTER);
			app.textSize(28);
			app.fill(43, 39, 95);
			app.text(turno + "/10", 1140, 60);

			// Texto Dinero-------------------------------------
			app.fill(255, 217, 46);
			app.textAlign(PConstants.LEFT);
			app.text("Tú: " + dineroPropio, 325, 200);
			app.text("Él: " + dineroOtro, 325, 250);
			app.text("Total: " + (dineroPropio + dineroOtro), 325, 300);

			// Texto Bonito x3
			app.textAlign(PConstants.RIGHT, PConstants.CENTER);
			app.fill(255);
			app.text(textoInterfaz, 366, 381);

			ladron.draw();

			if (enviado && recibido) {
				textoInterfaz = "Tu aliado indica:";
			} else {
				app.fill(0, 50);
				app.rect(500, 0, 700, 700);
				textoInterfaz = "Indica a tu aliado";
				app.textAlign(PConstants.CENTER, PConstants.CENTER);
				app.fill(255);
				app.text("ESPERANDO\nINDICACI�N...", 850, app.height / 2);
			}
		}

		if (indicacion == null) {
			interfaz.setInstructator(4);
		} else {
			switch (indicacion) {
			case "arriba":
				interfaz.setInstructator(0);
				break;

			case "derecha":
				interfaz.setInstructator(1);
				break;

			case "abajo":
				interfaz.setInstructator(2);
				break;

			case "izquierda":
				interfaz.setInstructator(3);
				break;
			}
		}

	}

	private void toFinish() {
		// vitoria
		if (matrixA[0][7] == 3 || matrixB[0][0] == 3)
			interfaz.setPantalla(99);

		// Errota A
		if (matrixA[0][2] == 3 || matrixA[0][4] == 3 || matrixA[1][0] == 3 || matrixA[3][0] == 3 || matrixA[7][4] == 3
				|| matrixA[7][6] == 3) {

		}

		// Errota B
		if (matrixB[0][3] == 3 || matrixB[0][6] == 3 || matrixB[2][7] == 3 || matrixB[4][7] == 3 || matrixB[7][1] == 3
				|| matrixB[7][3] == 3) {

		}
	}

	public void click() {
		// System.out.println(app.mouseX + " " + app.mouseY);

		if (PApplet.dist(1060, 600, app.mouseX, app.mouseY) < 60 && interfaz.getPantalla() == 1) {
			interfaz.setPantalla(interfaz.getPantalla() + 1);
			enviarIndicacion = true;
			enviado = false;
			recibido = false;
		}

		if (app.mouseX > 432 && app.mouseX < 769 && app.mouseY > 572 && app.mouseY < 632
				&& interfaz.getPantalla() == 0) {
			try {
				con.enviar("empezar");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (enviarIndicacion) {
			try {
				// Arriba
				if (PApplet.dist(250, 450, app.mouseX, app.mouseY) < 25) {
					con.enviar(new Mensaje(enviarA, "arriba"));
					enviado = true;
				}
				// Derecha
				if (PApplet.dist(325, 525, app.mouseX, app.mouseY) < 25) {
					con.enviar(new Mensaje(enviarA, "derecha"));
					enviado = true;
				}
				// Abajo
				if (PApplet.dist(250, 600, app.mouseX, app.mouseY) < 25) {
					con.enviar(new Mensaje(enviarA, "abajo"));
					enviado = true;
				}
				// Izquierda
				if (PApplet.dist(175, 525, app.mouseX, app.mouseY) < 25) {
					con.enviar(new Mensaje(enviarA, "izquierda"));
					enviado = true;
				}
				// Espera
				if (PApplet.dist(250, 525, app.mouseX, app.mouseY) < 25) {
					con.enviar(new Mensaje(enviarA, "espera"));
					enviado = true;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (enviado && recibido) {
			// UP
			if (app.mouseX > 725 && app.mouseX < 960 && app.mouseY > 0 && app.mouseY < 245) {
				try {
					movMatrix(0);
					enviado = false;
					recibido = false;
					turno++;
					interfaz.setConfianzaTotal(interfaz.getConfianzaTotal() + 50);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// RIGHT
			if (app.mouseX > 960 && app.mouseX < 1200 && app.mouseY > 245 && app.mouseY < 475) {
				try {
					movMatrix(1);
					enviado = false;
					recibido = false;
					turno++;
					interfaz.setConfianzaTotal(interfaz.getConfianzaTotal() + 50);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// DOWN
			if (app.mouseX > 725 && app.mouseX < 960 && app.mouseY > 475 && app.mouseY < 700) {
				try {
					movMatrix(2);
					enviado = false;
					recibido = false;
					turno++;
					interfaz.setConfianzaTotal(interfaz.getConfianzaTotal() + 50);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// LEFT
			if (app.mouseX > 500 && app.mouseX < 725 && app.mouseY > 245 && app.mouseY < 475) {
				try {
					movMatrix(3);
					enviado = false;
					recibido = false;
					turno++;
					interfaz.setConfianzaTotal(interfaz.getConfianzaTotal() + 50);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void matrix() {
		// Se decide que matriz se va a pintar respecto a su ID
		if (con.getId() == 1) {
			matrix = matrixA;
			position = matrixB;
			// matrixDir = matrixADir;
		} else if (con.getId() == 2) {
			matrix = matrixB;
			position = matrixA;
			// matrixDir = matrixBDir;
		}

		// For para pintar el laberinto en miniatura
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {

				switch (matrix[j][i]) {
				case 0:
					app.fill(1);
					app.noFill();
					break;

				case 1:
					app.fill(0, 182, 110);
					break;

				case 2:
					app.fill(0, 182, 110);
					break;

				case 3:
					app.fill(0, 182, 110);
					break;

				case 4:
					app.fill(255, 0, 255);
					break;

				case 5:
					app.fill(0, 182, 110);
					break;
				}

				app.rect(i * s + 70, j * s + 70, s, s);

				if (matrix[j][i] == 3) {
					app.imageMode(PConstants.CORNER);
					app.image(compa, i * s + 70, j * s + 70);
					app.imageMode(PConstants.CENTER);
				}
				if (matrix[j][i] == 5) {
					app.imageMode(PConstants.CORNER);
					app.image(cops, i * s + 70, j * s + 70);
					app.imageMode(PConstants.CENTER);
				}
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof String) {
			if (arg.equals("empezar")) {
				interfaz.setPantalla(1);
			}

			if (arg.equals("confiado")) {
				// interfaz.setConfianza(interfaz.getConfianza() + 50);
			}
		}

		if (arg instanceof Mensaje) {
			if (((Mensaje) arg).getId() == con.getId()) {
				if (((Mensaje) arg).getTipo().equals("indicacion")) {
					// que hace si le envia un mensaje de solo indicacion
					indicacion = ((Mensaje) arg).getDireccion();
					recibido = true;
				}

				if (((Mensaje) arg).getTipo().equals("posicion")) {
					// que hace si le envia un mensaje de solo posicion

					x = ((Mensaje) arg).getX();
					y = ((Mensaje) arg).getY();
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							if (matrix[j][i] == 3) {
								matrix[j][i] = 1;
								matrix[y][x] = 3;
							}
						}
					}
				}
			}
		}

	}

	public void movMatrix(int dir) throws IOException {
		// For para movimiento dentro de la matriz
		// Salto de punto a punto
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				if (position[j][i] == 3) {
					if (con.getId() == 2) {
						switch (dir) {
						case 0: // UP

							if (indicacion.contains("arriba")) {
								interfaz.setConfianza(interfaz.getConfianza() + 50);
								con.enviar("confiado");
							}

							// Nivel 7 -----------------
							if (j == 7 && i == 0) {
								position[5][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 5));
								interfaz.setLaberintator(matrixAimg[5][1]);
								return;
							}

							// Nivel 6 -----------------
							if (j == 6 && i == 4) {
								position[4][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 4));
								interfaz.setLaberintator(matrixAimg[4][4]);
								return;
							}

							// Nivel 4 -----------------
							if (j == 4 && i == 2) {
								position[3][2] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 2, 3));
								interfaz.setLaberintator(matrixAimg[3][2]);
								return;
							}
							if (j == 4 && i == 4) {
								position[3][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 3));
								interfaz.setLaberintator(matrixAimg[3][4]);
								return;
							}
							if (j == 4 && i == 6) {
								position[2][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 2));
								interfaz.setLaberintator(matrixAimg[2][6]);
								return;
							}

							// Nivel 3 -----------------
							if (j == 3 && i == 1) {
								position[1][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 1));
								interfaz.setLaberintator(matrixAimg[1][1]);
								return;
							}
							if (j == 3 && i == 4) {
								position[1][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 1));
								interfaz.setLaberintator(matrixAimg[1][4]);
								return;
							}

							// Nivel 2 -----------------
							if (j == 2 && i == 6) {
								position[1][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 1));
								interfaz.setLaberintator(matrixAimg[1][6]);
								return;
							}

							// Nivel 1 -----------------
							if (j == 1 && i == 2) {
								position[0][2] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 2, 0));
								interfaz.setLaberintator(matrixAimg[0][2]);
								return;
							}
							if (j == 1 && i == 4) {
								position[0][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 0));
								interfaz.setLaberintator(matrixAimg[0][4]);
								return;
							}
							if (j == 1 && i == 6) {
								position[0][7] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 7, 0));
								interfaz.setLaberintator(matrixAimg[0][7]);
								return;
							}
							break;

						case 1: // RIGHT

							if (indicacion.contains("derecha")) {
								interfaz.setConfianza(interfaz.getConfianza() + 50);
								con.enviar("confiado");
							}

							// Nivel 7 -------------
							if (j == 7 && i == 0) {
								position[6][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 6));
								interfaz.setLaberintator(matrixAimg[6][4]);
								return;
							}

							// Nivel 6 --------------
							if (j == 6 && i == 6) {
								position[6][7] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 7, 6));
								interfaz.setLaberintator(matrixAimg[6][7]);
								return;
							}
							if (j == 6 && i == 4) {
								position[6][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 6));
								interfaz.setLaberintator(matrixAimg[6][6]);
								return;
							}

							// Nivel 4 --------------
							if (j == 4 && i == 4) {
								position[4][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 4));
								interfaz.setLaberintator(matrixAimg[4][6]);
								return;
							}
							if (j == 4 && i == 6) {
								position[4][7] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 7, 4));
								interfaz.setLaberintator(matrixAimg[4][7]);
								return;
							}

							// Nivel 3 ---------------
							if (j == 3 && i == 1) {
								position[3][2] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 2, 3));
								interfaz.setLaberintator(matrixAimg[3][2]);
								return;
							}
							if (j == 3 && i == 2) {
								position[3][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 3));
								interfaz.setLaberintator(matrixAimg[3][4]);
								return;
							}

							// Nivel 2 ----------------
							if (j == 2 && i == 6) {
								position[2][7] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 7, 2));
								interfaz.setLaberintator(matrixAimg[2][7]);
								return;
							}

							// Nivel 1 -----------------
							if (j == 1 && i == 1) {
								position[1][2] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 2, 1));
								interfaz.setLaberintator(matrixAimg[1][2]);
								return;
							}
							if (j == 1 && i == 2) {
								position[1][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 1));
								interfaz.setLaberintator(matrixAimg[1][4]);
								return;
							}
							if (j == 1 && i == 4) {
								position[1][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 1));
								interfaz.setLaberintator(matrixAimg[1][6]);
								return;
							}
							break;

						case 2: // DOWN

							if (indicacion.contains("abajo")) {
								interfaz.setConfianza(interfaz.getConfianza() + 50);
								con.enviar("confiado");
							}

							// Nivel 6 ------------------
							if (j == 6 && i == 4) {
								position[7][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 7));
								interfaz.setLaberintator(matrixAimg[7][4]);
								return;
							}
							if (j == 6 && i == 6) {
								position[7][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 7));
								interfaz.setLaberintator(matrixAimg[7][6]);
								return;
							}

							// Nivel 4 -------------------
							if (j == 4 && i == 4) {
								position[6][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 6));
								interfaz.setLaberintator(matrixAimg[6][4]);
								return;
							}

							// Nivel 3 --------------------
							if (j == 3 && i == 2) {
								position[4][2] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 2, 4));
								interfaz.setLaberintator(matrixAimg[4][2]);
								return;
							}
							if (j == 3 && i == 4) {
								position[4][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 4));
								interfaz.setLaberintator(matrixAimg[4][4]);
								return;
							}

							// Nivel 2 ----------------------
							if (j == 2 && i == 6) {
								position[4][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 4));
								interfaz.setLaberintator(matrixAimg[4][6]);
								return;
							}

							// Nivel 1 ---------------------
							if (j == 1 && i == 1) {
								position[3][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 3));
								interfaz.setLaberintator(matrixAimg[3][1]);
								return;
							}
							if (j == 1 && i == 4) {
								position[3][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 3));
								interfaz.setLaberintator(matrixAimg[3][4]);
								return;
							}
							if (j == 1 && i == 6) {
								position[2][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 2));
								interfaz.setLaberintator(matrixAimg[2][6]);
								return;
							}
							break;

						case 3: // LEFT

							if (indicacion.contains("izquierda")) {
								interfaz.setConfianza(interfaz.getConfianza() + 50);
								con.enviar("confiado");
							}

							// Nivel 6 --------------------
							if (j == 6 && i == 4) {
								position[7][0] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 0, 7));
								interfaz.setLaberintator(matrixAimg[7][0]);
								return;
							}
							if (j == 6 && i == 6) {
								position[6][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 6));
								interfaz.setLaberintator(matrixAimg[6][4]);
								return;
							}
							if (j == 6 && i == 7) {
								position[6][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 6));
								interfaz.setLaberintator(matrixAimg[6][6]);
								return;
							}

							// Nivel 5 ---------------------
							if (j == 5 && i == 1) {
								position[7][0] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 0, 7));
								interfaz.setLaberintator(matrixAimg[7][0]);
								return;
							}

							// Nivel 4 --------------------
							if (j == 4 && i == 6) {
								position[4][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 4));
								interfaz.setLaberintator(matrixAimg[4][4]);
								return;
							}
							if (j == 4 && i == 7) {
								position[4][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 4));
								interfaz.setLaberintator(matrixAimg[4][6]);
								return;
							}

							// Nivel 3 ----------------------
							if (j == 3 && i == 1) {
								position[3][0] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 0, 3));
								interfaz.setLaberintator(matrixAimg[3][0]);
								return;
							}
							if (j == 3 && i == 2) {
								position[3][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 3));
								interfaz.setLaberintator(matrixAimg[3][1]);
								return;
							}
							if (j == 3 && i == 4) {
								position[3][2] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 2, 3));
								interfaz.setLaberintator(matrixAimg[3][2]);
								return;
							}

							// Nivel 2 -------------------
							if (j == 2 && i == 7) {
								position[2][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 2));
								interfaz.setLaberintator(matrixAimg[2][6]);
								return;
							}

							// Nivel 1 -------------------
							if (j == 1 && i == 1) {
								position[1][0] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 0, 1));
								interfaz.setLaberintator(matrixAimg[1][0]);
								return;
							}
							if (j == 1 && i == 2) {
								position[1][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 1));
								interfaz.setLaberintator(matrixAimg[1][1]);
								return;
							}
							if (j == 1 && i == 4) {
								position[1][2] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 2, 1));
								interfaz.setLaberintator(matrixAimg[1][2]);
								return;
							}
							if (j == 1 && i == 6) {
								position[1][4] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 4, 1));
								interfaz.setLaberintator(matrixAimg[1][4]);
								return;
							}
							break;
						}
					} else if (con.getId() == 1) { /////////////////////////////////////////////////////

						switch (dir) {

						case 0: // UP

							if (indicacion.contains("arriba")) {
								interfaz.setConfianza(interfaz.getConfianza() + 50);
								con.enviar("confiado");
							}

							// Nivel 6 ------------------
							if (j == 6 && i == 1) {
								position[5][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 5));
								interfaz.setLaberintator(matrixBimg[5][1]);
								return;
							}
							if (j == 6 && i == 3) {
								position[4][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 4));
								interfaz.setLaberintator(matrixBimg[4][3]);
								return;
							}
							if (j == 6 && i == 5) {
								position[4][5] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 5, 4));
								interfaz.setLaberintator(matrixBimg[4][5]);
								return;
							}

							// Nivel 5 -----------------
							if (j == 5 && i == 1) {
								position[3][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 3));
								interfaz.setLaberintator(matrixBimg[3][1]);
								return;
							}

							// Nivel 4 -----------------
							if (j == 4 && i == 3) {
								position[2][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 2));
								interfaz.setLaberintator(matrixBimg[2][3]);
								return;
							}
							if (j == 4 && i == 6) {
								position[2][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 2));
								interfaz.setLaberintator(matrixBimg[2][6]);
								return;
							}

							// Nivel 3 ---------------------
							if (j == 3 && i == 1) {
								position[2][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 2));
								interfaz.setLaberintator(matrixBimg[2][1]);
								return;
							}

							// Nivel 2 --------------------
							if (j == 2 && i == 1) {
								position[0][0] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 0, 0));
								interfaz.setLaberintator(matrixBimg[0][0]);
								return;
							}
							if (j == 2 && i == 3) {
								position[0][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 0));
								interfaz.setLaberintator(matrixBimg[0][3]);
								return;
							}
							if (j == 2 && i == 6) {
								position[0][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 0));
								interfaz.setLaberintator(matrixBimg[0][6]);
								return;
							}
							break;

						case 1: // RIGHT

							if (indicacion.contains("derecha")) {
								interfaz.setConfianza(interfaz.getConfianza() + 50);
								con.enviar("confiado");
							}

							// Nivel 7 --------------------
							if (j == 7 && i == 6) {
								position[6][7] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 7, 6));
								interfaz.setLaberintator(matrixBimg[6][7]);
								return;
							}

							// Nivel 6 ---------------
							if (j == 6 && i == 1) {
								position[6][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 6));
								interfaz.setLaberintator(matrixBimg[6][3]);
								return;
							}
							if (j == 6 && i == 3) {
								position[6][5] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 5, 6));
								interfaz.setLaberintator(matrixBimg[6][5]);
								return;
							}

							// Nivel 5 --------------
							if (j == 5 && i == 0) {
								position[5][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 5));
								interfaz.setLaberintator(matrixBimg[5][1]);
								return;
							}

							// Nivel 4 ----------------
							if (j == 4 && i == 3) {
								position[4][5] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 5, 4));
								interfaz.setLaberintator(matrixBimg[4][5]);
								return;
							}
							if (j == 4 && i == 5) {
								position[4][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 4));
								interfaz.setLaberintator(matrixBimg[4][6]);
								return;
							}
							if (j == 4 && i == 6) {
								position[4][7] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 7, 4));
								interfaz.setLaberintator(matrixBimg[4][7]);
								return;
							}

							// Nivel 3 -----------------
							if (j == 3 && i == 0) {
								position[3][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 3));
								interfaz.setLaberintator(matrixBimg[3][1]);
								return;
							}

							// Nivel 2 -----------------
							if (j == 2 && i == 1) {
								position[2][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 2));
								interfaz.setLaberintator(matrixBimg[2][3]);
								return;
							}
							if (j == 2 && i == 3) {
								position[2][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 2));
								interfaz.setLaberintator(matrixBimg[2][6]);
								return;
							}
							if (j == 2 && i == 6) {
								position[2][7] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 7, 2));
								interfaz.setLaberintator(matrixBimg[2][7]);
								return;
							}
							break;

						case 2: // DOWN

							if (indicacion.contains("abajo")) {
								interfaz.setConfianza(interfaz.getConfianza() + 50);
								con.enviar("confiado");
							}

							// Nivel 6 ------------
							if (j == 6 && i == 1) {
								position[7][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 7));
								interfaz.setLaberintator(matrixBimg[7][1]);
								return;
							}
							if (j == 6 && i == 3) {
								position[7][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 7));
								interfaz.setLaberintator(matrixBimg[7][3]);
								return;
							}
							if (j == 6 && i == 5) {
								position[7][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 7));
								interfaz.setLaberintator(matrixBimg[7][6]);
								return;
							}
							if (j == 6 && i == 7) {
								position[7][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 7));
								interfaz.setLaberintator(matrixBimg[7][6]);
								return;
							}

							// Nivel 5 ---------------
							if (j == 5 && i == 1) {
								position[6][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 6));
								interfaz.setLaberintator(matrixBimg[6][1]);
								return;
							}

							// Nivel 4 --------------
							if (j == 4 && i == 3) {
								position[6][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 6));
								interfaz.setLaberintator(matrixBimg[6][3]);
								return;
							}
							if (j == 4 && i == 5) {
								position[6][5] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 5, 6));
								interfaz.setLaberintator(matrixBimg[6][5]);
								return;
							}

							// Nivel 3 ---------------
							if (j == 3 && i == 1) {
								position[5][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 5));
								interfaz.setLaberintator(matrixBimg[5][1]);
								return;
							}

							// Nivel 2 --------------
							if (j == 2 && i == 1) {
								position[3][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 3));
								interfaz.setLaberintator(matrixBimg[3][1]);
								return;
							}
							if (j == 2 && i == 3) {
								position[4][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 4));
								interfaz.setLaberintator(matrixBimg[4][3]);
								return;
							}
							if (j == 2 && i == 6) {
								position[4][6] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 6, 4));
								interfaz.setLaberintator(matrixBimg[4][6]);
								return;
							}
							break;

						case 3: // LEFT

							if (indicacion.contains("izquierda")) {
								interfaz.setConfianza(interfaz.getConfianza() + 50);
								con.enviar("confiado");
							}

							// Nivel 7 -------------
							if (j == 7 && i == 6) {
								position[6][5] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 5, 6));
								interfaz.setLaberintator(matrixBimg[6][5]);
								return;
							}

							// Nivel 6 --------------
							if (j == 6 && i == 3) {
								position[6][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 6));
								interfaz.setLaberintator(matrixBimg[6][1]);
								return;
							}
							if (j == 6 && i == 5) {
								position[6][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 6));
								interfaz.setLaberintator(matrixBimg[6][3]);
								return;
							}

							// Nivel 5 --------------
							if (j == 5 && i == 1) {
								position[5][0] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 0, 5));
								interfaz.setLaberintator(matrixBimg[5][0]);
								return;
							}

							// Nivel 4 ------------
							if (j == 4 && i == 5) {
								position[4][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 4));
								interfaz.setLaberintator(matrixBimg[4][3]);
								return;
							}
							if (j == 4 && i == 6) {
								position[4][5] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 5, 4));
								interfaz.setLaberintator(matrixBimg[4][5]);
								return;
							}

							// Nivel 3 --------------
							if (j == 3 && i == 1) {
								position[3][0] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 0, 3));
								interfaz.setLaberintator(matrixBimg[3][0]);
								return;
							}

							// Nivel 2 ---------------
							if (j == 2 && i == 3) {
								position[2][1] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 1, 2));
								interfaz.setLaberintator(matrixBimg[2][1]);
								return;
							}
							if (j == 2 && i == 6) {
								position[2][3] = 3;
								position[j][i] = 1;
								con.enviar(new Mensaje(enviarA, 3, 2));
								interfaz.setLaberintator(matrixBimg[2][3]);
								return;
							}
							break;
						}
					}
				}
			}
		}
	}
}
