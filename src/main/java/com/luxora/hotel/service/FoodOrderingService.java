package com.luxora.hotel.service;

import java.util.ArrayList;
import java.util.List;

import com.luxora.hotel.model.BeverageItem;
import com.luxora.hotel.model.BreakfastItem;
import com.luxora.hotel.model.Cart;
import com.luxora.hotel.model.DessertItem;
import com.luxora.hotel.model.DietaryType;
import com.luxora.hotel.model.FoodItem;
import com.luxora.hotel.model.MainCourseItem;
import com.luxora.hotel.model.OrderItem;
import com.luxora.hotel.model.SnackItem;

public class FoodOrderingService {
    private final List<FoodItem> menu = new ArrayList<>();

    public void addMenuItem(FoodItem item) {
        if (item != null) {
            menu.add(item);
        }
    }

    public List<FoodItem> getAvailableMenu() {
        return menu.stream().filter(FoodItem::isAvailable).toList();
    }

    public double calculateCartTotal(Cart cart) {
        if (cart == null) {
            return 0.0;
        }
        return cart.getItems().stream().mapToDouble(OrderItem::getLineTotal).sum();
    }

    public void printMenu() {
        for (FoodItem item : menu) {
            System.out.println(item.getName() + " | " + item.getCategory().getDisplayName()
                    + " | " + item.getDietaryType() + " | " + item.getServingSuggestion()
                    + " | Price: ₹" + item.calculatePrice(1));
        }
    }

    public static void main(String[] args) {
        FoodOrderingService service = new FoodOrderingService();

        service.addMenuItem(new BreakfastItem("B01", "Avocado Toast", 280.0, DietaryType.VEGETARIAN, true));
        service.addMenuItem(new MainCourseItem("M02", "Herb Chicken Bowl", 680.0, DietaryType.NON_VEGETARIAN, true));
        service.addMenuItem(new BeverageItem("V03", "Citrus Cooler", 210.0, DietaryType.VEGAN, true));
        service.addMenuItem(new DessertItem("D04", "Dark Chocolate Tart", 320.0, DietaryType.GENERIC, true));
        service.addMenuItem(new SnackItem("S05", "Crispy Nachos", 260.0, DietaryType.VEGETARIAN, true));

        System.out.println("=== LUXORA FOOD MENU ===");
        service.printMenu();

        Cart cart = new Cart();
        cart.addItem(service.getAvailableMenu().get(0), 2);
        cart.addItem(service.getAvailableMenu().get(2), 1);

        System.out.println("\n=== RUNTIME POLYMORPHISM DEMO ===");
        for (FoodItem item : service.getAvailableMenu()) {
            double totalForTwo = item.calculatePrice(2);
            System.out.println(item.getName() + " -> Qty 2 total: ₹" + totalForTwo
                    + " | Suitability: " + item.isSuitableFor(DietaryType.VEGETARIAN));
        }

        System.out.println("\nFinal cart total: ₹" + service.calculateCartTotal(cart));
    }
}
