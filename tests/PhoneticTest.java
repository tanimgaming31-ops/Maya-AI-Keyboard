package com.maya.keyboard;
public class PhoneticTest {
    public static void main(String[] args) {
        String[][] cases={{"ami","আমি"},{"tumi","তুমি"},{"bangla","বাংলা"},{"bhalo","ভালো"},{"kh","খ"},{"ka","কা"},{"ki","কি"},{"ku","কু"},{"k","ক"},{"a","আ"},{"kto","ক্ত"},{"123","123"},{"",""},{"T","ট"},{"th","থ"},{"oi","ঐ"}};
        for(String[] c:cases) if(!Phonetic.convert(c[0]).equals(c[1])) throw new AssertionError(c[0]+" => "+Phonetic.convert(c[0]));
        System.out.println(cases.length+" phonetic cases passed");
    }
}
