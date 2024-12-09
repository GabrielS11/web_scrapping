package gvv.Types;

public enum FlightClass {
    ECONOMY,
    PREMIUM_ECONOMY,
    BUSINESS,
    FIRST
    ;


    public static FlightClass getFlightClass(String s) {
        return switch (s.toUpperCase()) {
            case "ECONOMY" -> ECONOMY;
            case "PREMIUM ECONOMY" -> PREMIUM_ECONOMY;
            case "BUSINESS" -> BUSINESS;
            case "FIRST-CLASS" -> FIRST;
            default -> null;
        };
    }
}
