package younger.vrp.algrithm;

import java.util.ArrayList;
import java.util.List;

import younger.vrp.instance.Node;
import younger.vrp.instance.Route;
import younger.vrp.alns.config.VRPCatetory;
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

    /**
     * All the vehicles.
     */
    private List<Route> vehicles;

    /**
     * The distance matrix for the customers
     */
    private double[][] distanceMatrix;

    private int vehicleCapacity;
    private int maxCustomerNum;

    /**
     * Constructor
     */
    public GreedyVRP(Instance instance) {
        this.customers = instance.getCustomers();
        this.initialCustomerNr = instance.getCustomerNr();
        this.distanceMatrix = instance.getDistanceMatrix();
        this.vehicleCapacity = instance.getVehicleCapacity();
        this.maxCustomerNum = instance.getMaxCustomerNum();

        int vehicleNr = instance.getVehicleNr();
        this.vehicles = new ArrayList<Route>();
        for (int i = 0; i < vehicleNr; ++i) {
            Route route = new Route(i);
            this.vehicles.add(route);
        }
    }

    private int initialCustomerNr;

    public int getCustomerNr() {
        return this.initialCustomerNr;
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    /**
     * Finds and returns a solution to the VRP using greedy algorithm approach
     *
     * @return Solution
     */
    public Solution getInitialSolution(VRPCatetory cata) {
        // The final Solution
        Solution solution = new Solution();

        // ! 节点中取取第一个为仓库，并移除
        Node depot = this.customers.remove(0);

        // Fetch the first available vehicle
        Route currentVehicle = this.vehicles.remove(0);

        // Add the depot to the vehicle.
        // ! 把仓库放入第一条路径的第一个位置
        currentVehicle.add(depot);

        // Repeat until all customers are routed or if we run out vehicles.
        while (true) {

            // If we served all customers, exit.
            if (this.customers.size() == 0)
                break;

            // Get the last node of the current route. We will try to find the closest node
            // to it that also satisfies the capacity constraint.
            Node lastNode = currentVehicle.getLastNode();

            // The distance of the closest node, if any, to the last node in the route.
            double smallestDistance = Double.MAX_VALUE;

            // The closest node, if any, to the last node in the route that also satisfies
            // the capacity constraint.
            Node closestNode = null;

            // Find the nearest neighbor based on distance
            for (Node n : this.customers) {
                double distance = this.distanceMatrix[lastNode.getId()][n.getId()];

                // If we found a customer with closer that the value of "smallestDistance"
                // ,store him temporarily
                boolean isAddThisNodeToRoute = 
                    // currentVehicle.getSize() - 1 < this.maxCustomerNum && 
                    distance < smallestDistance && isSatisfyConstraint(depot, currentVehicle, n);
                if (isAddThisNodeToRoute) {
                    smallestDistance = distance;
                    closestNode = n;
                }
            }

            // A node that satisfies the capacity constraint found
            if (closestNode != null) {
                // Increase the cost of the current route by the distance of the previous final
                // node to the new one
                currentVehicle.getCost().cost += smallestDistance;
                solution.cost.cost += smallestDistance;

                // Increase the time of the current route by the distance of the previous final
                // node to the new one and serves time
                currentVehicle.getCost().time += smallestDistance;
                solution.cost.time += smallestDistance;

                // waiting time windows open
                if (currentVehicle.getCost().time < closestNode.getTW()[0]) {
                    solution.cost.time += (closestNode.getTW()[0] - currentVehicle.getCost().time);
                    currentVehicle.getCost().time = closestNode.getTW()[0];
                }

                currentVehicle.getCost().time += closestNode.getServiceTime();
                solution.cost.time += closestNode.getServiceTime();

                // Increase the load of the vehicle by the demand of the new node-customer
                currentVehicle.getCost().load += closestNode.getDemand();
                solution.cost.load += closestNode.getDemand();

                // Add the closest node to the route
                currentVehicle.add(closestNode);

                // Remove customer from the non-served customers list.
                this.customers.remove(closestNode);

                // We didn't find any node that satisfies the condition.
            } else {
                // Increase cost by the distance to travel from the last node back to depot
                currentVehicle.getCost().cost += this.distanceMatrix[lastNode.getId()][depot.getId()];
                currentVehicle.getCost().time += this.distanceMatrix[lastNode.getId()][depot.getId()];
                solution.cost.cost += this.distanceMatrix[lastNode.getId()][depot.getId()];
                solution.cost.time += this.distanceMatrix[lastNode.getId()][depot.getId()];

                // Terminate current route by adding the depot as a final destination
                currentVehicle.add(depot);

                // !路径结束，计算这条路径的 total cost
                currentVehicle.getCost().calculateTotalCost();

                // Add the finalized route to the solution
                solution.addRoute(currentVehicle);

                // If we used all vehicles, exit.
                if (this.vehicles.size() == 0) {
                    break;

                    // if we still have some vehicles, use.
                } else {
                    // Recruit a new vehicle.
                    currentVehicle = this.vehicles.remove(0);

                    // Add the depot as a starting point to the new route
                    currentVehicle.add(depot);
                }
            }
        }

        // Now add the final route to the solution
        Node lastNode = currentVehicle.getLastNode();
        currentVehicle.getCost().cost += this.distanceMatrix[lastNode.getId()][depot.getId()];
        currentVehicle.getCost().time += this.distanceMatrix[lastNode.getId()][depot.getId()];
        solution.cost.cost += this.distanceMatrix[lastNode.getId()][depot.getId()];
        solution.cost.time += this.distanceMatrix[lastNode.getId()][depot.getId()];

        currentVehicle.add(depot);
        currentVehicle.getCost().calculateTotalCost();
        solution.addRoute(currentVehicle);

        return solution;
    }

    private boolean isSatisfyConstraint(Node depot, Route route, Node n) {
        Node lastNode = route.getLastNode();
        boolean isSatisfyCapacity = (route.getCost().load + n.getDemand()) <= vehicleCapacity;
        boolean isSatisfyTW = (route.getCost().time + distanceMatrix[lastNode.getId()][n.getId()]) < n.getTW()[1];
        boolean isSatisfyDepotTW = (route.getCost().time + distanceMatrix[lastNode.getId()][n.getId()]
                + n.getServiceTime() + distanceMatrix[n.getId()][depot.getId()]) < depot.getTW()[1];
        return isSatisfyCapacity && isSatisfyTW && isSatisfyDepotTW;
    }
}
