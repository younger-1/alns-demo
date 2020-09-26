package zll.vrptw.algrithm;

import java.util.ArrayList;
import java.util.List;

import zll.vrptw.instance.Node;
import zll.vrptw.instance.Route;
import zll.vrptw.instance.Instance;

/**
 * <p>
 * Title: ALNSSolution
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author zll_hust
 * @date 2020年3月18日
 */
public class MyALNSSolution {

	public List<Route> routes;
	public Cost cost;
	public int vehicleNr;
	public Instance instance;

	public double alpha; // α
	public double beta; // β
	public final double gamma = 10000;

	public static final double punish = 1000;

	public ArrayList<Node> removalCustomers;

	public MyALNSSolution(Solution sol, Instance instance) {
		this.cost = new Cost(sol.cost);
		this.vehicleNr = sol.getVehicleNr();
		this.instance = instance;

		this.alpha = punish;
		this.beta = punish;

		this.routes = new ArrayList<>();
		for (Route route : sol.getRoutes()) {
			this.routes.add(route.cloneRoute());
		}

		this.removalCustomers = new ArrayList<Node>();
	}

	public MyALNSSolution(MyALNSSolution sol) {
		this.cost = new Cost(sol.cost);
		this.vehicleNr = sol.vehicleNr;
		this.instance = sol.instance;

		this.alpha = sol.alpha;
		this.beta = sol.beta;

		this.routes = new ArrayList<>();
		for (Route route : sol.routes) {
			this.routes.add(route.cloneRoute());
		}

		this.removalCustomers = new ArrayList<Node>();
	}

	public void removeCustomer(int routePosition, int cusPosition) {
		double[][] distance = instance.getDistanceMatrix();

		Route removenRoute = this.routes.get(routePosition);

		// System.out.println(this);
		double load = -removenRoute.getRoute().get(cusPosition).getDemand();
		double cost = -distance[removenRoute.getRoute().get(cusPosition - 1).getId()][removenRoute.getRoute()
				.get(cusPosition).getId()]
				- distance[removenRoute.getRoute().get(cusPosition).getId()][removenRoute.getRoute()
						.get(cusPosition + 1).getId()]
				+ distance[removenRoute.getRoute().get(cusPosition - 1).getId()][removenRoute.getRoute()
						.get(cusPosition + 1).getId()];

		this.cost.cost += cost;
		this.cost.load += load;
		removenRoute.getCost().cost += cost;
		removenRoute.getCost().load += load;

		// * 1.计算当前路径、总路径的 load violation
		double routeLoad = removenRoute.getCost().load;
		if (routeLoad > this.instance.getVehicleCapacity()) {
			double violaton = routeLoad - this.instance.getVehicleCapacity();
			this.cost.loadViolation += violaton - removenRoute.getCost().loadViolation;
			removenRoute.getCost().loadViolation = violaton;
		} else if (removenRoute.getCost().loadViolation > 0) {
			this.cost.loadViolation -= removenRoute.getCost().loadViolation;
			removenRoute.getCost().loadViolation = 0;
		}

		// ! 移除节点
		removalCustomers.add(removenRoute.removeNode(cusPosition));

		// 计算当前路径的time windows，time
		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < removenRoute.getRoute().size(); i++) {
			time += distance[removenRoute.getRoute().get(i - 1).getId()][removenRoute.getRoute().get(i).getId()];
			if (time < removenRoute.getRoute().get(i).getTimeWindow()[0])
				time = removenRoute.getRoute().get(i).getTimeWindow()[0];
			else if (time > removenRoute.getRoute().get(i).getTimeWindow()[1])
				timeWindowViolation += time - removenRoute.getRoute().get(i).getTimeWindow()[1];

			time += removenRoute.getRoute().get(i).getServiceTime();
		}
		this.cost.time += (time - removenRoute.getCost().time);
		removenRoute.getCost().time = time;

		// * 2. 计算当前路径、总路径的 time windows violation
		if (timeWindowViolation > 0) {
			double violationDiff = timeWindowViolation - removenRoute.getCost().timeViolation;
			removenRoute.getCost().timeViolation = timeWindowViolation;
			this.cost.timeViolation += violationDiff;
		} else if (removenRoute.getCost().timeViolation > 0) {
			this.cost.timeViolation -= removenRoute.getCost().timeViolation;
			removenRoute.getCost().timeViolation = 0;
		}

		// * 3. 计算当前路径、总路径的 maxCustomerNumViolation
		// removenRoute.getCustomerNum() > instance.getMaxCustomerNum();
		if (removenRoute.getCost().maxCustomerNumViolation > 0) {
			removenRoute.getCost().maxCustomerNumViolation -= 1;
			this.cost.maxCustomerNumViolation -= 1;
		}

		// * 4.计算 total
		// removenRoute.getCost().calculateTotalCost(this.alpha, this.beta);
		removenRoute.getCost().calculateTotalCost(this.alpha, this.beta, this.gamma);
		// this.cost.calculateTotalCost(this.alpha, this.beta);
		this.cost.calculateTotalCost(this.alpha, this.beta, this.gamma);
	}

	public void insertCustomer(int routePosition, int insertCusPosition, Node insertCustomer) {
		// * this.cost 只需存储 this.cost.cost, this.cost.loadViolation, this.cost.timeViolation
		double[][] distance = instance.getDistanceMatrix();

		// ! 无需clone
		Route insertRoute = this.routes.get(routePosition);

		// 计算load和cost的变化量
		double load = +insertCustomer.getDemand();
		double cost = +distance[insertRoute.getRoute().get(insertCusPosition - 1).getId()][insertCustomer.getId()]
				+ distance[insertCustomer.getId()][insertRoute.getRoute().get(insertCusPosition).getId()]
				- distance[insertRoute.getRoute().get(insertCusPosition - 1).getId()][insertRoute.getRoute()
						.get(insertCusPosition).getId()];

		// 更新当前路径、总路径的cost、load
		this.cost.cost += cost;
		this.cost.load += load;
		insertRoute.getCost().cost += cost;
		insertRoute.getCost().load += load;

		// * 1.计算 load violation
		double routeLoad = insertRoute.getCost().load;
		if (routeLoad > this.instance.getVehicleCapacity()) {
			double violaton = routeLoad - this.instance.getVehicleCapacity();
			this.cost.loadViolation += violaton - insertRoute.getCost().loadViolation;
			insertRoute.getCost().loadViolation = violaton;
		}

		// ! 插入节点
		insertRoute.addNodeToRouteWithIndex(insertCustomer, insertCusPosition);

		// 计算当前路径的time windows，time
		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < insertRoute.getRoute().size(); i++) {
			time += distance[insertRoute.getRoute().get(i - 1).getId()][insertRoute.getRoute().get(i).getId()];
			if (time < insertRoute.getRoute().get(i).getTimeWindow()[0])
				time = insertRoute.getRoute().get(i).getTimeWindow()[0];
			else if (time > insertRoute.getRoute().get(i).getTimeWindow()[1])
				timeWindowViolation += time - insertRoute.getRoute().get(i).getTimeWindow()[1];

			time += insertRoute.getRoute().get(i).getServiceTime();
		}
		this.cost.time += (time - insertRoute.getCost().time);
		insertRoute.getCost().time = time;

		// * 2. 计算当前路径、总路径的time windows violation
		if (timeWindowViolation > 0) {
			double violationDiff = timeWindowViolation - insertRoute.getCost().timeViolation;
			insertRoute.getCost().timeViolation = timeWindowViolation;
			this.cost.timeViolation += violationDiff;
		}

		// * 3. 计算 maxCustomerNumViolation
		if (insertRoute.getCustomerNum() > instance.getMaxCustomerNum()) {
			insertRoute.getCost().maxCustomerNumViolation += 1;
			this.cost.maxCustomerNumViolation += 1;
		}

		// * 4.计算 total
		// insertRoute.getCost().calculateTotalCost(this.alpha, this.beta);
		insertRoute.getCost().calculateTotalCost(this.alpha, this.beta, this.gamma);
		// this.cost.calculateTotalCost(this.alpha, this.beta);
		this.cost.calculateTotalCost(this.alpha, this.beta, this.gamma);
	}

	public void evaluateInsertCustomer(int routePosition, int insertCusPosition, Node insertCustomer, Cost newCost) {
		// * this.cost, insertRoute.getCost 不应该在这里变化
		// * newCost 代替 this.cost 变化
		double[][] distance = instance.getDistanceMatrix();

		// ! 这是条 clone 路径
		Route insertRoute = this.routes.get(routePosition).cloneRoute();

		double cost = +distance[insertRoute.getRoute().get(insertCusPosition - 1).getId()][insertCustomer.getId()]
				+ distance[insertCustomer.getId()][insertRoute.getRoute().get(insertCusPosition).getId()]
				- distance[insertRoute.getRoute().get(insertCusPosition - 1).getId()][insertRoute.getRoute()
						.get(insertCusPosition).getId()];

		// 更新总路径 cost
		newCost.cost += cost;

		// * 1.计算 load violation
		insertRoute.getCost().load += insertCustomer.getDemand();
		double routeLoad = insertRoute.getCost().load;
		if (routeLoad > this.instance.getVehicleCapacity()) {
			double violaton = routeLoad - this.instance.getVehicleCapacity();
			newCost.loadViolation += violaton - insertRoute.getCost().loadViolation;
		}
		
		// ! 插入节点
		insertRoute.addNodeToRouteWithIndex(insertCustomer, insertCusPosition);

		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < insertRoute.getRoute().size(); i++) {
			time += distance[insertRoute.getRoute().get(i - 1).getId()][insertRoute.getRoute().get(i).getId()];
			if (time < insertRoute.getRoute().get(i).getTimeWindow()[0])
				time = insertRoute.getRoute().get(i).getTimeWindow()[0];
			else if (time > insertRoute.getRoute().get(i).getTimeWindow()[1])
				timeWindowViolation += time - insertRoute.getRoute().get(i).getTimeWindow()[1];

			time += insertRoute.getRoute().get(i).getServiceTime();
		}

		// * 2. 计算总路径的time windows violation
		if (timeWindowViolation > 0) {
			newCost.timeViolation += timeWindowViolation - insertRoute.getCost().timeViolation;
		}

		// * 3. 计算 maxCustomerNumViolation
		if (insertRoute.getCustomerNum() > instance.getMaxCustomerNum()) {
			newCost.maxCustomerNumViolation += 1;
		}

		// * 4.计算 total
		// newCost.calculateTotalCost(this.alpha, this.beta);
		newCost.calculateTotalCost(this.alpha, this.beta, this.gamma);
	}

	public boolean feasible() {
		return (cost.timeViolation < 0.01 && cost.loadViolation < 0.01);
	}

	public Solution toSolution() {
		Solution sol = new Solution();

		List<Route> solutionRoutes = new ArrayList<>();
		for (Route route : this.routes) {
			solutionRoutes.add(route.cloneRoute());
		}

		sol.setRoutes(solutionRoutes);
		sol.setTotalCost(cost.cost);
		sol.setVehicleNr(vehicleNr);

		return sol;
	}

	@Override
	public String toString() {
		String result = "Solution{" + "Cost = " + cost + ", routes = [";

		for (Route vehicle : this.routes) {
			result += "\n\t" + vehicle;
		}

		return result + "]}";
	}

}
