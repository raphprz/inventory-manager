package org.exercise.inventorymanager.services;

import org.exercise.inventorymanager.entities.InventoryEntity;
import org.exercise.inventorymanager.errors.ItemNotFoundError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class InventoryServiceTest {

    private final InventoryService inventoryService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    InventoryServiceTest(InventoryService inventoryService, MongoTemplate mongoTemplate) {
        this.inventoryService = inventoryService;
        this.mongoTemplate = mongoTemplate;
    }

    @Test
    void findAllItemsByName_ShouldBeEmpty() {
        List<InventoryEntity> actualEntities = inventoryService.findAllItemsByName("findAllItemsByName_ShouldBeEmpty");
        assertThat(actualEntities).isEmpty();

        InventoryEntity entity1 = InventoryEntity.builder()
                .name("findAllItemsByName_ShouldBeEmpty_DoesNotMatch")
                .category("cat1")
                .subcategory("subcat1")
                .build();

        mongoTemplate.insert(entity1);

        List<InventoryEntity> actualEntitiesAfterInsert = inventoryService.findAllItemsByName(
                "findAllItemsByName_ShouldBeEmpty");

        assertThat(actualEntitiesAfterInsert).isEmpty();
    }

    @Test
    void findAllItemsByName_ShouldHave2Items() {
        InventoryEntity entity1 = InventoryEntity.builder()
                .name("findAllItemsByName_ShouldHave2Items")
                .category("cat1")
                .subcategory("subcat1")
                .build();

        InventoryEntity entity2 = InventoryEntity.builder()
                .name("findAllItemsByName_ShouldHave2Items")
                .category("cat2")
                .subcategory("subcat3")
                .build();

        InventoryEntity entity3 = InventoryEntity.builder()
                .name("findAllItemsByName_ShouldHave2Items_ButNotThisOne")
                .category("cat2")
                .subcategory("subcat4")
                .build();

        mongoTemplate.insert(entity1);
        mongoTemplate.insert(entity2);
        mongoTemplate.insert(entity3);

        List<InventoryEntity> expectedEntities = List.of(entity1, entity2);
        List<InventoryEntity> actualEntities = inventoryService.findAllItemsByName("findAllItemsByName_ShouldHave2Items");

        assertThat(actualEntities).hasSize(2);
        assertThat(actualEntities).containsExactlyInAnyOrderElementsOf(expectedEntities);
    }

    @Test
    void findAllItemsByNameAndCategory_ShouldBeEmpty() {
        InventoryEntity entity = InventoryEntity.builder()
                .name("findAllItemsByNameAndCategory_ShouldBeEmpty")
                .category("cat1")
                .subcategory("subcat1")
                .build();

        mongoTemplate.insert(entity);

        List<InventoryEntity> entities = inventoryService
                .findAllItemsByNameAndCategory("findAllItemsByNameAndCategory_ShouldBeEmpty", "cat2");

        assertThat(entities).isEmpty();
    }

    @Test
    void findAllItemsByNameAndCategory_ShouldHave1Item() {
        InventoryEntity entity1 = InventoryEntity.builder()
                .name("findAllItemsByNameAndCategory_ShouldHave1Item")
                .category("cat1")
                .subcategory("subcat1")
                .build();

        InventoryEntity entity2 = InventoryEntity.builder()
                .name("findAllItemsByNameAndCategory_ShouldHave1Item")
                .category("cat2")
                .subcategory("subcat3")
                .build();

        mongoTemplate.insert(entity1);
        mongoTemplate.insert(entity2);

        List<InventoryEntity> expectedEntities = List.of(entity1);
        List<InventoryEntity> actualEntities = inventoryService.findAllItemsByNameAndCategory(
                "findAllItemsByNameAndCategory_ShouldHave1Item",
                "cat1");

        assertThat(actualEntities).hasSize(1);
        assertThat(actualEntities).containsExactlyInAnyOrderElementsOf(expectedEntities);
    }

    @Test
    void findSpecificItemInInventory_ShouldBeEmpty() {
        InventoryEntity entity = InventoryEntity.builder()
                .name("findSpecificItemInInventory_ShouldBeEmpty")
                .category("cat1")
                .subcategory("subcat1")
                .build();

        mongoTemplate.insert(entity);

        InventoryEntity actualEntity = inventoryService
                .findSpecificItemInInventory(
                        "findAllItemsByNameAndCategory_ShouldBeEmpty",
                        "cat1",
                        "subcat2");

        assertThat(actualEntity).isNull();

    }

    @Test
    void findSpecificItemInInventory_ShouldHave1Item() {
        InventoryEntity entity1 = InventoryEntity.builder()
                .name("findSpecificItemInInventory_ShouldHave1Item")
                .category("cat1")
                .subcategory("subcat1")
                .build();

        InventoryEntity entity2 = InventoryEntity.builder()
                .name("findSpecificItemInInventory_ShouldHave1Item")
                .category("cat1")
                .subcategory("subcat2")
                .build();

        mongoTemplate.insert(entity1);
        mongoTemplate.insert(entity2);

        InventoryEntity entity = inventoryService
                .findSpecificItemInInventory(
                        "findSpecificItemInInventory_ShouldHave1Item",
                        "cat1",
                        "subcat2");

        assertThat(entity).isNotNull();
        assertThat(entity).isEqualTo(entity2);
    }

    @Test
    void updateItemQuantity_ShouldCreateItemAndReturnNewlyCreatedItem() {
        InventoryEntity expectedEntity = InventoryEntity.builder()
                .name("updateItemQuantity_ShouldCreateItem")
                .category("cat1")
                .subcategory("subcat2")
                .quantity(8)
                .build();

        InventoryEntity actualEntityBeforeUpdate = inventoryService.findSpecificItemInInventory(
                "updateItemQuantity_ShouldCreateItem",
                "cat1",
                "subcat2");

        assertThat(actualEntityBeforeUpdate).isNull();

        InventoryEntity actualEntityAfterUpdate = inventoryService.updateItemQuantity(
                "updateItemQuantity_ShouldCreateItem",
                "cat1",
                "subcat2",
                8);

        assertThat(actualEntityAfterUpdate).isNotNull();
        assertThat(actualEntityAfterUpdate.getId()).isNotNull();

        assertThat(actualEntityAfterUpdate.getName()).isEqualTo(expectedEntity.getName());
        assertThat(actualEntityAfterUpdate.getCategory()).isEqualTo(expectedEntity.getCategory());
        assertThat(actualEntityAfterUpdate.getSubcategory()).isEqualTo(expectedEntity.getSubcategory());
        assertThat(actualEntityAfterUpdate.getQuantity()).isEqualTo(expectedEntity.getQuantity());
    }

    @Test
    void updateItemQuantity_ShouldUpdateItem() {
        InventoryEntity expectedEntity = InventoryEntity.builder()
                .name("updateItemQuantity_ShouldUpdateItem")
                .category("cat1")
                .subcategory("subcat2")
                .quantity(8)
                .build();

        mongoTemplate.insert(expectedEntity);

        InventoryEntity actualEntityBeforeUpdate = inventoryService.findSpecificItemInInventory(
                "updateItemQuantity_ShouldUpdateItem",
                "cat1",
                "subcat2");

        assertThat(actualEntityBeforeUpdate).isNotNull();
        assertThat(actualEntityBeforeUpdate.getQuantity()).isEqualTo(8);

        InventoryEntity actualEntityAfterUpdate = inventoryService.updateItemQuantity(
                "updateItemQuantity_ShouldUpdateItem",
                "cat1",
                "subcat2",
                16);

        assertThat(actualEntityAfterUpdate).isNotNull();
        assertThat(actualEntityAfterUpdate.getId()).isNotNull();

        assertThat(actualEntityAfterUpdate.getName()).isEqualTo(expectedEntity.getName());
        assertThat(actualEntityAfterUpdate.getCategory()).isEqualTo(expectedEntity.getCategory());
        assertThat(actualEntityAfterUpdate.getSubcategory()).isEqualTo(expectedEntity.getSubcategory());
        assertThat(actualEntityAfterUpdate.getQuantity()).isEqualTo(16);
    }

    @Test
    void updateItemQuantity_ShouldThrow() {
        assertThatThrownBy(() -> inventoryService.updateItemQuantity(
                null,
                "cat1",
                "subcat1",
                1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> inventoryService.updateItemQuantity(
                "",
                "cat1",
                "subcat1",
                1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> inventoryService.updateItemQuantity(
                "updateItemQuantity_ShouldThrow",
                "cat1",
                "subcat1",
                -8))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> inventoryService.updateItemQuantity(
                "updateItemQuantity_ShouldThrow",
                "cat1",
                "subcat3",
                8))
                .isInstanceOf(ItemNotFoundError.class);
    }

    @Test
    void deleteItemInInventory_ShouldNotDeleteAnything_BecauseNoMatch() {
        InventoryEntity entity1 = InventoryEntity.builder()
                .name("deleteItemInInventory_ShouldNotDeleteAnything_BecauseNoMatch")
                .category("cat1")
                .subcategory("subcat1")
                .build();

        InventoryEntity entity2 = InventoryEntity.builder()
                .name("deleteItemInInventory_ShouldNotDeleteAnything_BecauseNoMatch")
                .category("cat1")
                .subcategory("subcat2")
                .build();

        mongoTemplate.insert(entity1);
        mongoTemplate.insert(entity2);

        inventoryService.deleteItemInInventory(
                "deleteItemInInventory_ShouldNotDeleteAnything_BecauseNoMatch",
                "cat2",
                "subcat3"
        );

        List<InventoryEntity> expectedEntities = List.of(entity1, entity2);
        List<InventoryEntity> actualEntities = inventoryService.findAllItemsByName(
                "deleteItemInInventory_ShouldNotDeleteAnything_BecauseNoMatch");

        assertThat(actualEntities).hasSize(2);
        assertThat(actualEntities).containsExactlyInAnyOrderElementsOf(expectedEntities);
    }

    @Test
    void deleteItemInInventory_ShouldDeleteOneItem() {
        InventoryEntity entity1 = InventoryEntity.builder()
                .name("deleteItemInInventory_ShouldDeleteOneItem")
                .category("cat1")
                .subcategory("subcat1")
                .build();

        InventoryEntity entity2 = InventoryEntity.builder()
                .name("deleteItemInInventory_ShouldDeleteOneItem")
                .category("cat1")
                .subcategory("subcat2")
                .build();

        mongoTemplate.insert(entity1);
        mongoTemplate.insert(entity2);

        inventoryService.deleteItemInInventory(
                "deleteItemInInventory_ShouldDeleteOneItem",
                "cat1",
                "subcat2"
        );

        List<InventoryEntity> expectedEntities = List.of(entity1);
        List<InventoryEntity> actualEntities = mongoTemplate.find(
                Query.query(Criteria.where("name").is("deleteItemInInventory_ShouldDeleteOneItem")),
                InventoryEntity.class);

        assertThat(actualEntities).hasSize(1);
        assertThat(actualEntities).containsExactlyInAnyOrderElementsOf(expectedEntities);
    }

    @Test
    void deleteInventory_ShouldDeleteAllItems() {
        InventoryEntity entity1 = InventoryEntity.builder()
                .name("deleteInventory_ShouldDeleteAllItems")
                .category("cat1")
                .subcategory("subcat1")
                .build();

        InventoryEntity entity2 = InventoryEntity.builder()
                .name("deleteInventory_ShouldDeleteAllItems")
                .category("cat1")
                .subcategory("subcat2")
                .build();

        mongoTemplate.insert(entity1);
        mongoTemplate.insert(entity2);

        inventoryService.deleteInventory(
                "deleteInventory_ShouldDeleteAllItems");

        List<InventoryEntity> actualEntities = mongoTemplate.find(
                Query.query(Criteria.where("name").is("deleteInventory_ShouldDeleteAllItems")),
                InventoryEntity.class);

        assertThat(actualEntities).isEmpty();
    }

    @Test
    void isAllowedItem_ShouldReturnTrue() {
        assertThat(inventoryService.isAllowedItem(
                "cat1",
                "subcat1"))
                .isTrue();
    }

    @Test
    void isAllowedItem_ShouldReturnFalse() {
        assertThat(inventoryService.isAllowedItem(
                "cat1",
                "subcat3"))
                .isFalse();
    }

    @Test
    void isAllowedItem_ShouldThrow() {
        assertThatThrownBy(() -> inventoryService.isAllowedItem(
                "",
                "subcat1"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> inventoryService.isAllowedItem(
                "cat1",
                ""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> inventoryService.isAllowedItem(
                "",
                ""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> inventoryService.isAllowedItem(
                null,
                "subcat1"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> inventoryService.isAllowedItem(
                "cat1",
                null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> inventoryService.isAllowedItem(
                null,
                null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}