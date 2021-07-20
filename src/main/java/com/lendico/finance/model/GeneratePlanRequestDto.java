package com.lendico.finance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lendico.finance.constant.GlobalConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneratePlanRequestDto {

    private Long loanAmount;
    private Double nominalRate;
    private Integer duration;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_TIME_FORMAT)
    private Date startDate;
}
