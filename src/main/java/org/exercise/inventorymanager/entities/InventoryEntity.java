package org.exercise.inventorymanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "inventories")
@CompoundIndex(name = "unique_item_per_inventory",
        def = "{'name' : 1, 'category' : 1, 'subcategory': 1}",
        unique = true)
public class InventoryEntity {

    @Id
    @JsonIgnore
    private String id;

    private String name;
    private String category;
    private String subcategory;
    private Integer quantity;

}
