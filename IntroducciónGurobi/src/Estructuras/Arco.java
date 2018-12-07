package Estructuras;

/*
 * Proyecto 2 Flujo en Redes 2018-20 
 */
public class Arco{
	
	/*
	 * En este caso hacen falta la cabeza y el costo.
	 */
	private int fin;
	
	private int inicio;
	
	private int tiempo;
	
	
	/* 
	 * En este caso hace falta fijar la cabeza y costo del arco a crear.
	 */
	
	public Arco(int tail, int head, int cost){
		this.setTail(tail);
		this.setHead(head);
		this.setCost(cost);
		
	}
	

	/*
	 * TO-DO: Metodos getters and setters.
	 * 
	 * En este caso hacen falta los metodos para obtener y fijar la cabeza y los costos.
	 */
	
	public int getTail() {
		return fin;
	}

	public void setTail(int tail) {
		this.fin = tail;
	}


	public int getHead() {
		return inicio;
	}


	public void setHead(int head) {
		this.inicio = head;
	}


	public int getCost() {
		return tiempo;
	}


	public void setCost(int cost) {
		this.tiempo = cost;
	}
	
}