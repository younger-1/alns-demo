package younger.vrp.alns.config;

public enum ALNSConfiguration implements IALNSConfig {

    Default(20, 30, 5000, 500, 0.1, 20, 5, 1, 0.99937, 0.05, 0.5),
    CostMin(30, 10, 10000, 500, 0.1, 20, 5, 1, 0.99937, 0.05, 0.5),
    TotalMin(10, 30, 10000, 500, 0.1, 20, 5, 1, 0.99937, 0.05, 0.5);

    private final int v;// 迭代次数v
    private final int w;// 迭代次数w
    private final int omega;// 迭代次数
    private final int tau;// 更新算子选择概率的间隔迭代次数
    private final double r_p;// 计算概率
    private final int sigma_1;// 发现全局最优，add
    private final int sigma_2;// 发现局部最优，add
    private final int sigma_3;// 发现较差，add
    private final double c;
    private final double delta;
    private final double big_omega;

    ALNSConfiguration(int v, int w, int omega, int tau, double r_p, int sigma_1, int sigma_2, int sigma_3, double c,
            double delta, double big_omega) {
        this.v = v;
        this.w = w;
        this.omega = omega;
        this.tau = tau;
        this.r_p = r_p;
        this.sigma_1 = sigma_1;
        this.sigma_2 = sigma_2;
        this.sigma_3 = sigma_3;
        this.c = c;
        this.delta = delta;
        this.big_omega = big_omega;
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public int getV() {
        return v;
    }

    @Override
    public int getOmega() {
        return omega;
    }

    @Override
    public int getTau() {
        return tau;
    }

    @Override
    public double getR_p() {
        return r_p;
    }

    @Override
    public int getSigma_1() {
        return sigma_1;
    }

    @Override
    public int getSigma_2() {
        return sigma_2;
    }

    @Override
    public int getSigma_3() {
        return sigma_3;
    }

    @Override
    public double getC() {
        return c;
    }

    @Override
    public double getDelta() {
        return delta;
    }

    @Override
    public double getBig_omega() {
        return big_omega;
    }
}
