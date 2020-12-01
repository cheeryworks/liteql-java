package org.cheeryworks.liteql.service.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractServiceController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected ResponseEntity getOkResponseEntity() {
        return getOkResponseEntity(null);
    }

    protected ResponseEntity getOkResponseEntity(Object data) {
        if (data != null) {
            return new ResponseEntity(data, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    protected ResponseEntity getErrorResponseEntity(String message) {
        Map<String, Object> errorData = new HashMap<>();

        errorData.put("message", message);

        return getErrorResponseEntity(errorData);
    }

    protected ResponseEntity getErrorResponseEntity(Exception ex) {
        Map<String, Object> errorData = new HashMap<>();

        if (ex != null) {
            errorData.put("message", ex.getMessage());
        }

        return getErrorResponseEntity(errorData);
    }

    protected ResponseEntity getErrorResponseEntity(Object errorData) {
        return new ResponseEntity(errorData, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
