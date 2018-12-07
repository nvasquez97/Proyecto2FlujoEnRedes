package Estructuras;

import java.util.ArrayList;

/*
 * Optimización Avanzada - 201810
 * 
 * Clase Grafo.
 * 
 */
public class Grafo {

	/*
	 * TO-DO: Componentes de un grafo.
	 * Nodos y arcos.
	 * 
	 * En este caso hace falta una lista para los arcos.
	 */
	private ArrayList<Integer>nodos;
	private ArrayList<Arco> arcos;
	
	/*
	 * TO-DO: Metodo constructor 1.
	 * 
	 * En este caso hace falta crear la lista de arcos.
	 */
	public Grafo(){
		nodos = new ArrayList<Integer>();
		arcos = new ArrayList<Arco>();
	}
	
	/*
	 * TO-DO: Metodo constructor 2.
	 * 
	 * En este caso hace falta fijar la lista de arcos.
	 */
	public Grafo(ArrayList<Integer> nodos, ArrayList<Arco>arcos){
		setNodos(nodos);
		setArcos(arcos);
		
	}

	/*
	 * TO-DO: Metodos getters and setters.
	 * 
	 * En este caso hacen falta los metodos para obtener y fijar la lista de arcos.
	 */
	public ArrayList<Integer> getNodos() {
		return nodos;
	}

	public void setNodos(ArrayList<Integer> nodos) {
		this.nodos = nodos;
	}

	/*
	 * TO-DO: Metodos para agregar arcos y nodos al grafo.
	 * 
	 * En este caso hace falta el metodo para agregar un arco a la lista.
	 */
	
	public void agregarNodo(Integer nodo){
		nodos.add(nodo);
	}

	public void agregarArco(Arco arco)
	{
		arcos.add(arco);
	}
	
	public ArrayList<Arco> getArcos() {
		return arcos;
	}

	public void setArcos(ArrayList<Arco> arcos) {
		this.arcos = arcos;
	}
	
	
	
}
