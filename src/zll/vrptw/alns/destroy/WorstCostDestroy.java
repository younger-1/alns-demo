package zll.vrptw.alns.destroy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import zll.vrptw.algrithm.MyALNSSolution;
import zll.vrptw.alns.operation.ALNSAbstractOperation;
import zll.vrptw.instance.Route;
import zll.vrptw.instance.Instance;

/**
 * <p>
 * Title: WorstCostDestroy
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author zll_hust
 * @date 2020年3月19日
 */
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
		HashMap<Fitness, Integer> hash = new HashMap<>();
		for (int k = 0; k < customList.size(); k++) {
			hash.put(customList.get(k), k);
		}

		ArrayList<Fitness> customListCopy = new ArrayList<>(customList);
		Collections.sort(customListCopy);

		ArrayList<Integer> removeList = new ArrayList();
		for (int i = 0; i < removeNr; i++) {
			removeList.add(hash.get(customListCopy.get(i)));
		}

		Collections.sort(removeList, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;
			}

		});
		System.out.println(removeList);
		// 移除
		for (int i = 0; i < removeNr; i++) {
			s.removeCustomer(customList.get(removeList.get(i)).routeNo, customList.get(removeList.get(i)).customerNo);
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
		if (s.fitness > this.fitness) {
			return 1;
		} else if (this.fitness == s.fitness) {
			return 0;
		} else {
			return -1;
		}
	}

}
