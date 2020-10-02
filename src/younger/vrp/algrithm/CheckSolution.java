package younger.vrp.algrithm;

import younger.vrp.instance.Route;
import younger.vrp.instance.Instance;

/**
 * <p>
 * Title: CheckSolution
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author zll_hust
 * @date 2020年3月1日
 */
public class CheckSolution {

	private double[][] distance;

	public CheckSolution(Instance instance) {
		this.distance = instance.getDistanceMatrix();
	}

	public String Check(Solution solution) {
		String result = "";
		double totalCost = 0;

		int id = 0;

		for (int i = 0; i < solution.getRoutes().size(); i++) {
			Route vehicle = solution.getRoutes().get(i);
			if (vehicle.getRoute().size() >= 3) {
				id++;

				double costInVehicle = 0;
				double loadInVehicle = 0;
				double time = 0;

				boolean checkCost = true;
				boolean checkLoad = true;
				boolean checkTime = true;
				boolean checkTimeWindows = true;

				for (int j = 1; j < vehicle.getRoute().size(); j++) {
					time += distance[vehicle.getRoute().get(j - 1).getId()][vehicle.getRoute().get(j).getId()];
					costInVehicle += distance[vehicle.getRoute().get(j - 1).getId()][vehicle.getRoute().get(j).getId()];
					loadInVehicle += vehicle.getRoute().get(j).getDemand();
					if (time < vehicle.getRoute().get(j).getTimeWindow()[0])
						time = vehicle.getRoute().get(j).getTimeWindow()[0];
					else if (time > vehicle.getRoute().get(j).getTimeWindow()[1])
						checkTimeWindows = false;

					time += vehicle.getRoute().get(j).getServiceTime();
				}

				totalCost += costInVehicle;

				if (Math.abs(vehicle.getCost().cost - costInVehicle) > 0.001)
					checkCost = false;
				if (Math.abs(vehicle.getCost().load - loadInVehicle) > 0.001)
					checkLoad = false;
				if (Math.abs(vehicle.getCost().time - time) > 0.001)
					checkTime = false;

				result += String.format(
						"\n  check route %d: \n  check cost = %.2f , %b \n  check demand = %.2f , %b \n  check time = %.2f , %b \n  check time windows = %b \n",
						id, costInVehicle, checkCost, loadInVehicle, checkLoad, time, checkTime, checkTimeWindows);
			}
		}

		boolean checkTotalCost = true;
		if (Math.abs(totalCost - solution.getTotalCost()) > 0.001)
			checkTotalCost = false;

		result += String.format("\ncheck total cost = %.2f , %b \n", totalCost, checkTotalCost);

		return result;
	}

}