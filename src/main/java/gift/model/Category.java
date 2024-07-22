package gift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
<<<<<<< HEAD
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "category")
@SQLDelete(sql = "update category set deleted = true where id = ?")
@SQLRestriction("deleted is false")
=======

@Entity
@Table(name = "category")
>>>>>>> f2878d9 (setup: 베이스코드 세팅)
public class Category extends BaseEntity {
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(name = "description")
    private String description;
    @NotNull
    @Column(name = "color")
    private String color;
    @NotNull
    @Column(name = "image_url")
    private String imageUrl;
<<<<<<< HEAD
    @NotNull
    @Column(name = "deleted")
    private Boolean deleted = Boolean.FALSE;
=======
>>>>>>> f2878d9 (setup: 베이스코드 세팅)

    protected Category() {
    }

    public Category(String name, String description, String color, String imageUrl) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void updateCategory(String name, String description, String color, String imageUrl) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.imageUrl = imageUrl;
    }
}
