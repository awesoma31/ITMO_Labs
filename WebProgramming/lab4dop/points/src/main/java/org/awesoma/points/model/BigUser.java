package org.awesoma.points.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.awesoma.points.util.JwtUser;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class BigUser implements Serializable {
    private User user;
    private JwtUser jwtUser;
}
