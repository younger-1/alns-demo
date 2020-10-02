package younger.vrp.alns;

import younger.vrp.algrithm.CheckSolution;
import younger.vrp.algrithm.Solution;
import younger.vrp.algrithm.Solver;
import younger.vrp.alns.config.ALNSConfiguration;
import younger.vrp.alns.config.VisualizationControl;
import younger.vrp.alns.config.IALNSConfig;
import younger.vrp.instance.Instance;

/**
 * @author Xavier Young
 */
public class Main {

	public static final String[] SOLOMON_ALL = new String[] { "C101", "C102", "C103", "C104", "C105", "C106", "C107",
			"C108", "C109", "C201", "C202", "C203", "C204", "C205", "C206", "C207", "C208", "R101", "R102", "R103",
			"R104", "R105", "R106", "R107", "R108", "R109", "R110", "R111", "R112", "R201", "R202", "R203", "R204",
			"R205", "R206", "R207", "R208", "R209", "R210", "R211", "RC101", "RC102", "RC103", "RC104", "RC105",
			"RC106", "RC107", "RC108", "RC201", "RC202", "RC203", "RC204", "RC205", "RC206", "RC207", "RC208" };
	static String[] SOLOMON_CLUSTERED = new String[] { "C101", "C102", "C103", "C104", "C105", "C106", "C107", "C108",
			"C109", "C201", "C202", "C203", "C204", "C205", "C206", "C207", "C208" };
	static String[] SOLOMON_RANDOM = new String[] { "R101", "R102", "R103", "R104", "R105", "R106", "R107", "R108",
			"R109", "R110", "R111", "R112", "R201", "R202", "R203", "R204", "R205", "R206", "R207", "R208", "R209",
			"R210", "R211", };
	static String[] SOLOMON_CLUSTERRANDOM = new String[] { "RC101", "RC102", "RC103", "RC104", "RC105", "RC106",
			"RC107", "RC108", "RC201", "RC202", "RC203", "RC204", "RC205", "RC206", "RC207", "RC208" };
	static String[] VRPFD_INSTANCES = new String[] { "C108", "C206", "C203", "R202", "R207", "R104", "RC202", "RC205",
			"RC208" };
	static String[] Homberger_200 = new String[] { "C1_2_1", "C1_2_2", "C1_2_3", "C1_2_4" };
	static String[] Homberger_400 = new String[] { "C1_4_1", "C1_4_2", "C1_4_3", "C1_4_4" };
	static String[] instances = { "C1_4_1", "C1_4_2", "C1_4_3" };

	public static void main(String args[]) {

		try {
			solve("RC2_2_1", // 需要测试的算例
					"Homberger", // 算例类型,输入 Homberger 或 Solomon ，注意大写
					200, // 客户点数量，Solomon可选择 25,50,100，Homberger可选择200，400
					ALNSConfiguration.DEFAULT, // ALNS相关参数
					// new VisualizationControl(false, true, false, false));
					new VisualizationControl(true, true, true, true));
			// new VisualizationControl());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	// solve函数，输出解 输入变量：算例名，客户数，
	private static double[] solve(String name, String instanceType, int size, IALNSConfig c, VisualizationControl cp)
			throws Exception {

		// 输入Solomon算例
		Instance instance = new Instance(size, name, instanceType);
		instance.setVehicleNr(150);
		// 检查结果
		CheckSolution checkSolution = new CheckSolution(instance);
		// 解决策略
		Solver solver = new Solver();
		// 初始解
		Solution is = solver.getInitialSolution(instance);
		// System.out.println(is);
		System.out.println(checkSolution.Check(is));
		// 满意解
		Solution ims = solver.improveSolution(is, c, cp, instance);
		System.out.println(ims);
		System.out.println(checkSolution.Check(ims));
		System.out.println("ALNS progress cost " + ims.testTime + "s.");
		double[] result = new double[] { ims.getTotalCost(), ims.testTime };

		return result;
	}
}
