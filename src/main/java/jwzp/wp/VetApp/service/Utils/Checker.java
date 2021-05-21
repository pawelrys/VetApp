package jwzp.wp.VetApp.service.Utils;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessageFormatter;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Optional;

public class Checker {

    public static final Logger logger = LogManager.getLogger(Checker.class);

    public static <T> Optional<ResponseErrorMessage> getMissingData(T dto){
        Optional<ResponseErrorMessage> missingData;
        try {
            missingData = getMissingDataAux(dto);
        } catch (IllegalAccessException e) {
            logger.error(LogsUtils.logException(e));
            return Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.INTERNAL_SERVICE_ERROR));
        }
        return missingData;
    }

    private static <T> Optional<ResponseErrorMessage> getMissingDataAux(T dto) throws IllegalAccessException {
        var missingData = new ErrorMessagesBuilder();
        for (Field field : dto.getClass().getFields()) {
            if (field.get(dto) == null) {
                missingData.addToMessage(ErrorMessageFormatter.missingField(field.getName()));
                logger.info(LogsUtils.logMissingData(dto, field));
            }
        }
        return missingData.isEmpty() ?
                Optional.empty() :
                Optional.of(missingData.build(ErrorType.WRONG_ARGUMENTS));
    }

}
