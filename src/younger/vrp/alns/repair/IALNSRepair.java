package younger.vrp.alns.repair;

import younger.vrp.algrithm.MyALNSSolution;
import younger.vrp.alns.operation.IALNSOperation;

public interface IALNSRepair extends IALNSOperation {

    MyALNSSolution repair(MyALNSSolution from);
}
