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
	
	public static void main(String[] args){
		tiempoMilis =  System.currentTimeMillis();
		
		Reader grafo = new Reader("./data/Temp_GCUT17.txt");
		
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
				ArrayList<String> camino= new ArrayList<String>();
				String anterior ="0";
				boolean termino=false;
				
				/*for(int j=0; j<vars.length;j++)
				{
					String n1=vars[j].get(GRB.StringAttr.VarName);
					String viaje=n1.split("\\(")[1];
					String inicio=viaje.split(",")[0];
					String destino=viaje.split(",")[1].split("\\)")[0];
					String corteOno="";
					double valorX=vars[j].get(GRB.DoubleAttr.X);
					if(valorX>0 ){
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
						System.out.println(inicio+" >> "+destino +" "+corteOno);
					}
				}*/
				int i=0;
				//Esto todavia no imprime bien los atributos
				while(!termino){
					String n1=vars[i].get(GRB.StringAttr.VarName);
					String viaje=n1.split("\\(")[1];
					String inicio=viaje.split(",")[0];
					String destino=viaje.split(",")[1].split("\\)")[0];
					String corteOno="";
					double valorX=vars[i].get(GRB.DoubleAttr.X);
					if(valorX>0 && inicio.equals(anterior)){
						if(!buscarEnCamino(camino, inicio, destino))
						{
							camino.add(inicio);
							anterior = destino;
							i=0;
							int inicioN = Integer.parseInt(inicio);
							int fin = Integer.parseInt(destino);
							if(inicioN <= numNodosCorte && fin <= numNodosCorte)
							{
								corteOno = "Corta";
							}
							else if(inicioN > numNodosCorte && fin > numNodosCorte)
							{
								corteOno = "Aire";
							}
							System.out.println(inicio+" >> "+destino +" "+corteOno);
						}
						else
						{
							camino.add(inicio);
						}
					}
					if(i==vars.length-1)
					{
						termino=true;
					}
					i++;
				}
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
	
}
