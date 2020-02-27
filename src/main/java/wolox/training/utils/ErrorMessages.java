package wolox.training.utils;

public class ErrorMessages {
    public static String emptyErrorMessage(String identifier){
        return String.format("%s cannot be empty!", identifier);
    }

    public static String nullErrorMessage(String identifier){
        return String.format("%s cannot be null!", identifier);
    }
}
