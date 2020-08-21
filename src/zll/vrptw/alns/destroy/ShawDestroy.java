package zll.vrptw.alns.destroy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import zll.vrptw.algrithm.MyALNSSolution;
import zll.vrptw.alns.operation.ALNSAbstractOperation;
import zll.vrptw.instance.Node;
import zll.vrptw.instance.Route;

/**
 * <p>
 * Title: ShawDestroy
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author zll_hust
 * @date 2020年3月20日
 */
public class ShawDestroy extends ALNSAbstractOperation implements IALNSDestroy {
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
		Node lastRemove;
		Route lastRoute;
		int lastRemovePos;
		int lastRoutePos;

		ArrayList<Integer> routeList = new ArrayList<Integer>();
		for (int j = 0; j < s.routes.size(); j++)
			routeList.add(j);

		Collections.shuffle(routeList);

		// 选择被移除客户所在的路径
		int removenRoutePosition = routeList.remove(0);
		Route removenRoute = s.routes.get(removenRoutePosition);

		while (removenRoute.getRoute().size() <= 2) {
			removenRoutePosition = routeList.remove(0);
			removenRoute = s.routes.get(removenRoutePosition);
		}

		// 选择被移除客户
		ArrayList<Integer> cusList = new ArrayList<Integer>();
		for (int j = 1; j < removenRoute.getRoute().size() - 1; j++)
			cusList.add(j);
		Collections.shuffle(cusList);

		int removenCusPosition = cusList.remove(0);
		Node removenCus = removenRoute.getRoute().get(removenCusPosition);
		s.removeCustomer(removenRoutePosition, removenCusPosition);

		// !记录被移除的点
		lastRemove = removenCus;
		lastRoute = removenRoute;
		lastRemovePos = -1;
		lastRoutePos = -1;

		double[][] distance = s.instance.getDistanceMatrix();

		while (s.removalCustomers.size() < removeNr) {

			double minRelate = Double.MAX_VALUE;

			for (int j = 0; j < s.routes.size(); j++) {
				for (int i = 1; i < s.routes.get(j).getRoute().size() - 1; ++i) {

					Node relatedNode = s.routes.get(j).getRoute().get(i);
					int l = (lastRoute.getId() == s.routes.get(j).getId()) ? -1 : 1;

					double fitness = l * 2 + 3 * distance[lastRemove.getId()][relatedNode.getId()]
							+ 2 * Math.abs(lastRemove.getTimeWindow()[0] - relatedNode.getTimeWindow()[0])
							+ 2 * Math.abs(lastRemove.getDemand() - relatedNode.getDemand());

					if (minRelate > fitness) {
						minRelate = fitness;
						// !没必要，基节点得固定
						// lastRemove = relatedNode;
						// lastRoute = s.routes.get(j);
						lastRemovePos = i;
						lastRoutePos = j;
					}
				}
			}
			// !lastRemovePos是节点在路径中的位置，不是id
			s.removeCustomer(lastRoutePos, lastRemovePos);
		}

		return s;
	}
}
