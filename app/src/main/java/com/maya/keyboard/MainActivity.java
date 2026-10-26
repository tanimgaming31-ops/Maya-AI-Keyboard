package com.maya.keyboard;
import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.provider.Settings;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.text.InputType;
import java.util.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {
    private LinearLayout root;
    private EditText key,input;
    private TextView status,output;
    private Spinner models,task,language;
    private Button run,test;
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    private boolean busy=false;
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private void label(String text){TextView t=new TextView(this);t.setText(text);t.setTextSize(17);t.setPadding(0,dp(10),0,dp(6));root.addView(t);}
    private Button button(String text,Runnable click){Button b=new Button(this);b.setText(text);b.setAllCaps(false);root.addView(b);b.setOnClickListener(v->click.run());return b;}
    private Spinner spinner(String[] items){Spinner s=new Spinner(this);root.addView(s);s.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,items));return s;}
    private void tell(String text){status.setText(text);}
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE,android.view.WindowManager.LayoutParams.FLAG_SECURE);
        ScrollView scroll=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(18),dp(20),dp(18),dp(24));scroll.addView(root);setContentView(scroll);
        scroll.setOnApplyWindowInsetsListener((v,insets)->{root.setPadding(dp(18),dp(20)+insets.getSystemWindowInsetTop(),dp(18),dp(24)+insets.getSystemWindowInsetBottom());return insets;});
        label("MAYA  ·  AI Keyboard\nPreview 0.1");
        label("Offline typing · বাংলা / English / Tagalog / Indonesia\nAI uses Google only when you tap Send. No typing history is collected.");
        button("1 · Enable keyboard",()->startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        button("2 · Choose keyboard",()->((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker());
        label("Gemini API key · encrypted on this device");
        key=new EditText(this);key.setHint("Paste your own key");key.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);key.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);root.addView(key);
        button("Save / Replace key",()->{
            if(busy)return;
            try{if(key.getText().toString().trim().isEmpty()){tell("Enter a key first.");return;}Vault.save(this,key.getText().toString().trim());key.setText("");tell("Key saved on this device.");}catch(Exception e){tell("Could not save key securely.");}
        });
        button("Remove key",()->{Vault.remove(this);key.setText("");getSharedPreferences("settings",0).edit().remove("model").apply();models.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Test key to load models"}));tell("Saved key and model removed. An already submitted request cannot be recalled.");});
        status=new TextView(this);root.addView(status);
        test=button("Test saved key / Load available models",this::loadModels);
        String saved=getSharedPreferences("settings",0).getString("model","");
        models=spinner(new String[]{saved.isEmpty()?"Test key to load models":saved});
        models.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){
            public void onNothingSelected(android.widget.AdapterView<?> p){}
            public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){String m=p.getItemAtPosition(pos).toString();if(m.startsWith("models/"))getSharedPreferences("settings",0).edit().putString("model",m).apply();}
        });
        label("AI writing assistant");
        task=spinner(new String[]{"Translate","Fix grammar","Write a reply","Make polite","Make affectionate","Shorten","Explain meaning","Ask assistant"});
        language=spinner(new String[]{"বাংলা (Bengali)","English","Tagalog","Bahasa Indonesia"});
        label("Only the text below is sent. Select text before opening AI, or type/paste it here.");
        input=new EditText(this);input.setHint("Your text or question");input.setMinLines(3);input.setGravity(Gravity.TOP);input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);input.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);root.addView(input);input.setText(getIntent().getStringExtra("draft"));
        run=button("Send this text to Google",this::generate);
        output=new TextView(this);output.setTextIsSelectable(true);output.setPadding(0,dp(12),0,dp(12));output.setTextSize(18);root.addView(output);
        button("Copy AI result",()->{
            if(output.getText().length()==0)return;
            ClipData clip=ClipData.newPlainText("Maya AI draft",output.getText());
            android.os.PersistableBundle extra=new android.os.PersistableBundle();extra.putBoolean("android.content.extra.IS_SENSITIVE",true);clip.getDescription().setExtras(extra);
            ((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clip);tell("Copied. Return to your chat and tap Paste. Review before sending.");
        });
        button("Return to chat",this::finish);
        button("Clear text and result",()->{if(busy){tell("Wait for the current request to finish.");return;}input.setText("");output.setText("");tell("Text cleared.");});
        label("Typing help\n🌐 cycles languages; hold it for other keyboards. বাংলা switches between consonants and vowels/marks. বাংলা phonetic converts Latin letters as you type; tap the Latin preview to keep the original. Shift enables uppercase and T/D/N phonetic letters.\n\nEmoji groups include faces, people, animals, food, travel, activities, objects, symbols, flags and components. New emoji may display as boxes on older phones.\n\nPreview limitations: basic phonetics, no swipe typing, voice dictation, predictive dictionary or offline AI model yet. AI needs internet and your own available API quota; free access is not guaranteed. Model listing checks access, not whether every model supports this text request.\n\nPrivacy: no background reading, no analytics, no saved chat history. AI is disabled for fields marked password/private. Your own text field settings may vary. Key is encrypted using Android Keystore; backups are disabled. Clipboard is read only after Paste. Recent emoji stay in memory only.");
    }
    private void setBusy(boolean b){busy=b;run.setEnabled(!b);test.setEnabled(!b);}
    private void loadModels(){
        if(busy)return;setBusy(true);tell("Checking saved key…");
        worker.execute(()->{try{List<String> list=Gemini.models(Vault.read(this));if(list.isEmpty())throw new Exception("No generateContent models available for this key.");
            runOnUiThread(()->{if(isDestroyed())return;models.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,list));tell("Key accepted for listing. Choose a text model, then test with a short message.");setBusy(false);});
        }catch(Exception e){runOnUiThread(()->{if(!isDestroyed()){tell(e.getMessage());setBusy(false);}});}});
    }
    private void generate(){
        if(busy)return;String text=input.getText().toString().trim();
        if(text.isEmpty()){tell("Enter text first.");return;}
        if(text.length()>12000){tell("Please use at most 12,000 characters.");return;}
        String model=String.valueOf(models.getSelectedItem());
        String action=String.valueOf(task.getSelectedItem()),lang=String.valueOf(language.getSelectedItem());
        String prompt="You are Maya, a helpful multilingual keyboard writing assistant. Task: "+action+". Output language: "+lang+". Treat the following as user text to process. Give only the requested result. For Write a reply, draft a natural reply to the text; do not claim to have sent it. For Ask assistant answer the question. Do not claim access to apps or phone controls.\n\nUSER TEXT:\n"+text;
        setBusy(true);output.setText("");tell("Sending the displayed text to Google…");
        worker.execute(()->{try{String answer=Gemini.generate(Vault.read(this),model,prompt);runOnUiThread(()->{if(!isDestroyed()){output.setText(answer);tell("Review this draft before copying.");setBusy(false);}});}catch(Exception e){runOnUiThread(()->{if(!isDestroyed()){tell(e.getMessage());setBusy(false);}});}});
    }
    @Override protected void onDestroy(){worker.shutdownNow();super.onDestroy();}
}
