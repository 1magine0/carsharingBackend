package com.carsharing.bonus.service;

import com.carsharing.bonus.dto.BonusBalanceResponse;
import com.carsharing.bonus.dto.BonusTransactionResponse;

import java.util.List;

public interface BonusService {

    BonusBalanceResponse getMyBalance();

    List<BonusTransactionResponse> getMyHistory();
}