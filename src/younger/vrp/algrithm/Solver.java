package younger.vrp.algrithm;

import younger.vrp.instance.Instance;
import younger.vrp.alns.config.VisualizationControl;
import younger.vrp.alns.config.IALNSConfig;
import younger.vrp.alns.config.VRPCatatory;

public class Solver {

    public Solver() {
    }

    public Solution getInitialSolution(Instance instance) {
        GreedyVRP greedyVRP = new GreedyVRP(instance);
        return greedyVRP.getInitialSolution();
    }

    public Solution improveSolution(Solution s, Instance is, IALNSConfig ac, VisualizationControl vc, VRPCatatory cata)
            throws Exception {
        MyALNSProcess ALNS = new MyALNSProcess(s, is, ac, vc);
        return ALNS.improveSolution(vc);
    }
}
