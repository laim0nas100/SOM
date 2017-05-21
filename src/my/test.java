/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


/**
 *
 * @author Lemmin
 */
public class test {
    public static void main(String[] args){
        try {
            
            ArrayList<Vector> training = CSVparser.readFile(new File("irisTrain.arff"));
            ArrayList<Vector> evaluate = CSVparser.readFile(new File("irisEvaluate.arff"));

            ArrayList<Vector> global = new ArrayList<>();
            global.addAll(training);
            global.addAll(evaluate);
            
            Vector[] minmax = Vector.minmax(global);
            Vector globalMin = minmax[0];
            Vector globalMax = minmax[1];
            for(Vector v:training){
                v.normalize(globalMin, globalMax);
//                System.out.println(v.toString());
            }
            SOM s = new SOM(4,16);
            s.trace = true;
            s.single = false;
//            for(int i = 0; i < s.neuronXY.length; i++){
//                System.out.println(s.neuronXY[i][0] + ","+s.neuronXY[i][1]);
//            }
            while(s.iteration < 500 ){
                s.trainPick1(training);
            }

            for(Vector testSet:evaluate){
                int test = s.test(testSet.normalized(globalMin, globalMax));
                System.out.println("Class: " + test+" "+testSet.toString());// + ". Class: " + s.vectors.get(test).toString());
            }
            
            for(Vector v :s.vectors.values()){
//                System.out.println(v.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
