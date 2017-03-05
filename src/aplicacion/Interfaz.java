package aplicacion;

import processing.core.PApplet;
import processing.core.PImage;

public class Interfaz {

	private PApplet app;

	private PImage fondoLab;
	private PImage inicio;
	private PImage instrucciones;
	private PImage interfazCom;
	private PImage turno;
	private PImage continuar;
	private PImage continuarOver;

	private PImage[] laberinto = new PImage[16];
	private PImage[] direccion = new PImage[5];
	private PImage[] botones = new PImage[5];
	private PImage[] botonesOver = new PImage[5];

	private int pantalla;
	private int laberintator;
	private int instructator;
	private int botonator;
	private int confianza;
	private int confianzaTotal;

	private boolean over;

	public Interfaz(PApplet app) {
		this.app = app;

		over = false;
		confianza = 0;
		instructator = 4;

		confianza = 1;
		confianzaTotal = 1;

		fondoLab = app.loadImage("../data/fondoLab.png");
		inicio = app.loadImage("../data/inicio.png");
		instrucciones = app.loadImage("../data/instrucciones.png");
		interfazCom = app.loadImage("../data/zonaCom.png");
		turno = app.loadImage("../data/turno.png");
		continuar = app.loadImage("../data/continuar.png");
		continuarOver = app.loadImage("../data/continuarOver.png");

		for (int i = 0; i < 16; i++) {
			laberinto[i] = app.loadImage("../data/lab" + i + ".png");
		}

		for (int i = 0; i < 5; i++) {
			direccion[i] = app.loadImage("../data/to" + i + ".png");
			botones[i] = app.loadImage("../data/dir" + i + ".png");
			botonesOver[i] = app.loadImage("../data/over" + i + ".png");
		}
	}

	public void draw() {
		switch (pantalla) {
		case 0:
			app.image(inicio, app.width / 2, app.height / 2);

			if (over)
				app.image(continuarOver, app.width / 2, 600);
			else
				app.image(continuar, app.width / 2, 600);
			break;

		case 1:
			app.image(instrucciones, app.width / 2, app.height / 2);
			break;

		case 2:

			// ---------Zona Interfaz--------------------------------
			app.image(interfazCom, 250, app.height / 2);
			app.image(fondoLab, 850, app.height / 2);
			app.image(laberinto[laberintator], 850, app.height / 2);
			app.image(turno, app.width / 2, app.height / 2);
			// ------------------------------------------------------

			// ---------Zona Comunicacion--------------------------
			app.image(botones[0], 250, 450);
			app.image(botones[1], 325, 525);
			app.image(botones[2], 250, 600);
			app.image(botones[3], 175, 525);
			app.image(botones[4], 250, 525);

			app.image(direccion[instructator], 425, 415);

			showOver();
			// ----------------------------------------------------

			// -------- Medidor Confianza -------------------------
			int promedio = (confianza * 100) / confianzaTotal;
			app.text("Porcentaje de confianza: " + promedio + "%", app.width / 2, 650);
			// ------------------------------------------------
			break;

		}
	}

	public void showOver() {
		if (PApplet.dist(250, 450, app.mouseX, app.mouseY) < 25) {
			app.image(botonesOver[0], 250, 450);
		}
		if (PApplet.dist(325, 525, app.mouseX, app.mouseY) < 25) {
			app.image(botonesOver[1], 325, 525);
		}
		if (PApplet.dist(250, 600, app.mouseX, app.mouseY) < 25) {
			app.image(botonesOver[2], 250, 600);
		}
		if (PApplet.dist(175, 525, app.mouseX, app.mouseY) < 25) {
			app.image(botonesOver[3], 175, 525);
		}
		if (PApplet.dist(250, 525, app.mouseX, app.mouseY) < 25) {
			app.image(botonesOver[4], 250, 525);
		}
	}

	public int getPantalla() {
		return pantalla;
	}

	public void setPantalla(int pantalla) {
		this.pantalla = pantalla;
	}

	public int getLaberintator() {
		return laberintator;
	}

	public void setLaberintator(int laberintator) {
		this.laberintator = laberintator;
	}

	public int getInstructator() {
		return instructator;
	}

	public void setInstructator(int instructator) {
		this.instructator = instructator;
	}

	public int getBotonator() {
		return botonator;
	}

	public void setBotonator(int botonator) {
		this.botonator = botonator;
	}

	public boolean isOver() {
		return over;
	}

	public void setOver(boolean over) {
		this.over = over;
	}

	public int getConfianza() {
		return confianza;
	}

	public void setConfianza(int confianza) {
		this.confianza = confianza;
	}

	public int getConfianzaTotal() {
		return confianzaTotal;
	}

	public void setConfianzaTotal(int confianzaTotal) {
		this.confianzaTotal = confianzaTotal;
	}

}
