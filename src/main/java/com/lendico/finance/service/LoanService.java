package com.lendico.finance.service;

import com.lendico.finance.model.GeneratePlanRequestDto;
import com.lendico.finance.model.GeneratePlanResponseDto;

public interface LoanService {

    GeneratePlanResponseDto generatePlan(final GeneratePlanRequestDto generatePlanRequestDto);
}
