package younger.vrp.alns.config;

public enum VRPCategory {

    CVRP(true, Constraint.Default, MaxViolation.Default, Fare.Default, Rate.Case1, InitRelax.None),
    OVRP(false, Constraint.Default, MaxViolation.Default, Fare.Default, Rate.Case1, InitRelax.Z2);

    private boolean addDepotToEnd;
    private Constraint cons;
    private MaxViolation violation;
    private Fare fare;
    private Rate rate;
    private InitRelax ir;

    private VRPCategory(boolean addDepotToEnd, Constraint cons, MaxViolation mv, Fare fare, Rate rate, InitRelax ir) {
        this.addDepotToEnd = addDepotToEnd;
        this.cons = cons;
        this.violation = mv;
        this.fare = fare;
        this.rate = rate;
        this.ir = ir;
    }

    /**
     * @return
     * true 则为 CVRP， false 则为 OVRP
     */
    public boolean isAddDepotToEnd() {
        return addDepotToEnd;
    }

    /**
     * @return
     * 速率：车速，卸货速度
     */
    public Rate getRate() {
        return rate;
    }

    /**
     * @return
     * 约束：可用车辆数，车容量，每辆车可服务的团长数，车辆时间
     */
    public Constraint getCons() {
        return cons;
    }

    /**
     * @return
     * 可容许的约束违反量：车容量，每辆车可服务的团长数，车辆时间
     */
    public MaxViolation getVio() {
        return violation;
    }

    /**
     * @return
     * 初始松弛
     */
    public InitRelax getIr() {
        return ir;
    }

    /**
     * @return
     * 资源的单位费用
     */
    public Fare getFare() {
        return fare;
    }

}