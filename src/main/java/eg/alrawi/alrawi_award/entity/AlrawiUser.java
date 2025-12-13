package eg.alrawi.alrawi_award.entity;

import eg.alrawi.alrawi_award.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;


@Table(name = "alrawi_user")
@Entity
@Setter
@Getter
public class AlrawiUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String username;

    @Column(unique = true)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String fullName;
    private String mobileNumber;
    private String dateOfBirth;
    private String city;
    private String government;

    @Column(unique = true)
    private String nationalId;

    @Column(unique = true)
    private String passportNumber;

    private String gender;

    @CreationTimestamp
    @Column(updatable = false)
    private Date dateCreated;

    @OneToMany(mappedBy = "alrawiUser",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserImage> alrawiUserImages;

    @OneToMany(mappedBy = "alrawiUser",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AlrawiProject> projects;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



}
