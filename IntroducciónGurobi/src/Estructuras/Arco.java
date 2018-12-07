package Estructuras;

/*
 * Optimización Avanzada - 201810
 * 
 * Clase Arco. 
 * 
 */
public class Arco{
	
	/*
	 * TO-DO: Caracteristicas de un arco
	 * Cola, cabeza y costo.
	 * 
	 * En este caso hacen falta la cabeza y el costo.
	 */
	private int tail;
	
	private int head;
	
	private int cost;
	
	
	/*
	 * TO-DO: Metodo constructor. Crea un arco con cabeza, cola, y costo que vienen por parámetros.
	 * 
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
		return tail;
	}

	public void setTail(int tail) {
		this.tail = tail;
	}


	public int getHead() {
		return head;
	}


	public void setHead(int head) {
		this.head = head;
	}


	public int getCost() {
		return cost;
	}


	public void setCost(int cost) {
		this.cost = cost;
	}
	
}