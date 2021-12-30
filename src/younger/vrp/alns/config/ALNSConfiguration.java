package younger.vrp.alns.config;

public enum ALNSConfiguration implements IALNSConfig {

    Default(2000, 3, 3, 300, 0.1, 15, 9, 2, 0.99967),
    CostMin(20000, 30, 10, 250, 0.1, 25, 8, 1, 0.99967),
    TotalMin(20000, 10, 30, 250, 0.1, 25, 8, 1, 0.99967);

    private final int u;                // 迭代次数
    private final int v;                // 第一阶段更新次数 v
    private final int w;                // 第二阶段更新次数 w
    private final int tau;              // 更新算子的权重和选择概率的间隔迭代次数
    private final double lambda;        // 算子的权重更新速度
    private final int reward_1;         // 发现全局最优
    private final int reward_2;         // 发现局部最优
    private final int reward_3;         // 发现较差
    private final double sa;            // 模拟退火(SA)的降温系数

    private ALNSConfiguration(int u, int v, int w, int tau, double lambda, int reward_1, int reward_2, int reward_3, double sa) {
        this.u = u;
        this.v = v;
        this.w = w;
        this.tau = tau;
        this.lambda = lambda;
        this.reward_1 = reward_1;
        this.reward_2 = reward_2;
        this.reward_3 = reward_3;
        this.sa = sa;
    }

    @Override
    public int get_w() {
        return w;
    }

    @Override
    public int get_v() {
        return v;
    }

    @Override
    public int get_u() {
        return u;
    }

    @Override
    public int get_tau() {
        return tau;
    }

    @Override
    public double get_lambda() {
        return lambda;
    }

    @Override
    public int get_reward_1() {
        return reward_1;
    }

    @Override
    public int get_reward_2() {
        return reward_2;
    }

    @Override
    public int get_reward_3() {
        return reward_3;
    }

    @Override
    public double get_sa() {
        return sa;
    }
}
