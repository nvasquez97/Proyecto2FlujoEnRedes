package Estructuras;

import java.util.ArrayList;

import Lectura.Reader;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

/**
 * Proyecto 2 Flujo en redes 2018-20
 * @author Nicolás Hernández, Nicolás Vásquez 
 */
public class Model {

	private static long tiempoMilis;
	private static int numNodosCorte;
	private static int numNodosTotal;
	
	private static int[][] matrizAdyacencia;
	private static double[][] matrizCostos;
	
	private static ArrayList<String> caminoFinal = new ArrayList<String>();
	private static ArrayList<String> miniCamino = new ArrayList<String>();
	private static ArrayList<Integer> nodosBifucardos = new ArrayList<Integer>();
	
	public static void main(String[] args){
		tiempoMilis =  System.currentTimeMillis();
		
		Reader grafo = new Reader("./data/Temp_GCUT13.txt");
		
		matrizAdyacencia = ((Reader) grafo).getMatrizAdyacencia();
		matrizCostos = grafo.getMatrizCostos();
		//Existen 2 nodos dummies
		numNodosTotal = grafo.getNumNodosAire()+grafo.getNumNodosCorte()+2;
		
		numNodosCorte= grafo.getNumNodosCorte();
		//Crear entorno para gurobi
		GRBEnv env;
		
		try{
			env = new GRBEnv(null);
			GRBModel model = new GRBModel(env);
			//Crear las variables binarias 
			int arco=0;
			for(int i=0;i<matrizAdyacencia.length;i++)
			{
				for(int j=0;j<matrizAdyacencia.length;j++)
				{
					arco = matrizAdyacencia[i][j];
					if(arco>0)
					{
						//Agrega variable que representa ir de nodo i a j y devuelta
						model.addVar(0,1,matrizCostos[i][j],GRB.BINARY,"x("+i+","+j+")");
					}
				}
			}
			model.update();
			
			//Crear las restricciones de balance
			GRBLinExpr balance;
			
			//Estas restricciones de balance tienen que ser para todos los nodos menos el nodo inicial y final
			for(int i=0;i<matrizAdyacencia.length;i++){	
				balance = new GRBLinExpr();
				for(int j=0;j<matrizAdyacencia.length;j++){
					//La sumatoria de los arcos disidentes
					if(matrizAdyacencia[i][j]==1){
						balance.addTerm(1, model.getVarByName("x("+i+","+j+")"));
					}
					//Menos la sumatoria de los arcos incidentes
					if(matrizAdyacencia[j][i]==1){
						balance.addTerm(-1, model.getVarByName("x("+j+","+i+")"));
					}
				}
				
				if(i!=0 && i<numNodosTotal-1){
					model.addConstr(balance,GRB.EQUAL,0,"Balance nodo "+i);
				} else if(i==0){
					model.addConstr(balance, GRB.EQUAL, 1,"Balance nodo inicial");
				}
				else {
					model.addConstr(balance, GRB.EQUAL, -1,"Balance nodo final");
				}
			}
			model.update();
			
			//Crear restriccion para cortar una sola vez por cada arco
			GRBLinExpr corteU;
			for(int i =1; i<numNodosCorte+1;i++)
			{
				for(int j =i+1; j<numNodosCorte+1;j++){
					if(matrizAdyacencia[i][j]==1)
					{
						corteU = new GRBLinExpr();
						corteU.addTerm(1, model.getVarByName("x("+i+","+j+")"));
						corteU.addTerm(1, model.getVarByName("x("+j+","+i+")"));
						model.addConstr(corteU, GRB.EQUAL, 1, "Corte Unico entre "+i+" y "+j);
					}
				}
			}
			
			/*
			 * 1: Minimización
			 * -1: Maximización
			 */
				model.set(GRB.IntAttr.ModelSense, 1);
				model.update();
				
				model.write("Cortes.lp");
				
				model.optimize();
				
				System.out.println("SE DEMORA EN CORRER TODO ANTES DE IMPRIMIR SOLUCION "+ (System.currentTimeMillis()-tiempoMilis)+" milisegundos");
				//Imprimir los resultados
				
				double objval = model.get(GRB.DoubleAttr.ObjVal);
				System.out.println("El tiempo total de corte es de: "+objval+" segundos");
				
				GRBVar[] vars=model.getVars();
				ArrayList<String> soluciones = new ArrayList<String>();				
				
				for(int j=0; j<vars.length;j++)
				{
					try
					{
						String n1=vars[j].get(GRB.StringAttr.VarName);
						double valorX=vars[j].get(GRB.DoubleAttr.X);
						if(valorX>0 )
						{
							soluciones.add(n1);							
						}
						
					}
					catch(Exception e)
					{
						
					}
				}
				
				model.dispose();
				env.dispose();
				
				for(int j=0; j<soluciones.size();j++)
				{
					try
					{
						String n1= soluciones.get(j);
						String viaje=n1.split("\\(")[1];
						String inicio=viaje.split(",")[0];
						String destino=viaje.split(",")[1].split("\\)")[0];
						String corteOno="";					
							
						int inicioN = Integer.parseInt(inicio);
						int fin = Integer.parseInt(destino);
						if(inicioN < numNodosCorte && fin < numNodosCorte)
						{
							corteOno = "Corta";
						}
						else if(inicioN >= numNodosCorte && fin >= numNodosCorte)
						{
							corteOno = "Aire";
						}
						System.out.println( inicio+" >> "+destino +" "+corteOno);					
					}
					catch(Exception e)
					{
						
					}
				}
				
				boolean ordenamiento = construirCamino(soluciones, 0, numNodosTotal-1, numNodosTotal-1);
				
				for(int i = 0; i < caminoFinal.size(); i++)
				{
					System.out.println(caminoFinal.get(i));
				}				
				
				System.out.println("SE DEMORA EN CORRER TODO "+ (System.currentTimeMillis()-tiempoMilis)+" milisegundos");
				
			}
			catch(GRBException e){
				e.printStackTrace();
			}
		}
	
	
	public static int darNumNodosCorte()
	{
		return numNodosCorte;
	}
	
	public static int darNumNodosTotal()
	{
		return numNodosTotal;
	}
	
	public static boolean buscarEnCamino(ArrayList<String> camino, String nodoInicio, String nodoFin)
	{
		boolean esta=false;
		String anterior="0";
		String nodoC;

		for(int i=1; i<camino.size() && !esta;i++)
		{
			nodoC=camino.get(i);
			if(nodoC.equals(nodoFin) && anterior.equals(nodoInicio)){
				esta=true;
			}
			else
			{
				anterior = nodoC;
			}
		}
		return esta;
	}
	
	public static boolean construirCamino(ArrayList<String> grafo, int nodoInicial, int nodoFinal, int nodoDefinitivo)
	{
		boolean llego = false;
		
		ArrayList<String> caminoRet = new ArrayList<String>();
		try
		{
			
			int nodoActual = nodoInicial;				
			
			while(!llego)
			{
				ArrayList<int[]> arcosSalida = new ArrayList<int[]>();
				
				for(int i = 0; i < grafo.size(); i++)
				{
					
					String viajeI=grafo.get(i).split("\\(")[1];
					int inicioI= Integer.parseInt(viajeI.split(",")[0]);
					int destinoI= Integer.parseInt(viajeI.split(",")[1].split("\\)")[0]);
					
					if(inicioI == nodoActual)
					{
						int[] arco = new int[2];
						arco[0] = inicioI;
						arco[1] = destinoI;
						arcosSalida.add(arco);
						miniCamino.add(nodoInicial + " - " +  arco[0] + " >> " + arco[1]);
					}
					
					
				}
				
				if(arcosSalida.size() == 1)
				{
					boolean llegoPuntoBifurcado = false;
					
					for (int i = 0; i < nodosBifucardos.size() && !llegoPuntoBifurcado; i++)
					{
						if(nodosBifucardos.get(i) == arcosSalida.get(0)[1])
						{
							llegoPuntoBifurcado = true;
						}
					}
					
					caminoRet.add(nodoActual +" >> " + arcosSalida.get(0)[1]);
					nodoActual = arcosSalida.get(0)[1];	
					if(nodoActual == nodoInicial)
					{
						llego = true;						
					}
					else if (nodoActual == nodoDefinitivo)
					{
						llego = true;						
					}
					else if (llegoPuntoBifurcado)
					{
						llego = true;
					}
				}
				else
				{
					boolean[] llegoPuntoBifurcado = new boolean[arcosSalida.size()];					
					
					for (int j = 0; j < arcosSalida.size(); j++)
					{
						for (int i = 0; i < nodosBifucardos.size(); i++)
						{
							if(nodosBifucardos.get(i) == arcosSalida.get(j)[1])
							{
								llegoPuntoBifurcado[j] = true;
							}
						}						
					}					
					
					nodosBifucardos.add(arcosSalida.get(0)[0]);
					boolean[] caminos = new boolean[arcosSalida.size()];
					boolean[] caminoDefinitivo = new boolean[arcosSalida.size()];
					for(int j = 0; j < arcosSalida.size(); j++)
					{
						if(!llegoPuntoBifurcado[j])
						{
							caminos[j] = construirCamino(grafo, arcosSalida.get(j)[1], arcosSalida.get(j)[0], nodoDefinitivo);
							if(caminos[j])
							{
								caminoDefinitivo[j] = true;
							}
						}						
					}
					
					llego = true;
				
					for(int j = 0; j < arcosSalida.size(); j++)
					{						
						if(!(llegoPuntoBifurcado[j] || caminoDefinitivo[j]))
						{
							llego = false;
						}
					}					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		
		return llego;
	}
	
}
