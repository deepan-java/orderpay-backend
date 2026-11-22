package com.deepan.orderpay.catalog.exception;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource , Long id) {
        super("RESOURCE_NOT_FOUND", resource + " not found with id " + id);
    }
}
