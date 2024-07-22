package gift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
<<<<<<< HEAD
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "product")
@SQLDelete(sql = "update product set deleted = true where id = ?")
@SQLRestriction("deleted is false")
=======

@Entity
@Table(name = "product")
>>>>>>> f2878d9 (setup: 베이스코드 세팅)
public class Product extends BaseEntity {
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(name = "price")
    private Integer price;
    @NotNull
    @Column(name = "image_url")
    private String imageUrl;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
<<<<<<< HEAD
    @NotNull
    @Column(name = "deleted")
    private Boolean deleted = Boolean.FALSE;
=======
>>>>>>> f2878d9 (setup: 베이스코드 세팅)

    protected Product() {
    }

    public Product(String name, Integer price, String imageUrl, Category category) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void updateProductInfo(String name, Integer price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}
