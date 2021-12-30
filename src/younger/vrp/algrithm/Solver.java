package younger.vrp.algrithm;

import younger.vrp.instance.Instance;
import younger.vrp.alns.config.VisualizationControl;
import younger.vrp.alns.config.IALNSConfig;
import younger.vrp.alns.config.VRPCategory;

public class Solver {

    private Instance instance;

    public Solver(Instance instance) {
        this.instance = instance;
    }

    public ALNSSolution getInitialSolution(VRPCategory cate) {
        GreedyVRP greedyVRP = new GreedyVRP(instance);
        return greedyVRP.getInitialSolution(cate);
    }

    public ALNSSolution improveSolution(ALNSSolution s, IALNSConfig ac, VisualizationControl vc)
            throws Exception {
        ALNSProcess ALNS = new ALNSProcess(s, instance, ac, vc);
        ALNSSolution sol = ALNS.improveSolution(vc);
        try {
            // is.exportResult(sol);
        } catch (Exception e) {
            System.out.println(e);
        }
        return sol;
    }

}
