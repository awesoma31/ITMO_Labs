package org.awesoma.auth.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static UserPOJO fromBigUser(BigUser bigUser) {
        return new UserPOJO(
                bigUser.getUser().getId(),
                bigUser.getJwtUser().getUsername()
        );
    }

    public static String jsonFromBigUser(BigUser bigUser) throws JsonProcessingException {
        var pojo = fromBigUser(bigUser);
        return new ObjectMapper().writeValueAsString(pojo);
    }
}
