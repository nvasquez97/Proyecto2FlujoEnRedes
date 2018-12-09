package Estructuras;

import java.util.ArrayList;
import Lectura.*;

import gurobi.*;

/**
 * Proyecto 2 Flujo en redes 2018-20
 * @author Nicolás Hernández, Nicolás Vásquez 
 */
public class Model {

	private static int numNodosCorte;
	private static int numNodosTotal;
	
	private static int[][] matrizAdyacencia;
	private static double[][] matrizCostos;
	
	public static void main(String[] args){
		
		//TODO: Cargar el archivo de datos para armar el grafo
		Reader grafo = new Reader("./data/Temp_GCUT16.txt");
		
		matrizAdyacencia = grafo.getMatrizAdyacencia();
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
						if(i==94 && j == 1)
						{
							System.out.println("si esta");
						}
					}
					if(i==94 && j == 1)
					{
						System.out.println("si esta");
					}
				}
			}
			model.update();
			
			//Crear las restricciones de balance
			GRBLinExpr balance;
			
			//Estas restricciones de balance tienen que ser para todos los nodos menos el nodo inicial y final
			for(int i=0;i<numNodosTotal;i++){	
				balance = new GRBLinExpr();
				for(int j=0;j<numNodosTotal;j++){
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
			for(int i =1; i<numNodosCorte;i++)
			{
				for(int j =i+1; j<numNodosCorte;j++){
					if(matrizAdyacencia[i][j]==1)
					{
						corteU = new GRBLinExpr();
						corteU.addTerm(1, model.getVarByName("x("+i+","+j+")"));
						corteU.addTerm(1, model.getVarByName("x("+j+","+i+")"));
						System.out.println(model.getVarByName("x("+i+","+j+")"));
						System.out.println(model.getVarByName("x("+j+","+i+")"));
						System.out.println(j+","+i);
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
				
				
				//Imprimir los resultados
				
				double objval = model.get(GRB.DoubleAttr.ObjVal);
				System.out.println("El tiempo total de corte es de: "+objval+" segundos");
				
				GRBVar[] vars=model.getVars();
				ArrayList<String> camino= new ArrayList();
				String anterior ="nodoInicial";
				boolean termino=false;
				int i=0;
				while(!termino){
					String n1=vars[i].get(GRB.StringAttr.VarName);
					String nombrex=n1.split("x")[1];
					String viaje=n1.split("(")[1];
					String inicio=viaje.split(",")[0];
					String destino=viaje.split(",")[1].split(")")[0];
					double valorX=vars[i].get(GRB.DoubleAttr.X);
					if(valorX>0 && inicio.equals(anterior)){
						camino.add(inicio);
						anterior = inicio;
						i=0;
						System.out.println(inicio+" >> "+destino +" km");
					}
					if(i==vars.length)
					{
						termino=true;
					}
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
	
}
