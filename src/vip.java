public class vip extends vehicle implements Reportable
{
    @Override
    public double calculateFee() {
        this.TotalFee = 0.0;
        return 0.0;
    }

    public String generateReport()
    {
        return getHeader() + "\nVIP" + LicensePlate;
    }
}
