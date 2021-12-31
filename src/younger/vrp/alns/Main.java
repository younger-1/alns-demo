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
			Instance instance = new Instance("Homberger", "RC2_2_1", 200);
			solve(instance, ALNSConfiguration.Default, VisualizationControl.AllPic, VRPCategory.OVRP);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    // solve函数，输出解 输入变量：算例名，客户数，
	private static double solve(Instance instance, IALNSConfig ac, VisualizationControl vc, VRPCategory cate)
			throws Exception {
        cate.getCons().setVehicleNr(instance.getVehicleNr());
        cate.getCons().setVehicleCapacity(instance.getVehicleCapacity());

        // 检查结果
        // CheckSolution checkSolution = new CheckSolution(instance);

		Distance.createInstance(instance.getCopyOfCustomers());

		// 解决策略
		Solver solver = new Solver(instance);

        // 初始解
		ALNSSolution is = solver.getInitialSolution(cate);
        System.out.println(is);
        // System.out.println(checkSolution.Check(is, cate));

        // 满意解
		ALNSSolution ims = solver.improveSolution(is, ac, vc);
        System.out.println(ims);
        // System.out.println(checkSolution.Check(ims, cate));

        double result = ims.costs.getDist();

        return result;
    }
}
