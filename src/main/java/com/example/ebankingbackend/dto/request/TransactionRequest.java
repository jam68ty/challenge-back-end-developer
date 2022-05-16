package com.example.ebankingbackend.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Transaction Request")
public class TransactionRequest {

    @ApiModelProperty(value = "multiCurrencyAccountId", required = true, position = 0)
    private String multiCurrencyAccountId;

    @ApiModelProperty(value = "transactionCurrency", required = true, position = 1)
    private String transactionCurrency;

    @ApiModelProperty(value = "amount", required = true, position = 2)
    private double amount;

    @ApiModelProperty(value = "description", required = false, position = 3)
    private String description;

    @ApiModelProperty(value = "type", required = true, position = 4)
    private String type;

}
