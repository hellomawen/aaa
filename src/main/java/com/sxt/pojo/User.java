package com.sxt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private String name;
    private String password;
    private String salt;
    private List<String> permissions;

}
