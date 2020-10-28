package com.duing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class GroupModel {

    private String name;
    private String id;
    private List<UserModel> users;
}
