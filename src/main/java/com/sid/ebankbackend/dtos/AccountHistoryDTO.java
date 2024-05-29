package com.sid.ebankbackend.dtos;

import lombok.Data;

import java.util.List;
@Data

public class AccountHistoryDTO {
    private String accountId;
    private double balance;
    private int currentPages;
    private int totalePages;
    private int pageSize;
    private List<AccountOperationDTO> accountOperationDTOS;

}
