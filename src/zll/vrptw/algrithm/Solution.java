package zll.vrptw.algrithm;

import java.util.ArrayList;
import java.util.List;

import zll.vrptw.instance.Route;

/**
 * @author zll_hust
 *
 *         An instance of this class repserents a solution to the VRP problem.
 */
public class Solution {
    public double testTime;

    /**
     * All the routes of the current solution.
     */
    private List<Route> routes;

    /**
     * The number of the vehicles.
     */
    private int vehicleNr;

    public Cost cost;

    /**
     * Default constructor
     */
    public Solution() {
        this.routes = new ArrayList<>();
        this.cost = new Cost();
        this.vehicleNr = 0;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route) {
        this.routes.add(route);
    }

    public double getTotalCost() {
        return this.cost.cost;
    }

    public void setTotalCost(double totalCost) {
        this.cost.cost = totalCost;
    }

    public int getVehicleNr() {
        return vehicleNr;
    }

    public void setVehicleNr(int vehicleNr) {
        this.vehicleNr = vehicleNr;
    }

    @Override
    public String toString() {
        String result = "\nSolution { " + "totalCost = " + Math.round(this.cost.cost * 100) / 100.0 + ", routes = [";

        for (Route vehicle : this.routes) {
            if (vehicle.getRoute().size() > 2)
                result += "\n\t" + vehicle;
        }

        return result + "]}";
    }
}