package com.peak.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.peak.httpUiltr.HttpEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NotNull
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GlobalException extends RuntimeException{

    private HttpEnum httpEnum;
    private String msg;

    public GlobalException(HttpEnum httpEnum) {
        this.httpEnum = httpEnum;
    }

    public GlobalException(HttpEnum httpEnum, String msg) {
        this.httpEnum = httpEnum;
        this.msg = msg;
    }
}
