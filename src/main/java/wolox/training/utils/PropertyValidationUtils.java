package wolox.training.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static wolox.training.utils.ErrorMessages.emptyErrorMessage;
import static wolox.training.utils.ErrorMessages.nullErrorMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;

public class PropertyValidationUtils {
    public static LocalDate checkLocalDate(LocalDate date, String identifier){
        checkArgument(date != null, nullErrorMessage(identifier));
        return date;
    }

    public static String checkString(String str, String identifier) {
        checkArgument(str != null, nullErrorMessage(identifier));
        checkArgument( !str.isEmpty(), emptyErrorMessage(identifier));
        return str;
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
