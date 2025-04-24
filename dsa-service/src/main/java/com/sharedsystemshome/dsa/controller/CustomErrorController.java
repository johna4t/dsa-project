package com.sharedsystemshome.dsa.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    private boolean hasToken(HttpServletRequest request) {

        // Assuming the token is stored in the "Authorization" header
        String token = request.getHeader("Authorization");

        // Check if the token is present and not empty
        return token != null && !token.isEmpty();
    }

    @RequestMapping("/error")
    public ResponseEntity<?> error(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer statusCode = status != null ?
                Integer.valueOf(status.toString()) : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);

        String msg = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        Exception ex = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        String token = request.getHeader("Authorization"); // Assuming the token is stored in the "Authorization" header
        Boolean hasToken = token != null && !token.isEmpty();

        String message = "";
        if(HttpStatus.FORBIDDEN == httpStatus && !hasToken){
            httpStatus = HttpStatus.BAD_REQUEST;
            message = "Invalid Request.";
        } else {

            if (null != ex && "" != ex.getMessage()) {
                message = ex.getMessage();
            } else if (null != msg && "" != msg) {
                message = msg;
            } else {
                message = "Invalid Request.";
            }
        }

        if(message.contains("JWT")){
            httpStatus = HttpStatus.UNAUTHORIZED;
            message = "Unable to authenticate user.";
        }

        if(HttpStatus.INTERNAL_SERVER_ERROR == httpStatus){
            message = "Please check application server log for further details.";
        }

        HttpRequestException httpRequestException = new HttpRequestException(
                httpStatus,
                message
        );

        if(httpStatus.is5xxServerError()){
            logger.error(message, ex);
        } else {
            logger.warn(message, ex);
        }

        return new ResponseEntity<>(httpRequestException, httpStatus);

    }

}
