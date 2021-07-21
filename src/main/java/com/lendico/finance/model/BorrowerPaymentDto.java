package com.lendico.finance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lendico.finance.constant.GlobalConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowerPaymentDto {

    private Double borrowerPaymentAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_TIME_FORMAT)
    private LocalDateTime date;
    private Double initialOutstandingPrincipal;
    private Double interest;
    private Double principal;
    private Double remainingOutstandingPrincipal;

}
