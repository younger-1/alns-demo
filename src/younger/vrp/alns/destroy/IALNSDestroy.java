package younger.vrp.alns.destroy;

import younger.vrp.algrithm.MyALNSSolution;
import younger.vrp.alns.operation.IALNSOperation;

public interface IALNSDestroy extends IALNSOperation {

    MyALNSSolution destroy(MyALNSSolution s, int nodes) throws Exception;

}
