package com.carsharing.payment.service.impl;

import com.carsharing.payment.service.LiqPayService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LiqPayServiceImpl implements LiqPayService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${liqpay.public-key}")
    private String publicKey;

    @Value("${liqpay.private-key}")
    private String privateKey;

    @Value("${liqpay.result-url}")
    private String resultUrl;

    @Value("${liqpay.server-url}")
    private String serverUrl;

    @Override
    public String createData(Map<String, Object> params) {
        try {
            String json = objectMapper.writeValueAsString(params);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося сформувати LiqPay data", e);
        }
    }

    @Override
    public String createSignature(String data) {
        try {
            String signString = privateKey + data + privateKey;

            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] digest = sha1.digest(signString.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося сформувати LiqPay signature", e);
        }
    }

    @Override
    public boolean isSignatureValid(String data, String signature) {
        return createSignature(data).equals(signature);
    }

    @Override
    public JsonNode decodeData(String data) {
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            String json = new String(decoded, StandardCharsets.UTF_8);
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося розкодувати LiqPay data", e);
        }
    }

    @Override
    public Map<String, Object> createCheckoutParams(
            String orderId,
            BigDecimal amount,
            String description
    ) {
        Map<String, Object> params = new LinkedHashMap<>();

        params.put("version", 3);
        params.put("public_key", publicKey);
        params.put("action", "pay");
        params.put("amount", amount);
        params.put("currency", "UAH");
        params.put("description", description);
        params.put("order_id", orderId);
        params.put("result_url", resultUrl);
        params.put("server_url", serverUrl);
        params.put("sandbox", 1);

        return params;
    }
}