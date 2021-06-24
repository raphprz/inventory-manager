package org.exercise.inventorymanager.repositories;

import lombok.RequiredArgsConstructor;
import org.exercise.inventorymanager.entities.InventoryEntity;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InventoryRepository {

    private final MongoTemplate mongoTemplate;

    public List<InventoryEntity> findByName(String name) {
        Query query = Query.query(Criteria.where("name").is(name));

        return mongoTemplate.find(query, InventoryEntity.class);
    }

    public List<InventoryEntity> findByNameAndCategory(String name, String category) {
        Query query = Query.query(Criteria.where("name").is(name)
                .and("category").is(category));

        return mongoTemplate.find(query, InventoryEntity.class);

    }

    public InventoryEntity findByNameAndCategoryAndSubcategory(String name, String category, String subcategory) {
        Query query = Query.query(Criteria.where("name").is(name)
                .and("category").is(category)
                .and("subcategory").is(subcategory));

        return mongoTemplate.findOne(query, InventoryEntity.class);
    }

    public InventoryEntity updateQuantity(String name, String category, String subcategory, Integer quantity) {
        // we take advantage of the unique index on this collection (see InventoryEntity class)
        // that was we can easily update but also create if it does not exist, using upsert

        Query query = Query.query(Criteria.where("name").is(name)
                .and("category").is(category)
                .and("subcategory").is(subcategory));

        Update update = Update.update("quantity", quantity);

        FindAndModifyOptions options = FindAndModifyOptions.options()
                .upsert(true)
                .returnNew(true);

        return mongoTemplate.findAndModify(query, update, options, InventoryEntity.class);
    }

    public void deleteByName(String name) {
        Query query = Query.query(Criteria.where("name").is(name));

        mongoTemplate.remove(query, InventoryEntity.class);
    }

    public void deleteByNameAndCategoryAndSubcategory(String name, String category, String subcategory) {
        Query query = Query.query(Criteria.where("name").is(name)
                .and("category").is(category)
                .and("subcategory").is(subcategory));

        mongoTemplate.remove(query, InventoryEntity.class);
    }

}
