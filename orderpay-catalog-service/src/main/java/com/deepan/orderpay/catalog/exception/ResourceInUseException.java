package com.deepan.orderpay.catalog.exception;

public class ResourceInUseException extends BusinessException {
    public ResourceInUseException(String resource, Long id, String usedBy) {
        super("RESOURCE_IN_USE",
                resource + " " + id + " cannot be deleted because it is still used by " + usedBy);
    }
}