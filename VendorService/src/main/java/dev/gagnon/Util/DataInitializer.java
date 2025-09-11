package dev.gagnon.Util;

import dev.gagnon.Model.Category;
import dev.gagnon.Model.Shop;
import dev.gagnon.Model.ShopTier;
import dev.gagnon.Model.SubCategory;
import dev.gagnon.Repository.CategoryRepository;
import dev.gagnon.Repository.ShopRepository;
import dev.gagnon.Repository.SubCategoryRepository;
import dev.gagnon.Repository.TierRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final TierRepository tierRepository;
    private final ShopRepository shopRepository;

    public DataInitializer(CategoryRepository categoryRepository,
                           SubCategoryRepository subCategoryRepository, TierRepository tierRepository, ShopRepository shopRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.tierRepository = tierRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            initializeCategories();
        }
        if (tierRepository.count() == 0) {
            initializeTiers();
        }
    }

    private void initializeTiers() {
        ShopTier tier = new ShopTier();
        tier.setTierName("BASIC");
        tier.setPrice(5000);
        tier.setFeaturedNumber(3L);
        tier.setPromotedNumber(1L);
        tierRepository.save(tier);

        ShopTier tier2 = new ShopTier();
        tier2.setTierName("PREMIUM");
        tier2.setPrice(7000);
        tier2.setFeaturedNumber(5L);
        tier2.setPromotedNumber(3L);
        tier2.setFloatedNumber(1L);
        tierRepository.save(tier2);

        ShopTier tier3 = new ShopTier();
        tier3.setTierName("PLATINUM");
        tier3.setPrice(10000);
        tier3.setFeaturedNumber(7L);
        tier3.setPromotedNumber(5L);
        tier3.setFloatedNumber(2L);
        tierRepository.save(tier3);
    }

    private void initializeCategories() {
        // 1. Electronics
        Category electronics = saveCategory("Electronics");
        saveSubCategory("Smartphones", electronics);
        saveSubCategory("Laptops", electronics);
        saveSubCategory("Headphones", electronics);

        // 2. Clothing
        Category clothing = saveCategory("Clothing");
        saveSubCategory("Men's Clothing", clothing);
        saveSubCategory("Women's Clothing", clothing);
        saveSubCategory("Kids' Clothing", clothing);

        // 3. Home & Kitchen
        Category homeKitchen = saveCategory("Home & Kitchen");
        saveSubCategory("Furniture", homeKitchen);
        saveSubCategory("Cookware", homeKitchen);
        saveSubCategory("Home Decor", homeKitchen);

        // 4. Beauty & Personal Care
        Category beauty = saveCategory("Beauty & Personal Care");
        saveSubCategory("Skincare", beauty);
        saveSubCategory("Makeup", beauty);
        saveSubCategory("Hair Care", beauty);

        // 5. Sports & Outdoors
        Category sports = saveCategory("Sports & Outdoors");
        saveSubCategory("Fitness Equipment", sports);
        saveSubCategory("Outdoor Recreation", sports);

        // 6. Books
        Category books = saveCategory("Books");
        saveSubCategory("Fiction", books);
        saveSubCategory("Non-Fiction", books);
        saveSubCategory("Children's Books", books);

        // 7. Toys & Games
        Category toys = saveCategory("Toys & Games");
        saveSubCategory("Board Games", toys);
        saveSubCategory("Educational Toys", toys);

        // 8. Groceries
        Category groceries = saveCategory("Groceries");
        saveSubCategory("Pantry Staples", groceries);
        saveSubCategory("Snacks", groceries);
        saveSubCategory("Beverages", groceries);

        // 9. Automotive
        Category automotive = saveCategory("Automotive");
        saveSubCategory("Car Accessories", automotive);
        saveSubCategory("Car Care", automotive);

        // 10. Health & Wellness
        Category health = saveCategory("Health & Wellness");
        saveSubCategory("Vitamins & Supplements", health);
        saveSubCategory("Medical Supplies", health);
        saveSubCategory("Fitness Trackers", health);
    }

    private Category saveCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    private void saveSubCategory(String name, Category category) {
        SubCategory subCategory = new SubCategory();
        subCategory.setName(name);
        subCategory.setCategory(category);
        subCategoryRepository.save(subCategory);
    }

}