package younger.vrp.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import younger.vrp.base.IDistance;

/**
 *         Every instance of this class represents a Route (Vehicle) that will
 *         be used in order to serve a set of customers
 * 
 *
 */
public class Route {

    private int id;

    /**
     * A sequence of Customers, that will be served from the current Vehicle.
     */
    private List<Node> route;

    /**
     * The cost of the current Route. It is calculated as the sum of the distances
     * of every next node from the previous one.
     */
    public Cost costs;

    // private static double[][] distance;
    // public static void referenceDistance(double[][] distance_matrix) {
    //     distance = distance_matrix;
    // }
    /**
     * Constructor
     *
     * The capacity for this Vehicle
     */
    public Route(int id) {
        this.route = new ArrayList<>();
        this.id = id;
        this.costs = new Cost();
    }

    public Route cloneRoute() {
        Route clone = new Route(this.id);
        clone.costs = new Cost(this.costs);
        clone.route = new ArrayList<>(this.route);
        return clone;
    }

    public int getId() {
        return this.id;
    }

    public List<Node> getRoute() {
        return route;
    }

    /**
     * Returns the last node in the route
     */
    public Node getLastNode() {
        return this.route.get(this.route.size() - 1);
    }

    /**
     * Adds a customer in the end of the route.
     *
     * @param node The new customer to be inserted.
     */
    public void add(Node node) {
        this.route.add(node);
    }

    /**
     * Adds a customer in the route in a specific position in the sequence.
     *
     * @param node  The new customer to be inserted
     * @param index The position in which the customer will be inserted.
     */
    public void addNodeToRouteWithIndex(Node node, int index) {
        this.route.add(index, node);
    }

    /**
     * Removes a customer from a specific position in the route.
     *
     * @param index The index from which the customer will be removed
     * @return The removed customer.
     */
    public Node removeNode(int index) {
        return this.route.remove(index);
    }

    @Override
    public String toString() {
        int[][] distanceMatrix = IDistance.getDistanceInstance().distanceMatrix();
        String result = String.format(
                "Route { route_id = %3d , customer_number = %2d ,\n\t    Cost = %s,\n\t    route = [", this.id,
                getSize() - 2, this.costs);

        for (int i = 0; i < this.getSize(); i++) {
            Node customer = this.getNode(i);
            Node before = i == 0 ? this.getNode(0) : this.getNode(i - 1);
            result += "\n\t\t" + customer
                    + String.format("    -    Traj: %7d", distanceMatrix[before.getId()][customer.getId()]);
        }

        return result + " ] }";
    }

    public int getSize() {
        return this.route.size();
    }

    public Node getNode(int index) {
        return this.route.get(index);
    }

    public Optional<Integer> getIndexOfNode(int id) {
        return Stream.iterate(0, i -> i + 1).limit(this.getSize()).filter(i -> this.getNode(i).getId() == id)
                .findFirst();
    }

    public Optional<Node> getNodeWithID(int id) {
        return this.getIndexOfNode(id).map(index -> this.getNode(index));
    }
}
