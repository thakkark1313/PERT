/* Driver code for PERT algorithm (LP4)
 * @author
 */

// change package to your netid
package krt170130;

import rbk.Graph;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;

import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {
    List<Vertex> finishList;
    public static class PERTVertex implements Factory {
        int lc;
        int ec;
        int slack;
        int duration;
        public PERTVertex(Vertex u) {
            this.lc = 0;
            this.ec = 0;
            this.slack = 0;
            this.duration = 0;
        }
        public PERTVertex make(Vertex u) { return new PERTVertex(u); }
    }

    public PERT(Graph g) {
        super(g, new PERTVertex(null));
        for (Vertex u: g) {
            if(u.getIndex() != 0)
            g.addEdge(0, u.getIndex(), 1);
            if(u.getIndex() != 0 && u.getIndex() != g.size() - 1)
                g.addEdge(u.getIndex(), g.size() - 1, 1);
        }
        g.printGraph(false);
        this.finishList = DFS.topologicalOrder1(g);
    }

    public void setDuration(Vertex u, int d) {
        get(u).duration = d;
    }

    public boolean pert() {
        if(this.finishList == null)
            return true;
        System.out.println(this.finishList);
        for (Vertex u: this.finishList) {
            PERTVertex pu = get(u);
            for (Edge edge: g.outEdges(u)) {
                PERTVertex pv  = get(edge.otherEnd(u));
                if(pu.ec + pv.duration > pv.ec) {
                    pv.ec = pu.ec + pv.duration;
                }
            }
        }
        int maxTime = get(this.finishList.get(this.finishList.size() - 1)).ec;
        for (Vertex u: g) {
            PERTVertex pu = get(u);
            pu.lc = maxTime;
        }
        ListIterator li = this.finishList.listIterator(this.finishList.size());

        while(li.hasPrevious()) {
            Vertex v = (Vertex) li.previous();
            PERTVertex pv = get(v);
            for (Edge edge: g.inEdges(v)) {
                PERTVertex pu = get(edge.otherEnd(v));
                if(pv.lc - pv.duration < pu.lc) {
                    pu.lc = pv.lc - pv.duration;
                }
                pu.slack = pu.lc - pu.ec;
            }
        }

        return false;
    }
    public int ec(Vertex u) {
        return get(u).ec;
    }

    public int lc(Vertex u) {
        return get(u).lc;
    }

    public int slack(Vertex u) {
        return get(u).slack;
    }

    private int calculateCritical() {
        int criticalNodes  = 0;
        for (Vertex u: g) {
            if(get(u).slack == 0)
                criticalNodes++;
        }
        return criticalNodes;
    }
    public int criticalPath() {
        return calculateCritical() + 1;
    }

    public boolean critical(Vertex u) {
        return get(u).slack == 0;
    }

    public int numCritical() {
        return calculateCritical();
    }

    // setDuration(u, duration[u.getIndex()]);
    public static PERT pert(Graph g, int[] duration) {
        PERT p = new PERT(g);
        for (Vertex u: g) {
            p.setDuration(u, duration[u.getIndex()]);
        }
        return p.pert() ? null : p;
    }

    public static void main(String[] args) throws Exception {
        String graph = "11 12   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1 0";
        Scanner in;
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
        Graph g = Graph.readDirectedGraph(in);
        g.printGraph(false);

        PERT p = new PERT(g);
        for(Vertex u: g) {
            p.setDuration(u, in.nextInt());
        }
        // Run PERT algorithm.  Returns null if g is not a DAG
        if(p.pert()) {
            System.out.println("Invalid graph: not a DAG");
        } else {
            System.out.println("Number of critical vertices: " + p.numCritical());
            System.out.println("u\tEC\tLC\tSlack\tCritical");
            for(Vertex u: g) {
                System.out.println(u + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u));
            }
        }
    }
}