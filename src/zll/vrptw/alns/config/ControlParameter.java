package zll.vrptw.alns.config;

public class ControlParameter {

    private final boolean init_sol_chart;
    private final boolean s1_sol_chart;
    private final boolean s2_sol_chart;
    private final boolean global_sol_chart;

    public ControlParameter(boolean init_sol_chart, boolean s1_sol_chart, boolean s2_sol_chart,
            boolean global_sol_chart) {
        this.init_sol_chart = init_sol_chart;
        this.s1_sol_chart = s1_sol_chart;
        this.s2_sol_chart = s2_sol_chart;
        this.global_sol_chart = global_sol_chart;
    }

    public boolean isGlobal_sol_chart() {
        return global_sol_chart;
    }

    public boolean isS2_sol_chart() {
        return s2_sol_chart;
    }

    public boolean isS1_sol_chart() {
        return s1_sol_chart;
    }

    public boolean isInit_sol_chart() {
        return init_sol_chart;
    }
}
