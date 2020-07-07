package com.duing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class GroupModel {

    private String id;
    private String name;
    private List<UserModel> users;
}
