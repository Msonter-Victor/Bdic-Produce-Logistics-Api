package dev.gagnon.Repository;

import dev.gagnon.Model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    @Query("select s from SubCategory s where s.category.name=:categoryName")
    List<SubCategory> findAllByCategoryName(String categoryName);
}
