package com.example.ebankingbackend.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ApiModel(description = "Transaction Response")
public class TransactionResponse {

    @ApiModelProperty(value = "transactionId", position = 0)
    private UUID transactionId;

    @ApiModelProperty(value = "multiCurrencyAccountId", position = 1)
    private MultiCurrencyAccountResponse multiCurrencyAccountId;

    @ApiModelProperty(value = "amount", position = 2)
    private double amount;

    @ApiModelProperty(value = "currency", position = 3)
    private String currency;

    @ApiModelProperty(value = "valueDate", position = 4)
    private LocalDateTime valueDate;

    @ApiModelProperty(value = "description", position = 5)
    private String description;

    @ApiModelProperty(value = "type", position = 6)
    private String type;

}
