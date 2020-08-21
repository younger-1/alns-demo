package zll.vrptw.instance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
 * @date 2020��3��14��
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

    public Random getRandom() {
        return r;
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

    // ! size �ǲ��������ļ����еĽڵ���
    public Instance(int size, String name, String instanceType) throws IOException {
        // ��ȡ��������
        this.name = name;
        this.type = instanceType;
        importVehicleData(size, name);

        this.customers = new ArrayList<Node>();
        importCustomerData(size, name);

        this.distanceMatrix = new double[size + 5][size + 5];
        createDistanceMatrix();

        r = new Random();
        r.setSeed(-1);
    }

    // ��ȡ���ݿͻ�������
    public void importCustomerData(int size, String name) throws IOException {

        String dataFileName = "";
        if (type.equals("Solomon"))
            dataFileName = "./instances" + "/solomon" + "/solomon_" + size + "/" + name + ".txt";
        else if (type.equals("Homberger"))
            dataFileName = "./instances" + "/homberger" + "/homberger_" + size + "/" + name + ".txt";

        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));

        int data_in_x_lines = Integer.MAX_VALUE;

        String line;

        while ((line = bReader.readLine()) != null) {
            // �Կո�Ϊ�������ȡ����
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

        numberOfNodes = customers.size();

        System.out.println("Input customers success !");

    }

    // ��ȡ���ݳ�����Ϣ
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
                // ���ó�������
                this.vehicleNr = Integer.valueOf(datavalue[1]);
                // ��������
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
