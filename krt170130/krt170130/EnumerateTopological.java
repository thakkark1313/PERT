/* Starter code for enumerating topological orders of a DAG
 * @author
 */

package krt170130;
import rbk.Graph;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Timer;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.Factory;

import java.util.HashSet;
import java.util.List;

public class EnumerateTopological extends GraphAlgorithm<EnumerateTopological.EnumVertex> {
    boolean print;  // Set to true to print array in visit
    long count;      // Number of permutations or combinations visited
    Selector sel;
    List <Vertex> finishList;
    HashSet<Vertex> visited;
    public EnumerateTopological(Graph g) {
        super(g, new EnumVertex());
        print = false;
        count = 0;
        sel = new Selector();
        this.finishList = DFS.topologicalOrder1(g);
        this.visited = new HashSet<>();
        System.out.println(this.finishList);
    }

    static class EnumVertex implements Factory {
        EnumVertex() { }
        public EnumVertex make(Vertex u) { return new EnumVertex();	}
    }

    class Selector extends Enumerate.Approver<Vertex> {
        @Override
        public boolean select(Vertex u) {
            for (Edge edge: g.outEdges(u)) {
                if(visited.contains(edge.otherEnd(u))) return false;
            }
            visited.add(u);
            return true;
        }

        @Override
        public void unselect(Vertex u) {
            visited.remove(u);
        }

        @Override
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
    public long enumerateTopological(boolean flag) {
        print = flag;
        permute(this.finishList.size());
        return count;
    }
    private void visit(List <Vertex> current) {
        if(!this.print) {
            for (Vertex v: current) {
                System.out.print(v + " ");
            }
            System.out.println();
        }
        count++;
    }


    public void permute(int c) {  // To do for LP4
        if( c == 0 ) {
            visit(this.finishList);
            return;
        }
        int d = this.finishList.size() - c;
        permute(c-1);
        for (int i=d+1; i<this.finishList.size();i++) {
            swap(d, i);
            permute(c-1);
            swap(d, i);
        }
    }

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
