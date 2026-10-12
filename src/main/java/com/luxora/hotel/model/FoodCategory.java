package com.luxora.hotel.model;

public enum FoodCategory {
    BREAKFAST("Breakfast", "Morning favorites"),
    MAIN_COURSE("Main Course", "Chef-crafted signatures"),
    SNACKS("Snacks", "Quick bites and light cravings"),
    SWEETS("Desserts", "Sweet finishing touches"),
    BEVERAGES("Beverages", "Refreshing drinks and pairings"),
    SPECIALTY("Specialty", "Chef's curated experiences");

    private final String displayName;
    private final String description;

    FoodCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
