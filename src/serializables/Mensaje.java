package serializables;

import java.io.Serializable;

public class Mensaje implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private int x;
	private int y;
	private String tipo;
	private String direccion;
	private int turno;

public Mensaje(int id, int x, int y){
	this.id=id;
	this.x=x;
	this.y=y;
	tipo= "posicion";
}
public Mensaje(int id, String direccion){
	this.id=id;
	this.direccion=direccion;
	tipo= "indicacion";
}
public Mensaje(int id, int turno){
	this.id=id;
	this.turno=turno;
	tipo= "turno";
}
public int getTurno() {
	return turno;
}
public int getId() {
	return id;
}
public int getX() {
	return x;
}
public int getY() {
	return y;
}
public String getTipo() {
	return tipo;
}
public String getDireccion() {
	return direccion;
}
}
