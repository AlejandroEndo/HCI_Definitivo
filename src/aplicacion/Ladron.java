package aplicacion;

import processing.core.PApplet;
import processing.core.PImage;

public class Ladron {

	private PApplet app;

	private PImage ladronsito;

	private int x;
	private int y;

	public Ladron(PApplet app, int x, int y) {
		this.app = app;
		this.x = x;
		this.y = y;
		ladronsito = app.loadImage("../data/ladron.png");
	}

	public void draw() {
		app.image(ladronsito, 850, app.height / 2);
	}

	public void setPos(int y, int x) {
		this.y = y;
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
}
