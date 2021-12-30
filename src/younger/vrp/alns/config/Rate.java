package younger.vrp.alns.config;

/**
 * Assumed rates will be used in VRP.
 * 速率包括车速和卸货速，其真实值随车型、路况、天气等动态变化，现在暂时取的估计值，为保险起见，取值都较低。
 * 360米/分 近似为 21.6公里/时
 */
public enum Rate {

    None(1, 1), Case1(360, 8), Case2(420, 8), Case3(480, 8), Case4(600, 8), Case5(720, 8);

    /**
     * @apiNote
     * The average speed of vehicles.
     * Unit: meter/minute
     */
    private double vehicleSpeed;
    /**
     * @apiNote
     * The average service speed of unload.
     * Unit: entry/minute
     */
    private double serviceSpeed;

    public double getVehicleSpeed() {
        return vehicleSpeed;
    }

    public double getServiceSpeed() {
        return serviceSpeed;
    }

    private Rate(double vehicleSpeed, double serviceSpeed) {
        this.vehicleSpeed = vehicleSpeed;
        this.serviceSpeed = serviceSpeed;
    }

}