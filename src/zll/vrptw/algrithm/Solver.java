package zll.vrptw.algrithm;

import zll.vrptw.instance.Instance;
import zll.vrptw.alns.config.VisualizationControl;
import zll.vrptw.alns.config.IALNSConfig;

public class Solver {

    public Solver() {
    }

    public Solution getInitialSolution(Instance instance) {
        GreedyVRP greedyVRP = new GreedyVRP(instance);
        return greedyVRP.getInitialSolution();
    }

    public Solution improveSolution(Solution s, IALNSConfig ac, VisualizationControl vc, Instance is) throws Exception {
        MyALNSProcess ALNS = new MyALNSProcess(s, is, ac, vc);
        return ALNS.improveSolution(vc);
    }
}
