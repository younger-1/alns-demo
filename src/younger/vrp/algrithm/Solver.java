package younger.vrp.algrithm;

import younger.vrp.instance.Instance;
import younger.vrp.alns.config.VisualizationControl;
import younger.vrp.alns.config.IALNSConfig;
import younger.vrp.alns.config.VRPCatatory;

public class Solver {

    public Solver() {
    }

    public Solution getInitialSolution(Instance instance, VRPCatatory cata) {
        GreedyVRP greedyVRP = new GreedyVRP(instance);
        return greedyVRP.getInitialSolution(cata);
    }

    public Solution improveSolution(Solution s, Instance is, IALNSConfig ac, VisualizationControl vc) throws Exception {
        MyALNSProcess ALNS = new MyALNSProcess(s, is, ac, vc);
        return ALNS.improveSolution(vc);
    }
}
