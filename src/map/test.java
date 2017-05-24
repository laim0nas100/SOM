package map;

import java.io.File;
import java.util.ArrayList;
import map.SOM.ClassHitCounter;


/**
 *
 * @author Lemmin
 */
public class test {
    
    public static void clusterizeUseCase(){
        try {
            
            ArrayList<Vector> training = CSVparser.readFile(new File("irisTrain.arff"));
            int columnCount = training.get(0).size();
            Vector[] minmax = Vector.minmax(training);
            Vector globalMin = minmax[0];
            Vector globalMax = minmax[1];
            for(Vector v:training){
                v.normalize(globalMin, globalMax);
            }
            //must be quadratic number i.e. (4,9,16,25,...) for grid reprezentation
            SOM s = new SOM(columnCount,100);
//            s.trace = true;
//            s.single = false;
            while(s.iteration < 500 ){
                s.trainPick1(training);
            }
            if(s.gridSize<0){
                System.out.println("Grid is not in use, abort");
                return;
            }
            System.out.println("Input ammount:"+training.size());
            System.out.println("Errors");
            System.out.printf("Quantization:%05f\n",s.quantizationError(training));
            System.out.printf("Topological:%05f\n",s.topologicalError(training));
            
            //Clusterize table
            ClassHitCounter hit = new ClassHitCounter(3);
            //manualy set classes for inputs
            int class1 = 40;
            int class2 = 80;
            for(int i=0; i<training.size(); i++){
                Vector in = training.get(i);
                int nodeID = s.test(in);
                if(i<class1){
                    hit.hit(nodeID, 1);
                }else if(i<class2){
                    hit.hit(nodeID,2);
                }else{
                    hit.hit(nodeID, 3);
                }   
            }
            //print cluster table
            for(int i=0; i<s.nodes; i+= s.gridSize){
                for(int j=0; j<s.gridSize; j++){             
                    Integer resolved = hit.resolveClass(i+j);
                    if(resolved == null){
                        System.out.print("  ");
                    }else{
                        System.out.print(" "+resolved);
                    }
                }
                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void newClusterCaseUse(){
        try{
            ArrayList<Vector> training = CSVparser.readFile(new File("irisTrain.arff"));
            ArrayList<Vector> evaluate = CSVparser.readFile(new File("irisEvaluate.arff"));

            ArrayList<Vector> global = new ArrayList<>();
            global.addAll(training);
            global.addAll(evaluate);
            int columnCount = global.get(0).size();
            Vector[] minmax = Vector.minmax(global);
            Vector globalMin = minmax[0];
            Vector globalMax = minmax[1];
            for(Vector v:global){
                v.normalize(globalMin, globalMax);
//                System.out.println(v.toString());
            }
            //must be quadratic number i.e. (4,9,16,25,...) for grid reprezentation
            SOM s = new SOM(columnCount,100);
//            s.trace = true;
//            s.single = false;
            while(s.iteration < 500 ){
                s.trainPick1(training);
            }
            if(s.gridSize<0){
                System.out.println("Grid is not in use, abort");
                return;
            }

            System.out.println("Input ammount:"+training.size());
            System.out.println("Errors");
            System.out.printf("Quantization:%05f\n",s.quantizationError(training));
            System.out.printf("Topological:%05f\n",s.topologicalError(training));
            
            //Clusterize table
            ClassHitCounter hit = new ClassHitCounter(3);
            //manualy set classes
            int class1 = 10;
            int class2 = 20;
            for(int i=0; i<evaluate.size(); i++){
                Vector in = evaluate.get(i);
                int nodeID = s.test(in);
                if(i<class1){
                    hit.hit(nodeID, 1);
                }else if(i<class2){
                    hit.hit(nodeID,2);
                }else{
                    hit.hit(nodeID, 3);
                }   
            }
            //print table
            for(int i=0; i<s.nodes; i+= s.gridSize){
                for(int j=0; j<s.gridSize; j++){             
                    Integer resolved = hit.resolveClass(i+j);
                    if(resolved == null){
                        System.out.print("  ");
                    }else{
                        System.out.print(" "+resolved);
                    }
                }
                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void classifyCaseUse(){
        try {
            
            ArrayList<Vector> training = CSVparser.readFile(new File("irisTrain.arff"));
            ArrayList<Vector> evaluate = CSVparser.readFile(new File("irisEvaluate.arff"));

            ArrayList<Vector> global = new ArrayList<>();
            global.addAll(training);
            global.addAll(evaluate);
            int columnCount = global.get(0).size();
            Vector[] minmax = Vector.minmax(global);
            Vector globalMin = minmax[0];
            Vector globalMax = minmax[1];
            for(Vector v:training){
                v.normalize(globalMin, globalMax);
//                System.out.println(v.toString());
            }
            SOM s = new SOM(columnCount,3);
//            s.trace = true;
//            s.single = false;
            while(s.iteration < 1000 ){
                s.trainPick1(training);
            }
            System.out.println("Class only reprezents different cluster");
            for(Vector testSet:evaluate){
                int test = s.test(testSet.normalized(globalMin, globalMax));
                System.out.println("Class: " + test+" values:"+testSet.toString());// + ". Class: " + s.vectors.get(test).toString());
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        //pick one
        clusterizeUseCase();
//        newClusterCaseUse();
//        classifyCaseUse();
        
    }
}
