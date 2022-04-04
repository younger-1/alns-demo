package younger.vrp.alns.recreate;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.operation.IALNSOperation;

public interface IALNSRecreate extends IALNSOperation {

    ALNSSolution recreate(ALNSSolution from) throws Exception;
}
