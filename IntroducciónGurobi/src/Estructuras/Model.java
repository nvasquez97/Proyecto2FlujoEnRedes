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
		
		Reader grafo = new Reader("./data/Temp_GCUT4.txt");
		
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
				ArrayList<GRBVar> soluciones = new ArrayList<GRBVar>();
				
				for(int j=0; j<vars.length;j++)
				{
					String n1=vars[j].get(GRB.StringAttr.VarName);
					String viaje=n1.split("\\(")[1];
					String inicio=viaje.split(",")[0];
					String destino=viaje.split(",")[1].split("\\)")[0];
					String corteOno="";
					double valorX=vars[j].get(GRB.DoubleAttr.X);
					if(valorX>0 ){
						soluciones.add(vars[j]);
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
				}
				ArrayList<String> camino = new ArrayList<String>();
				camino=construirCamino(soluciones, 0, camino); 
				int anterior=0;
				for(int i =1;i<camino.size();i++)
				{
					System.out.println(camino.get(anterior)+" >> "+camino.get(i));
					anterior=i;
				}
				
				
				System.out.println("SE DEMORA EN CORRER TODO "+ (System.currentTimeMillis()-tiempoMilis)+" milisegundos");
				
				System.out.println(soluciones.size()+" , "+camino.size());
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
	
	public static ArrayList<String> construirCamino(ArrayList<GRBVar> camino, int posInicial, ArrayList<String> caminoActual)
	{
		ArrayList<String> caminoRet = new ArrayList<String>();
		boolean termino = false;
		int i=1;
		try
		{
			String n1I = camino.get(posInicial).get(GRB.StringAttr.VarName);
			String viajeI=n1I.split("\\(")[1];
			String inicioI=viajeI.split(",")[0];
			String destinoI=viajeI.split(",")[1].split("\\)")[0];
			String anterior = destinoI;
			if(caminoActual.size() == 0 )
			{
				caminoRet.add(inicioI);
				caminoRet.add(destinoI);
			}
			else
			{
				caminoRet.addAll(caminoActual);
				caminoRet.add(destinoI);
			}
			String n1;
			String viaje;
			String inicio;
			String destino;
			String corteOno;
			String nuevoIni;
			String nuevoFin;
			while(!termino)
			{
				n1 = camino.get(i).get(GRB.StringAttr.VarName);
				viaje=n1.split("\\(")[1];
				inicio=viaje.split(",")[0];
				destino=viaje.split(",")[1].split("\\)")[0];
				corteOno="";
				
				if(inicio.equals(anterior) &&!buscarEnCamino(caminoRet, inicio, destino))
				{
					if(destino.equals(inicioI))
					{
						caminoRet.add(destino);
						return caminoRet;
					}
					else
					{
						if(i<camino.size()-1)
						{
							n1 = camino.get(i+1).get(GRB.StringAttr.VarName);
							
							viaje=n1.split("\\(")[1];
							nuevoIni=viaje.split(",")[0];
							nuevoFin =viaje.split(",")[1].split("\\)")[0];
							boolean esta=false;
							if(nuevoIni.equals(inicio))
							{ 
								ArrayList<String> nuevoC= construirCamino(camino, i, caminoRet);
								if(nuevoC.get(nuevoC.size()-1).equals(inicio))
								{
									caminoRet = nuevoC;
									anterior = nuevoFin;
									caminoRet.add(nuevoFin);
									i=0;
									esta=true;
								}
								else
								{
									for(int j =caminoRet.size();j<nuevoC.size()-1 && !esta;j++)
									{
										if(nuevoC.get(j).equals(inicio))
										{
											caminoRet = nuevoC;
											anterior = nuevoC.get(nuevoC.size()-1);
											i=0;
											esta=true;
										}
									}
									if(!esta)
									{
										nuevoC= construirCamino(camino, i+1, caminoRet);
										if(nuevoC.get(nuevoC.size()-1).equals(inicio))
										{
											caminoRet=nuevoC;
											anterior = destino;
											caminoRet.add(destino);
											i=0;
											esta=true;
										}
										for(int j =caminoRet.size();j<nuevoC.size()-1 && !esta;j++)
										{
											if(nuevoC.get(j).equals(inicio))
											{
												caminoRet = nuevoC;
												anterior = nuevoC.get(nuevoC.size()-1);
												i=0;
												esta=true;
											}
										}
									}
								}
								
							}
							else
							{
								caminoRet.add(destino);
								anterior = destino;
								i=0;
							}
						}
						else
						{
							caminoRet.add(destino);
							anterior = destino;
							i=0;
						}
					}
					
				}
				i++;
				if(i==camino.size())
				{
					termino=true;
				}
			}
		}
		catch(GRBException e)
		{
			e.printStackTrace();
		}
		
		return caminoRet;
		
	}
	
}
