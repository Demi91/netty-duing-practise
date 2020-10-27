package com.duing.model;

import lombok.Data;

import java.util.List;

@Data
public class GroupModel {

    private String name;
    private String id;
    private List<UserModel> users;
}
