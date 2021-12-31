package younger.vrp.alns.config;

/**
 * Available resources will be consumed in VRP
 */
public enum Constraint {
    Default(999, 2000, 14, 300),
    ;

    public void setVehicleNr(int x) {
        this.vehicleNr = x;
    }

    public void setVehicleCapacity(int x) {
        this.vehicleCapacity = x;
    }

    private Constraint(int vehicleNr, int vehicleCapacity, int maxCustomerNum, double time) {
        this.vehicleNr = vehicleNr;
        this.vehicleCapacity = vehicleCapacity;
        this.maxCustomerNum = maxCustomerNum;
        this.time = time;
    }

    /**
     * @apiNote
     * The available vehicles numbers. Due to impl of GreedyVRP,
     * it will result in using necessary less vehicles to get init solution.
     */
    private int vehicleNr;

    /**
     * @apiNote
     * The capacity of vehicles.
     */
    private int vehicleCapacity;

    /**
     * @apiNote
     * The max number of customers, not nodes.
     * The route will always contain two nodes,
     * one at the first of the route and another the last, which represents the depot
     */
    private int maxCustomerNum;

    /**
     * @apiNote
     * The max interval time of vehicle travel from depot to last customer.
     * Unit: minute
     */
    private double time;

    public int getVehicleNr() {
        return vehicleNr;
    }

    public int getMaxCustomerNum() {
        return maxCustomerNum;
    }

    public int getVehicleCapacity() {
        return vehicleCapacity;
    }

    public double getTime() {
        return time;
    }

}