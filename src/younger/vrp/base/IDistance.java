package younger.vrp.base;

import java.util.List;

public interface IDistance {
    public static IDistance getDistanceInstance() {
        return Distance.getInstance();
    }

    public int getDistance(String fromLeader, String toLeader);

    public int getDistance(int from, int to);

    public List<String> getNeighbours(String leader);

    public List<String> getNeighbours(int index);

    public int[][] distanceMatrix();
}
