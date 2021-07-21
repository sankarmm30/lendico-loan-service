package com.lendico.finance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lendico.finance.constant.GlobalConstant;
import com.lendico.finance.util.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneratePlanRequestDto {

    @NotNull(message = "Loan amount should be provided")
    private Double loanAmount;

    @NotNull(message = "Nominal should be provided")
    private Double nominalRate;

    @NotNull(message = "Loan duration should be provided")
    private Integer duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_TIME_FORMAT)
    @NotNull(message = "Start date should be provided")
    private LocalDateTime startDate;

    @AssertTrue(message = "Loan amount should be greater than zero")
    private boolean isValidLoanAmount() {

        if(CommonUtil.isNotNull(loanAmount)) {

            return loanAmount > 0;
        }

        return false;
    }

    @AssertTrue(message = "Nominal rate should be greater than zero")
    private boolean isValidNominalRate() {

        if(CommonUtil.isNotNull(nominalRate)) {

            return nominalRate > 0;
        }

        return false;
    }

    @AssertTrue(message = "Duration should be greater than zero")
    private boolean isValidDuration() {

        if(CommonUtil.isNotNull(duration)) {

            return duration > 0;
        }

        return false;
    }
}
