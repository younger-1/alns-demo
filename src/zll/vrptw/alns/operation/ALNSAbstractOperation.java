package zll.vrptw.alns.operation;

import java.util.*;

public abstract class ALNSAbstractOperation implements IALNSOperation {
    // protected final ALNSStrategieVisualizationManager asvm = new
    // ALNSStrategieVisualizationManager();
    private int pi;
    private double p; // ! 初始值算子次数分之1，轮盘选择用。归一化的概率.
    private int draws; // ! 被使用的次数
    private double w;

    /*
     * public ALNSStrategieVisualizationManager getVisualizationManager() { return
     * asvm; }
     */
    @Override

    public void drawn() {
        draws++;
    }

    @Override
    // 优化最优满意解，则增加pi值
    public void addToPi(int pi) {
        this.pi += pi;
    }

    public int getPi() {
        return this.pi;
    }

    public void setPi(int pi) {
        this.pi = pi;
    }

    public double getP() {
        return this.p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public int getDraws() {
        return this.draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public double getW() {
        return this.w;
    }

    public void setW(double w) {
        this.w = w;
    }
}
