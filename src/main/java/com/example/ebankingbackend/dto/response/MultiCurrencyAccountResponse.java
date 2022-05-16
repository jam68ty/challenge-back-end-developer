package com.example.ebankingbackend.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Multi Currency Account Response")
public class MultiCurrencyAccountResponse {
    @ApiModelProperty(value = "multiCurrencyAccountId", position = 0)
    private String multiCurrencyAccountId;
    @ApiModelProperty(value = "currency", position = 1)
    private String currency;
    @ApiModelProperty(value = "type", position = 2)
    private String type;
    @ApiModelProperty(value = "balance", position = 3)
    private double balance;

}
