package serializables;

import java.io.Serializable;

public class Identificador implements Serializable {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
String saludo;
int id;
public Identificador(String saludo, int id) {
	this.saludo = saludo;
	this.id = id;
}
public String getSaludo() {
	return saludo;
}
public int getId() {
	return id;
}
}
