package younger.vrp.alns.config;

/**
 * 约束对应的最大违反量
 * @apiNote
 * 计算 total 所用的参数可通过根目录下的 violation_parameter_fitting.py 脚本拟合获取
 */
public enum MaxViolation {
    Default(500, 5, 10),
    ;

    private int loadViolation;
    private int nodeViolation;
    private double timeViolation;

    private MaxViolation(int loadViolation, int nodeViolation, double timeViolation) {
        this.loadViolation = loadViolation;
        this.nodeViolation = nodeViolation;
        this.timeViolation = timeViolation;
    }

    public double getTimeVio() {
        return timeViolation;
    }

    public int getNodeVio() {
        return nodeViolation;
    }

    public int getLoadVio() {
        return loadViolation;
    }

}