package younger.vrp.alns.recreate;

import younger.vrp.alns.config.VRPCategory;
import younger.vrp.alns.operation.ALNSAbstractOperation;
import younger.vrp.alns.recreate.balance.GreedyBalance;
import younger.vrp.alns.recreate.balance.RandomBalance;
import younger.vrp.alns.recreate.balance.RegretBalance;
import younger.vrp.alns.recreate.repair.GreedyRepair;
import younger.vrp.alns.recreate.repair.RandomRepair;
import younger.vrp.alns.recreate.repair.RegretRepair;

public abstract class ALNSAbstractRecreate extends ALNSAbstractOperation {

    public static void use(VRPCategory cate) {
        vrpCate = cate;
    }

    // public static ALNSAbstractRepair use(VRPCategory cate) {
    //     ALNSAbstractRepair obj = new ALNSAbstractRepair(cate);
    //     return obj;
    // }

    public static IALNSRecreate[] common_repair() {

        return new IALNSRecreate[] {
            GreedyRepair.of(),
        };
    }

    public static IALNSRecreate[] common_balance() {

        return new IALNSRecreate[] {
            GreedyBalance.of(),
        };
    }

    public static IALNSRecreate[] rare_repair() {

        return new IALNSRecreate[] {
            RegretRepair.of(),
            RandomRepair.of(),
        };
    }

    public static IALNSRecreate[] rare_balance() {

        return new IALNSRecreate[] {
            RegretBalance.of(),
            RandomBalance.of(),
        };
    }
}
