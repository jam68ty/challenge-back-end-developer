package com.example.ebankingbackend.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Currency Exchange Response")
public class CurrencyExchangeResponse {

    @ApiModelProperty(value = "success", position = 0)
    private boolean success;

    @ApiModelProperty(value = "date", position = 1)
    private String date;

    @ApiModelProperty(value = "result", position = 2)
    private double result;

    @ApiModelProperty(value = "info", position = 3)
    private ExchangeInfoResponse info;
}
