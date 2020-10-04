package younger.vrp.alns.repair;

import java.util.ArrayList;
import java.util.Collections;

import younger.vrp.algrithm.Cost;
import younger.vrp.algrithm.MyALNSSolution;
import younger.vrp.instance.Node;

public class RegretRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public MyALNSSolution repair(MyALNSSolution s) {
		// 如果没有移除的客户，上一步错误
		if (s.removalCustomers.size() == 0) {
			System.err.println("removalCustomers is empty!");
			return s;
		}

		ArrayList<BestPos> bestPoses = new ArrayList<BestPos>();

		int insertCusNr = s.removalCustomers.size();

		for (int k = 0; k < insertCusNr; k++) {

			Node insertNode = s.removalCustomers.remove(0);

			double first, second;
			int bestCusP = -1;
			int bestRouteP = -1;
			first = second = Double.POSITIVE_INFINITY;

			for (int j = 0; j < s.routes.size(); j++) {

				if (s.routes.get(j).getSize() < 1) {
					continue;
				}

				// 寻找最优插入位置
				for (int i = 1; i < s.routes.get(j).getSize() - 1; ++i) {

					// 评价插入情况
					Cost newCost = new Cost(s.cost);
					s.evaluateInsertCustomer(j, i, insertNode, newCost);

					// if a better insertion is found, set the position to insert in the move and
					// update the minimum cost found
					if (newCost.cost < first) {
						// System.out.println(varCost.checkFeasible());
						bestCusP = i;
						bestRouteP = j;
						second = first;
						first = newCost.cost;
					} else if (newCost.cost < second && newCost.cost != first) {
						second = newCost.cost;
					}
				}
			}
			bestPoses.add(new BestPos(insertNode, bestCusP, bestRouteP, second - first));
		}
		Collections.sort(bestPoses);

		for (BestPos bp : bestPoses) {
			s.insertCustomer(bp.bestRroutePosition, bp.bestCustomerPosition, bp.insertNode);
		}

		return s;
	}
}

class BestPos implements Comparable<BestPos> {

	public int bestRroutePosition;
	public int bestCustomerPosition;
	public Node insertNode;
	public double deltaCost;

	public BestPos() {
	}

	public BestPos(Node insertNode, int customer, int route, double f) {
		this.insertNode = insertNode;
		this.bestRroutePosition = route;
		this.bestCustomerPosition = customer;
		this.deltaCost = f;
	}

	@Override
	public int compareTo(BestPos o) {
		BestPos s = (BestPos) o;
		if (this.bestCustomerPosition > s.bestCustomerPosition) {
			return -1;
		} else if (this.bestCustomerPosition < s.bestCustomerPosition) {
			return 1;
		} else {
			return 0;
		}
		// if (s.deltaCost > this.deltaCost) {
		// return 1;
		// } else if (this.deltaCost == s.deltaCost) {
		// return 0;
		// } else {
		// return -1;
		// }
	}
}
