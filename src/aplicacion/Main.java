package aplicacion;

import processing.core.PApplet;
import processing.core.PFont;

public class Main extends PApplet {
	public Logica log;

	private PFont pier;
	
	public void setup() {
		size(1200, 700);
		pier = createFont("../data/pier-regular.otf", 20);
		noStroke();
		colorMode(RGB, 255,255,255,100);
		imageMode(CENTER);
		log = new Logica(this);
		textFont(pier);
	}

	public void draw() {
		background(255);
		log.draw();
	}

	public void mouseClicked() {
		log.click();
	}
}
