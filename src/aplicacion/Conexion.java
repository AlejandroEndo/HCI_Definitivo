package aplicacion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Observable;

import serializables.Identificador;

// NO TOCAR, SOLO USAR enviar() EN LA LOGICA
public class Conexion extends Observable implements Runnable {
	MulticastSocket socket;

	final int PORT = 5000;
	InetAddress ip;
	private int id = 1;
	private static Conexion instance;
	private static Thread thread;
	private boolean identificar;
	private int destino;

	public Conexion() {
		instance = null;
		thread = new Thread(this);

		identificar = false;
		try {
			socket = new MulticastSocket(PORT);
			ip = InetAddress.getByName("230.9.5.96");
			socket.joinGroup(ip);
			autoId();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void run() {
		while (true) {
			try {
				Object identificador = recibir();
				if (identificador instanceof serializables.Identificador) {
					if (((Identificador) identificador).getSaludo().contains("hola")) {
						enviar(new Identificador("respuesta", id));
					}
				}
				// if(destino==id){
				setChanged();
				notifyObservers(recibir());
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Conexion getInstance() {
		if (instance == null) {
			instance = new Conexion();
			thread.start();
		}
		return instance;
	}

	public Object recibir() throws IOException, ClassNotFoundException {

		byte[] by = new byte[1024];
		DatagramPacket paquete = new DatagramPacket(by, by.length);
		socket.receive(paquete);
		ByteArrayInputStream flujoBytes = new ByteArrayInputStream(by);
		ObjectInputStream deserial = new ObjectInputStream(flujoBytes);
		Object obj = deserial.readObject();
		deserial.close();
		return obj;

	}

	public void enviar(Object obj) throws IOException {

		ByteArrayOutputStream bytesFlujito = new ByteArrayOutputStream();
		ObjectOutputStream cereal = new ObjectOutputStream(bytesFlujito);
		cereal.writeObject(obj);
		cereal.flush();
		cereal.close();
		bytesFlujito.close();
		byte[] bytes = bytesFlujito.toByteArray();
		DatagramPacket paquete = new DatagramPacket(bytes, bytes.length, ip, PORT);
		socket.send(paquete);
		socket.send(paquete);
	}

	public void autoId() throws Exception {
		enviar(new Identificador("hola", id));

		try {
			socket.setSoTimeout(1000);
			while (identificar == false) {
				Object identificador = recibir();
				if (identificador instanceof serializables.Identificador) {
					if (((Identificador) identificador).getSaludo().contains("respuesta")) {
						if (((Identificador) identificador).getId() >= id) {
							id = ((Identificador) identificador).getId() + 1;
						}
					}
				}
			}
		} catch (SocketTimeoutException e) {
			identificar = true;
			socket.setSoTimeout(0);
			System.out.println(id);
		}
	}

	public int getId() {
		return id;
	}

}
