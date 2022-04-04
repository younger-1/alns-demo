package younger.vrp.alns.operation;

public interface IALNSOperation {

    /**
     * 算子使用次数加 1
     */
    void drawn();

    /**
     * @return
     * 算子总使用次数
     */
    int get_total_draws();

    /**
     * 设置算子总使用次数
     */
    void set_total_draws(int draws);

    /**
     * @return
     * 算子使用次数
     */
    int get_draws();

    /**
     * 设置算子使用次数
     * @param d
     */
    void set_draws(int d);

    /**
    * @return
    * 算子总得分
    */
    int get_total_score();

    /**
     * 设置算子总得分
     */
    void set_total_score(int score);

    /**
     * @return
     * 算子得分
     */
    int get_score();

    /**
     * 设置算子得分
     * @param pi
     */
    void set_score(int pi);

    /**
     * 增加算子得分
     * @param pi
     */
    void add_to_score(int pi);

    /**
     * @return
     * 算子的权重
     */
    double get_w();

    /**
     * 设置算子权重
     */
    void set_w(double p);

    /**
    * @return
    * 算子被选择的概率
    */
    double get_p();

    /**
     * 设置算子被选择的概率
     * @param p
     */
    void set_p(double p);

}
