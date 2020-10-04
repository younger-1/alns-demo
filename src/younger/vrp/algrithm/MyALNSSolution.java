package younger.vrp.algrithm;

import java.util.ArrayList;
import java.util.List;

import younger.vrp.instance.Node;
import younger.vrp.instance.Route;
import younger.vrp.instance.Instance;

/**
 * <p>
 * Title: ALNSSolution
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Xavier Young
 */
public class MyALNSSolution {

	public List<Route> routes;
	public Cost cost;
	public int vehicleNr;
	public Instance instance;

	public double alpha; // α
	public double beta; // β
	public final double gamma = 2000;

	public static final double punish = 1000;

	public ArrayList<Node> removeNodes;

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

		this.removeNodes = new ArrayList<Node>();
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

		this.removeNodes = new ArrayList<Node>();
	}

	public void removeCustomer(int routePosition, int cusPosition) {
		double[][] distance = instance.getDistanceMatrix();

		Route removeRoute = this.routes.get(routePosition);

		// System.out.println(this);
		double load = -removeRoute.getNode(cusPosition).getDemand();
		double cost = -distance[removeRoute.getNode(cusPosition - 1).getId()][removeRoute.getNode(cusPosition)
				.getId()]
				- distance[removeRoute.getNode(cusPosition).getId()][removeRoute.getNode(cusPosition + 1).getId()]
				+ distance[removeRoute.getNode(cusPosition - 1).getId()][removeRoute.getNode(cusPosition + 1)
						.getId()];

		this.cost.cost += cost;
		this.cost.load += load;
		removeRoute.getCost().cost += cost;
		removeRoute.getCost().load += load;

		double routeLoad = removeRoute.getCost().load;
		if (routeLoad > this.instance.getVehicleCapacity()) {
			double violaton = routeLoad - this.instance.getVehicleCapacity();
			this.cost.loadVio += violaton - removeRoute.getCost().loadVio;
			removeRoute.getCost().loadVio = violaton;
		} else if (removeRoute.getCost().loadVio > 0) {
			this.cost.loadVio -= removeRoute.getCost().loadVio;
			removeRoute.getCost().loadVio = 0;
		}

		// ! 移除节点
		removeNodes.add(removeRoute.removeNode(cusPosition));

		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < removeRoute.getSize(); i++) {
			time += distance[removeRoute.getNode(i - 1).getId()][removeRoute.getNode(i).getId()];
			if (time < removeRoute.getNode(i).getTW()[0])
				time = removeRoute.getNode(i).getTW()[0];
			else if (time > removeRoute.getNode(i).getTW()[1])
				timeWindowViolation += time - removeRoute.getNode(i).getTW()[1];

			time += removeRoute.getNode(i).getServiceTime();
		}
		this.cost.time += (time - removeRoute.getCost().time);
		removeRoute.getCost().time = time;

		if (timeWindowViolation > 0) {
			double violationDiff = timeWindowViolation - removeRoute.getCost().timeVio;
			removeRoute.getCost().timeVio = timeWindowViolation;
			this.cost.timeVio += violationDiff;
		} else if (removeRoute.getCost().timeVio > 0) {
			this.cost.timeVio -= removeRoute.getCost().timeVio;
			removeRoute.getCost().timeVio = 0;
		}

		if (removeRoute.getCost().nodeNumVio > 0) {
			removeRoute.getCost().nodeNumVio -= 1;
			this.cost.nodeNumVio -= 1;
		}

		// removenRoute.getCost().calculateTotalCost(this.alpha, this.beta);
		removeRoute.getCost().calculateTotalCost(this.alpha, this.beta, this.gamma);
		// this.cost.calculateTotalCost(this.alpha, this.beta);
		this.cost.calculateTotalCost(this.alpha, this.beta, this.gamma);
	}

	public void insertCustomer(int routePosition, int insertCusPosition, Node insertCustomer) {
		double[][] distance = instance.getDistanceMatrix();
		Route insertRoute = this.routes.get(routePosition);

		double load = +insertCustomer.getDemand();
		double cost = +distance[insertRoute.getNode(insertCusPosition - 1).getId()][insertCustomer.getId()]
				+ distance[insertCustomer.getId()][insertRoute.getNode(insertCusPosition).getId()]
				- distance[insertRoute.getNode(insertCusPosition - 1).getId()][insertRoute.getNode(insertCusPosition)
						.getId()];

		this.cost.cost += cost;
		this.cost.load += load;
		insertRoute.getCost().cost += cost;
		insertRoute.getCost().load += load;

		double routeLoad = insertRoute.getCost().load;
		if (routeLoad > this.instance.getVehicleCapacity()) {
			double violaton = routeLoad - this.instance.getVehicleCapacity();
			this.cost.loadVio += violaton - insertRoute.getCost().loadVio;
			insertRoute.getCost().loadVio = violaton;
		}

		insertRoute.addNodeToRouteWithIndex(insertCustomer, insertCusPosition);

		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < insertRoute.getSize(); i++) {
			time += distance[insertRoute.getNode(i - 1).getId()][insertRoute.getNode(i).getId()];
			if (time < insertRoute.getNode(i).getTW()[0])
				time = insertRoute.getNode(i).getTW()[0];
			else if (time > insertRoute.getNode(i).getTW()[1])
				timeWindowViolation += time - insertRoute.getNode(i).getTW()[1];

			time += insertRoute.getNode(i).getServiceTime();
		}
		this.cost.time += (time - insertRoute.getCost().time);
		insertRoute.getCost().time = time;

		if (timeWindowViolation > 0) {
			double violationDiff = timeWindowViolation - insertRoute.getCost().timeVio;
			insertRoute.getCost().timeVio = timeWindowViolation;
			this.cost.timeVio += violationDiff;
		}

		if (insertRoute.getSize() - 2 > instance.getMaxCustomerNum()) {
			insertRoute.getCost().nodeNumVio += 1;
			this.cost.nodeNumVio += 1;
		}

		// insertRoute.getCost().calculateTotalCost(this.alpha, this.beta);
		insertRoute.getCost().calculateTotalCost(this.alpha, this.beta, this.gamma);
		// this.cost.calculateTotalCost(this.alpha, this.beta);
		this.cost.calculateTotalCost(this.alpha, this.beta, this.gamma);
	}

	public void evaluateInsertCustomer(int routePosition, int insertCusPosition, Node insertCustomer, Cost newCost) {
		double[][] distance = instance.getDistanceMatrix();

		Route insertRoute = this.routes.get(routePosition).cloneRoute();

		double cost = +distance[insertRoute.getNode(insertCusPosition - 1).getId()][insertCustomer.getId()]
				+ distance[insertCustomer.getId()][insertRoute.getNode(insertCusPosition).getId()]
				- distance[insertRoute.getNode(insertCusPosition - 1).getId()][insertRoute.getNode(insertCusPosition)
						.getId()];
		newCost.cost += cost;

		insertRoute.getCost().load += insertCustomer.getDemand();
		double routeLoad = insertRoute.getCost().load;
		if (routeLoad > this.instance.getVehicleCapacity()) {
			double violaton = routeLoad - this.instance.getVehicleCapacity();
			newCost.loadVio += violaton - insertRoute.getCost().loadVio;
		}

		insertRoute.addNodeToRouteWithIndex(insertCustomer, insertCusPosition);

		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < insertRoute.getSize(); i++) {
			time += distance[insertRoute.getNode(i - 1).getId()][insertRoute.getNode(i).getId()];
			if (time < insertRoute.getNode(i).getTW()[0])
				time = insertRoute.getNode(i).getTW()[0];
			else if (time > insertRoute.getNode(i).getTW()[1])
				timeWindowViolation += time - insertRoute.getNode(i).getTW()[1];

			time += insertRoute.getNode(i).getServiceTime();
		}

		if (timeWindowViolation > 0) {
			newCost.timeVio += timeWindowViolation - insertRoute.getCost().timeVio;
		}

		if (insertRoute.getSize() - 2 > instance.getMaxCustomerNum()) {
			newCost.nodeNumVio += 1;
		}

		// newCost.calculateTotalCost(this.alpha, this.beta);
		newCost.calculateTotalCost(this.alpha, this.beta, this.gamma);
	}

	public boolean feasible() {
		return cost.loadVio < 0.01;
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
