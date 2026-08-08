package com.example.shopapi.product.repositories;

import com.example.shopapi.product.dto.TrendingScore;
import com.example.shopapi.product.entities.ProductEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductEventRepository
        extends JpaRepository<ProductEvent, Long> {

    @Query("""
select new com.example.shopapi.product.dto.TrendingScore(

    e.product.id,

    sum(

        case

            when e.type = com.example.shopapi.product.enums.ProductEventType.PURCHASE
                then e.quantity * :purchaseWeight

            when e.type = com.example.shopapi.product.enums.ProductEventType.WISHLIST
                then :wishlistWeight

            when e.type = com.example.shopapi.product.enums.ProductEventType.VIEW
                then :viewWeight

            else 0

        end

    )

)

from ProductEvent e

where e.createdAt >= :from

group by e.product.id

order by sum(

    case

        when e.type = com.example.shopapi.product.enums.ProductEventType.PURCHASE
            then e.quantity * :purchaseWeight

        when e.type = com.example.shopapi.product.enums.ProductEventType.WISHLIST
            then :wishlistWeight

        when e.type = com.example.shopapi.product.enums.ProductEventType.VIEW
            then :viewWeight

        else 0

    end

) desc

""")
    List<TrendingScore> calculateTrending(

            @Param("from")
            LocalDateTime from,

            @Param("purchaseWeight")
            int purchaseWeight,

            @Param("wishlistWeight")
            int wishlistWeight,

            @Param("viewWeight")
            int viewWeight

    );
}