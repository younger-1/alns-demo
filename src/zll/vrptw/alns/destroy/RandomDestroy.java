package zll.vrptw.alns.destroy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import zll.vrptw.algrithm.MyALNSSolution;
import zll.vrptw.alns.operation.ALNSAbstractOperation;
import zll.vrptw.instance.Route;

/**
 * <p>
 * Title: RandomDestroy
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author zll_hust
 * @date 2020��3��19��
 */

public class RandomDestroy extends ALNSAbstractOperation implements IALNSDestroy {
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

		while (s.removalCustomers.size() < removeNr) {

			ArrayList<Integer> routeList = new ArrayList<Integer>();
			for (int j = 0; j < s.routes.size(); j++)
				routeList.add(j);

			Collections.shuffle(routeList);

			// ѡ���Ƴ��ͻ����ڵ�·��
			int removenRoutePosition = routeList.remove(0);
			Route removenRoute = s.routes.get(removenRoutePosition);

			while (removenRoute.getRoute().size() <= 2) {
				removenRoutePosition = routeList.remove(0);
				removenRoute = s.routes.get(removenRoutePosition);
			}

			// ѡ���Ƴ��Ŀͻ�
			Random r = s.instance.getRandom();
			int removenCustomerPosition = r.nextInt(removenRoute.getRoute().size() - 2) + 1;

			s.removeCustomer(removenRoutePosition, removenCustomerPosition);
		}

		return s;
	}

}
