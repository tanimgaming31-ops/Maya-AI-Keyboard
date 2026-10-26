package com.maya.keyboard;
import org.json.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
final class Gemini {
    private static JSONObject request(String key,String path,JSONObject body) throws Exception {
        if(key.trim().isEmpty()) throw new IOException("Add your Gemini API key first.");
        HttpURLConnection c=(HttpURLConnection)new URL("https://generativelanguage.googleapis.com/v1beta/"+path).openConnection();
        c.setInstanceFollowRedirects(false); c.setConnectTimeout(15000); c.setReadTimeout(45000);
        c.setRequestProperty("x-goog-api-key",key); c.setRequestProperty("Content-Type","application/json");
        try {
            if(body!=null) { c.setRequestMethod("POST"); c.setDoOutput(true); try(OutputStream o=c.getOutputStream()) { o.write(body.toString().getBytes(StandardCharsets.UTF_8)); } }
            int status=c.getResponseCode();
            if(status!=200) throw new IOException("Google API HTTP "+status+". Check key, model, quota or connection.");
            try(InputStream in=c.getInputStream(); ByteArrayOutputStream out=new ByteArrayOutputStream()) {
                byte[] buf=new byte[8192]; int n;
                while((n=in.read(buf))!=-1) { out.write(buf,0,n); if(out.size()>2000000) throw new IOException("Response too large"); }
                return new JSONObject(out.toString("UTF-8"));
            }
        } finally { c.disconnect(); }
    }
    static List<String> models(String key) throws Exception {
        List<String> result=new ArrayList<>(); String token="";
        do {
            JSONObject r=request(key,"models?pageSize=1000"+(token.isEmpty()?"":"&pageToken="+URLEncoder.encode(token,"UTF-8")),null);
            JSONArray list=r.optJSONArray("models");
            if(list!=null) for(int i=0;i<list.length();i++) {
                JSONObject m=list.getJSONObject(i); JSONArray methods=m.optJSONArray("supportedGenerationMethods");
                if(methods!=null) for(int j=0;j<methods.length();j++) if(methods.getString(j).equals("generateContent")) result.add(m.getString("name"));
            }
            token=r.optString("nextPageToken","");
        } while(!token.isEmpty());
        Collections.sort(result); return result;
    }
    static String generate(String key,String model,String prompt) throws Exception {
        if(!model.matches("models/[a-zA-Z0-9._-]+")) throw new IOException("Test key and choose an available model first.");
        JSONObject body=new JSONObject().put("contents",new JSONArray().put(new JSONObject().put("parts",new JSONArray().put(new JSONObject().put("text",prompt))))).put("generationConfig",new JSONObject().put("maxOutputTokens",2048));
        JSONObject r=request(key,model+":generateContent",body); JSONArray candidates=r.optJSONArray("candidates");
        if(candidates==null||candidates.length()==0) throw new IOException("No answer returned. The request may have been filtered.");
        JSONArray parts=candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts");
        StringBuilder text=new StringBuilder();
        for(int i=0;i<parts.length();i++) if(!parts.getJSONObject(i).optBoolean("thought",false)) text.append(parts.getJSONObject(i).optString("text",""));
        if(text.length()==0) throw new IOException("This model returned no text. Try another text model.");
        return text.toString();
    }
}
