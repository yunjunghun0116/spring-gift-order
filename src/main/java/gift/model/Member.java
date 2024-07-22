package gift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
<<<<<<< HEAD
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "member")
@SQLDelete(sql = "update member set deleted = true where id = ?")
@SQLRestriction("deleted is false")
=======

@Entity
@Table(name = "member")
>>>>>>> f2878d9 (setup: 베이스코드 세팅)
public class Member extends BaseEntity {
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(name = "email", unique = true)
    private String email;
    @NotNull
    @Column(name = "password")
    private String password;
    @NotNull
    @Enumerated
    @Column(name = "role")
    private MemberRole role;
<<<<<<< HEAD
    @NotNull
    @Column(name = "deleted")
    private Boolean deleted = Boolean.FALSE;
=======
>>>>>>> f2878d9 (setup: 베이스코드 세팅)

    protected Member() {
    }

    public Member(String name, String email, String password, MemberRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public MemberRole getRole() {
        return role;
    }
}
