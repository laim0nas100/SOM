/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Lemmin
 */
public class SOM {
    public class PosDetails{
        public int top = -1;
        public int bot = -1;
        public int left = -1;
        public int right = -1;
    }
    public ArrayList<PosDetails> pos = new ArrayList<>();
    public double[][] neuronXY;
    public int iteration = 0;
    public int dim;
    public int nodes;
    public double decay = 1;
    public double decayRate = 0.98;
    public HashMap<Integer,Vector> vectors = new HashMap<>();
    public boolean single = false;
    public boolean trace = false;
    private int gridSize = -1;
    public static Random rnd = new Random();
    public SOM(int dimension, int nodeCount){
        int potGridSize = (int)Math.floor(Math.sqrt(nodeCount));
        if(nodeCount == potGridSize*potGridSize){
            this.gridSize = potGridSize;
        }
        this.nodes = nodeCount;
        this.dim = dimension;
        this.neuronXY = new double[nodeCount][2];
        double nodeSpace = 0;
        for(int i = 0; i < nodeCount; i++){
            vectors.put(i,Vector.getRandom(dimension));
            
            double x = 0.5 + 0.5 * Math.cos(nodeSpace);
            double y = 0.5 + 0.5 * Math.sin(nodeSpace);
            nodeSpace += Math.PI * 2.0 / (double)nodeCount;
            neuronXY[i][0] = x;
            neuronXY[i][1] = y;
        }
        
    }
    
    public int resolve(int index,int type, int gridSize){
        int result = -1;
        switch(type){
            case 0://top
                result = index - gridSize;
                break;
            case 1://bot
                result = index + gridSize;
                break;
            case 2://left
                result = index - 1;
                break;
            case 3://right
                result = index + 1;
                break;
        }
        if(result < 0 || result >= this.nodes){
            return -1;
        }
        return result;
        
    }    
    
    public double distance(Vector input, Vector node){
        double sum = 0;
        for(int i = 0; i < input.size(); i++){
            sum += Math.pow(input.get(i) - node.get(i), 2);
        }
        return Math.sqrt(sum);
    }

    
    
    public void trainWithAll(List<Vector> input){
        for(Vector v:input){
            train(v);
        }
        
    }
    
    public void trainPick1(List<Vector> input){
        int nextInt = rnd.nextInt(input.size());
        train(input.get(nextInt));
    }
    public void debug(String s){
        if(trace){
            System.out.println(s);
        }
    }
    public void train(Vector input){
        decay *= decayRate;
        iteration++;
        double smallestDistance = Double.MAX_VALUE;
        int smallestIndex = 0;
        
        for(int j = 0; j < this.nodes; j++){
            double tryNewDist = this.distance(input, this.vectors.get(j));
            if(smallestDistance > tryNewDist){
                smallestIndex = j;
                smallestDistance = tryNewDist;
            }
        }
        String t = "\nIteration:"+this.iteration+" decay:"+this.decay + " winner:" +smallestIndex;
        // update winner
        Vector winnerNode = this.vectors.get(smallestIndex);
        updateWeights(winnerNode,input,1);
        
        // update the rest
        if(!single){
            if(gridSize <= 0){
                for(Vector v:resolveNeighbours(smallestIndex)){
                    updateWeights(v,input,0.9);
                }  
            }
            else{
                HashSet<Integer> set = new HashSet<>();
                set.add(smallestIndex);
                recursiveResolve(set,smallestIndex,(int) Math.round(decay * this.gridSize));
                set.remove(smallestIndex);
                
                t += "\n"+smallestIndex + " Update set"+set.toString();
                
                for(Integer i:set){
                    int dist = this.topologicalDistance(i, smallestIndex, this.gridSize);
                    
                    if(dist > 0 && dist < this.gridSize){
                        double distMult = 1 / (double)dist;
                        t+="\n"+i+" Top dist:"+dist +" Dist multi:"+distMult;
                        updateWeights(vectors.get(i),input,distMult);
                    }
                    
                }
            }
            
        }
        for(Vector v:this.vectors.values()){
            t += "\n"+v.toString();
        }
        debug(t);  
        
  
    }
    
    
    public int test(Vector input){
        int smallestIndex = 0;
        double minValue = Double.MAX_VALUE;
        for(Map.Entry<Integer, Vector> v :vectors.entrySet()){
            double tryNewDist = distance(input,v.getValue());
            if(minValue > tryNewDist){
                smallestIndex = v.getKey();
                minValue = tryNewDist;
            }
        }
        return smallestIndex;
    }
    public int topologicalDistance(int first,int second, int gridSize){
        if(first > second){
            int t = first;
            first = second;
            second = t;
        }
        int y = 0;
        int x = 0;
        while(first < second){
            first += gridSize;
            y++;
        }
        while(first < second){
            first++;
            x++;
        }
        return x + y;
        
        
    }
    public void recursiveResolve(HashSet<Integer> set,int index, int iteration){
        if(iteration <= 0){
            return;
        }
        ArrayList<Integer> thisIteration = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            int n = resolve(index,i,this.gridSize);
            if(n >= 0){
                if(!set.contains(n)){
                    thisIteration.add(n);
                }
            }
        }
        set.addAll(thisIteration);
        for(Integer i:thisIteration){
            recursiveResolve(set,i,iteration - 1);
        }
    }
    public ArrayList<Vector> resolveNeighbours(int index){
        
        ArrayList<Vector> list = new ArrayList<>();
        int size = this.vectors.size();
        int last = size - 1;
        if(size < 2){
            return list;
        }
        if(gridSize > 0){
            for(int i = 0; i < 4; i++){
            int n = resolve(index,i,this.gridSize);
            if(n >= 0){
                list.add(this.vectors.get(n));
            }
            
        }
        }else{
            if(index == 0){
                list.add(this.vectors.get(index + 1));
            }
            else if(index == last){
                list.add(vectors.get(last - 1));
            }
            else{
                list.add(vectors.get(index - 1));
                list.add(vectors.get(index + 1));
            } 
        }
        return list;
    }
    public void updateWeights(Vector node,Vector input,double distanceMultiplier){
        for(int j = 0; j < this.dim; j++){
            Double w = node.get(j);
            double diff = distanceMultiplier * decay * (input.get(j) - w);
            w += diff;
            node.weights.set(j, w);
        }
    }
    
    public double quantizationError(List<Vector> input){
        double sum = 0;
        for(Vector v:input){
            sum += distance(this.vectors.get(this.test(v)),v);
        }
        return sum / (double) this.nodes;
    }
    
    public double topologicalError(List<Vector> input){
        return 0;
    }
    
    
}
