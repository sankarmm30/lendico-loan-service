package com.lendico.finance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lendico.finance.constant.GlobalConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericExceptionResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_TIME_FORMAT)
    private LocalDateTime timestamp;

    private Integer status;
    private String message;
    private List<String> errors;
    private String path;
}