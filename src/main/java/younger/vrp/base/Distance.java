package younger.vrp.base;

import java.util.*;

import younger.vrp.instance.Node;

public class Distance implements IDistance {
    private static int[][] distanceMatrix;
    private static Distance dInstance = null;
    private static Map<Integer, List<Integer>> idToNeighboursMap = new HashMap<>();
    private static final int NUM_OF_NEIGHBOUR = 150;

    private Distance() {
    }

    public synchronized static Distance createInstance(List<Node> nodes) {
        if (dInstance == null) {
            distanceMatrix = new int[nodes.size()][nodes.size()];
            createDistanceMatrix(nodes);
            createNeighbours();
            dInstance = new Distance();
        }

        return dInstance;
    }

    private static void createDistanceMatrix(List<Node> nodes) {

        for (int id = 0; id < nodes.size(); id++) {
            Node n1 = nodes.get(id);

            for (int j = 0; j < nodes.size(); j++) {
                Node n2 = nodes.get(j);
                // distanceMatrix[id][j] = DistanceUtils.getDistance(n1.getX(), n1.getY(), n2.getX(), n2.getY());
                distanceMatrix[id][j] = DistanceUtils.getEuclideanDistance(n1.getX(), n1.getY(), n2.getX(), n2.getY());
            }

            // distanceMatrix[index][0] = 0;
        }
    }

    private static void createNeighbours() {
        for (int iFrom = 0; iFrom < distanceMatrix.length; iFrom++) {
            int finalIFrom = iFrom;
            Queue<Integer> closestNeighbours = new PriorityQueue<Integer>(
                    (a, b) -> (distanceMatrix[finalIFrom][b] - distanceMatrix[finalIFrom][a]));
            for (int iTo = 1; iTo < distanceMatrix.length; iTo++) {
                if (iFrom != iTo) {
                    closestNeighbours.add(iTo);
                    if (closestNeighbours.size() > NUM_OF_NEIGHBOUR) {
                        closestNeighbours.remove();
                    }
                }
            }

            List<Integer> neighbours = new ArrayList<Integer>(closestNeighbours);
            Collections.sort(neighbours, (a, b) -> (distanceMatrix[finalIFrom][a]
                    - distanceMatrix[finalIFrom][b]));
            idToNeighboursMap.put(iFrom, neighbours);
        }
    }

    public static Distance getInstance() {
        if (dInstance == null) {
            throw new RuntimeException("Distance instance is not initialized");
        }

        return dInstance;
    }

    @Override
    public List<Integer> getNeighbours(int id) {
        return idToNeighboursMap.get(id);
    }

    @Override
    public int getDistance(int from, int to) {
        return distanceMatrix[from][to];
    }

    @Override
    public int[][] distanceMatrix() {
        return distanceMatrix;
    }
}
