package zll.vrptw.instance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * Title: SolomnInstance
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author zll_hust
 * @date 2020年3月14日
 */
public class Instance {

    private int[] vehicleInfo;
    private int[][] customerInfo;
    private String name;
    private String type;
    private Random r;

    /**
     * This list will keep all the nodes of the problem. NOTE: position 0 of the
     * list contains the depot.
     */
    private List<Node> customers;

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
    private double[][] distanceMatrix;

    /**
     * The total number of customers.
     */
    private int numberOfNodes;
    private int maxCustomerNum;

    public Random getRandom() {
        return r;
    }

    public int getMaxCustomerNum() {
        return maxCustomerNum;
    }

    public void setR(Random r) {
        this.r = r;
    }

    public List<Node> getCustomers() {
        return this.customers;
    }

    public double[][] getDistanceMatrix() {
        return this.distanceMatrix;
    }

    public int getVehicleNr() {
        return this.vehicleNr;
    }

    public void setVehicleNr(int nr) {
        this.vehicleNr = nr;
    }

    public int getVehicleCapacity() {
        return this.vehicleCapacity;
    }

    public int getCustomerNr() {
        return this.numberOfNodes;
    }

    public int[] getVehicleInfo() {
        return vehicleInfo;
    }

    public int[][] getCustomerInfo() {
        return customerInfo;
    }

    public String getName() {
        return this.name;
    }

    // size 是测试用例文件名中的节点数
    public Instance(int size, String name, String instanceType) throws IOException {
        // 读取算例数据
        this.name = name;
        this.type = instanceType;
        // importVehicleData(size, name);

        this.customers = new ArrayList<Node>();
        importCustomerData(size, name);

        this.vehicleNr = 25;
        this.vehicleCapacity = 24;
        this.maxCustomerNum = 9;
        // this.distanceMatrix = new double[size + 5][size + 5];
        // createDistanceMatrix();

        r = new Random();
        r.setSeed(-1);
    }

    // 读取数据客户点数据
    public void importCustomerData(int size, String name) throws IOException {
        this.distanceMatrix = new double[110][110];
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                distanceMatrix[i][j] = 60000;
            }
        }
        String dataFileName = "C:/Users/didi/Downloads/distance_sample.csv";
        String line;
        boolean is_first_line_skiped = false;
        HashMap<Long, Integer> my = new HashMap<>();
        BufferedReader bReaders = new BufferedReader(new FileReader(dataFileName));
        while ((line = bReaders.readLine()) != null) {
            if (false == is_first_line_skiped) {
                is_first_line_skiped = true;
                continue;
            }
            String[] dataLine = line.split(",");

            // set distance matrix
            Long id_1 = Long.parseLong(dataLine[0]);
            Long id_2 = Long.parseLong(dataLine[1]);
            boolean is_add_this_customer = (null == my.get(id_1));
            boolean is_add_that_customer = (null == my.get(id_2));
            Integer distance = Integer.parseInt(dataLine[6]);
            Integer[] index = convert_id_to_index(my, id_1, id_2);
            distanceMatrix[index[0]][index[1]] = distance;
            distanceMatrix[index[1]][index[0]] = distance;

            // add depot
            if (index[0] == 0 && is_add_this_customer) {
                Node depot = new Node();
                depot.setId(0);
                depot.setDemand(0);
                depot.setTimeWindow(0, 999999);
                depot.setServiceTime(10);
                this.customers.add(depot);
                is_add_this_customer = false;
            }

            // set customer
            if (is_add_this_customer) {
                Node customer = new Node();
                customer.setId(index[0]);
                customer.setDemand(2);
                customer.setTimeWindow(0, 999999);
                customer.setServiceTime(10);
                this.customers.add(customer);
            }
            if (is_add_that_customer) {
                Node customer = new Node();
                customer.setId(index[1]);
                customer.setDemand(2);
                customer.setTimeWindow(0, 999999);
                customer.setServiceTime(10);
                this.customers.add(customer);
            }

        }
        bReaders.close();

        numberOfNodes = customers.size();
        System.out.println("Input customers success !");

    }

    private Integer[] convert_id_to_index(HashMap<Long, Integer> my, Long id_1, Long id_2) {
        Integer index_1 = my.get(id_1);
        Integer index_2 = my.get(id_2);
        if (null == index_1) {
            my.put(id_1, my.size());
        }
        if (null == index_2) {
            my.put(id_2, my.size());
        }
        return new Integer[] { my.get(id_1), my.get(id_2) };
    }

    // 读取数据车辆信息
    public void importVehicleData(int size, String name) throws IOException {

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

    /**
     * A helper function that creates the distance matrix.
     */
    private void createDistanceMatrix() {
        for (int i = 0; i < this.numberOfNodes; i++) {
            Node n1 = this.customers.get(i);

            for (int j = 0; j < this.numberOfNodes; j++) {
                Node n2 = this.customers.get(j);

                this.distanceMatrix[i][j] = (double) (Math
                        .round(Math.sqrt(Math.pow(n1.getX() - n2.getX(), 2) + Math.pow(n1.getY() - n2.getY(), 2)) * 100)
                        / 100.0);
                ;
            }
        }
    }

}
