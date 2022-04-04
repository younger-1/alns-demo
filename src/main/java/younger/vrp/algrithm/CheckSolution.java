package younger.vrp.algrithm;

import younger.vrp.instance.Route;
import younger.vrp.alns.config.VRPCategory;
import younger.vrp.base.IDistance;
import younger.vrp.instance.Instance;

/**
 * <p>
 * Title: CheckSolution
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Xavier Young
 */
public class CheckSolution {

    private IDistance dInstance = IDistance.getDistanceInstance();

    public CheckSolution(Instance instance) {
    }

    public String Check(ALNSSolution solution, VRPCategory cate) {
        String result = "";
        double distanceCost = 0;

        int id = 0;

        for (int i = 0; i < solution.getRoutes().size(); i++) {
            Route vehicle = solution.getRoutes().get(i);
            boolean isValidRoute = vehicle.getSize() >= 3;
            if (isValidRoute) {
                id++;

                double costInVehicle = 0;
                double loadInVehicle = 0;
                double time = 0;

                boolean checkCost = true;
                boolean checkLoad = true;
                boolean checkTime = true;
                boolean checkTimeWindows = true;

                for (int j = 1; j < vehicle.getSize(); j++) {
                    time += dInstance.getDistance(vehicle.getNode(j - 1).getId(), vehicle.getNode(j).getId());
                    costInVehicle += dInstance.getDistance(vehicle.getNode(j - 1).getId(), vehicle.getNode(j).getId());
                    loadInVehicle += vehicle.getNode(j).getDemand();

                    time += vehicle.getNode(j).getServiceTime();
                }

                distanceCost += costInVehicle;

                if (Math.abs(vehicle.costs.getDist() - costInVehicle) > 0.001)
                    checkCost = false;
                if (Math.abs(vehicle.costs.getLoad() - loadInVehicle) > 0.001)
                    checkLoad = false;
                if (Math.abs(vehicle.costs.getTime() - time) > 0.001)
                    checkTime = false;

                result += String.format(
                        "\n  check route %2d: \n  check demand = %8.2f , %b \n  check cost   = %8.2f , %b \n  check time   = %8.2f , %b \n  check time windows : %b \n",
                        id, loadInVehicle, checkLoad, costInVehicle, checkCost, time, checkTime, checkTimeWindows);
            }
        }

        boolean checkDistanceCost = true;
        if (Math.abs(distanceCost - solution.costs.getDist()) > 0.001)
            checkDistanceCost = false;

        result += String.format("\ncheck distance cost = %.2f , %b \n", distanceCost, checkDistanceCost);
        return result;
    }

}