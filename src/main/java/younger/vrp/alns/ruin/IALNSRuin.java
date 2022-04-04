package younger.vrp.alns.ruin;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.operation.IALNSOperation;

public interface IALNSRuin extends IALNSOperation {

    ALNSSolution ruin(ALNSSolution s, int nodes) throws Exception;

}
