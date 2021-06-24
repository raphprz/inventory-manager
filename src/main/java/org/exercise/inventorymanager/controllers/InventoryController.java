package org.exercise.inventorymanager.controllers;

import lombok.RequiredArgsConstructor;
import org.exercise.inventorymanager.entities.InventoryEntity;
import org.exercise.inventorymanager.services.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/inventory/{name}")
    public List<InventoryEntity> getAllItemsInInventory(@PathVariable(name = "name") String name) {
        if (!StringUtils.hasText(name))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name must be a valid string!");

        // get all items in an inventory
        List<InventoryEntity> inventoryEntities = inventoryService
                .findAllItemsByName(name);

        if (inventoryEntities.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nothing found!");

        return inventoryEntities;
    }

    @GetMapping("/inventory/{name}/{category}")
    public List<InventoryEntity> getInventoryItemsWithCategory(@PathVariable(name = "name") String name,
                                                               @PathVariable(name = "category") String category) {
        // get all item in an inventory that belong in a specific category
        if (!StringUtils.hasText(name))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name must be a valid string!");

        String formattedCategory = paramFormatter(category);

        List<InventoryEntity> inventoryEntities = inventoryService
                .findAllItemsByNameAndCategory(name, formattedCategory);

        if (inventoryEntities.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nothing found!");

        return inventoryEntities;
    }

    @GetMapping("/inventory/{name}/{category}/{subcategory}")
    public InventoryEntity getInventoryItemsWithCategoryAndSubcategory(@PathVariable(name = "name") String name,
                                                                       @PathVariable(name = "category") String category,
                                                                       @PathVariable(name = "subcategory") String subcategory) {
        // get a specific item in an inventory, if it exists
        if (!StringUtils.hasText(name))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name must be a valid string!");

        String formattedCategory = paramFormatter(category);
        String formattedSubcategory = paramFormatter(subcategory);

        InventoryEntity inventoryEntity = inventoryService
                .findSpecificItemInInventory(name, formattedCategory, formattedSubcategory);

        if (inventoryEntity == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nothing found!");

        return inventoryEntity;
    }

    @PostMapping("/inventory/{name}/{category}/{subcategory}/{quantity}")
    public void updateQuantity(@PathVariable(name = "name") String name,
                               @PathVariable(name = "category") String category,
                               @PathVariable(name = "subcategory") String subcategory,
                               @PathVariable(name = "quantity") int quantity) {
        // Update or insert a new item in an inventory
        String formattedCategory = paramFormatter(category);
        String formattedSubcategory = paramFormatter(subcategory);

        if (!StringUtils.hasText(name))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name must be a valid string!");

        if (quantity < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Quantity must be positive or zero!");

        if (!inventoryService.isAllowedItem(formattedCategory, formattedSubcategory))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Item category does not exists, create it first!");

        inventoryService.updateItemQuantity(name,
                formattedCategory,
                formattedSubcategory,
                quantity);
    }

    @DeleteMapping("/inventory/{name}")
    public void deleteInventory(@PathVariable(name = "name") String name) {
        // delete an inventory and all its items
        if (!StringUtils.hasText(name))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name must be a valid string!");

        inventoryService.deleteInventory(name);
    }

    @DeleteMapping("/inventory/{name}/{category}/{subcategory}")
    public void deleteItemInInventory(@PathVariable(name = "name") String name,
                                      @PathVariable(name = "category") String category,
                                      @PathVariable(name = "subcategory") String subcategory) {
        // delete a specific item in an inventory
        if (!StringUtils.hasText(name))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name must be a valid string!");

        String formattedCategory = paramFormatter(category);
        String formattedSubcategory = paramFormatter(subcategory);

        inventoryService.deleteItemInInventory(name, formattedCategory, formattedSubcategory);
    }

    public static String paramFormatter(String str) {
        return str == null
                ? null
                : str.toLowerCase();
    }

}
