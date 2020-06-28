package com.duing.version1.rpc.publicinterface;

//这个是接口，是服务提供方和 服务消费方都需要
//  进程B是服务提供方
//  进程A是服务消费方
public interface HelloService {

    String hello(String mes);
}
