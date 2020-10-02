package zll.vrptw.algrithm;

import java.io.IOException;
import java.util.Random;

import zll.vrptw.alns.config.VisualizationControl;
import zll.vrptw.alns.config.IALNSConfig;
import zll.vrptw.alns.destroy.IALNSDestroy;
import zll.vrptw.alns.destroy.RandomDestroy;
import zll.vrptw.alns.destroy.RandomFilter;
import zll.vrptw.alns.destroy.ShawDestroy;
import zll.vrptw.alns.destroy.ShawFilter;
import zll.vrptw.alns.destroy.WorstCostDestroy;
import zll.vrptw.alns.destroy.WorstTotalFilter;
import zll.vrptw.alns.repair.GreedyBalance;
import zll.vrptw.alns.repair.GreedyRepair;
import zll.vrptw.alns.repair.IALNSRepair;
import zll.vrptw.alns.repair.RandomRepair;
import zll.vrptw.alns.repair.RegretBalance;
import zll.vrptw.alns.repair.RegretRepair;
import zll.vrptw.instance.Instance;
import zll.vrptw.visualization.VRPDrawer;

public class MyALNSProcess {
    // 可视化
    // private final ALNSObserver o = new ALNSObserver();
    // 可视化，针对alns进程
    // private final ALNSProcessVisualizationManager apvm = new
    // ALNSProcessVisualizationManager();
    // 参数
    private final IALNSConfig config;

    // new ProximityZoneDestroy(),
    // new ZoneDestroy(),
    // new NodesCountDestroy(false),
    // new SubrouteDestroy(),
    private final IALNSDestroy[] destroy_ops = new IALNSDestroy[] { new ShawDestroy(), new WorstCostDestroy(),
            new RandomDestroy() };
    // new RandomDestroy(),

    private final IALNSRepair[] repair_ops = new IALNSRepair[] { new RegretRepair(), new GreedyRepair(),
            new RandomRepair() };
    // new RandomRepair(),

    private final IALNSDestroy[] filter_ops = new IALNSDestroy[] { new ShawFilter(), new WorstTotalFilter(),
            new RandomFilter() };

    private final IALNSRepair[] balance_ops = new IALNSRepair[] { new RegretBalance(), new GreedyBalance(), };

    private final double T_end_t = 0.01;
    // 全局满意解
    private MyALNSSolution s_g = null;
    // 局部满意解
    private MyALNSSolution s_1 = null;
    private MyALNSSolution s_2 = null;
    private boolean cpng = false;
    private int i = 0;
    // time
    private double T;
    private double T_s;
    // time start
    private long t_start;
    // time end
    private double T_end;

    public MyALNSProcess(Solution s_, Instance instance, IALNSConfig c, VisualizationControl vc)
            throws InterruptedException {

        // cpng = cp.isGlobal_sol_chart();

        config = c;
        s_g = new MyALNSSolution(s_, instance);
        s_g.cost.calculateTotalCost();
        s_1 = new MyALNSSolution(s_g);
        // 初始化alns参数
        initStrategies();
        if (vc.isInit_sol_chart()) {
            VRPDrawer.draw_sol(s_g, 1);
        }
    }

    // ! ALNS框架
    public Solution improveSolution(VisualizationControl cp) throws Exception {
        // o.onThreadStart(this);

        T = 0.01 * s_g.cost.total;

        // 计时开始
        t_start = System.currentTimeMillis();
        // o.onStartConfigurationObtained(this);

        int print_counter = 0;
        int segment_counter = 0;
        int segment_num = config.getTau();

        while (true) {
            MyALNSSolution s_c = null;
            if (s_2 != null) {
                s_c = s_2;
            } else {
                s_c = s_g;
            }
            // ! q为需要移除的节点数，大概10-30之间随机值
            int q = getQ(s_c);

            for (int v = 0; v < config.getV(); v++) {

                // 轮盘赌找出最优destroy、repair方法
                IALNSDestroy destroyOperator = getALNSDestroyOperator();
                IALNSRepair repairOperator = getALNSRepairOperator();
                // o.onDestroyRepairOperationsObtained(this, destroyOperator, repairOperator,
                // s_c_new, q);
                MyALNSSolution s_t = new MyALNSSolution(s_c);
                // destroy solution
                MyALNSSolution s_destroy = destroyOperator.destroy(s_t, q);
                // o.onSolutionDestroy(this, s_destroy);

                // repair solution，重组后新解st
                MyALNSSolution s_repair = repairOperator.repair(s_destroy);
                // o.onSolutionRepaired(this, s_t);

                // if (s_repair.cost.cost < s_1.cost.cost) {
                if (s_repair.cost.cost + s_repair.cost.total < s_1.cost.cost + s_1.cost.total) {
                    s_1 = s_repair;
                }

                T = config.getC() * T;
            }

            for (int w = 0; w < config.getW(); w++) {

                // 轮盘赌找出最优destroy、repair方法
                IALNSDestroy filterOperator = getALNSFilterOperator();
                IALNSRepair balanceOperator = getALNSBalanceOperator();
                // o.onDestroyRepairOperationsObtained(this, destroyOperator, repairOperator,
                // s_c_new, q);

                MyALNSSolution s_t = new MyALNSSolution(s_1);
                // destroy solution
                MyALNSSolution s_destroy = filterOperator.destroy(s_t, q);
                // o.onSolutionDestroy(this, s_destroy);

                // repair solution，重组后新解st
                MyALNSSolution s_repair = balanceOperator.repair(s_destroy);
                // o.onSolutionRepaired(this, s_t);
                if (s_2 == null) {
                    s_2 = s_repair;
                    // } else if (s_repair.cost.cost + s_repair.cost.total < s_2.cost.cost + s_2.cost.total) {
                } else if (s_repair.cost.total < s_2.cost.total) {
                    s_2 = s_repair;
                    // if (s_2.cost.cost + s_2.cost.total < s_g.cost.cost + s_g.cost.total) {
                    if (s_2.cost.cost < s_g.cost.cost) {
                        handleNewGlobalMinimum(filterOperator, balanceOperator, s_2);
                    } else {
                        // ?违约少，但路线差
                        handleNewLocalMinimum(filterOperator, balanceOperator);
                    }
                } else {
                    handleWorseSolution(filterOperator, balanceOperator, s_repair);
                }

                T = config.getC() * T;
            }

            i += config.getV() + config.getW();

            if (print_counter == i / 100) {
                print_counter += 1;
                String ss = String.format(
                        "Iterations: %4d, Solution: { s_1:[cost: %.1f, total: %.1f], s_2:[cost: %.1f, total: %.1f] }",
                        i,
                        Math.round(s_1.cost.cost * 100) / 100.0, Math.round(s_1.cost.total * 100) / 100.0,
                        Math.round(s_2.cost.cost * 100) / 100.0, Math.round(s_2.cost.total * 100) / 100.0);
                System.out.println(ss);
            }

            // if (s_t.cost.total < s_c.cost.total) {
            //     s_c = s_t;
            //     if (s_t.cost.total < s_g.cost.total) {
            //         handleNewGlobalMinimum(destroyOperator, repairOperator, s_t);
            //     } else {
            //         handleNewLocalMinimum(destroyOperator, repairOperator);
            //     }
            // } else {
            //     handleWorseSolution(destroyOperator, repairOperator, s_t);
            // }

            if (i > 0 && segment_counter == i / segment_num) {
                segment_counter += 1;
                segmentFinsihed();
                // o.onSegmentFinsihed(this, s_t);
            }

            if (i > config.getOmega() && s_g.feasible())
                break;
            if (i > config.getOmega() * 1.5)
                break;
        }

        Solution solution = s_g.toSolution();

        // 输出程序耗时s
        double s = Math.round((System.currentTimeMillis() - t_start) * 1000) / 1000000.;
        solution.testTime = s;
        System.out.print("\n");

        // 输出算子使用情况
        for (IALNSDestroy destroy : destroy_ops) {
            System.out.println(destroy.getClass().getName() + " is used " + destroy.getDraws() + " times.");
        }

        for (IALNSRepair repair : repair_ops) {
            System.out.println(repair.getClass().getName() + " is used " + repair.getDraws() + " times.");
        }

        for (IALNSDestroy filter : filter_ops) {
            System.out.println(filter.getClass().getName() + " is used " + filter.getDraws() + " times.");
        }

        for (IALNSRepair balance : balance_ops) {
            System.out.println(balance.getClass().getName() + " is used " + balance.getDraws() + " times.");
        }
        if (cp.isS1_sol_chart()) {
            VRPDrawer.draw_sol(s_1, 2);
        }
        if (cp.isS2_sol_chart()) {
            VRPDrawer.draw_sol(s_2, 3);
        }
        if (cp.isGlobal_sol_chart()) {
            VRPDrawer.draw_sol(s_g, 4);
        }
        return solution;
    }

    private void handleWorseSolution(IALNSDestroy destroyOperator, IALNSRepair repairOperator,
            MyALNSSolution s_repair) {
        // 概率接受较差解
        double p_accept = calculateProbabilityToAcceptTempSolutionAsNewCurrent(s_repair);
        if (Math.random() < p_accept) {
            s_2 = s_repair;
        }
        destroyOperator.addToPi(config.getSigma_3());
        repairOperator.addToPi(config.getSigma_3());
    }

    private void handleNewLocalMinimum(IALNSDestroy destroyOperator, IALNSRepair repairOperator) {
        destroyOperator.addToPi(config.getSigma_2());
        repairOperator.addToPi(config.getSigma_2());
    }

    private void handleNewGlobalMinimum(IALNSDestroy destroyOperator, IALNSRepair repairOperator, MyALNSSolution s_2)
            throws IOException {
        // System.out.println(String.format("[%d]: Found new global minimum: %.2f,
        // Required Vehicles: %d, I_uns: %d", i, s_t.getCostFitness(),
        // s_t.activeVehicles(), s_g.getUnscheduledJobs().size()));
        if (this.cpng) {
            // TODO OutputUtil.createPNG(s_t, i);
        }
        // 接受全局较优
        if (s_2.feasible())
            s_g = s_2;
        destroyOperator.addToPi(config.getSigma_1());
        repairOperator.addToPi(config.getSigma_1());
    }

    private double calculateProbabilityToAcceptTempSolutionAsNewCurrent(MyALNSSolution s_t) {
        return Math.exp(-(s_t.cost.total - s_2.cost.total) / T);
    }

    private int getQ(MyALNSSolution s_c2) {
        int q_l = Math.min((int) Math.ceil(0.05 * s_c2.instance.getCustomerNr()), 10);
        int q_u = Math.min((int) Math.ceil(0.20 * s_c2.instance.getCustomerNr()), 30);

        Random r = new Random();
        return r.nextInt(q_u - q_l + 1) + q_l;
    }

    private void segmentFinsihed() {
        double w_sum = 0;
        // Update neue Gewichtung der Destroy Operatoren
        for (IALNSDestroy dstr : destroy_ops) {
            double w_old1 = dstr.getW() * (1 - config.getR_p());
            double recentFactor = dstr.getDraws() < 1 ? 0 : (double) dstr.getPi() / (double) dstr.getDraws();
            double w_old2 = config.getR_p() * recentFactor;
            double w_new = w_old1 + w_old2;
            w_sum += w_new;
            dstr.setW(w_new);
        }
        // Update neue Wahrs. der Destroy Operatoren
        for (IALNSDestroy dstr : destroy_ops) {
            dstr.setP(dstr.getW() / w_sum);
            // dstr.setDraws(0);
            // dstr.setPi(0);
        }
        w_sum = 0;
        // Update neue Gewichtung der Repair Operatoren
        for (IALNSRepair rpr : repair_ops) {
            double recentFactor = rpr.getDraws() < 1 ? 0 : (double) rpr.getPi() / (double) rpr.getDraws();
            double w_new = (rpr.getW() * (1 - config.getR_p())) + config.getR_p() * recentFactor;
            w_sum += w_new;
            rpr.setW(w_new);
        }
        // Update neue Wahrs. der Repair Operatoren
        for (IALNSRepair rpr : repair_ops) {
            rpr.setP(rpr.getW() / w_sum);
            // rpr.setDraws(0);
            // rpr.setPi(0);
        }

        w_sum = 0;
        for (IALNSDestroy fr : filter_ops) {
            double recentFactor = fr.getDraws() < 1 ? 0 : (double) fr.getPi() / (double) fr.getDraws();
            double w_new = (fr.getW() * (1 - config.getR_p())) + config.getR_p() * recentFactor;
            w_sum += w_new;
            fr.setW(w_new);
        }
        for (IALNSDestroy fr : filter_ops) {
            fr.setP(fr.getW() / w_sum);
        }

        w_sum = 0;
        for (IALNSRepair br : balance_ops) {
            double recentFactor = br.getDraws() < 1 ? 0 : (double) br.getPi() / (double) br.getDraws();
            double w_new = (br.getW() * (1 - config.getR_p())) + config.getR_p() * recentFactor;
            w_sum += w_new;
            br.setW(w_new);
        }
        for (IALNSRepair br : balance_ops) {
            br.setP(br.getW() / w_sum);
        }
    }

    private IALNSRepair getALNSRepairOperator() {
        double random = Math.random();
        double threshold = 0.;
        for (IALNSRepair rpr : repair_ops) {
            threshold += rpr.getP();
            if (random <= threshold) {
                rpr.drawn();
                return rpr;
            }
        }
        repair_ops[repair_ops.length - 1].drawn();
        return repair_ops[repair_ops.length - 1];
    }

    private IALNSDestroy getALNSDestroyOperator() {
        double random = Math.random();
        double threshold = 0.;
        for (IALNSDestroy dstr : destroy_ops) {
            threshold += dstr.getP();
            if (random <= threshold) {
                dstr.drawn();
                return dstr;
            }
        }

        destroy_ops[destroy_ops.length - 1].drawn();
        return destroy_ops[destroy_ops.length - 1];
    }

    private IALNSRepair getALNSBalanceOperator() {
        double random = Math.random();
        double threshold = 0.;
        for (IALNSRepair br : balance_ops) {
            threshold += br.getP();
            if (random <= threshold) {
                br.drawn();
                return br;
            }
        }
        balance_ops[balance_ops.length - 1].drawn();
        return balance_ops[balance_ops.length - 1];
    }

    private IALNSDestroy getALNSFilterOperator() {
        double random = Math.random();
        double threshold = 0.;
        for (IALNSDestroy fr : filter_ops) {
            threshold += fr.getP();
            if (random <= threshold) {
                fr.drawn();
                return fr;
            }
        }

        filter_ops[filter_ops.length - 1].drawn();
        return filter_ops[filter_ops.length - 1];
    }

    private void initStrategies() {
        for (IALNSDestroy dstr : destroy_ops) {
            dstr.setDraws(0);
            dstr.setPi(0);
            dstr.setW(1.);
            dstr.setP(1 / (double) destroy_ops.length);
        }
        for (IALNSRepair rpr : repair_ops) {
            rpr.setDraws(0);
            rpr.setPi(0);
            rpr.setW(1.);
            rpr.setP(1 / (double) repair_ops.length);
        }
        for (IALNSDestroy fr : filter_ops) {
            fr.setDraws(0);
            fr.setPi(0);
            fr.setW(1.);
            fr.setP(1 / (double) filter_ops.length);
        }
        for (IALNSRepair br : balance_ops) {
            br.setDraws(0);
            br.setPi(0);
            br.setW(1.);
            br.setP(1 / (double) balance_ops.length);
        }

    }

    /*
     * public ALNSObserver getO() { return this.o; }
     * 
     * public ALNSProcessVisualizationManager getApvm() { return this.apvm; }
     */
    public IALNSConfig getConfig() {
        return this.config;
    }

    public IALNSDestroy[] getDestroy_ops() {
        return this.destroy_ops;
    }

    public IALNSRepair[] getRepair_ops() {
        return this.repair_ops;
    }

    public MyALNSSolution getS_g() {
        return this.s_g;
    }

    public boolean isCpng() {
        return this.cpng;
    }

    public int getI() {
        return this.i;
    }

    public double getT() {
        return this.T;
    }

    public double getT_s() {
        return this.T_s;
    }

    public long getT_start() {
        return this.t_start;
    }

    public double getT_end_t() {
        return this.T_end_t;
    }

    public double getT_end() {
        return this.T_end;
    }
}
