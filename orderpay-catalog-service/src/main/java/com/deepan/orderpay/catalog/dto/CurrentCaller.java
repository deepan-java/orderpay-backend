package com.deepan.orderpay.catalog.dto;

import java.util.List;

public record CurrentCaller(String subject, String email, List<String> authorities) {}