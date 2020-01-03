import java.io.*;
import java.util.Scanner;

public class Kmean {

    public class Point {
        double xCoord, yCoord;
        int label;
        double distance;

        public Point(double xCoord, double yCoord) {
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            //default values
            label = 0;
            distance = 99999.00;
        }
    }

    // K-mean variables
    private int k;
    private int numPts;
    private Point[] pointSet;
    private int numRows, numCols;
    private int[][] imgAry;
    private Point[] kCentroids;
    private int change;
    private String outputFileName;

    public Kmean(String inputFileName, String outputFileName) {
        this.outputFileName = outputFileName;
        loadPointSet(inputFileName);
    }

    private void loadPointSet(String inputFileName) {
        try {
            // load file
            Scanner scanner = new Scanner(new File(inputFileName));

            // read dimensions of image array and number of points
            numRows = scanner.nextInt();
            numCols = scanner.nextInt();
            numPts = scanner.nextInt();

            // initialize pointSet array
            pointSet = new Point[numPts];

            // record all points
            for(int i = 0; i < numPts; i++)
                pointSet[i] = new Point(scanner.nextInt(), scanner.nextInt());

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void kMeansClustering(int k){
        // initialize kCentroids and pointSet labels
        this.k = k;
        kCentroids = new Point[k+1];
        for(int i = 1; i <= k; i++)
            kCentroids[i] = new Point(0, 0);
        assignLabel();
        clearOutput();

        int iteration = 0;
        do {
            // create and print image
            point2Image();
            printImage(iteration);

            change = 0;
            computeCentroids();
            for(int i = 0; i < numPts; i++) {
                distanceMinLabel(pointSet[i]);
            }
            iteration++;
        } while (change >= 2);

    }

    private void assignLabel() {
        int front = 0;
        int back = numPts - 1;
        int label = 1;
        while(front <= back) {
            if(label > k)
                label = 1;
            pointSet[front].label = label;
            front++;
            label++;
            //  have to check again, otherwise program crashes when k = odd
            if(label > k)
                label = 1;
            pointSet[back].label = label;
            back--;
            label++;
        }
    }

    private void point2Image() {
        imgAry = new int[numRows][numCols];
        for(int i = 0; i < numPts; i++)
            imgAry[(int)pointSet[i].xCoord][(int)pointSet[i].yCoord] = pointSet[i].label;
    }

    private void printImage(int iteration) {
        try {
            FileWriter writer = new FileWriter(outputFileName, true);
            writer.write("*** Result of iteration " + iteration + " ***\n");
            for(int i = 0; i < numRows; i++) {
                for(int j = 0; j < numCols; j++) {
                    if(imgAry[i][j] == 0)
                        writer.write(" ");
                    else
                        writer.write(imgAry[i][j]+"");
                }
                writer.write("\n");
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void computeCentroids() {
        double[] sumX = new double[k+1];
        double[] sumY = new double[k+1];
        int[] totalPt = new int[k+1];
        int label;
        for(int i = 0; i < numPts; i++) {
            label = pointSet[i].label;
            sumX[label] += pointSet[i].xCoord;
            sumY[label] += pointSet[i].yCoord;
            totalPt[label]++;
        }
        for(int i = 1; i <= k; i++) {
            kCentroids[i].xCoord = sumX[i] / totalPt[i];
            kCentroids[i].yCoord = sumY[i] / totalPt[i];
        }
    }

    private void distanceMinLabel(Point point) {
        double minDistance = 99999.00;
        int minLabel = 1;
        double distance;
        for(int i = 1; i <= k; i++) {
            distance = computeDist(point, kCentroids[i]);
            if(distance < minDistance) {
                minDistance = distance;
                minLabel = i;
            }
        }
        point.distance = minDistance;
        if(point.label != minLabel) {
            point.label = minLabel;
            change++;
        }
    }

    private double computeDist(Point pt1, Point pt2) {
        double x = pt1.xCoord - pt2.xCoord;
        double y = pt1.yCoord - pt2.yCoord;
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    // this is necessary since the printImage function
    // appends to the end of the output file so if it already exists
    // with old output data, the function will clear it
    private void clearOutput() {
        try {
            FileWriter writer = new FileWriter(outputFileName);
            writer.write("");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.print("type k(int) and press enter:");
        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        scanner.close();

        Kmean kmean = new Kmean(args[0], args[1]);
        kmean.kMeansClustering(input);

    }
}
