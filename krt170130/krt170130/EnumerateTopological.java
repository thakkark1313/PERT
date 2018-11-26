/* Starter code for enumerating topological orders of a DAG
 * @author
 */

package krt170130;
import rbk.Graph;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.Factory;

import java.util.HashMap;
import java.util.List;

/*
* Purpose
* Parameters
* Precondition
* PostCondition
* Return type
 */
public class EnumerateTopological extends GraphAlgorithm<EnumerateTopological.EnumVertex> {
    boolean print;  // Set to true to print array in visit
    long count;      // Number of permutations or combinations visited
    Selector sel; // Selector to decide whether to enumerate permutations with precedence constraints
    List <Vertex> finishList; // TO store topological order of a graph.
    HashMap<Vertex, Integer> inDegree;  // Storing In Degree for each vertex.
    public EnumerateTopological(Graph g) {
        super(g, new EnumVertex());
        print = false;
        count = 0;
        sel = new Selector();
        this.finishList = DFS.topologicalOrder1(g); // Finding topological order of the given DAG.
        this.inDegree = getInDegree(); // Setting up Indegree for each vertex
    }

    /*
     * Purpose To form inDegree for each vertex of the graph
     * Return  Returns HashMap of Vertex and it's indegree.
     */
    private HashMap<Vertex,Integer> getInDegree() {
        HashMap <Vertex, Integer> hm = new HashMap<>();
        for (Vertex v: g) {
            hm.put(v, v.inDegree());
        }
        return hm;
    }

    static class EnumVertex implements Factory {
        EnumVertex() { }
        public EnumVertex make(Vertex u) { return new EnumVertex();	}
    }

    class Selector extends Enumerate.Approver<Vertex> {
        /*
         * Purpose          To Check whether to select a vertex for permutation.
         * Parameters       Vertex to be checked
         * Return type      Returns value whether the vertex is selected or not.
         */
        @Override
        public boolean select(Vertex u) {
            if(inDegree.get(u) == 0) {
                for (Edge edge: g.outEdges(u)) {
                    inDegree.put(edge.otherEnd(u), inDegree.get(edge.otherEnd(u)) - 1);
                }
                return true;
            }
            return false;
        }

        @Override
        /*
         * Purpose          To unselect the vertex and do postprocessing.
         * Parameters       Vertex u
         */
        public void unselect(Vertex u) {
            for (Edge edge: g.outEdges(u)) {
                inDegree.put(edge.otherEnd(u), inDegree.get(edge.otherEnd(u)) + 1);
            }
        }

        @Override
        /*
         * Purpose          To print first k elements of the array.
         * Parameters       Array to be printed and the limit k.
         */
        public void visit(Vertex[] arr, int k) {
            count++;
            if(print) {
                for(Vertex u: arr) {
                    System.out.print(u + " ");
                }
                System.out.println();
            }
        }
    }


    // To do: LP4; return the number of topological orders of g

    /*
     * Purpose          Method to enumerate all the valid topological orders ot the graph.
     * Parameters       Flag to indicate whether to print the enumerations or not.
     * Return type      Returns the number of valid topological orders.
     */
    public long enumerateTopological(boolean flag) {
        if(this.finishList == null || this.finishList.size() == 0) {
            throw new IllegalArgumentException("Not a valid DAG");
        }
        print = flag;
        permute(this.finishList.size());
        return count;
    }

    /*
     * Purpose          To print first k elements of the array.
     * Parameters       Array to be printed and the limit k.
     */
    private void visit(List <Vertex> current) {
        if(this.print) {
            for (Vertex v: current) {
                System.out.print(v + " ");
            }
            System.out.println();
        }
        count++;
    }

    /*
     * Purpose          Permute the topological sequence
     * Parameters       integer denoting number of elements which can be possible permuted.
     */
    public void permute(int c) {  // To do for LP4
        if( c == 0 ) {
            visit(this.finishList);
            return;
        }
        int d = this.finishList.size() - c;
        if(this.sel.select(this.finishList.get(d))) {
            permute(c-1);
            this.sel.unselect(this.finishList.get(d));
        }
        for (int i=d+1; i<this.finishList.size();i++) {
            if(this.sel.select(this.finishList.get(i))) {
                swap(d, i);
                permute(c - 1);
                swap(d, i);
                this.sel.unselect(this.finishList.get(i));
            }
        }
    }

    /*
     * Purpose          Fucntion to swap the elements.
     * Parameters       Indices of the element that are to be swapped.
     */
    private void swap(int d, int i) {
        Vertex temp = this.finishList.get(d);
        this.finishList.set(d,  this.finishList.get(i));
        this.finishList.set(i, temp);
    }


    //-------------------static methods----------------------

    public static long countTopologicalOrders(Graph g) {
        EnumerateTopological et = new EnumerateTopological(g);
        return et.enumerateTopological(false);
    }

    public static long enumerateTopologicalOrders(Graph g) {
        EnumerateTopological et = new EnumerateTopological(g);
        return et.enumerateTopological(true);
    }

    public static void main(String[] args) {
        int VERBOSE = 0;
        if(args.length > 0) { VERBOSE = Integer.parseInt(args[0]); }
        Graph g = Graph.readDirectedGraph(new java.util.Scanner(System.in));
        Graph.Timer t = new Graph.Timer();
        long result;
        if(VERBOSE > 0) {
            result = enumerateTopologicalOrders(g);
        } else {
            result = countTopologicalOrders(g);
        }
        System.out.println("\n" + result + "\n" + t.end());
    }

}
