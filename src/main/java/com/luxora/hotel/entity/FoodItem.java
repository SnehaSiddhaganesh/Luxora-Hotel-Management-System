package com.luxora.hotel.entity;

import com.luxora.hotel.model.DietaryType;
import com.luxora.hotel.model.FoodCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "food_items",
    indexes = {
        @Index(name = "idx_food_item_name", columnList = "name"),
        @Index(name = "idx_food_item_category", columnList = "category")
    }
)
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String itemCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FoodCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DietaryType dietaryType;

    @Column(nullable = false)
    private boolean available = true;

    @OneToMany(mappedBy = "foodItem")
    private List<OrderItem> orderItems = new ArrayList<>();

    public FoodItem() {
    }

    public FoodItem(String itemCode, String name, BigDecimal price, FoodCategory category, DietaryType dietaryType, boolean available) {
        this.itemCode = itemCode;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dietaryType = dietaryType != null ? dietaryType : DietaryType.GENERIC;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    public DietaryType getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(DietaryType dietaryType) {
        this.dietaryType = dietaryType;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
