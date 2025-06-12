package dev.gagnon.Repository;

//public class ProductSpecifications {
//}


import dev.gagnon.DTO.ProductSearchRequest;
import dev.gagnon.Model.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
public class ProductSpecifications {

    public static Specification<Product> matchesSearchRequest(ProductSearchRequest request) {
        return Specification
                .where(hasCategoryName(request.getCategoryName()))
                .and(hasShopName(request.getShopName()))
                .and(hasMarketName(request.getMarketName()));
    }

    public static Specification<Product> hasCategoryName(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (categoryName == null || categoryName.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filter
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("category").get("name")),
                    "%" + categoryName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasShopName(String shopName) {
        return (root, query, criteriaBuilder) -> {
            if (shopName == null || shopName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("shop").get("name")),
                    "%" + shopName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasMarketName(String marketName) {
        return (root, query, criteriaBuilder) -> {
            if (marketName == null || marketName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> shopJoin = root.join("shop", JoinType.LEFT);
            Join<Object, Object> marketSectionJoin = shopJoin.join("marketSection", JoinType.LEFT);
            Join<Object, Object> marketJoin = marketSectionJoin.join("market", JoinType.LEFT);

            return criteriaBuilder.like(
                    criteriaBuilder.lower(marketJoin.get("name")),
                    "%" + marketName.toLowerCase() + "%"
            );
        };
    }
}
