package br.com.murilo.luizalab.exception;

import lombok.Getter;

import java.util.Date;

@Getter
public class ExceptionResponse {

    private Date timestamp;
    private String message;
    private String details;

    public ExceptionResponse(final Date timestamp, final String message, final String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}
