package org.exercise.inventorymanager.errors;

public class ItemNotFoundError extends RuntimeException {
    public ItemNotFoundError(String message) {
        super(message);
    }
}
