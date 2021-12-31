package younger.vrp.instance;

public class Cost {
    protected double total;
    protected double dist;
    protected double time;
    protected double load;
    protected double arc;

    protected double loadVio;
    protected double timeVio;
    protected int nodeNumVio;

    public Cost() {
        total = 0;
        dist = 0;
        load = 0;
        time = 0;
        arc = 0;

        loadVio = 0;
        timeVio = 0;
        nodeNumVio = 0;
    }

    public Cost(Cost cost) {
        this.total = cost.total;
        this.dist = cost.dist;
        this.load = cost.load;
        this.time = cost.time;
        this.arc = cost.arc;

        this.loadVio = cost.loadVio;
        this.timeVio = cost.timeVio;
        this.nodeNumVio = cost.nodeNumVio;
    }

    public void reset() {
        setTotal(0);
        setDist(0);
        setLoad(0);
        setTime(0);
        setArc(0);

        setLoadVio(0);
        setTimeVio(0);
        setNodeVio(0);
    }


    @Override
    public String toString() {
        String result = String.format(
                "[ total=%7.1f | distance=%7.1f, arc=%7.1f | time=%5.1f, time_vio=%4.1f | load=%4.0f, load_vio=%3.0f | nodeNum_vio=%2d ]",
                total, dist, arc, time, timeVio, load, loadVio, nodeNumVio);
        return result;
    }

    /**
     * Allow violation: 500:10:5
     * @apiNotee
     * 拟合的参数可通过根目录下的 violation_parameter_fitting.py 脚本获取
     */
    public void calc_total() {

        double load_conv, time_conv, node_conv;

        if (loadVio == 0) {
            load_conv = 0;
        } else if (loadVio <= 500) {
            load_conv = 1.47192536 * loadVio + 0.138419987 * Math.exp(0.0471018309 * (loadVio - 254.937186));
        } else {
            load_conv = 1.47192536 * 500 + 0.138419987 * Math.exp(0.0471018309 * (500 - 254.937186)) + 2 * 100 * (loadVio - 500);
        }

        if (timeVio == 0) {
            time_conv = 0;
        } else if (timeVio <= 10) {
            time_conv = 73.59624425 * timeVio + 5.66041471 * Math.exp(2.35509121 * (timeVio - 6.67446117));
        } else {
            time_conv = 73.59624425 * 10 + 5.66041471 * Math.exp(2.35509121 * (10 - 6.67446117)) + 100 * 100 * (timeVio - 10);
        }

        if (nodeNumVio == 0) {
            node_conv = 0;
        } else if (nodeNumVio <= 5) {
            node_conv = 147.19248639 * nodeNumVio + 10.87644987 * Math.exp(4.71018239 * (nodeNumVio - 3.47588821));
        } else {
            node_conv = 147.19248639 * 5 + 10.87644987 * Math.exp(4.71018239 * (5 - 3.47588821)) + 200 * 100 * (nodeNumVio - 5);
        }

        total = arc + (load_conv + time_conv + node_conv);
        // total = arc + loadVio * 2 + timeVio * 100 + nodeNumVio * 200;
    }
    
    /**
     *  Cost Getters and Setters
     */

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public double getArc() {
        return arc;
    }

    public void setArc(double arc) {
        this.arc = arc;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    /**
     *  Violation Getters and Setters
     */

    public double getLoadVio() {
        return loadVio;
    }

    public void setLoadVio(double loadVio) {
        this.loadVio = loadVio;
    }

    public double getTimeVio() {
        return timeVio;
    }

    public void setTimeVio(double timeVio) {
        this.timeVio = timeVio;
    }

    public int getNodeVio() {
        return nodeNumVio;
    }

    public void setNodeVio(int nodeNumVio) {
        this.nodeNumVio = nodeNumVio;
    }
}
