public class e_car extends vehicle implements Chargeable
{
    private int batteryLevel;

    @Override
    public double calculateFee() {
        double hours = Math.ceil(Parkingtime / 60);
        double totalFee = hours * 1.5;
        double finalFee = totalFee * 0.9;
        this.TotalFee = finalFee;
        return finalFee;
    }

    public void chargeBattery() {
        this.batteryLevel = 100;
    }

    public String generateReport()
    {
        return getHeader() + "\nE_Cars" + LicensePlate;
    }

}
