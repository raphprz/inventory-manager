package org.exercise.inventorymanager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exercise.inventorymanager.config.AppConfig;
import org.exercise.inventorymanager.entities.InventoryEntity;
import org.exercise.inventorymanager.errors.ItemNotFoundError;
import org.exercise.inventorymanager.repositories.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final AppConfig appConfig;
    private final InventoryRepository inventoryRepository;

    public List<InventoryEntity> findAllItemsByName(String name) {
        return inventoryRepository.findByName(name);
    }

    public List<InventoryEntity> findAllItemsByNameAndCategory(String name, String category) {
        return inventoryRepository.findByNameAndCategory(name, category);
    }

    public InventoryEntity findSpecificItemInInventory(String name, String category, String subcategory) {
        return inventoryRepository.findByNameAndCategoryAndSubcategory(name, category, subcategory);
    }

    public InventoryEntity updateItemQuantity(String name, String category, String subcategory, Integer quantity) {
        if (!StringUtils.hasText(name))
            throw new IllegalArgumentException("name must be a valid string!");

        if (quantity < 0)
            throw new IllegalArgumentException("quantity must be positive!");

        if (!isAllowedItem(category, subcategory))
            throw new ItemNotFoundError("Item does not exist, create it first!");

        // returns the updated entity
        return inventoryRepository
                .updateQuantity(name, category, subcategory, quantity);
    }

    public void deleteInventory(String name) {
        inventoryRepository.deleteByName(name);
    }

    public void deleteItemInInventory(String name, String category, String subcategory) {
        inventoryRepository.deleteByNameAndCategoryAndSubcategory(name, category, subcategory);
    }

    public boolean isAllowedItem(String category, String subcategory) {
        if (!StringUtils.hasText(category) || !StringUtils.hasText(subcategory))
            throw new IllegalArgumentException("Item must have a valid category and subcategory!");

        Map<String, Set<String>> allowedCategories = appConfig.getAllowedItems();

        return allowedCategories.containsKey(category)
                && allowedCategories.get(category).contains(subcategory);
    }

}
