package team.a9043.sign_in_system.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import team.a9043.sign_in_system.exception.UnauthorizedException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ApiIgnore
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    private static final String errResStr =
        "{\"success\":false,\"error\":true,\"errType\":\"%s\"," +
            "\"message\":%s}";

    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleUNAUTHORIZED(Exception e,
                                   HttpServletResponse response) throws IOException {
        response.setHeader("Content-type",
            "application/json;charset=utf-8");
        response.getWriter().write(
            formatErr(HttpStatus.UNAUTHORIZED, e.getMessage()));
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public void handleUnSupportedMediaType(Exception e,
                                           HttpServletResponse response) throws IOException {
        response.setHeader("Content-type",
            "application/json;charset=utf-8");
        response.getWriter().write(
            formatErr(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage()));
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleForbidden(Exception e,
                                HttpServletResponse response) throws IOException {
        response.setHeader("Content-type",
            "application/json;charset=utf-8");
        response.getWriter().write(
            formatErr(HttpStatus.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler({
        MissingServletRequestParameterException.class,
        MissingPathVariableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleBadRequest(Exception e,
                                 HttpServletResponse response) throws IOException {
        response.setHeader("Content-type",
            "application/json;charset=utf-8");
        response.getWriter().write(
            formatErr(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleOther(Exception e,
                            HttpServletResponse response) throws IOException {
        response.setHeader("Content-type",
            "application/json;charset=utf-8");
        e.printStackTrace();
        response.getWriter().write(
            formatErr(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    private String formatErr(HttpStatus errType, String message) {
        return String.format(errResStr,
            errType.getReasonPhrase(),
            JSONObject.quote(message));
    }
}
