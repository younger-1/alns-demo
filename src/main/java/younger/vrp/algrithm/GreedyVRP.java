package younger.vrp.algrithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import younger.vrp.instance.Node;
import younger.vrp.instance.Route;
import younger.vrp.alns.config.VRPCategory;
import younger.vrp.base.IDistance;
import younger.vrp.instance.Instance;

/**
 * This class contains all the necessary functionality in order to solve the VRP
 * problem using a greedy approach.
 */
public class GreedyVRP {

    /**
     * All the customers
     */
    private List<Node> customers;
    private int customerNr;

    /**
     * All the vehicles.
     */
    private List<Route> available_vehicles;

    /**
     * The distance matrix for the customers
     */
    private int[][] distanceMatrix = IDistance.getDistanceInstance().distanceMatrix();

    private int vehicleCapacity;
    private int maxCustomerNum;
    private double maxTime;

    /**
     * Constructor
     */
    public GreedyVRP(Instance instance) {
        this.customers = instance.getCopyOfCustomers();
        this.customerNr = this.customers.size() - 1;
    }

    private ALNSSolution pre_init(VRPCategory cate) {

        ALNSSolution sol = new ALNSSolution(this.customerNr ,cate);

        this.vehicleCapacity = cate.getCons().getVehicleCapacity();
        this.maxCustomerNum = cate.getCons().getMaxCustomerNum();
        this.maxTime = cate.getCons().getTime();

        int availableVehicleNr = cate.getCons().getVehicleNr();
        this.available_vehicles = new ArrayList<Route>();

        // * ZDVRP or Not
        if (!cate.isAddDepotToEnd()) {
            Arrays.stream(this.distanceMatrix).forEach(x -> x[0] = 0);
        }

        // Route.referenceDistance(this.distanceMatrix);
        for (int i = 0; i < availableVehicleNr; ++i) {
            Route route = new Route(i);
            this.available_vehicles.add(route);
        }

        // * Examine abnormal demand of customers
        Node depot = this.customers.get(0);
        for (int i = 0; i < this.customers.size(); i++) {
            Node n = this.customers.get(i);
            double loadViolation = n.getDemand() - this.vehicleCapacity;
            if (loadViolation > 0) {
                Route overloadedVehicle = this.available_vehicles.remove(0);
                overloadedVehicle.add(depot);
                overloadedVehicle.add(this.customers.remove(i));
                overloadedVehicle.add(depot);

                // load
                overloadedVehicle.costs.setLoad(n.getDemand());

                // distance
                overloadedVehicle.costs.setDist(distanceMatrix[0][i] + distanceMatrix[i][0]);

                // time
                double travel_time = distanceMatrix[0][i] / cate.getRate().getVehicleSpeed();
                overloadedVehicle.costs.setTime(travel_time);

                // loadViolation
                overloadedVehicle.costs.setLoadVio(loadViolation);

                overloadedVehicle.costs.calc_total();

                // NOTE: don't arrange the vehicles with such customer
                // sol.addRoute(overloadedVehicle);

                // NOTE: move this to where data is readin and print demands
                System.err.println("\u001B[45m\u001B[37mCustomer {id:" + n.getId() + ", demand:" + n.getDemand()
                        + "} exceeds the maximum capacity of the vehicle!\u001B[0m");

                i--;
            }
        }

        return sol;
    }

    /**
     * Finds and returns a solution to the VRP using greedy algorithm approach
     *
     * @return ALNSSolution
     */
    public ALNSSolution getInitialSolution(VRPCategory cate) {
        // The result Solution
        ALNSSolution solution = pre_init(cate);

        // * Relax in a route
        double customerNumRelax = cate.getIr().getCustomerNumRelax();
        double capacityRelax = cate.getIr().getCapacityRelax();
        double timeRelax = cate.getIr().getTimeRelax();

        // ! 节点中取取第一个为仓库，并移除
        Node depot = this.customers.remove(0);

        // Fetch the first available vehicle
        Route currentVehicle = this.available_vehicles.remove(0);

        // Add the depot to the vehicle.
        // ! 把仓库放入第一条路径的第一个位置
        currentVehicle.add(depot);

        // Repeat until all customers are routed or if we run out vehicles.
        while (true) {
            // Get the last node of the current route. We will try to find the closest node
            // to it that also satisfies the capacity constraint.
            Node lastNode = currentVehicle.getLastNode();

            // The distance of the closest node, if any, to the last node in the route.
            double smallestDistance = Double.MAX_VALUE;

            // The closest node, if any, to the last node in the route that also satisfies
            // the capacity constraint.
            Node closestNode = null;
            int closestNodeIndex = -1;

            // Find the nearest neighbor based on distance
            for (int i = 0; i < this.customers.size(); i++) {
                Node n = this.customers.get(i);
                double distance = this.distanceMatrix[lastNode.getId()][n.getId()];

                if (currentVehicle.getSize() == 1) {
                    // boolean isBreakCapacityOfEmptyVehicle = n.getDemand() > vehicleCapacity * (1 - capacityRelax);
                    // if (isBreakCapacityOfEmptyVehicle) {
                    smallestDistance = distance;
                    closestNode = n;
                    closestNodeIndex = i;
                    break;
                    // }
                }
                if (distance < smallestDistance) {
                    boolean isSatisfyCapacity = (currentVehicle.costs.getLoad() + n.getDemand()) <= vehicleCapacity
                            * (1 - capacityRelax);
                    boolean isSatisfyNodeNumber = currentVehicle.getSize() - 1 < this.maxCustomerNum * (1 - customerNumRelax);
                    boolean isSatisfyTime = currentVehicle.costs.getTime() + distance / cate.getRate().getVehicleSpeed()
                            + n.getDemand() / cate.getRate().getServiceSpeed() <= maxTime * (1 - timeRelax);

                    if (cate.isSatisfy(isSatisfyCapacity, isSatisfyNodeNumber, isSatisfyTime)) {
                        smallestDistance = distance;
                        closestNode = n;
                        closestNodeIndex = i;
                    }
                }
            }

            // A node that satisfies the capacity constraint found
            if (closestNode != null) {
                // ! Increase the cost of the current route by the distance of the previous final
                currentVehicle.costs.setDist(currentVehicle.costs.getDist() + smallestDistance);

                // ! Increase the time of the current route by the distance of the previous final
                double travel_time = smallestDistance / cate.getRate().getVehicleSpeed();
                currentVehicle.costs.setTime(currentVehicle.costs.getTime() + travel_time);

                double service_time = closestNode.getDemand() / cate.getRate().getServiceSpeed();
                currentVehicle.costs.setTime(currentVehicle.costs.getTime() + service_time);

                // ! Increase the load of the vehicle by the demand of the new node-customer
                currentVehicle.costs.setLoad(currentVehicle.costs.getLoad() + closestNode.getDemand());

                // Add the closest node to the route
                currentVehicle.add(closestNode);

                // Remove customer from the non-served customers list.
                this.customers.remove(closestNodeIndex);

            }
            // We didn't find any node that satisfies the condition
            // or we have served all customers,
            // then we should put this route to solution
            if (closestNode == null || this.customers.size() == 0) {
                // Increase cost by the distance to travel from the last node back to depot
                double distance_to_depot = this.distanceMatrix[currentVehicle.getLastNode().getId()][depot.getId()];
                currentVehicle.costs.setDist(currentVehicle.costs.getDist() + distance_to_depot);

                double time_to_depot = distance_to_depot / cate.getRate().getVehicleSpeed();
                currentVehicle.costs.setTime(currentVehicle.costs.getTime() + time_to_depot);

                // Terminate current route by adding the depot as a final destination
                currentVehicle.add(depot);

                // Add the finalized route to the solution
                solution.addRoute(currentVehicle);

                // If we used all vehicles, exit.
                if (this.available_vehicles.size() == 0) {
                    // review: add more car
                    System.err.println("The vehicles are used up! Please add more vehicles!");
                    break;
                    // if we still have some vehicles and still have customers to be served
                } else if (this.customers.size() != 0) {
                    // Recruit a new vehicle.
                    currentVehicle = this.available_vehicles.remove(0);
                    // Add the depot as a starting point to the new route
                    currentVehicle.add(depot);
                }
            }

            // If we served all customers, exit.
            if (this.customers.size() == 0)
                break;
        }

        // * Pre-recruit vehicles
        int redundant_vehicles = (int) (cate.getIr().getVehicleNumRelax() * solution.getRoutes().size());
        for (int i = 0; i < redundant_vehicles; i++) {
            if (this.available_vehicles.size() > 0) {
                currentVehicle = this.available_vehicles.remove(0);
                currentVehicle.add(depot);
                currentVehicle.add(depot);
                solution.addRoute(currentVehicle);
            }
        }

        solution.costs.setPrice(cate.getFare().getVehicleFee() * solution.getRoutes().size());

        post_init(solution);

        return solution;
    }

    private void post_init(ALNSSolution sol) {
        for (Route route : sol.getRoutes()) {
            route.costs.setArc(this.calcArc(route));
            route.costs.calc_total();

            sol.costs.setArc(sol.costs.getArc() + route.costs.getArc());
            sol.costs.setDist(sol.costs.getDist() + route.costs.getDist());
            sol.costs.setLoad(sol.costs.getLoad() + route.costs.getLoad());
            sol.costs.setTime(sol.costs.getTime() + route.costs.getTime());

            sol.costs.setLoadVio(sol.costs.getLoadVio() + route.costs.getLoadVio());

            // For SpreadRevive
            // sol.costs.setTotal(sol.costs.getTotal() + route.costs.getTotal() * (route.costs.getDist() / average_dist));
            sol.costs.setTotal(sol.costs.getTotal() + route.costs.getTotal());
        }
        sol.costs.total_to_fare();
    }

    private double calcArc(Route route) {
        return route.costs.getDist()
                - distanceMatrix[0][route.getNodeId(1)]
                - distanceMatrix[route.getNodeId(route.getSize() - 2)][0];
    }
}
