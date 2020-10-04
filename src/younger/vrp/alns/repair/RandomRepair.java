package younger.vrp.alns.repair;

import java.util.*;

import younger.vrp.algrithm.Cost;
import younger.vrp.algrithm.MyALNSSolution;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class RandomRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public MyALNSSolution repair(MyALNSSolution s) {
		// 如果没有移除的客户，上一步错误
		if (s.removalCustomers.size() == 0) {
			System.err.println("removalCustomers is empty!");
			return s;
		}

		// 获取随机数
		Random r = s.instance.getRandom();
		int insertCusNr = s.removalCustomers.size();

		// ! 插入节点循环
		for (int i = 0; i < insertCusNr; i++) {

			Node insertNode = s.removalCustomers.remove(0);

			// ! 随机决定查找多少条路径
			int randomRouteNr = r.nextInt(s.routes.size()) + 1;

			// 最优插入方案
			int bestRoutePosition = -1;
			int bestCusomerPosition = -1;
			Cost bestCost = new Cost();
			bestCost.total = Double.MAX_VALUE;

			ArrayList<Integer> routeList = new ArrayList<Integer>();
			for (int j = 0; j < s.routes.size(); j++)
				routeList.add(j);

			Collections.shuffle(routeList);

			// ! 询问路径循环
			for (int j = 0; j < randomRouteNr; j++) {

				// 随机选择一条route
				int insertRoutePosition = routeList.remove(0);
				Route insertRoute = s.routes.get(insertRoutePosition);

				// while (insertRoute.getSize() < 1) {
				// insertRoutePosition = routeList.remove(0);
				// insertRoute = s.routes.get(insertRoutePosition);
				// }

				// ! 随机决定查找多少个位置
				int insertTimes = r.nextInt(insertRoute.getSize() - 1) + 1;

				ArrayList<Integer> customerList = new ArrayList<Integer>();
				for (int k = 1; k < insertRoute.getSize(); k++)
					customerList.add(k); // 1,2,3,..,N-1
				Collections.shuffle(customerList);

				// ! 询问节点循环
				for (int k = 0; k < insertTimes; k++) {
					int insertCusPosition = customerList.remove(0);
					Cost newCost = new Cost(s.cost);
					s.evaluateInsertCustomer(insertRoutePosition, insertCusPosition, insertNode, newCost);
					// 更新最优插入位置
					if (newCost.total < bestCost.total) {
						bestRoutePosition = insertRoutePosition;
						bestCusomerPosition = insertCusPosition;
						bestCost = newCost;
					}
				}
			}
			// 执行插入操作
			s.insertCustomer(bestRoutePosition, bestCusomerPosition, insertNode);
		}

		return s;
	}

}
