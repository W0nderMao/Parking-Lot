public class car extends vehicle implements Reportable
{
    @Override
    public double calculateFee() {
        double hours = Math.max(1, Math.ceil(Parkingtime / 60.0));
        TotalFee = hours * 2;
        return TotalFee;
    }

    public String generateReport()
    {
        return getHeader() + "\nCAR" + LicensePlate;
    }
}
