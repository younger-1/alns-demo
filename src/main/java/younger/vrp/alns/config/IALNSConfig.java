package younger.vrp.alns.config;

public interface IALNSConfig {

    /**
     * @return
     * 总迭代次数
     */
    int get_u();

    /**
     * @return
     * 第一阶段更新次数 v
     */
    int get_v();

    /**
     * @return
     * 第二阶段更新次数 w
     */
    int get_w();

    /**
     * @return
     * 更新算子的权重和选择概率的间隔迭代次数
     */
    int get_tau();

    /**
     * @return
     * 算子的权重更新速度
     */
    double get_lambda();

    /**
     * @return
     * 发现全局最优解时所加分数
     */
    int get_reward_1();

    /**
    * @return
    * 发现局部最优解时所加分数
    */
    int get_reward_2();

    /**
     * @return
     * 发现可接受的较差解时所加分数
     */
    int get_reward_3();

    /**
     * @return
     * 模拟退火(SA)的降温系数
     */
    double get_sa();
}