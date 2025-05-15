public interface Reportable {
    String generateReport();
    default String getHeader() {
        return "===== SMART PARKING REPORT =====";
    }
}

