package com.example.ebankingbackend.dto.response;

import com.example.ebankingbackend.model.MultiCurrencyAccount;
import com.example.ebankingbackend.model.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel(description = "Account Response")
public class AccountResponse {

    @ApiModelProperty(value = "ibanCode", position = 0)
    private String ibanCode;
    @ApiModelProperty(value = "user", position = 1)
    private User userId;
    @ApiModelProperty(value = "multiCurrencyAccountSet", position = 2)
    private Set<MultiCurrencyAccount> multiCurrencyAccountSet;
}
