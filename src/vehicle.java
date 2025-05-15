import java.util.Scanner;

public abstract class vehicle
{
    protected String LicensePlate;
    protected double Parkingtime;
    protected double TotalFee;

    public abstract double calculateFee();

    public boolean isValidPlate(String plate){
        return plate.matches("[A-Z]{2}[]0-9]{2} [A-Z]{3}$");
    }

    public void SetParkingtime(double time){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the parking time: ");
        this.Parkingtime = scanner.nextInt();
        scanner.close();
    }
}