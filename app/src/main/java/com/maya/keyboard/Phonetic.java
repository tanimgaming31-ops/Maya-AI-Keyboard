package com.maya.keyboard;
import java.util.*;
/** Small original phonetic engine. Not Avro-compatible; preview spelling before committing. */
public final class Phonetic {
    private static final Map<String,String> C = new LinkedHashMap<>();
    private static final Map<String,String> WORD = new HashMap<>();
    static {
        String[] keys={"ksh","chh","kh","gh","ch","jh","Th","Dh","th","dh","ph","bh","sh","Sh","ng","Ng","rr","Rh","R","T","D","N","k","g","c","j","t","d","n","p","f","b","v","m","r","l","s","h","y","z"};
        String[] vals={"ক্ষ","ছ","খ","ঘ","চ","ঝ","ঠ","ঢ","থ","ধ","ফ","ভ","শ","ষ","ং","ঙ","ড়","ঢ়","ড়","ট","ড","ণ","ক","গ","চ","জ","ত","দ","ন","প","ফ","ব","ভ","ম","র","ল","স","হ","য়","য"};
        for(int i=0;i<keys.length;i++) C.put(keys[i],vals[i]);
        String[][] words={{"ami","আমি"},{"tumi","তুমি"},{"apni","আপনি"},{"bangla","বাংলা"},{"bhalo","ভালো"},{"bhalobashi","ভালোবাসি"},{"amar","আমার"},{"tomar","তোমার"},{"kemon","কেমন"},{"acho","আছো"},{"achi","আছি"},{"dhonnobad","ধন্যবাদ"},{"ki","কি"},{"na","না"},{"ha","হ্যাঁ"},{"maya","মায়া"}};
        for(String[] w:words) WORD.put(w[0],w[1]);
    }
    public static String convert(String input) {
        if(WORD.containsKey(input)) return WORD.get(input);
        StringBuilder out=new StringBuilder(); boolean consonant=false;
        String[] vowels={"ou","oi","aa","ee","oo","a","i","u","e","o"};
        String[] independent={"ঔ","ঐ","আ","ঈ","ঊ","আ","ই","উ","এ","অ"};
        String[] dependent={"ৌ","ৈ","া","ী","ূ","া","ি","ু","ে",""};
        for(int i=0;i<input.length();) {
            boolean found=false;
            for(int v=0;v<vowels.length;v++) if(input.startsWith(vowels[v],i)) {
                out.append(consonant?dependent[v]:independent[v]);
                consonant=false; i+=vowels[v].length(); found=true; break;
            }
            if(found) continue;
            for(Map.Entry<String,String> c:C.entrySet()) if(input.startsWith(c.getKey(),i)) {
                if(consonant && !c.getValue().equals("ং")) out.append("্");
                out.append(c.getValue()); consonant=!c.getValue().equals("ং"); i+=c.getKey().length(); found=true; break;
            }
            if(!found) { out.append(input.charAt(i++)); consonant=false; }
        }
        return out.toString();
    }
}
