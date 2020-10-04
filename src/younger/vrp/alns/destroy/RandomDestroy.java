package younger.vrp.alns.destroy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import younger.vrp.algrithm.MyALNSSolution;
import younger.vrp.alns.operation.ALNSAbstractOperation;
import younger.vrp.instance.Route;

public class RandomDestroy extends ALNSAbstractOperation implements IALNSDestroy {
	@Override
	public MyALNSSolution destroy(MyALNSSolution s, int removeNr) throws Exception {

		if (s.removalCustomers.size() != 0) {
			System.err.println("removalCustomers is not empty.");
			return s;
		}

		while (s.removalCustomers.size() < removeNr) {

			ArrayList<Integer> routeList = new ArrayList<Integer>();
			for (int j = 0; j < s.routes.size(); j++)
				routeList.add(j);

			Collections.shuffle(routeList);

			// 选择被移除客户所在的路径
			int removenRoutePosition = routeList.remove(0);
			Route removenRoute = s.routes.get(removenRoutePosition);

			while (removenRoute.getSize() <= 2) {
				removenRoutePosition = routeList.remove(0);
				removenRoute = s.routes.get(removenRoutePosition);
			}

			// 选择被移除的客户
			Random r = s.instance.getRandom();
			int removenCustomerPosition = r.nextInt(removenRoute.getSize() - 2) + 1;

			s.removeCustomer(removenRoutePosition, removenCustomerPosition);
		}

		return s;
	}

}
