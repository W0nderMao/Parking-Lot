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

        label:
        while (true) {
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "end":
                    handleEndLogic();
                    break label;
                case "start":
                    handleParking(scanner);
                    break;
                case "out":
                    handleExit(scanner);
                    break;
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

        // ===== 超24小时额外收费 =====
        if (!type.equals("vip")) {
            int totalMinutes = time; // 用户输入的停放时间（已解析为int）
            if (totalMinutes > 1440) { // 超过24小时
                int extraMinutes = totalMinutes - 1440;
                int extraHours = extraMinutes / 60;
                if (extraMinutes % 60 > 0) { // 不足1小时按1小时算
                    extraHours += 1;
                }
                fee += extraHours * 5; // 累加额外费用
            }
        }

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

    private static void handleEndLogic() {
        // 1. 计算总收益
        double totalRevenue = 0.0;
        List<String[]> sortedVehicles = new ArrayList<>();

        // 过滤有效数据并收集可排序的车辆
        for (int i = 0; i < currentIndex; i++) {
            String[] vehicle = parkingData[i];
            if (vehicle[3].equals("false")) { // 仅统计已离场车辆
                totalRevenue += Double.parseDouble(vehicle[4]);
            }
            sortedVehicles.add(vehicle);
        }

        // 2. 按停放时间（降序）和车牌（升序）排序
        sortedVehicles.sort((v1, v2) -> {
            int time1 = Integer.parseInt(v1[2]);
            int time2 = Integer.parseInt(v2[2]);
            if (time2 != time1) {
                return Integer.compare(time2, time1); // 时间降序
            }
            return v1[0].compareTo(v2[0]); // 车牌升序
        });

        // 输出总收益
        System.out.printf("Today's total revenue is：%.2f\n", totalRevenue);

        // 输出排序后的车辆信息
        System.out.println("==== Vehicle Parking Record ====");
        for (String[] vehicle : sortedVehicles) {
            String type = vehicle[1];
            String plate = vehicle[0];
            String time = vehicle[2];
            String fee = vehicle[3].equals("false") ? String.format("%.2f", Double.parseDouble(vehicle[4])) : "Not exited";

            System.out.printf("%s type vehicle %s parking time is %s minutes, earnings are %s yuan.\n",
                    type.toUpperCase(), plate, time, fee);
        }

        // 3. 检查未离场车辆
        boolean hasOvernight = false;
        for (int i = 0; i < currentIndex; i++) {
            String[] vehicle = parkingData[i];
            if (vehicle[3].equals("true")) {
                if (!hasOvernight) {
                    System.out.println("==== Overnight vehicles ====");
                    hasOvernight = true;
                }
                System.out.printf("%s type vehicle %s parking time is %s minutes (overnight vehicle)\n",
                        vehicle[1].toUpperCase(), vehicle[0], vehicle[2]);
            }
        }

        System.out.println("Thank you for using the 'Smart Park' system.");
    }

    private static vehicle createVehicle(String type) {
        return switch (type) {
            case "car" -> new car();
            case "moto" -> new moto();
            case "e_car" -> new e_car();
            case "vip" -> new vip();
            default -> throw new IllegalArgumentException("Invalid vehicle type");
        };
    }

    private static void writeToFile(String plate, String type, String action) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(type + "," + plate + "," + action + "\n");
        } catch (IOException e) {
            System.out.println("File write error：" + e.getMessage());
        }
    }
}