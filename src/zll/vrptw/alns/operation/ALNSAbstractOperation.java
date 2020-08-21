package zll.vrptw.alns.operation;

import java.util.*;

public abstract class ALNSAbstractOperation implements IALNSOperation {
    // protected final ALNSStrategieVisualizationManager asvm = new
    // ALNSStrategieVisualizationManager();
    private final Random r = new Random();
    private int pi;
    private double p; // ! ���Ӵ�����֮1������ѡ����
    private int draws; // ! ��ʹ�õĴ���
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
    // �Ż���������⣬������piֵ
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
