package com.maya.keyboard;
import android.content.Context;
import android.security.keystore.*;
import android.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.KeyStore;
final class Vault {
    private static javax.crypto.SecretKey secret() throws Exception {
        KeyStore s=KeyStore.getInstance("AndroidKeyStore"); s.load(null);
        if(!s.containsAlias("maya-key")) {
            KeyGenerator g=KeyGenerator.getInstance("AES","AndroidKeyStore");
            g.init(new KeyGenParameterSpec.Builder("maya-key",KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build());
            g.generateKey();
        }
        return (javax.crypto.SecretKey)s.getKey("maya-key",null);
    }
    static void save(Context c,String key) throws Exception {
        Cipher x=Cipher.getInstance("AES/GCM/NoPadding"); x.init(Cipher.ENCRYPT_MODE,secret());
        c.getSharedPreferences("secret",0).edit().putString("iv",Base64.encodeToString(x.getIV(),Base64.NO_WRAP)).putString("key",Base64.encodeToString(x.doFinal(key.getBytes(java.nio.charset.StandardCharsets.UTF_8)),Base64.NO_WRAP)).apply();
    }
    static String read(Context c) throws Exception {
        android.content.SharedPreferences p=c.getSharedPreferences("secret",0);
        if(!p.contains("key")) return "";
        Cipher x=Cipher.getInstance("AES/GCM/NoPadding");
        x.init(Cipher.DECRYPT_MODE,secret(),new GCMParameterSpec(128,Base64.decode(p.getString("iv",""),Base64.NO_WRAP)));
        return new String(x.doFinal(Base64.decode(p.getString("key",""),Base64.NO_WRAP)),java.nio.charset.StandardCharsets.UTF_8);
    }
    static void remove(Context c) { c.getSharedPreferences("secret",0).edit().clear().apply(); }
}
