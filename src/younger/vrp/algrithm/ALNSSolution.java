package younger.vrp.algrithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import younger.vrp.instance.Node;
import younger.vrp.instance.Route;
import younger.vrp.alns.config.VRPCategory;
import younger.vrp.base.IDistance;
import younger.vrp.instance.Expense;

public class ALNSSolution {
    /**
     * All the routes of the current solution.
     */
    public List<Route> routes;
    public Expense costs;
    public int[][] distance = IDistance.getDistanceInstance().distanceMatrix();
    public int customerNr;
    // optimize: new random for each solution
    public Random random = new Random(1004);
    public ArrayList<Node> removeNodes;
    private VRPCategory vrpCate;
    private double average_dist;

    public ALNSSolution(int customerNr, VRPCategory vrpCate) {
        this.customerNr = customerNr;
        this.vrpCate = vrpCate;

        this.costs = new Expense();
        this.routes = new ArrayList<>();
        this.removeNodes = new ArrayList<Node>();
        // note: may use it
        // this.update_average_dist();
    }

    // public Solution toSolution() {
    //     Solution sol = new Solution();
    //     sol.setRoutes(this.routes.stream().filter(r -> r.getSize() > 2).collect(Collectors.toList()));
    //     sol.costs = this.costs;
    //     // sol.setVehicleNr(vehicleNr);
    //     sol.setInstance(instance);
    //     sol.setVrpCate(vrpCate);
    //     return sol;
    // }

    public ALNSSolution(ALNSSolution sol) {
        this.customerNr = sol.customerNr;
        this.vrpCate = sol.vrpCate;

        this.costs = new Expense(sol.costs);
        this.routes = new ArrayList<>();
        this.removeNodes = new ArrayList<Node>();

        for (Route route : sol.routes) {
            this.routes.add(route.cloneRoute());
        }

        this.average_dist = sol.average_dist;
    }

    public double update_average_dist() {
        return this.average_dist = this.routes.stream().mapToDouble(r -> r.costs.getDist()).average().getAsDouble();
    }

    public VRPCategory getVrpCate() {
        return vrpCate;
    }

    public void setVrpCate(VRPCategory vrpCate) {
        this.vrpCate = vrpCate;
    }

    public void addRoute(Route route) {
        this.routes.add(route);
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void removeRoute(int routePos) {
        pre_do(this.costs, this.routes.get(routePos));

        this.costs.setPrice(this.costs.getPrice() - this.vrpCate.getFare().getVehicleFee());
        this.costs.total_to_fare();

        this.routes.remove(routePos);
    }

    public void removeAllCustomerOfRoute(Route route) {
        pre_do(this.costs, route);

        while (route.getSize() > 2) {
            this.removeNodes.add(route.removeNode(1));
        }

        route.costs.reset();
        this.costs.total_to_fare();
    }

    private void pre_do(Expense fare, Route r) {
        fare.setDist(fare.getDist() - r.costs.getDist());
        fare.setLoad(fare.getLoad() - r.costs.getLoad());
        fare.setTime(fare.getTime() - r.costs.getTime());
        fare.setArc(fare.getArc() - r.costs.getArc());

        fare.setLoadVio(fare.getLoadVio() - r.costs.getLoadVio());
        fare.setTimeVio(fare.getTimeVio() - r.costs.getTimeVio());
        fare.setNodeVio(fare.getNodeVio() - r.costs.getNodeVio());

        fare.total_to_arc();
        // For SpreadRevive
        fare.setTotal(fare.getTotal() - r.costs.getTotal() * (r.costs.getDist() / average_dist));
    }

    private void post_do(Expense fare, Route r) {
        fare.setDist(fare.getDist() + r.costs.getDist());
        fare.setLoad(fare.getLoad() + r.costs.getLoad());
        fare.setTime(fare.getTime() + r.costs.getTime());
        fare.setArc(fare.getArc() + r.costs.getArc());

        fare.setLoadVio(fare.getLoadVio() + r.costs.getLoadVio());
        fare.setTimeVio(fare.getTimeVio() + r.costs.getTimeVio());
        fare.setNodeVio(fare.getNodeVio() + r.costs.getNodeVio());

        // For SpreadRevive
        fare.setTotal(fare.getTotal() + r.costs.getTotal() * (r.costs.getDist() / average_dist));
        fare.total_to_fare();
    }

    public void removeCustomer(int routePosition, int cusPosition) {

        Route route = this.routes.get(routePosition);
        pre_do(this.costs, route);

        double load = -route.getNode(cusPosition).getDemand();
        double dist = (-distance[route.getNode(cusPosition - 1).getId()][route.getNode(cusPosition).getId()]
                        - distance[route.getNode(cusPosition).getId()][route.getNode(cusPosition + 1).getId()]
                        + distance[route.getNode(cusPosition - 1).getId()][route.getNode(cusPosition + 1).getId()]);
        double arc = cusPosition == 1
                ? -distance[route.getNode(cusPosition).getId()][route.getNode(cusPosition + 1).getId()]
                : dist;
        double time = dist / vrpCate.getRate().getVehicleSpeed() + load / vrpCate.getRate().getServiceSpeed();

        route.costs.setDist(route.costs.getDist() + dist);
        route.costs.setLoad(route.costs.getLoad() + load);
        route.costs.setTime(route.costs.getTime() + time);
        route.costs.setArc(route.costs.getArc() + arc);

        // ! 移除节点
        removeNodes.add(route.removeNode(cusPosition));

        double routeLoad = route.costs.getLoad();
        if (routeLoad > vrpCate.getCons().getVehicleCapacity()) {
            double violaton = routeLoad - vrpCate.getCons().getVehicleCapacity();
            // this.costs.getLoad()Vio += violaton - removeRoute.costs.getLoad()Vio;
            route.costs.setLoadVio(violaton);
        } else if (route.costs.getLoadVio() > 0) {
            // this.costs.getLoad()Vio -= removeRoute.costs.getLoad()Vio;
            route.costs.setLoadVio(0);
        }

        double usedTime = route.costs.getTime();
        if (usedTime > vrpCate.getCons().getTime()) {
            double violation = usedTime - vrpCate.getCons().getTime();
            // this.costs.getTime()Vio += violation - removeRoute.costs.getTime()Vio;
            route.costs.setTimeVio(violation);
        } else if (route.costs.getTimeVio() > 0) {
            // this.costs.getTime()Vio -= removeRoute.costs.getTime()Vio;
            route.costs.setTimeVio(0);
        }

        if (route.costs.getNodeVio() > 0) {
            route.costs.setNodeVio(route.costs.getNodeVio() - 1);
            // this.costs.nodeNumVio -= 1;
        }

        route.costs.calc_total();
        post_do(this.costs, route);
    }

    public Expense evaluateRemoveCustomer(int routePosition, int cusPosition) {

        Expense newCost = new Expense(this.costs);
        Route route = this.routes.get(routePosition).cloneRoute();
        pre_do(newCost, route);

        double load = -route.getNode(cusPosition).getDemand();
        double dist = (-distance[route.getNode(cusPosition - 1).getId()][route.getNode(cusPosition).getId()]
                        - distance[route.getNode(cusPosition).getId()][route.getNode(cusPosition + 1).getId()]
                        + distance[route.getNode(cusPosition - 1).getId()][route.getNode(cusPosition + 1).getId()]);
        double arc = cusPosition == 1
                ? -distance[route.getNode(cusPosition).getId()][route.getNode(cusPosition + 1).getId()]
                : dist;
        double time = dist / vrpCate.getRate().getVehicleSpeed() + load / vrpCate.getRate().getServiceSpeed();

        route.costs.setDist(route.costs.getDist() + dist);
        route.costs.setLoad(route.costs.getLoad() + load);
        route.costs.setTime(route.costs.getTime() + time);
        route.costs.setArc(route.costs.getArc() + arc);

        // ! 移除节点
        route.removeNode(cusPosition);

        double routeLoad = route.costs.getLoad();
        if (routeLoad > vrpCate.getCons().getVehicleCapacity()) {
            double violaton = routeLoad - vrpCate.getCons().getVehicleCapacity();
            route.costs.setLoadVio(violaton);
        } else if (route.costs.getLoadVio() > 0) {
            route.costs.setLoadVio(0);
        }

        double usedTime = route.costs.getTime();
        if (usedTime > vrpCate.getCons().getTime()) {
            double violation = usedTime - vrpCate.getCons().getTime();
            route.costs.setTimeVio(violation);
        } else if (route.costs.getTimeVio() > 0) {
            route.costs.setTimeVio(0);
        }

        if (route.costs.getNodeVio() > 0) {
            route.costs.setNodeVio(route.costs.getNodeVio() - 1);
        }

        route.costs.calc_total();
        post_do(newCost, route);

        return newCost;
    }

    public void insertCustomer(int routePosition, int cusPosition, Node customer) {

        Route route = this.routes.get(routePosition);
        pre_do(this.costs, route);

        double load = +customer.getDemand();
        double dist = (+distance[route.getNode(cusPosition - 1).getId()][customer.getId()]
                        + distance[customer.getId()][route.getNode(cusPosition).getId()]
                        - distance[route.getNode(cusPosition - 1).getId()][route.getNode(cusPosition).getId()]);
        double arc = cusPosition == 1 ? +distance[customer.getId()][route.getNode(cusPosition).getId()] : dist;
        double time = dist / vrpCate.getRate().getVehicleSpeed() + load / vrpCate.getRate().getServiceSpeed();

        route.costs.setDist(route.costs.getDist() + dist);
        route.costs.setLoad(route.costs.getLoad() + load);
        route.costs.setTime(route.costs.getTime() + time);
        route.costs.setArc(route.costs.getArc() + arc);

        // ! 插入节点
        route.addNodeToRouteWithIndex(customer, cusPosition);

        double routeLoad = route.costs.getLoad();
        if (routeLoad > vrpCate.getCons().getVehicleCapacity()) {
            double violaton = routeLoad - vrpCate.getCons().getVehicleCapacity();
            // this.costs.getLoad()Vio += violaton - insertRoute.costs.getLoad()Vio;
            route.costs.setLoadVio(violaton);
        }

        double usedTime = route.costs.getTime();
        if (usedTime > vrpCate.getCons().getTime()) {
            double violation = usedTime - vrpCate.getCons().getTime();
            // this.costs.getTime()Vio += violation - insertRoute.costs.getTime()Vio;
            route.costs.setTimeVio(violation);
        }

        if (route.getSize() - 2 > vrpCate.getCons().getMaxCustomerNum()) {
            route.costs.setNodeVio(route.costs.getNodeVio() + 1);
            // this.costs.nodeNumVio += 1;
        }

        route.costs.calc_total();
        post_do(this.costs, route);
    }

    public Expense evaluateInsertCustomer(int routePosition, int cusPosition, Node customer) {

        Expense newCost = new Expense(this.costs);
        Route route = this.routes.get(routePosition).cloneRoute();
        pre_do(newCost, route);

        double dist = (+distance[route.getNode(cusPosition - 1).getId()][customer.getId()]
                        + distance[customer.getId()][route.getNode(cusPosition).getId()]
                        - distance[route.getNode(cusPosition - 1).getId()][route.getNode(cusPosition).getId()]);
        double arc = cusPosition == 1 ? +distance[customer.getId()][route.getNode(cusPosition).getId()] : dist;
        double load = +customer.getDemand();
        double time = dist / vrpCate.getRate().getVehicleSpeed() + load / vrpCate.getRate().getServiceSpeed();

        route.costs.setDist(route.costs.getDist() + dist);
        route.costs.setLoad(route.costs.getLoad() + load);
        route.costs.setTime(route.costs.getTime() + time);
        route.costs.setArc(route.costs.getArc() + arc);

        // ! 插入节点
        route.addNodeToRouteWithIndex(customer, cusPosition);

        double routeLoad = route.costs.getLoad();
        if (routeLoad > vrpCate.getCons().getVehicleCapacity()) {
            double violaton = routeLoad - vrpCate.getCons().getVehicleCapacity();
            route.costs.setLoadVio(violaton);
        }

        double usedTime = route.costs.getTime();
        if (usedTime > vrpCate.getCons().getTime()) {
            double violation = usedTime - vrpCate.getCons().getTime();
            route.costs.setTimeVio(violation);
        }

        if (route.getSize() - 2 > vrpCate.getCons().getMaxCustomerNum()) {
            route.costs.setNodeVio(route.costs.getNodeVio() + 1);
        }

        route.costs.calc_total();
        post_do(newCost, route);

        return newCost;
    }

    public boolean feasible() {
        boolean feasible = this.routes.stream()
                .allMatch(r -> r.costs.getNodeVio() <= vrpCate.getVio().getNodeVio()
                        && r.costs.getLoadVio() <= vrpCate.getVio().getLoadVio()
                        && r.costs.getTimeVio() <= vrpCate.getVio().getTimeVio());
        return feasible;
    }

    /**
     * Estimate if <code>this</code> is more feasible than sol
     */
    public boolean isFeasibleThan(ALNSSolution sol) {
        return this.costs.getNodeVio() <= sol.costs.getNodeVio() || this.costs.getLoadVio() <= sol.costs.getLoadVio()
                || this.costs.getTimeVio() <= sol.costs.getTimeVio();
    }

    @Override
    public String toString() {
        String result = String.format(
                "\nALNS_Solution { route_number = %d , customer_number = %d , \n    Expense = %s , \n\n    Violation Overview:",
                this.routes.size(), this.customerNr, this.costs);

        String time_vio_result = "\n\troute_id\ttime_violation";
        String load_vio_result = "\n\troute_id\tload_violation";
        String node_vio_result = "\n\troute_id\tnode_violation";
        String route_result = "\n\n    Routes = [";

        for (Route route : this.routes) {
            if (route.costs.getTimeVio() > 0) {
                time_vio_result += String.format("\n\t%8d\t\t%6.1f", route.getId(), route.costs.getTimeVio());
            }
            if (route.costs.getLoadVio() > 0) {
                load_vio_result += String.format("\n\t%8d\t\t%6.0f", route.getId(), route.costs.getLoadVio());
            }
            if (route.costs.getNodeVio() > 0) {
                node_vio_result += String.format("\n\t%8d\t\t%6d", route.getId(), route.costs.getNodeVio());
            }
            if (route.getSize() > 2) {
                route_result += "\n\t" + route;
            }
        }
        return result + time_vio_result + load_vio_result + node_vio_result + route_result + "\n    ]\n}";
    }
}
