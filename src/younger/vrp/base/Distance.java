package younger.vrp.base;

import java.util.*;

import younger.vrp.instance.Node;

public class Distance implements IDistance {
    private static int[][] distanceMatrix;
    private static Distance dInstance = null;
    private static Map<String, List<String>> uidToNeighboursMap = new HashMap<String, List<String>>();
    private static Map<String, Integer> uidToIdMap = new HashMap<String, Integer>();
    private static Map<Integer, String> idToUidMap = new HashMap<Integer, String>();
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
            uidToIdMap.put(n1.getUid(), id);
            idToUidMap.put(id, n1.getUid());

            for (int j = 0; j < nodes.size(); j++) {
                Node n2 = nodes.get(j);
                distanceMatrix[id][j] = DistanceUtils.getDistance(n1.getX(), n1.getY(), n2.getX(), n2.getY());
            }

            // review: we don't require the driver to come back to the depot now
            // distanceMatrix[index][0] = 0;
        }
    }

    private static void createNeighbours() {
        for (int iFrom = 0; iFrom < distanceMatrix.length; iFrom++) {
            int finalIFrom = iFrom;
            Queue<String> closestNeighbours = new PriorityQueue<String>(
                    (a, b) -> (distanceMatrix[finalIFrom][uidToIdMap.get(b)]
                            - distanceMatrix[finalIFrom][uidToIdMap.get(a)]));
            for (int iTo = 1; iTo < distanceMatrix.length; iTo++) {
                if (iFrom != iTo) {
                    closestNeighbours.add(idToUidMap.get(iTo));
                    if (closestNeighbours.size() > NUM_OF_NEIGHBOUR) {
                        closestNeighbours.remove();
                    }
                }
            }

            List<String> neighbours = new ArrayList<String>(closestNeighbours);
            Collections.sort(neighbours, (a, b) -> (distanceMatrix[finalIFrom][uidToIdMap.get(a)]
                    - distanceMatrix[finalIFrom][uidToIdMap.get(b)]));
            uidToNeighboursMap.put(idToUidMap.get(iFrom), neighbours);
        }
    }

    public static Distance getInstance() {
        if (dInstance == null) {
            throw new RuntimeException("Distance instance is not initialized");
        }

        return dInstance;
    }

    public int getId(String uid) {
        return uidToIdMap.get(uid);
    }

    public String getUid(int id) {
        return idToUidMap.get(id);
    }

    @Override
    public List<String> getNeighbours(int id) {
        return getNeighbours(idToUidMap.get(id));
    }

    @Override
    public List<String> getNeighbours(String uid) {
        return uidToNeighboursMap.get(uid);
    }

    @Override
    public int getDistance(int from, int to) {
        return distanceMatrix[from][to];
    }

    @Override
    public int getDistance(String fromLeader, String toLeader) {
        return getDistance(uidToIdMap.get(fromLeader), uidToIdMap.get(toLeader));
    }

    @Override
    public int[][] distanceMatrix() {
        return distanceMatrix;
    }
}
