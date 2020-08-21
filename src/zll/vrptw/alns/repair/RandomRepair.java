package zll.vrptw.alns.repair;

import java.util.*;

import zll.vrptw.algrithm.Cost;
import zll.vrptw.algrithm.MyALNSSolution;
import zll.vrptw.instance.Node;
import zll.vrptw.instance.Route;

/**  
* <p>Title: RandomRepair</p>  
* <p>Description: </p>  
* @author zll_hust  
* @date 2020��3��19��  
*/
public class RandomRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public MyALNSSolution repair(MyALNSSolution s) {
		// ���û���Ƴ��Ŀͻ�����һ������
    	if(s.removalCustomers.size() == 0) {
			System.err.println("removalCustomers is empty!");
			return s;
		}
    	
    	// ��ȡ�����
    	Random r = s.instance.getRandom();
    	int insertCusNr = s.removalCustomers.size();	
    	
    	for (int i = 0; i < insertCusNr; i++) {
    		
    		Node insertNode = s.removalCustomers.remove(0);
    		
    		// ����������Ҷ�����·��
    		int randomRouteNr = r.nextInt(s.routes.size() - 1) + 1;
    		
    		// ���Ų��뷽��
    		int bestRoutePosition = -1;
    		int bestCusomerPosition = -1;
    		Cost bestCost = new Cost();
    		bestCost.total = Double.MAX_VALUE;
    		
    		ArrayList<Integer> routeList= new ArrayList<Integer>();
            for(int j = 0; j < s.routes.size(); j++)
                routeList.add(j);  
            
            Collections.shuffle(routeList);  
            
    		for (int j = 0; j < randomRouteNr; j++) {
    			
    			// ���ѡ��һ��route
    			int insertRoutePosition = routeList.remove(0);
    			Route insertRoute = s.routes.get(insertRoutePosition);
    			
    			while(insertRoute.getRoute().size() < 1) {
    				insertRoutePosition = routeList.remove(0);
    				insertRoute = s.routes.get(insertRoutePosition);
    			}
    			
    			// ����������Ҷ��ٸ�λ��
    			int insertTimes = r.nextInt(insertRoute.getRoute().size() - 1) + 1;
    			
        		ArrayList<Integer> customerList= new ArrayList<Integer>();
                for(int k = 1; k < insertRoute.getRoute().size(); k++)
                	customerList.add(k);  
                
                Collections.shuffle(customerList); 
                
                // ���ѡ��һ��λ��
    			for (int k = 0; k < insertTimes; k++) {
    				
    				int insertCusPosition = customerList.remove(0);
    				
    				// ���۲������
    				Cost newCost = new Cost(s.cost);
    				s.evaluateInsertCustomer(insertRoutePosition, insertCusPosition, insertNode, newCost);
                    
    				// �������Ų���λ��
    				if (newCost.total < bestCost.total) {
    					bestRoutePosition = insertRoutePosition;
    					bestCusomerPosition = insertCusPosition;
    					bestCost = newCost;
    				}
    			}
    			// ִ�в������
    			s.insertCustomer(bestRoutePosition, bestCusomerPosition, insertNode);
    		}
    	}
    	
		return s;
	}
   
}
