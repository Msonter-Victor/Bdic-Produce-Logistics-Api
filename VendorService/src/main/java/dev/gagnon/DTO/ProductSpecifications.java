package dev.gagnon.DTO;

import dev.gagnon.Model.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {
    public static Specification<Product> matchesSearchRequest(ProductSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Category> categoryJoin = root.join("category", JoinType.LEFT);
            Join<Product, Shop> shopJoin = root.join("shop", JoinType.LEFT);
            Join<Shop, MarketSection> marketSectionJoin = shopJoin.join("marketSection", JoinType.LEFT);
            Join<MarketSection, Market> marketJoin = marketSectionJoin.join("market", JoinType.LEFT);
            Predicate predicate = criteriaBuilder.conjunction();
            if (request.getProductName() != null && !request.getProductName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getProductName().toLowerCase() + "%"));
            }
            if (request.getCategoryName() != null && !request.getCategoryName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("name")), "%" + request.getCategoryName().toLowerCase() + "%"));
            }
            if (request.getShopName() != null && !request.getShopName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(shopJoin.get("name")), "%" + request.getShopName().toLowerCase() + "%"));
            }
            if (request.getMarketName() != null && !request.getMarketName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(marketJoin.get("name")), "%" + request.getMarketName().toLowerCase() + "%"));
            }
            return predicate;
        };
    }
}
