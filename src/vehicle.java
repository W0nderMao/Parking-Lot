import java.util.Scanner;

public abstract class vehicle
{
    protected String LicensePlate;
    protected double Parkingtime;
    protected double TotalFee;
    private Scanner scanner = new Scanner(System.in);

    public abstract double calculateFee();

    public boolean isValidPlate(String plate){
        return plate.matches("[A-Z]{2}[0-9]{2} [A-Z]{3}$");
    }

    public void SetParkingtime(double time){
        System.out.print("Please enter the parking time: ");
        this.Parkingtime = scanner.nextInt();
        scanner.close();
    }

    public void SetLicensePlate(){
        while (true) {
            System.out.print("Please enter the license plate number：");
            String inputPlate = scanner.nextLine().trim();

            if (isValidPlate(inputPlate)) {
                this.LicensePlate = inputPlate;
                break;
            } else {
                System.out.println("The license plate number format is incorrect! Please enter it in the correct format.（For example：AB12 XYZ）");
            }
        }
    }
}