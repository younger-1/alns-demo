package younger.vrp.alns.operation;

import java.util.Random;

import younger.vrp.alns.config.VRPCategory;

public abstract class ALNSAbstractOperation implements IALNSOperation {

    private int total_draws;    // 算子总使用次数
    private int total_score;    // 算子总得分

    private int draws;          // 算子使用次数
    private int score;          // 算子得分
    private double w;           // 算子权重
    private double p;           // 算子被选择的概率：初始值为算子个数分之1，轮盘选择用

    protected Random random;

    /**
     * VRP 问题的输入，包括：是否OVRP、约束条件、费用、速率、初始松弛
     */
    protected static VRPCategory vrpCate;

    public static void use(VRPCategory cate) {
        vrpCate = cate;
    }

    @Override
    public int get_total_draws() {
        return total_draws;
    }

    @Override
    public void set_total_draws(int draws) {
        total_draws = draws;
    }

    @Override
    public void drawn() {
        total_draws++;
        draws++;
    }

    @Override
    public int get_draws() {
        return this.draws;
    }

    @Override
    public void set_draws(int draws) {
        this.draws = draws;
    }

    @Override
    public int get_total_score() {
        return total_score;
    }

    @Override
    public void set_total_score(int score) {
        total_score = score;
    }

    @Override
    public void add_to_score(int reward) {
        this.score += reward;
    }

    @Override
    public int get_score() {
        return this.score;
    }

    @Override
    public void set_score(int pi) {
        this.score = pi;
    }

    @Override
    public double get_w() {
        return this.w;
    }

    @Override
    public void set_w(double w) {
        this.w = w;
    }

    @Override
    public double get_p() {
        return this.p;
    }

    @Override
    public void set_p(double p) {
        this.p = p;
    }
}
