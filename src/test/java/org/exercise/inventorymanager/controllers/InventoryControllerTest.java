package org.exercise.inventorymanager.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    InventoryControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void createAnInventory() throws Exception {
        // check if it's empty
        this.mockMvc.perform(get("/inventory/createAnInventory"))
                .andExpect(status().is4xxClientError());

        // create a product
        this.mockMvc.perform(post("/inventory/createAnInventory/cat1/subcat1/8"))
                .andExpect(status().is2xxSuccessful());

        // check the product is created
        this.mockMvc.perform(get("/inventory/createAnInventory"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteInventory_WithOnlyOneItem() throws Exception {
        // check if it's empty
        this.mockMvc.perform(get("/inventory/deleteInventory_WithOnlyOneItem"))
                .andExpect(status().is4xxClientError());

        // create a product
        this.mockMvc.perform(post("/inventory/deleteInventory_WithOnlyOneItem/cat1/subcat1/8"))
                .andExpect(status().is2xxSuccessful());

        // check the product is created
        this.mockMvc.perform(get("/inventory/deleteInventory_WithOnlyOneItem"))
                .andExpect(status().is2xxSuccessful());

        // delete it
        this.mockMvc.perform(delete("/inventory/deleteInventory_WithOnlyOneItem/cat1/subcat1"))
                .andExpect(status().is2xxSuccessful());

        // check if it's empty
        this.mockMvc.perform(get("/inventory/deleteInventory_WithOnlyOneItem"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteInventory_WithMultipleItems() throws Exception {
        // check if it's empty
        this.mockMvc.perform(get("/inventory/deleteInventory_WithMultipleItems"))
                .andExpect(status().is4xxClientError());

        // create 2 products
        this.mockMvc.perform(post("/inventory/deleteInventory_WithMultipleItems/cat1/subcat1/8"))
                .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(post("/inventory/deleteInventory_WithMultipleItems/cat1/subcat2/16"))
                .andExpect(status().is2xxSuccessful());

        // check products are created
        this.mockMvc.perform(get("/inventory/deleteInventory_WithMultipleItems"))
                .andExpect(status().is2xxSuccessful());

        // delete them all
        this.mockMvc.perform(delete("/inventory/deleteInventory_WithMultipleItems"))
                .andExpect(status().is2xxSuccessful());

        // check if it's empty
        this.mockMvc.perform(get("/inventory/deleteInventory_WithMultipleItems"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateQuantity_ShouldNotWorkBecauseOfBadInput() throws Exception {
        // no name
        this.mockMvc.perform(post("/inventory//cat1/subcat1/8"))
                .andExpect(status().is4xxClientError());

        // no category
        this.mockMvc.perform(post("/inventory/updateQuantity_ShouldNotWorkBecauseOfBadInput//subcat1/8"))
                .andExpect(status().is4xxClientError());

        // no subcategory
        this.mockMvc.perform(post("/inventory/updateQuantity_ShouldNotWorkBecauseOfBadInput/cat1//8"))
                .andExpect(status().is4xxClientError());

        // less than zero quantity
        this.mockMvc.perform(post("/inventory/updateQuantity_ShouldNotWorkBecauseOfBadInput/cat1/subcat1/-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void paramFormatter_ShouldReturnLowerCaseStrOrNull() {
        assertThat(InventoryController.paramFormatter(null)).isNull();
        assertThat(InventoryController.paramFormatter("")).isEqualTo("");
        assertThat(InventoryController.paramFormatter("test")).isEqualTo("test");
        assertThat(InventoryController.paramFormatter("TEST")).isEqualTo("test");
        assertThat(InventoryController.paramFormatter("Test1")).isEqualTo("test1");
    }

}