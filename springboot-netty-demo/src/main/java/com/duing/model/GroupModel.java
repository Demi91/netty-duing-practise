package com.duing.model;

import lombok.Data;

import java.util.List;

@Data
public class GroupModel {

    private String id;
    private String name;
    private List<UserModel> users;
}
