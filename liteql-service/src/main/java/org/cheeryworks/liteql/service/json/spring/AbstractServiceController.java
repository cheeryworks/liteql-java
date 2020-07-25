package org.cheeryworks.liteql.service.json.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
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

    protected ResponseEntity getErrorResponseEntity(Object data) {
        return getErrorResponseEntity(data, null);
    }

    protected ResponseEntity getErrorResponseEntity(Exception ex) {
        return getErrorResponseEntity(null, ex);
    }

    protected ResponseEntity getErrorResponseEntity(Object data, Exception ex) {
        if (data == null && ex != null) {
            Map<String, Object> errorData = new LinkedHashMap<>();

            String message = ex.getMessage();

            errorData.put("message", message);

            data = errorData;
        }

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity(data, httpStatus);
    }

}
