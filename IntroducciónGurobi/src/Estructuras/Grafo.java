package Estructuras;

import java.util.ArrayList;

/**
 * Proyecto 2 Flujo en Redes 2018-20
 * @author Nicolás Hernández, Nicolás Vásquez
 *
 */
public class Grafo {

	/*
	 * Nodos y arcos.
	 */
	//TODO: Cambiar de Integer a String los nodos
	private ArrayList<Integer>nodos;
	private ArrayList<Arco> arcos;
	
	public Grafo(){
		nodos = new ArrayList<Integer>();
		arcos = new ArrayList<Arco>();
	}

	public Grafo(ArrayList<Integer> nodos, ArrayList<Arco>arcos){
		setNodos(nodos);
		setArcos(arcos);
	}


	public ArrayList<Integer> getNodos() {
		return nodos;
	}

	public void setNodos(ArrayList<Integer> nodos) {
		this.nodos = nodos;
	}

	/* 
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
