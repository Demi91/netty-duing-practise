package com.duing.version1.protobuf;

public class ProtobufTest {

    public static void main(String[] args) throws Exception{

        // 建造者模式  套用内部类
        PersonModel.Person.Builder builder = PersonModel.Person.newBuilder();
        builder.setId(3);
//        builder.setName("\uD83C\uDF35 HiSimon");
        builder.setName("\uD83C\uDF3B  \uD83D\uDC18 \uD83E\uDD81  \uD83D\uDC05");

        PersonModel.Person person = builder.build();
        System.out.println("person : " + person);

        System.out.println("== person bytes start ==");
        for (byte b : person.toByteArray()){
            System.out.print(b);
        }
        System.out.println();
        System.out.println("== person bytes end ==");
        System.out.println();
        System.out.println("===================================");
        byte[] byteArray = person.toByteArray();
        PersonModel.Person person2 = PersonModel.Person.parseFrom(byteArray);
        System.out.println("person2 id: " + person2.getId());
        System.out.println("person2 name: " + person2.getName());

    }
}
