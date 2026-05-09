package com.carsharing.payment.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.Map;

public interface LiqPayService {

    String createData(Map<String, Object> params);

    String createSignature(String data);

    boolean isSignatureValid(String data, String signature);

    JsonNode decodeData(String data);

    Map<String, Object> createCheckoutParams(
            String orderId,
            BigDecimal amount,
            String description
    );
}