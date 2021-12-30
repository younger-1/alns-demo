package younger.vrp.alns.config;

/**
 * More relax will cause more <strong>vehicles</strong>, but can improve <strong>routes presentation</strong> greatly.
 */
public enum InitRelax {
    // * ABC for degree of customer relax
    // * 123 for degree of capacity relax
    // * IJK for degree of time relax
    None(0, 0, 0, 0),
    Z1(1 / 4.0, 3 / 14.0, 1 / 10.0, 1 / 20.0),
    Z2(1 / 8.0, 3 / 14.0, 1 / 10.0, 1 / 20.0),
    A1I(0, 1 / 14.0, 1 / 14.0, 1 / 30.0),
    A2I(0, 1 / 14.0, 1 / 7.0, 1 / 30.0),
    B1I(0, 1 / 7.0, 1 / 14.0, 1 / 30.0),
    B2I(0, 1 / 7.0, 1 / 7.0, 1 / 30.0),
    B3I(0, 1 / 7.0, 3 / 14.0, 1 / 30.0),
    A1J(0, 1 / 14.0, 1 / 14.0, 1 / 15.0),
    A2J(0, 1 / 14.0, 1 / 7.0, 1 / 15.0),
    A3J(0, 1 / 14.0, 3 / 14.0, 1 / 15.0),
    B1J(0, 1 / 7.0, 1 / 14.0, 1 / 15.0),
    B2J(0, 1 / 7.0, 1 / 7.0, 1 / 15.0),
    B3J(0, 1 / 7.0, 3 / 14.0, 1 / 15.0);

    private double vehicleNumRelax;
    private double customerNumRelax;
    private double capacityRelax;
    private double timeRelax;

    private InitRelax(double vehicleNumRelax, double customerNumRelax, double capacityRelax, double timeRelax) {
        this.vehicleNumRelax = vehicleNumRelax;
        this.customerNumRelax = customerNumRelax;
        this.capacityRelax = capacityRelax;
        this.timeRelax = timeRelax;
    }

    public double getTimeRelax() {
        return timeRelax;
    }

    public double getCapacityRelax() {
        return capacityRelax;
    }

    public double getCustomerNumRelax() {
        return customerNumRelax;
    }

    public double getVehicleNumRelax() {
        return vehicleNumRelax;
    }

}