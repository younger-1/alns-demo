package younger.vrp.alns.config;

/**
 * The fee of each kind of resourse used in VRP.
 */
public enum Fare {
    Default(400);

    private double vehicleFee;

    public double getVehicleFee() {
        return vehicleFee;
    }

    private Fare(double vehicleFee) {
        this.vehicleFee = vehicleFee;
    }

}