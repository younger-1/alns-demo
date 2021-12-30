package younger.vrp.alns.ruin;

import younger.vrp.alns.config.VRPCategory;
import younger.vrp.alns.operation.ALNSAbstractOperation;
import younger.vrp.alns.ruin.destroy.BrotherRouteDestroy;
import younger.vrp.alns.ruin.destroy.ShawDestroy;
import younger.vrp.alns.ruin.destroy.WorstDestroy;
import younger.vrp.alns.ruin.filter.BrotherRouteFilter;
import younger.vrp.alns.ruin.filter.ShawFilter;
import younger.vrp.alns.ruin.filter.WorstFilter;

public abstract class ALNSAbstractRuin extends ALNSAbstractOperation {

    public static void use(VRPCategory cate) {
        vrpCate = cate;
    }

    // public static ALNSAbstractDestroy use(VRPCategory cate) {
    //     ALNSAbstractDestroy obj = new ALNSAbstractDestroy(cate);
    //     return obj;
    // }

    public static IALNSRuin[] common_destroy() {

        return new IALNSRuin[] {
            RandomRuin.of(),
            RadialRuin.of(),
            AreaRuin.of(),
            BrotherRouteDestroy.of(),
            // WorstDestroy.of(),
        };
    }

    public static IALNSRuin[] common_filter() {

        return new IALNSRuin[] {
            RandomRuin.of(),
            RadialRuin.of(),
            AreaRuin.of(),
            BrotherRouteFilter.of(),
            // WorstFilter.of(),
        };
    }

    public static IALNSRuin[] rare_destroy() {

        return new IALNSRuin[] {
            ShawDestroy.of(),
            WorstDestroy.of(),
        };
    }

    public static IALNSRuin[] rare_filter() {

        return new IALNSRuin[] {
            ShawFilter.of(),
            WorstFilter.of(),
        };
    }
}
