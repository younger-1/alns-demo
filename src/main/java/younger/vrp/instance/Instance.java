package younger.vrp.instance;

import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Xavier Young
 */
public class Instance {

    /**
     * This list will keep all the nodes of the problem.
     * NOTE: position 0 of the list contains the depot.
     */
    private List<Node> customers = new ArrayList<Node>();

    // NOTE: info and name
    // private int[] vehicleInfo;
    // private int[][] customerInfo;
    private String name;

    /**
     * The available vehicles numbers.
     */
    private int vehicleNr;

    /**
     * The capacity of vehicles.
     */
    private int vehicleCapacity;

    /**
     * A 2-D matrix that will keep the distances of every node to each other.
     */
    // private double[][] distanceMatrix;

    public Instance(String type, String name, int size) throws IOException {
        this.name = String.format("%s_%s_%d", type.toLowerCase(), name, size);
        // 读取算例数据
        importVehicleData(type, name, size);

        this.customers = new ArrayList<Node>();
        importCustomerData(type, name, size);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public int getVehicleCapacity() {
        return vehicleCapacity;
    }

    public int getVehicleNr() {
        return vehicleNr;
    }

    // 读取数据客户点数据
    public void importCustomerData(String type, String name, int size) throws IOException {
        String dataFileName = "";
        if (type.equals("Solomon"))
            dataFileName = "./instances" + "/solomon" + "/solomon_" + size + "/" + name + ".txt";
        else if (type.equals("Homberger"))
            dataFileName = "./instances" + "/homberger" + "/homberger_" + size + "/" + name + ".txt";

        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));

        int data_in_x_lines = Integer.MAX_VALUE;

        String line;

        while ((line = bReader.readLine()) != null) {
            // 以空格为间隔符读取数据
            String datavalue[] = line.split("\\s+");

            if (datavalue.length > 0 && datavalue[0].equals("CUST")) {
                data_in_x_lines = 2;
            }

            if (data_in_x_lines < 1 && datavalue.length > 0) {

                Node customer = new Node();
                customer.setId(Integer.parseInt(datavalue[1]));
                customer.setX(Double.parseDouble(datavalue[2]));
                customer.setY(Double.parseDouble(datavalue[3]));
                customer.setDemand(Double.parseDouble(datavalue[4]));
                customer.setTimeWindow(Double.parseDouble(datavalue[5]), Double.parseDouble(datavalue[6]));
                customer.setServiceTime(Double.parseDouble(datavalue[7]));
                this.customers.add(customer);
            }
            data_in_x_lines--;
        }
        bReader.close();

        System.out.println("Input customers success !");
    }

    // 读取数据车辆信息
    public void importVehicleData(String type, String name, int size) throws IOException {

        String dataFileName = "";
        if (type.equals("Solomon"))
            dataFileName = "./instances" + "/solomon" + "/solomon_" + size + "/" + name + ".txt";
        else if (type.equals("Homberger"))
            dataFileName = "./instances" + "/homberger" + "/homberger_" + size + "/" + name + ".txt";

        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));

        int row = 0;

        String line;
        while ((line = bReader.readLine()) != null) {
            String datavalue[] = line.split("\\s+");

            if (row == 4) {
                // 可用车辆数量
                this.vehicleNr = Integer.valueOf(datavalue[1]);
                // 车辆容量
                this.vehicleCapacity = Integer.valueOf(datavalue[2]);
                break;
            }
            row++;
        }
        bReader.close();

        System.out.println("Input vehicle success !");
    }

    public List<Node> getCopyOfCustomers() {
        return new ArrayList<>(this.customers);
    }

    // public double[][] getDistanceMatrix() {
    //     return this.distanceMatrix;
    // }

    // public void setDistanceMatrix(double[][] distanceMatrix) {
    //     this.distanceMatrix = distanceMatrix;
    // }
}
