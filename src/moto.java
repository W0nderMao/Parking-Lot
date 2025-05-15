public class moto extends vehicle implements Reportable{

    @Override
    public double calculateFee() {
        this.TotalFee = Math.ceil(Parkingtime / 30);
        return TotalFee;
    }

    public String generateReport()
    {
        return getHeader() + "\nMOTO" + LicensePlate;
    }
}
