package Estructuras;

import gurobi.*;

/**
 * Proyecto 2 Flujo en redes 2018-20
 * @author Nicolás Hernández, Nicolás Vásquez 
 */
public class Model {

	private static int numNodosCorte;
	private static int numNodosTotal;
	
	private static int[][] matrizAdyacencia;
	private static int numEsquinas;
	
	public static void main(String[] args){
		
		//TODO: Cargar el archivo de datos para armar el grafo
		Reader leer = new Reader("src/RMC.txt");
		
		Grafo migrafo;
		migrafo=leer.getG();
		
		//Crear entorno para gurobi
		GRBEnv env;
		
		
		try{
			
			env = new GRBEnv(null);
			GRBModel model = new GRBModel(env);
			
			//Crear las variables binarias 
			
			for(int i=0;i<migrafo.getArcos().size();i++){
				model.addVar(0,1,migrafo.getArcos().get(i).getCost(),GRB.BINARY,"x("+migrafo.getArcos().get(i).getTail()+","+migrafo.getArcos().get(i).getHead()+")");
			}
			model.update();
			
			
			//Crear las restricciones de balance
			GRBLinExpr balance;
			
			//TODO: Estas restricciones de balance tienen que ser para todos los nodos menos el nodo inicial y final
			for(int i=1;i<migrafo.getNodos().size();i++){	
				balance = new GRBLinExpr();
				for(int j=0;j<migrafo.getArcos().size();j++){
					if(migrafo.getArcos().get(j).getTail()==migrafo.getNodos().get(i)){
						balance.addTerm(1, model.getVarByName("x("+migrafo.getArcos().get(j).getTail()+","+migrafo.getArcos().get(j).getHead()+")"));
					}
					if(migrafo.getArcos().get(j).getHead()==migrafo.getNodos().get(i)){
						balance.addTerm(-1, model.getVarByName("x("+migrafo.getArcos().get(j).getTail()+","+migrafo.getArcos().get(j).getHead()+")"));
					}
				}
				
				if(i!=0 && i<numNodosTotal-1){
					model.addConstr(balance,GRB.EQUAL,0,"Balance nodo"+i);
				} else if(i==0){
					model.addConstr(balance, GRB.EQUAL, 1,"Balance_"+i);
				}
				else {
					model.addConstr(balance, GRB.EQUAL, -1,"Balance_"+i);
				}
			}
			
			//Crear restriccion para cortar una sola vez por cada arco
			GRBLinExpr corteU;
			for(int i =1; i<numNodosCorte;i++)
			{
				for(int j =i+1; j<numNodosCorte;j++){
					corteU = new GRBLinExpr();
					corteU.addTerm(1, model.getVarByName("x("+migrafo.getArcos().get(j).getTail()+","+migrafo.getArcos().get(j).getHead()+")"));
					corteU.addTerm(1, model.getVarByName("x("+migrafo.getArcos().get(j).getHead()+","+migrafo.getArcos().get(j).getTail()+")"));
					model.addConstr(corteU, GRB.EQUAL, 1, "Corte Unico"+i+j);
				}
			}
			
						
			/*
			 * 1: Minimización
			 * -1: Maximización
			 */
				model.set(GRB.IntAttr.ModelSense, 1);
				
				model.update();
				
				model.write("EjemploRMC.lp");
				
				model.optimize();
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
