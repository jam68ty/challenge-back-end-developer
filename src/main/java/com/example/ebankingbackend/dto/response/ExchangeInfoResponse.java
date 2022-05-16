package com.example.ebankingbackend.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Exchange Info Response")
public class ExchangeInfoResponse {

    @ApiModelProperty(value = "timestamp", position = 0)
    private long timestamp;
    @ApiModelProperty(value = "rate", position = 1)
    private double rate;
}
