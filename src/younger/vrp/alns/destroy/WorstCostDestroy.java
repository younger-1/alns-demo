package younger.vrp.alns.destroy;

import java.util.ArrayList;
import java.util.Arrays;

import younger.vrp.algrithm.MyALNSSolution;
import younger.vrp.alns.operation.ALNSAbstractOperation;
import younger.vrp.instance.Route;
import younger.vrp.instance.Instance;

public class WorstCostDestroy extends ALNSAbstractOperation implements IALNSDestroy {
	/*
	 * @Override public ALNSStrategieVisualizationManager getVisualizationManager()
	 * { // TODO Auto-generated method stub return null; }
	 */
	@Override
	public MyALNSSolution destroy(MyALNSSolution s, int removeNr) throws Exception {

		if (s.removalCustomers.size() != 0) {
			System.err.println("removalCustomers is not empty.");
			return s;
		}

		// !new
		ArrayList<Fitness> customList = new ArrayList<>();
		for (int j = 0; j < s.routes.size(); j++) {
			for (int i = 1; i < s.routes.get(j).getRoute().size() - 1; ++i) {
				customList.add(Fitness.calculateFitness(s.instance, s.routes.get(j), j, i));
			}
		}

		// ! 记录 customList 中 fitness 最大的前 removeNr 个的序号，按序号从大到小排列
		// removeList 里放 customList 中 fitness 最大的前 removeNr 个的下标
		// 每次外循环替换掉 removeList 里 fitness 最小的下标

		int[] removeList = new int[removeNr];
		for (int i = 0; i < removeNr; i++) {
			removeList[i] = i;
		}

		for (int i = removeNr; i < customList.size(); i++) {
			double max_cost_diff = 0;
			int max_cost_diff_index = 0;
			boolean is_custom_into_removeList = false;
			for (int j = 0; j < removeNr; j++) {
				double tmp = customList.get(i).fitness - customList.get(removeList[j]).fitness;
				if (tmp > 0) {
					is_custom_into_removeList = true;
					if (tmp > max_cost_diff) {
						max_cost_diff = tmp;
						max_cost_diff_index = j;
					}
				}
			}
			if (is_custom_into_removeList)
				removeList[max_cost_diff_index] = i;
		}

		// Arrays.sort(removeList, Collections.reverseOrder());
		// Arrays.sort(removeList, new Comparator<Integer>() {
		// public int compare(Integer o1, Integer o2) {
		// return o1 < o2 ? 1 : -1;
		// }
		// });
		Arrays.sort(removeList);

		// 移除
		for (int i = removeNr - 1; i >= 0; i--) {
			s.removeCustomer(customList.get(removeList[i]).routeNo, customList.get(removeList[i]).customerNo);
		}
		return s;
	}
}

class Fitness implements Comparable<Fitness> {
	public int routeNo;
	public int customerNo;
	public double fitness;

	public Fitness() {
	}

	public static Fitness calculateFitness(Instance instance, Route a_Route, int rNo, int cNo) {
		double[][] distance = instance.getDistanceMatrix();

		// ! why
		// int x = route.getRoute().get(0).getId();
		// int y = customer.getId();
		// double fitness = (route.getCost().getTimeViolation() +
		// route.getCost().getLoadViolation()
		// + customer.getDemand()) * (distance[y][x] + distance[x][y]);

		// ! new
		Fitness f = new Fitness();

		double cost_down = distance[a_Route.getRoute().get(cNo - 1).getId()][a_Route.getRoute().get(cNo).getId()]
				+ distance[a_Route.getRoute().get(cNo).getId()][a_Route.getRoute().get(cNo + 1).getId()]
				- distance[a_Route.getRoute().get(cNo - 1).getId()][a_Route.getRoute().get(cNo + 1).getId()];
		f.fitness = cost_down;

		f.routeNo = rNo;
		f.customerNo = cNo;
		f.fitness = cost_down;
		return f;
	}

	@Override
	public int compareTo(Fitness s) {
		if (this.fitness < s.fitness) {
			return -1;
		} else if (this.fitness == s.fitness) {
			return 0;
		} else {
			return 1;
		}
	}

}