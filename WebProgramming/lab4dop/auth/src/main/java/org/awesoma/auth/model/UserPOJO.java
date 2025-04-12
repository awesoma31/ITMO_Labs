package org.awesoma.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserPOJO {
    private Long id;
    private String username;


    public static UserPOJO fromUser(User user) {
        return new UserPOJO(user.getId(), user.getUsername());
    }

}
