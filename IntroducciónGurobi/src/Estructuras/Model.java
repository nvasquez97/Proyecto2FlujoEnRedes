package Estructuras;

/*
 * Importar de gurobi..
 * 
 * Este error se soluciona al seguir los pasos explicados anteriormente.
 */
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;

/*
 * Optimización Avanzada - 201810
 * 
 * Clase modelo de optimización RMC.
 * 
 */
public class Model {

	/*
	 * Metodo principal. 
	 * 
	 * Al correr la clase, desarrolla todo el modelo.
	 * 
	 */
	public static void main(String[] args){
		
		/*
		 * Recuperar el grafo, teniendo en cuenta la lectura de parámetros.
		 */
		Reader leer = new Reader("src/RMC.txt");
		Grafo migrafo;
		migrafo=leer.getG();
		
		/*
		 * TO-DO: Crear un nuevo ambiente de gurobi.
		 * 
		 * Para esto se utiliza el comando GRBEnv
		 */
		GRBEnv env;
		
		
		try{
			
			env = new GRBEnv(null);
			
			/*
			 * TO-DO: Crear el modelo
			 * 
			 * Para esto se utiliza el comando GRBModel
			 */
			GRBModel model = new GRBModel(env);
			
			//Crear las variables
			
			for(int i=0;i<migrafo.getArcos().size();i++){
				/*
				 * TO-DO Crear las variables.
				 * 
				 * Para esto se utiliza el comando model.addVar
				 * 
				 *model.addVar(0,1,migrafo.getArcos().get(i).getCost(),GRB.CONTINUOUS,"x("+migrafo.getArcos().get(i).getTail()+","+migrafo.getArcos().get(i).getHead()+")"); 
				 */
				model.addVar(0,1,migrafo.getArcos().get(i).getCost(),GRB.CONTINUOUS,"x("+migrafo.getArcos().get(i).getTail()+","+migrafo.getArcos().get(i).getHead()+")");
			}
			
			/*
			 * TO-DO: Actualizar el modelo.
			 * 
			 * Para esto se utiliza el comando update
			 */
			model.update();
			
			
			//Crear las restricciones
			for(int i=0;i<migrafo.getNodos().size();i++){
				/*
				 * TO-DO: Crear una expresión lineal.
				 * 
				 * Para esto se utiliza el comando GRBLinExpr
				 */
				
				GRBLinExpr balance = new GRBLinExpr();
				for(int j=0;j<migrafo.getArcos().size();j++){
					if(migrafo.getArcos().get(j).getTail()==migrafo.getNodos().get(i)){
						balance.addTerm(1, model.getVarByName("x("+migrafo.getArcos().get(j).getTail()+","+migrafo.getArcos().get(j).getHead()+")"));
					}
					if(migrafo.getArcos().get(j).getHead()==migrafo.getNodos().get(i)){
						balance.addTerm(-1, model.getVarByName("x("+migrafo.getArcos().get(j).getTail()+","+migrafo.getArcos().get(j).getHead()+")"));
					}
				}
				
				/*
				 * TO-DO: Agregar las restricciones de balance
				 * 
				 * Para esto se utiliza el comando addConstr
				 */
				
				if(i!=0 && i!=6){
					model.addConstr(balance,GRB.EQUAL,0,"Balance_"+i);
				} else if(i==0){
					model.addConstr(balance, GRB.EQUAL, 1,"Balance_"+i);
				}
				else {
					model.addConstr(balance, GRB.EQUAL, -1,"Balance_"+i);
				}
			}
			
						
			/*
			 * TO-DO: dar el sentido del modelo.
			 * 
			 * Para esto se utiliza el comando set(GRB.IntAttr.ModelSense, ---);
			 * 1: Minimización
			 * -1: Maximización
			 */
				model.set(GRB.IntAttr.ModelSense, 1);
				
			/*
			 * TO-DO: Actualizar el modelo.
			 */
				model.update();
				
				model.write("EjemploRMC.lp");
				
				/*
				 * TO-DO: Optimizar!!!!!!!!!!!
				 */
				model.optimize();
			}
			catch(GRBException e){
				e.printStackTrace();
			}
		}
	
}
