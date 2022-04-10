package younger.vrp.base;

import java.util.List;

public interface IDistance {
    public static IDistance getDistanceInstance() {
        return Distance.getInstance();
    }

    public int getDistance(int from, int to);

    public List<Integer> getNeighbours(int index);

    public int[][] distanceMatrix();
}
