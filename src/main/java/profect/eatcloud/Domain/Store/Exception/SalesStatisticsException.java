package profect.eatcloud.Domain.Store.Exception;

public class SalesStatisticsException extends RuntimeException {
    
    public SalesStatisticsException(String message) {
        super(message);
    }
    
    public SalesStatisticsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static class InvalidDateRangeException extends SalesStatisticsException {
        public InvalidDateRangeException(String message) {
            super(message);
        }
    }
    
    public static class StoreNotFoundException extends SalesStatisticsException {
        public StoreNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class NoSalesDataException extends SalesStatisticsException {
        public NoSalesDataException(String message) {
            super(message);
        }
    }
}
