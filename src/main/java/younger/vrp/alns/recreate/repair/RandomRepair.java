package younger.vrp.alns.recreate.repair;

import java.util.*;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.recreate.ALNSAbstractRecreate;
import younger.vrp.alns.recreate.IALNSRecreate;
import younger.vrp.instance.Cost;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class RandomRepair extends ALNSAbstractRecreate implements IALNSRecreate {

    private RandomRepair() {
        this.random = new Random();
    }

    public static RandomRepair of() {
        return new RandomRepair();
    }

    @Override
    public ALNSSolution recreate(ALNSSolution s) {
        // 如果没有移除的客户，上一步错误
        if (s.removeNodes.size() == 0) {
            System.err.println("removalCustomers is empty!");
            return s;
        }

        // 获取随机数
        int insertCusNr = s.removeNodes.size();

        // ! 插入节点循环
        for (int i = 0; i < insertCusNr; i++) {

            Node insertNode = s.removeNodes.remove(0);

            // ! 随机决定查找多少条路径
            int randomRouteNr = random.nextInt(s.routes.size()) + 1;

            // 最优插入方案
            int bestRoutePosition = -1;
            int bestCusomerPosition = -1;
            double min_cost = Double.POSITIVE_INFINITY;

            ArrayList<Integer> routeList = new ArrayList<Integer>();
            for (int j = 0; j < s.routes.size(); j++)
                routeList.add(j);

            Collections.shuffle(routeList);

            // ! 询问路径循环
            for (int j = 0; j < randomRouteNr; j++) {

                // 随机选择一条route
                int insertRoutePosition = routeList.remove(0);
                Route insertRoute = s.routes.get(insertRoutePosition);

                // ! 随机决定查找多少个位置
                // * VRP 1~getSize()-1 都可插，OVRP 1~getSize() 都可插
                // * eg. when getSize()==3，VRP 1~2，OVRP 1~3
                // *      rand.nextInt(2)+1, rand.nextInt(3)+1
                int insertTimes = random.nextInt(insertRoute.getSize() - 1) + 1;

                ArrayList<Integer> customerList = new ArrayList<Integer>();
                for (int k = 1; k <= insertRoute.getSize() - 1; k++)
                    customerList.add(k); // 1,2,3,..,N-1
                Collections.shuffle(customerList);

                // ! 询问节点循环
                for (int k = 0; k < insertTimes; k++) {
                    int insertCusPosition = customerList.remove(0);

                    Cost newCost = s.evaluateInsertCustomer(insertRoutePosition, insertCusPosition, insertNode);
                    // 更新最优插入位置
                    if (newCost.getDist() < min_cost) {
                        bestRoutePosition = insertRoutePosition;
                        bestCusomerPosition = insertCusPosition;
                        min_cost = newCost.getDist();
                    }
                }
            }
            // 执行插入操作
            s.insertCustomer(bestRoutePosition, bestCusomerPosition, insertNode);
        }

        return s;
    }

}
