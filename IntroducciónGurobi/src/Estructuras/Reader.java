package Estructuras;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * Optimización Avanzada - 201810
 * 
 * Clase para leer los parámetros. 
 * 
 */
public class Reader {

	/*
	 * Crea un bufferedReader para leer el archivo txt.
	 */
	private BufferedReader in;
	
	/*
	 * Crea un nuevo grafo.
	 */
	private Grafo g = new Grafo();
	
	
	/*
	 * Metodo constructor. Recibe un filepath.
	 * 
	 */
	public Reader(String filepath){
		try{
			/*
			 * Inicializa los parametros. Y crea variables auxilizares para ir guardando los valores, linea tras linea.
			 */
			in = new BufferedReader(new FileReader(filepath));
			String line;
			int i=0;
			int tail;
			int head;
			int cost;
			Arco cadaarco;
			
			/*
			 * Mientras que hayan lineas por leer, lee la linea y separando por " ",
			 */
			while((line = in.readLine())!=null){
				String [] list = line.split(" ");
				if(i == 0){
					for(int j=0;j<list.length;j++){
						g.agregarNodo(Integer.parseInt(list[j]));
					}
					i++;
				}
				else{
					tail=Integer.parseInt(list[0]);
					head=Integer.parseInt(list[1]);
					cost=Integer.parseInt(list[2]);
					cadaarco=new Arco(tail,head,cost);
					
					/*
					 * Agrega un arco teniendo en cuenta la linea leida. (llamar el metodo)
					 */
					
					g.agregarArco(cadaarco);
				}
			}
		}
		/*
		 * Informa en caso de ocurrir un error.
		 */
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public Grafo getG(){
		return g;
	}
	public void setG(Grafo g){
		this.g = g;
	}
}
