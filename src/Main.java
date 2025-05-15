import java.util.*;
import java.io.*;

public class Main {
    private static final int MAX_CAPACITY = 80;
    private static final String PASSWORD = "hud.uk";
    private static final String LOG_FILE = "parking_logs.txt";

    // 存储车辆信息的数组：索引0-车牌号，1-类型，2-停放时间，3-在场状态
    private static String[][] parkingData = new String[MAX_CAPACITY][5];
    private static int carSpots = 50;
    private static int motoSpots = 30;
    private static int currentIndex = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Welcome to Smart Park Company, please enter the password：");
        String inputPassword = scanner.nextLine().trim();

        if (!inputPassword.equals(PASSWORD)) {
            System.out.println("Incorrect password! Program exiting.");
            return;
        }

        System.out.println("""
            Hello Administrator, the program has now started, here is the program guide:
            If someone needs to park now, please enter “start”.
            If today's work is finished, please enter ”end“.
            """);

        while (true) {
            String command = scanner.nextLine().trim().toLowerCase();

            if (command.equals("end")) break;

            if (command.equals("start")) {
                handleParking(scanner);
            } else if (command.equals("out")) {
                handleExit(scanner);
            }
        }

        scanner.close();
    }

    private static void handleParking(Scanner scanner) {
        System.out.print("Please enter the vehicle type（car/moto/e_car/vip）：");
        String type = scanner.nextLine().trim().toLowerCase();

        if (!Arrays.asList("car", "moto", "e_car", "vip").contains(type)) {
            System.out.println("Invalid vehicle type!");
            return;
        }

        // 创建对应车辆对象
        vehicle vehicle = createVehicle(type);

        vehicle.SetLicensePlate();

        // 存入数组
        parkingData[currentIndex][0] = vehicle.LicensePlate;
        parkingData[currentIndex][1] = type;
        parkingData[currentIndex][3] = "true";
        parkingData[currentIndex][4] = "0.0";
        currentIndex++;

        // 更新车位计数
        if (type.equals("moto")) {
            motoSpots--;
        } else {
            carSpots--;
        }

        // 写入文件
        writeToFile(vehicle.LicensePlate, type, "in");

        System.out.println("Parking procedures have been completed.");
        System.out.printf("""
            Currently, out of 50 car parking spaces, %d are occupied, leaving %d available.
            Currently, out of 30 motorcycle parking spaces, %d are occupied, leaving %d available.
            """, 50 - carSpots, carSpots, 30 - motoSpots, motoSpots);
    }

    private static void handleExit(Scanner scanner) {
        System.out.print("Please enter the license plate number of the car you are leaving:");
        String plate = scanner.nextLine().trim();

        int index = -1;
        for (int i = 0; i < currentIndex; i++) {
            if (parkingData[i][0].equals(plate) && parkingData[i][3].equals("true")) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("Vehicle not found！");
            return;
        }

        //载入停留时间
        System.out.print("Please enter the duration of stay (minutes): ");
        String timeStr = scanner.nextLine().trim();
        int time;
        try {
            time = Integer.parseInt(timeStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid time format! Please enter a number.");
            return;
        }
        parkingData[index][2] = timeStr;

        // 计算费用
        String type = parkingData[index][1];
        vehicle vehicle = createVehicle(type);
        vehicle.SetParkingtime(time); // 设置停放时间
        double fee = vehicle.calculateFee(); // 调用计费方法
        parkingData[index][4] = String.valueOf(fee); // 存储费用

        System.out.print("If you have already paid, please enter 'paid':");
        if (!scanner.nextLine().trim().equalsIgnoreCase("paid")) {
            System.out.println("Unconfirmed payment！");
            return;
        }

        double totalfee = Double.parseDouble(parkingData[index][4]);
        System.out.printf("Payment confirmed. Total fee: %.2f\n", totalfee);

        // 更新状态
        parkingData[index][3] = "false";

        // 更新车位计数
        if (parkingData[index][1].equals("moto")) {
            motoSpots++;
        } else {
            carSpots++;
        }

        // 写入文件
        writeToFile(parkingData[index][0], parkingData[index][1], "out");

        System.out.printf("""
            Currently, out of 50 car parking spaces, %d are occupied, leaving %d available.
            Currently, out of 30 motorcycle parking spaces, %d are occupied, leaving %d available.
            """, 50 - carSpots, carSpots, 30 - motoSpots, motoSpots);
    }

    private static vehicle createVehicle(String type) {
        switch (type) {
            case "car":
                return new car();
            case "moto":
                return new moto();
            case "e_car":
                return new e_car();
            case "vip":
                return new vip();
            default:
                throw new IllegalArgumentException("Invalid vehicle type");
        }
    }

    private static void writeToFile(String plate, String type, String action) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(type + "," + plate + "," + action + "\n");
        } catch (IOException e) {
            System.out.println("File write error：" + e.getMessage());
        }
    }
}