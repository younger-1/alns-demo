package younger.vrp.alns;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.algrithm.Solver;
import younger.vrp.alns.config.ALNSConfiguration;
import younger.vrp.alns.config.VisualizationControl;
import younger.vrp.base.Distance;
import younger.vrp.alns.config.IALNSConfig;
import younger.vrp.alns.config.VRPCategory;
import younger.vrp.instance.Instance;

/**
 * @author Xavier Young
 */
public class Main {
    public static void main(String args[]) {
        try {
            // Instance instance = new Instance("Homberger", "C1_2_1", 200);
            Instance instance = new Instance("Solomon", "r101", 100);
            // System.out.println(instance.getVehicleNr());
            // System.out.println(instance.getVehicleCapacity());
            solve(instance, ALNSConfiguration.Default, VisualizationControl.AllPic, VRPCategory.CVRP);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    // solve函数，输出解 输入变量：算例名，客户数，
    private static double solve(Instance instance, IALNSConfig ac, VisualizationControl vc, VRPCategory cate)
            throws Exception {
        cate.getCons().setVehicleNr(instance.getVehicleNr());
        cate.getCons().setVehicleCapacity(instance.getVehicleCapacity());
        if (cate.isTL()) {
            cate.getCons().setTimeLimit((int) instance.getCopyOfCustomers().get(0).getTW()[1]);
        } else {
            cate.getCons().setTimeLimit(99999);
        }
        cate.getCons().setMaxCustomerNum(99999);
        // System.out.println(cate.getCons().getTimeLimit());

        Distance.createInstance(instance.getCopyOfCustomers());

        Solver solver = new Solver(instance);

        // 初始解
        ALNSSolution is = solver.getInitialSolution(cate);
        // System.out.println(is);

        // 满意解
        ALNSSolution ims = solver.improveSolution(is, ac, vc);
        // System.out.println(ims);

        double result = ims.costs.getDist();

        return result;
    }
}
