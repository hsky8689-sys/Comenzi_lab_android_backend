package facultate.android_backend.Config.Jwt;

import facultate.android_backend.Domain.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoDetails implements UserDetails {
    private String username,password;
    private List<GrantedAuthority> authorities;
    public UserInfoDetails(User userInfo){
        try {
            this.username = userInfo.getUsername();
            this.password = userInfo.getPassword();
            this.authorities = List.of(userInfo.getRoles().split(","))
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            this.username="";
            this.password="";
            this.authorities=new ArrayList<>();
        }
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
