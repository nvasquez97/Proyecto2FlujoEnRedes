import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/*
 * Clase para leer los parámetros. 
 */
public class Reader {

	/*
	 * Crea un bufferedReader para leer el archivo txt.
	 */
	private BufferedReader in;
	
	/*
	 * Crea un nuevo grafo.
	 */
	
	private int matrizAdyacencia[][];
	
	private double matrizCostos[][];
	
	int numNodosCorte = 0;
	int numNodosAire = 0;
	ArrayList<int[]> nodosCorte = new ArrayList<int[]>();
	ArrayList<int[]> nodosAire = new ArrayList<int[]>();
	
	ArrayList<int[]> arcosCorte = new ArrayList<int[]>();
	ArrayList<int[]> arcosAire = new ArrayList<int[]>();
	
	Hashtable<String, Integer> correspondenciaNodos = new Hashtable<String, Integer>();
	
	long tiempo;
	
	/*
	 * Metodo constructor. Recibe un filepath.
	 * 
	 */
	public Reader(String filepath)
	{	
		int tamanioX = 0;
		int tamanioY = 0;
		tiempo = System.currentTimeMillis();
		
		try
		{
			/*
			 * Inicializa los parametros. Y crea variables auxilizares para ir guardando los valores, linea tras linea.
			 */
			in = new BufferedReader(new FileReader(filepath));
			String line;		
			
			/*
			 * Mientras que hayan lineas por leer, lee la linea y separando por "\t",
			 */
			int numLinea = 0;
			while((line = in.readLine())!=null)
			{
				numLinea ++;
				String [] list = line.split("\t");
				
				int[] nodoNuevo = new int[2];
				
				if(numLinea == 1)
				{		
					tamanioX =	Integer.parseInt(list[2]);	
					tamanioY = Integer.parseInt(list[3]);					
				}
				else if(numLinea > 1)
				{					
					
					int[] arcoArriba = new int[4];
					int[] arcoAbajo = new int[4];
					int[] arcoIzquierda = new int[4];
					int[] arcoDerecha = new int[4];
					
					//Esquina izquierda superior
					
					nodoNuevo = new int[2];
					
					nodoNuevo[0] = Integer.parseInt(list[0]);
					nodoNuevo[1] = Integer.parseInt(list[1]);
					
					if(!((nodoNuevo[0] == 0 && nodoNuevo[1] == 0) || (nodoNuevo[0] == tamanioX && nodoNuevo[1] == 0) || (nodoNuevo[0] == tamanioX && nodoNuevo[1] == tamanioY) || (nodoNuevo[0] == 0 && nodoNuevo[1] == tamanioY)))
					{
						agregarNodo(nodoNuevo);	
					}							
					
					arcoArriba[0] = nodoNuevo[0];
					arcoArriba[1]=nodoNuevo[1];
					arcoIzquierda[0] = nodoNuevo[0];
					arcoIzquierda[1] = nodoNuevo[1];				
					
					
					//Esquina derecha superior
					
					nodoNuevo = new int[2];
					
					nodoNuevo[0]= Integer.parseInt(list[3]);
					nodoNuevo[1]=Integer.parseInt(list[1]);

					if(!((nodoNuevo[0] == 0 && nodoNuevo[1] == 0) || (nodoNuevo[0] == tamanioX && nodoNuevo[1] == 0) || (nodoNuevo[0] == tamanioX && nodoNuevo[1] == tamanioY) || (nodoNuevo[0] == 0 && nodoNuevo[1] == tamanioY)))
					{
						agregarNodo(nodoNuevo);	
					}	
					
					arcoArriba[2] = nodoNuevo[0];
					arcoArriba[3] = nodoNuevo[1];
					arcoDerecha[0] = nodoNuevo[0];
					arcoDerecha[1] = nodoNuevo[1];
					
					//Esquina izquierda inferior
					
					nodoNuevo = new int[2];
					
					nodoNuevo[0]= Integer.parseInt(list[0]);
					nodoNuevo[1]=Integer.parseInt(list[4]);

					if(!((nodoNuevo[0] == 0 && nodoNuevo[1] == 0) || (nodoNuevo[0] == tamanioX && nodoNuevo[1] == 0) || (nodoNuevo[0] == tamanioX && nodoNuevo[1] == tamanioY) || (nodoNuevo[0] == 0 && nodoNuevo[1] == tamanioY)))
					{
						agregarNodo(nodoNuevo);	
					}	
					
					arcoAbajo[0] = nodoNuevo[0];
					arcoAbajo[1] = nodoNuevo[1];
					arcoIzquierda[2] = nodoNuevo[0];
					arcoIzquierda[3] = nodoNuevo[1];
					
					//Esquina derecha inferior
					
					nodoNuevo = new int[2];
					
					nodoNuevo[0]= Integer.parseInt(list[3]);
					nodoNuevo[1]=Integer.parseInt(list[4]);

					if(!((nodoNuevo[0] == 0 && nodoNuevo[1] == 0) || (nodoNuevo[0] == tamanioX && nodoNuevo[1] == 0) || (nodoNuevo[0] == tamanioX && nodoNuevo[1] == tamanioY) || (nodoNuevo[0] == 0 && nodoNuevo[1] == tamanioY)))
					{
						agregarNodo(nodoNuevo);	
					}	
					
					arcoAbajo[2] = nodoNuevo[0];
					arcoAbajo[3] = nodoNuevo[1];
					arcoDerecha[2] = nodoNuevo[0];
					arcoDerecha[3] = nodoNuevo[1];
					
					if (agregarArco(arcoArriba) == false && arcoArriba[1] > 0 && arcoArriba[1] < tamanioY)
					{
						arcosCorte.add(arcoArriba);
					}
					
					if (agregarArco(arcoAbajo) == false && arcoAbajo[1] > 0 && arcoAbajo[1] < tamanioY)
					{
						arcosCorte.add(arcoAbajo);
					}
					
					if (agregarArco(arcoIzquierda) == false && arcoIzquierda[0] > 0 && arcoIzquierda[0] < tamanioX)
					{
						arcosCorte.add(arcoIzquierda);
					}
					
					if (agregarArco(arcoDerecha) == false && arcoDerecha[0] > 0 && arcoDerecha[0] < tamanioX)
					{
						arcosCorte.add(arcoDerecha);
					}
				}				
			}
			
			tiempo = System.currentTimeMillis() - tiempo;
			
			generarMatrices();
			
			System.out.println("Acabé con " + tiempo + " milisegundos");
			
			for(int i = 0; i < arcosCorte.size(); i++)
			{
				//System.out.println("("+arcosCorte.get(i)[0] + "," + arcosCorte.get(i)[1] + ") -> ("+arcosCorte.get(i)[2]+","+arcosCorte.get(i)[3]+")");
				System.out.println(arcosCorte.get(i)[0]+"\t"+arcosCorte.get(i)[1]);
				System.out.println(arcosCorte.get(i)[2]+"\t"+arcosCorte.get(i)[3]);
			}
			
			System.out.println("Nodos:");
			
			for(int i = 0; i < nodosCorte.size(); i++)
			{
				//System.out.println("("+arcosCorte.get(i)[0] + "," + arcosCorte.get(i)[1] + ") -> ("+arcosCorte.get(i)[2]+","+arcosCorte.get(i)[3]+")");
				System.out.println(nodosCorte.get(i)[0]+"\t"+nodosCorte.get(i)[1]);
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
	
	public boolean agregarArco(int[] arcoAgregar)
	{
		
		for(int i = 0; i < arcosCorte.size(); i++)
		{
			int[] arco = arcosCorte.get(i);
			
			if(arco[0] == arcoAgregar[0] && arco[1] == arcoAgregar[1] && arco[2] == arcoAgregar[2] && arco[3] == arcoAgregar[3])
			{
				return true;
			}
			
			//Horizontales
			if(arco[1] == arco[3] && arcoAgregar[1] == arcoAgregar[3])
			{
				
				if(arcoAgregar[1] == arco[1] && arco[2]> arcoAgregar[0] && arco[0] < arcoAgregar[2])
				{	
					
					arcosCorte.remove(i);
					
					int[] arcoNuevo1 = new int[4];
					
					arcoNuevo1[0] = arcoAgregar[0];
					arcoNuevo1[1]=  arcoAgregar[1];
					arcoNuevo1[2] = arco[0];
					arcoNuevo1[3] = arco[1];
					
					if(arcoAgregar[0] > arco[0])
					{
						arcoNuevo1[0] = arco[0];
						arcoNuevo1[2] = arcoAgregar[0];
					}
					
					if(arcoNuevo1[2] > arcoNuevo1[0])
					{
						if(agregarArco(arcoNuevo1) == false)
						{
							arcosCorte.add(arcoNuevo1);
						}
					}
					
					int[] arcoNuevo2 = new int[4];
					
					arcoNuevo2[0] = arco[0];
					arcoNuevo2[1]=arco[1];
					arcoNuevo2[2] = arco[2];
					arcoNuevo2[3] = arco[3];
					
					if(arco[0] > arco[2])
					{
						arcoNuevo2[0] = arco[2];
						arcoNuevo2[2] = arco[0];
					}
					
					if(arcoNuevo2[2] > arcoNuevo2[0])
					{
						if(agregarArco(arcoNuevo2) == false)
						{
							arcosCorte.add(arcoNuevo2);
						}
					}
					
					int[] arcoNuevo3 = new int[4];
					
					arcoNuevo3[0] = arco[2];
					arcoNuevo3[1]=arco[3];
					arcoNuevo3[2] = arcoAgregar[2];
					arcoNuevo3[3] = arcoAgregar[3];
					
					if(arco[2] > arcoAgregar[2])
					{
						arcoNuevo3[0] = arcoAgregar[2];
						arcoNuevo3[2] = arco[2];
					}
					
					if(arcoNuevo3[2] > arcoNuevo3[0])
					{
						if(agregarArco(arcoNuevo3) == false)
						{
							arcosCorte.add(arcoNuevo3);
						}
					}					
					
					return true;
					
				}				
			}
			
			//Verticales
			else if (arco[0] == arco[2] && arcoAgregar[0] == arcoAgregar[2])
			{
				if(arcoAgregar[0] == arco[0] && arco[3]> arcoAgregar[1] && arco[1] < arcoAgregar[3])
				{	
					arcosCorte.remove(i);
					
					int[] arcoNuevo1 = new int[4];
					
					arcoNuevo1[0] = arcoAgregar[0];
					arcoNuevo1[1]=arcoAgregar[1];
					arcoNuevo1[2] = arco[0];
					arcoNuevo1[3] = arco[1];
					
					if(arcoAgregar[1] > arco[1])
					{
						arcoNuevo1[1] = arco[1];
						arcoNuevo1[3] = arcoAgregar[1];
					}
					
					if(arcoNuevo1[3] > arcoNuevo1[1])
					{
						if(agregarArco(arcoNuevo1) == false)
						{
							arcosCorte.add(arcoNuevo1);
						}
					}				
					
					int[] arcoNuevo2 = new int[4];
					
					arcoNuevo2[0] = arco[0];
					arcoNuevo2[1]=arco[1];
					arcoNuevo2[2] = arco[2];
					arcoNuevo2[3] = arco[3];
					
					if(arco[1] > arco[3])
					{
						arcoNuevo2[1] = arco[3];
						arcoNuevo2[3] = arco[1];
					}
					
					if(arcoNuevo2[3] > arcoNuevo2[1])
					{
						if(agregarArco(arcoNuevo2) == false)
						{
							arcosCorte.add(arcoNuevo2);
						}
					}
					
					int[] arcoNuevo3 = new int[4];
					
					arcoNuevo3[0] = arco[2];
					arcoNuevo3[1]=arco[3];
					arcoNuevo3[2] = arcoAgregar[2];
					arcoNuevo3[3] = arcoAgregar[3];
					
					if(arco[3] > arcoAgregar[3])
					{
						arcoNuevo3[1] = arcoAgregar[3];
						arcoNuevo3[3] = arco[3];
					}
					
					if(arcoNuevo3[3] > arcoNuevo3[1])
					{
						if(agregarArco(arcoNuevo3) == false)
						{
							arcosCorte.add(arcoNuevo3);
						}
					}				
					
					return true;					
				}				
			}
		}	
		
		return false;
	}
	
	public boolean agregarNodo(int[] nodoAgregado)
	{
		boolean yaAgregado = false;
		
		for(int i = 0; i < nodosCorte.size() && !yaAgregado; i++)
		{
			if( nodosCorte.get(i)[0]== nodoAgregado[0] && nodosCorte.get(i)[1] == nodoAgregado[1])
			{
				yaAgregado = true;
			}
		}
		
		if (yaAgregado)
		{			
			return false;
		}
		else
		{
			nodosCorte.add(nodoAgregado);
			nodosAire.add(nodoAgregado);
			correspondenciaNodos.put(Integer.toString(nodoAgregado[0])+Integer.toString(nodoAgregado[1]), nodosCorte.size());
			return true;
		}
	}
	
	public void generarMatrices()
	{
		numNodosAire = nodosAire.size();
		numNodosCorte = nodosCorte.size();
		matrizAdyacencia = new int[numNodosAire + numNodosCorte + 2][numNodosAire + numNodosCorte + 2];
		matrizCostos = new double[numNodosAire + numNodosCorte + 2][numNodosAire + numNodosCorte + 2];
		
		//Arcos del nodo doomie de salida a todos y de todos al dommie de llegada
		for(int i = 0; i<nodosCorte.size();i++)
		{
			matrizAdyacencia[0][i+1]=1;
			matrizAdyacencia[i+1][numNodosCorte] = 1;
			matrizCostos[0][i+1]=0;
			matrizCostos[i+1][numNodosCorte] = 0;
		}
		
		for(int i = 0; i < arcosCorte.size();i++)
		{
			int[] arco = arcosCorte.get(i);
			
			String nodoSalida = Integer.toString(arco[0]) + Integer.toString(arco[1]);
			String nodoLlegada = Integer.toString(arco[2]) + Integer.toString(arco[3]);		

			int numNodoSalida = correspondenciaNodos.get(nodoSalida);
			int numNodoLlegada = correspondenciaNodos.get(nodoLlegada);
			
			matrizAdyacencia[numNodoSalida][numNodoLlegada] = 1;
			matrizAdyacencia[numNodoLlegada][numNodoSalida] = 1;
			
			double costoArco = 0;
			
			double distanciaArco = Math.sqrt((arco[0]-arco[2])^2+(arco[1]-arco[3])^2);
			
			costoArco = 0.1/50 * distanciaArco;
			
			matrizCostos[numNodoSalida][numNodoLlegada] = costoArco;
			matrizCostos[numNodoLlegada][numNodoSalida] = costoArco;
	
		}
		
		for(int i = 0; i < numNodosAire; i++)
		{
			for(int j = 0; j < numNodosAire;j++)
			{
				int[] nodoSalida = new int[2];
				int[] nodoLlegada = new int[2];
				nodoSalida =nodosAire.get(i);
				nodoLlegada = nodosAire.get(j);
				
				if (i!=j)
				{
					double costoArco = 0;
					
					double distanciaArco = Math.sqrt((nodoSalida[0]-nodoLlegada[0])^2+(nodoSalida[1]-nodoLlegada[1])^2);
					
					double tramo1 = Math.min(5, distanciaArco);
					double tramo2 = Math.max(0,Math.min(100, distanciaArco)-5);
					double tramo3 = Math.max(0, distanciaArco-100);
					costoArco = 0.2/5 * tramo1+0.3/95*tramo2+0.1/100*tramo3;
					
					matrizCostos[numNodosCorte+i+1][numNodosCorte+j+1] = costoArco;
					matrizCostos[numNodosCorte+j+1][numNodosCorte+i+1] = costoArco;			
				}
			}
			
			matrizCostos[numNodosCorte+i+1][i+1] = 0;
			matrizCostos[i+1][numNodosCorte+i+1] = 0;
			matrizAdyacencia[numNodosCorte+i+1][i+1] = 0;
			matrizAdyacencia[i+1][numNodosCorte+i+1] = 0;	
		}
	}
	
	public int[][] getMatrizAdyacencia() 
	{
		return matrizAdyacencia;
	}
	
	public double[][] getMatrizCostos() 
	{
		return matrizCostos;
	}
	
	public int getNumNodosAire() 
	{
		return numNodosAire;
	}
	
	public int getNumNodosCorte() 
	{
		return numNodosCorte;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Reader hola = new Reader("./data/Temp_GCUT16.txt");
	}

}
