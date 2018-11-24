/** Starter code for SP10
 *  @author
 */

// change to your netid
package krt170130;

import rbk.Graph;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

public class DFS extends GraphAlgorithm<DFS.DFSVertex> {
    private int componentNum;

    enum color{
        WHITE,BLACK,GREY;
    }
    boolean isCyclic;
    int topNum;
    LinkedList<Vertex> finishList;
    public static class DFSVertex implements Factory {
        int cno;
        Vertex parent;
        color nodeColor;
        int top;
        DFSVertex(){

        }
        public DFSVertex(Vertex u) {
            this.cno = 0;
            this.parent = null;
            this.nodeColor = color.WHITE;
            this.top = 0;
        }
        public DFSVertex make(Vertex u) { return new DFSVertex(u); }
    }

    public DFS(Graph g) {
        super(g, new DFSVertex(null));
        initialize();
    }


    private void initialize() {
        this.finishList = new LinkedList<>();
        this.isCyclic = false;
        this.topNum = g.size();
        this.componentNum = 0;
    }

    // Depth Search On the vertex list.
    public void depthFirstSearch(Iterable <Vertex> iterable) {
        initialize();
        for (Vertex u: iterable) {
            if(get(u).nodeColor == color.WHITE){
                componentNum++;
                dfsVisit(u, componentNum);
            }
        }
    }

    // Function to find strongly Connected Components for Graph.
    public static DFS stronglyConnectedComponents(Graph g) {
        DFS d = new DFS(g);
        d.depthFirstSearch(g);
        List <Vertex> list = d.finishList;
        g.reverseGraph();
        d.changeColor(list);
        d.depthFirstSearch(list);
        g.reverseGraph();
        return d;
    }

    private void changeColor(List<Vertex> list) {
        for(Vertex u : list) {
            get(u).nodeColor = color.WHITE;
        }
    }


    // Member function to find topological order
    public List<Vertex> topologicalOrder1() {
        DFS d = new DFS(this.g);
        d.depthFirstSearch(this.g);
        return d.isCyclic ? null : d.finishList;
    }
    void dfsVisit(Vertex u, int cno){
        get(u).nodeColor = color.GREY;
        get(u).cno = cno;
        for(Edge e:g.outEdges(u)) {
            Vertex d2 = e.otherEnd(u);
            if(get(d2).nodeColor == color.GREY) {
                this.isCyclic = true;
            }
            else if(get(d2).nodeColor == color.WHITE) {
                get(d2).parent = u;
                dfsVisit(e.otherEnd(u), cno);
            }
        }
        get(u).nodeColor = color.BLACK;
        get(u).top = this.topNum--;
        this.finishList.addFirst(u);
    }



    // Find the number of connected components of the graph g by running dfs.
    // Enter the component number of each vertex u in u.cno.
    // Note that the graph g is available as a class field via GraphAlgorithm.
    public int connectedComponents() {
        return this.componentNum;
    }

    // After running the connected components algorithm, the component no of each vertex can be queried.
    public int cno(Vertex u) {
        return get(u).cno;
    }

    // Find topological oder of a DAG using DFS. Returns null if g is not a DAG.
    public static List<Vertex> topologicalOrder1(Graph g) {
        if(!g.isDirected()) return null;
        DFS d = new DFS(g);
        return d.topologicalOrder1();
    }

    // Find topological oder of a DAG using the second algorithm. Returns null if g is not a DAG.
    public static List<Vertex> topologicalOrder2(Graph g) {
        return null;
    }

    public static void main(String[] args) throws Exception {
        String string = "5 5   1 2 2   2 3 3   3 1 5   2 4 4   4 5 1 0";
        Scanner in;
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string);

        // Read graph from input
        Graph g = Graph.readGraph(in,true);
        g.printGraph(true);


        DFS d = DFS.stronglyConnectedComponents(g);
        if (d.finishList != null) {
            System.out.println("Number of Connected Components:"+ d.componentNum);
            for (Vertex v : d.finishList)
                System.out.println(v.getName()+":"+d.cno(v) + " ");

        }
    }
}