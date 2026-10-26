package com.maya.keyboard;
import android.inputmethodservice.InputMethodService;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.content.*;
import android.text.InputType;
import android.graphics.Color;
import java.util.*;
import java.io.*;

public class MayaIME extends InputMethodService {
    private LinearLayout root;
    private final String[] modes={"English","বাংলা","বাংলা phonetic","Tagalog","Indonesia"};
    private int mode=0, banglaPage=0, emojiGroup=0, emojiPage=0;
    private boolean shift=false, symbols=false, emoji=false, privateField=false;
    private String composing="";
    private final LinkedHashMap<String,List<String>> emojis=new LinkedHashMap<>();
    private final ArrayList<String> recent=new ArrayList<>();
    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private InputConnection ic(){return getCurrentInputConnection();}
    @Override public void onCreate() {
        super.onCreate();
        mode=getSharedPreferences("settings",0).getInt("mode",0);
        try(BufferedReader r=new BufferedReader(new InputStreamReader(getAssets().open("emoji.tsv"),"UTF-8"))) {
            String s; while((s=r.readLine())!=null) {
                String[] p=s.split("\t",3); if(p.length==3) emojis.computeIfAbsent(p[0],k->new ArrayList<>()).add(p[1]);
            }
        } catch(Exception ignored) {}
    }
    @Override public View onCreateInputView() {
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(3),dp(6),dp(3),dp(6));
        root.setBackgroundColor(Color.rgb(12,15,27));
        root.setOnApplyWindowInsetsListener((v,insets)-> {
            int bottom=insets.getSystemWindowInsetBottom();
            v.setPadding(dp(3),dp(6),dp(3),Math.max(dp(6),bottom));
            return insets;
        });
        render(); return root;
    }
    @Override public boolean onEvaluateFullscreenMode(){return false;}
    @Override public void onStartInput(EditorInfo info,boolean restarting) {
        super.onStartInput(info,restarting);
        composing=""; shift=false; emoji=false; symbols=false;
        int cls=info.inputType & InputType.TYPE_MASK_CLASS, v=info.inputType & InputType.TYPE_MASK_VARIATION;
        privateField=(cls==InputType.TYPE_CLASS_TEXT&&(v==InputType.TYPE_TEXT_VARIATION_PASSWORD||v==InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD||v==InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD))
            ||(cls==InputType.TYPE_CLASS_NUMBER&&v==InputType.TYPE_NUMBER_VARIATION_PASSWORD)
            ||(info.imeOptions & EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING)!=0;
        if(cls==InputType.TYPE_CLASS_NUMBER||cls==InputType.TYPE_CLASS_PHONE||cls==InputType.TYPE_CLASS_DATETIME) symbols=true;
        render();
    }
    @Override public void onFinishInput(){finishWord(); super.onFinishInput(); composing=""; emoji=false;}
    @Override public void onUpdateSelection(int a,int b,int c,int d,int e,int f) {
        super.onUpdateSelection(a,b,c,d,e,f);
        if(!composing.isEmpty()&&(c!=f||d!=f)) {composing=""; if(ic()!=null) ic().finishComposingText(); render();}
    }
    private void finishWord(){ if(ic()!=null) ic().finishComposingText(); composing=""; }
    private LinearLayout row(){LinearLayout r=new LinearLayout(this); root.addView(r,new LinearLayout.LayoutParams(-1,dp(47))); return r;}
    private void button(LinearLayout row,String title,Runnable action,float weight) {
        Button b=new Button(this); b.setText(title); b.setTextSize(16); b.setAllCaps(false); b.setTextColor(Color.WHITE);
        b.setPadding(0,0,0,0); b.setMinWidth(0); b.setMinimumWidth(0);
        android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();
        bg.setColor(Color.rgb(35,41,61)); bg.setCornerRadius(dp(8)); b.setBackground(bg);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,weight); p.setMargins(dp(2),dp(3),dp(2),dp(3)); row.addView(b,p);
        b.setOnClickListener(v->action.run()); b.setContentDescription(title);
        if(title.equals("🌐")) b.setOnLongClickListener(v->{((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker(); return true;});
    }
    private void insert(String s) { if(ic()!=null) ic().commitText(s,1); }
    private void type(String s) {
        if(ic()==null)return;
        if(mode==2&&!symbols&&!privateField&&s.matches("[a-zA-Z]")) {
            composing+=s; ic().setComposingText(Phonetic.convert(composing),1); render();
        } else { finishWord(); insert(s); }
    }
    private void delete(){
        if(ic()==null)return;
        if(!composing.isEmpty()) { composing=composing.substring(0,composing.length()-1); ic().setComposingText(Phonetic.convert(composing),1); render(); return; }
        CharSequence selected=ic().getSelectedText(0);
        if(selected!=null&&selected.length()>0){insert("");return;}
        CharSequence before=ic().getTextBeforeCursor(128,0);
        if(before==null||before.length()==0)return;
        android.icu.text.BreakIterator it=android.icu.text.BreakIterator.getCharacterInstance();
        String s=before.toString(); it.setText(s); it.last(); int start=it.previous();
        ic().deleteSurroundingText(s.length()-Math.max(0,start),0);
    }
    private void ai() {
        if(privateField)return;
        finishWord();
        CharSequence s=ic()==null?null:ic().getSelectedText(0);
        Intent i=new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("draft",s==null?"":s.toString()); startActivity(i);
    }
    private void paste(){
        if(privateField)return;
        finishWord(); ClipboardManager c=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        if(c.hasPrimaryClip()&&c.getPrimaryClip()!=null) {
            CharSequence t=c.getPrimaryClip().getItemAt(0).coerceToText(this); if(t!=null)insert(t.toString());
        }
    }
    private void render() {
        if(root==null)return; root.removeAllViews();
        LinearLayout top=row();
        button(top,privateField?"Private":"AI ✦",()->{if(!privateField)ai();},1);
        button(top,"😀",()->{finishWord();emoji=!emoji;emojiPage=0;render();},1);
        button(top,"Paste",this::paste,1);
        button(top,"⚙",()->startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)),1);
        if(emoji&&!privateField){renderEmoji();return;}
        if(!composing.isEmpty()) {
            LinearLayout preview=row();
            button(preview,Phonetic.convert(composing),()->{finishWord();render();},2);
            button(preview,composing,()->{if(ic()!=null)ic().commitText(composing,1);composing="";render();},1);
        }
        String[][] layout;
        if(symbols) layout=new String[][]{{"1","2","3","4","5","6","7","8","9","0"},{"@","#","$","%","&","*","(",")","-","+"},{"!","?","/","=",":",";","'","\"","_","।"}};
        else if(mode==1&&!privateField) {
            String[][][] pages={
                {{"ক","খ","গ","ঘ","ঙ","চ","ছ","জ","ঝ","ঞ"},{"ট","ঠ","ড","ঢ","ণ","ত","থ","দ","ধ","ন"},{"প","ফ","ব","ভ","ম","য","র","ল","শ","ষ"},{"স","হ","ড়","ঢ়","য়","ৎ","ং","ঃ","ঁ","্"}},
                {{"অ","আ","ই","ঈ","উ","ঊ","ঋ","এ","ঐ","ও","ঔ"},{"া","ি","ী","ু","ূ","ৃ","ে","ৈ","ো","ৌ"},{"০","১","২","৩","৪","৫","৬","৭","৮","৯"}}
            }; layout=pages[banglaPage];
        } else {
            layout=new String[][]{{"q","w","e","r","t","y","u","i","o","p"},{"a","s","d","f","g","h","j","k","l"},{"z","x","c","v","b","n","m",",","."}};
        }
        for(String[] keys:layout){LinearLayout r=row(); for(String key:keys){String k=shift?key.toUpperCase(Locale.ROOT):key;button(r,k,()->type(k),1);}}
        if(!symbols && mode==3 && !privateField) {LinearLayout accents=row();for(String a:new String[]{"ñ","á","é","í","ó","ú"}) {String value=shift?a.toUpperCase(Locale.ROOT):a;button(accents,value,()->type(value),1);}}
        LinearLayout edit=row();
        if(mode==1&&!privateField)button(edit,banglaPage==0?"অ আ":"ক খ",()->{banglaPage=1-banglaPage;render();},1);
        else button(edit,shift?"⇧ ON":"⇧",()->{shift=!shift;render();},1);
        button(edit,symbols?"ABC":"123",()->{finishWord();symbols=!symbols;render();},1);
        button(edit,"⌫",this::delete,1);
        button(edit,"↵",()->{finishWord(); if(ic()!=null){EditorInfo info=getCurrentInputEditorInfo();int action=info.imeOptions & EditorInfo.IME_MASK_ACTION;if((info.imeOptions&EditorInfo.IME_FLAG_NO_ENTER_ACTION)==0&&action!=EditorInfo.IME_ACTION_NONE&&action!=EditorInfo.IME_ACTION_UNSPECIFIED)ic().performEditorAction(action);else insert("\n");}render();},1);
        LinearLayout bottom=row();
        button(bottom,"🌐",()->{finishWord();mode=(mode+1)%modes.length;getSharedPreferences("settings",0).edit().putInt("mode",mode).apply();render();},1);
        button(bottom,privateField?"Space":modes[mode],()->{finishWord();insert(" ");render();},4);
        button(bottom,"←",()->{finishWord();if(ic()!=null)ic().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));},1);
        button(bottom,"→",()->{finishWord();if(ic()!=null)ic().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));},1);
    }
    private void renderEmoji(){
        ArrayList<String> groups=new ArrayList<>();groups.add("Recent");groups.addAll(emojis.keySet());
        if(emojiGroup>=groups.size())emojiGroup=0;
        LinearLayout nav=row();
        button(nav,"‹",()->{emojiGroup=(emojiGroup+groups.size()-1)%groups.size();emojiPage=0;render();},1);
        button(nav,groups.get(emojiGroup),()->{emojiGroup=(emojiGroup+1)%groups.size();emojiPage=0;render();},5);
        button(nav,"›",()->{emojiGroup=(emojiGroup+1)%groups.size();emojiPage=0;render();},1);
        List<String> list=emojiGroup==0?recent:emojis.get(groups.get(emojiGroup));
        int pages=Math.max(1,(list.size()+39)/40); emojiPage=Math.min(emojiPage,pages-1);
        for(int r=0;r<5;r++){LinearLayout line=row();for(int col=0;col<8;col++){int index=emojiPage*40+r*8+col;if(index<list.size()){String e=list.get(index);button(line,e,()->{insert(e);recent.remove(e);recent.add(0,e);if(recent.size()>40)recent.remove(40);},1);}else button(line,"",()->{},1);}}
        LinearLayout foot=row();
        button(foot,"ABC",()->{emoji=false;render();},1);
        button(foot,"‹",()->{emojiPage=(emojiPage+pages-1)%pages;render();},1);
        button(foot,(emojiPage+1)+"/"+pages,()->{},1);
        button(foot,"›",()->{emojiPage=(emojiPage+1)%pages;render();},1);
        button(foot,"⌫",this::delete,1);
    }
}
