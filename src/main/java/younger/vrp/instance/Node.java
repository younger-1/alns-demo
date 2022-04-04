package younger.vrp.instance;

/**
 * 
 * <p> Description: </p>
 * Every instance of this class represents a Node (customer) of the VRP problem
 * 
 */
public class Node {

    private double timeWindows[];
    private double serviceTime;

    /**
     * The X-axis coordinate in a theoretical 2-D space for the specific customer.
     */
    private double x;

    /**
     * The Y-axis coordinate in a theoretical 2-D space for the specific customer.
     */
    private double y;

    /**
     * A unique identifier for the customer
     */
    private int id;

    /**
     * The current customer's demand.
     */
    private double demand;

    /**
     * Empty default constructor.
     */
    public Node(Node n) {
        this.id = n.id;
        this.x = n.x;
        this.y = n.y;
        this.demand = n.demand;
        this.serviceTime = n.serviceTime;
        this.timeWindows = new double[] { n.timeWindows[0], n.timeWindows[1] };
    }

    public Node() {
        this.x = 0;
        this.y = 0;
        this.demand = 0;
        this.serviceTime = 0;
        this.timeWindows = new double[] { 0, Double.MAX_VALUE };
    }

    public double getServiceTime() {
        return this.serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    public double[] getTW() {
        return this.timeWindows;
    }

    public void setTimeWindow(double start, double end) {
        this.timeWindows = new double[] { start, end };
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }

    @Override
    public String toString() {
        // return String.format("Node { x = %10.6f, y = %9.6f, id = %4d, demand = %4.0f }", x, y, id, demand);
        return String.format("Node { x = %6.2f, y = %6.2f, id = %4d, demand = %4.0f }", x, y, id, demand);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Node node = (Node) o;

        return id == node.id;

    }
}
