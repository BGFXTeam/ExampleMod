package com.example.bgfx.mod;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

import com.executor.plugin.Plugin;
import com.executor.plugin.PluginContext;

import java.util.HashMap;
import java.util.Map;

public class ExampleMod extends Plugin {

    private Context ctx;
    private WindowManager wm;

    private View floatView;
    private View menuView;
    private WindowManager.LayoutParams floatParams;
    private WindowManager.LayoutParams menuParams;

    private LinearLayout listContainer;

    private Map<String, Boolean> toggleStates = new HashMap<>();
    private Map<String, Integer> seekValues    = new HashMap<>();
    
    private boolean initialized = false;

    private final int ACCENT = 0xFF9C6CFF;
    private final int BG = 0xEE101010;
    private final int CARD = 0xFF202020;
    private final int TEXT = Color.WHITE;

    @Override
    public void onLoad(PluginContext pluginContext) {
        ctx = pluginContext.getContext();
        LibraryLoader.load(
            ctx,
            "/storage/emulated/0/BGFX/plugins/libs/libMyMod.so"
        );
        initFloatIcon();
    }

    // ─── helpers ───

    private int dp(int v) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, v,
            ctx.getResources().getDisplayMetrics()
        );
    }

    private GradientDrawable bg(int color, float radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    // ─── floating icon ───

    private void initFloatIcon() {

        wm = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);


        floatParams = new WindowManager.LayoutParams(
            dp(42),
            dp(42),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );


        floatParams.gravity = Gravity.TOP|Gravity.LEFT;
        floatParams.x = 20;
        floatParams.y = 300;


        TextView icon = new TextView(ctx);

        icon.setText("EM");
        icon.setTextColor(Color.WHITE);
        icon.setTextSize(12);
        icon.setTypeface(Typeface.DEFAULT_BOLD);
        icon.setGravity(Gravity.CENTER);


        icon.setBackground(
            bg(ACCENT,dp(21))
        );


        floatView = icon;

        wm.addView(floatView,floatParams);



        icon.setOnTouchListener(new View.OnTouchListener(){

                float sx,sy;
                boolean moved;


                public boolean onTouch(View v,MotionEvent e){

                    switch(e.getAction()){

                        case MotionEvent.ACTION_DOWN:

                            sx=e.getRawX();
                            sy=e.getRawY();
                            moved=false;

                            break;


                        case MotionEvent.ACTION_MOVE:


                            float dx=e.getRawX()-sx;
                            float dy=e.getRawY()-sy;


                            if(Math.abs(dx)>5 || Math.abs(dy)>5)
                                moved=true;


                            floatParams.x += dx;
                            floatParams.y += dy;


                            sx=e.getRawX();
                            sy=e.getRawY();


                            wm.updateViewLayout(
                                floatView,
                                floatParams
                            );

                            break;



                        case MotionEvent.ACTION_UP:

                            if(!moved)
                                openMenu();

                            break;
                    }

                    return true;
                }
            });
    }

    // ─── menu ───

    private void openMenu(){

        if(initialized){

            floatView.setVisibility(View.GONE);
            menuView.setVisibility(View.VISIBLE);

            return;
        }


        initialized=true;

        floatView.setVisibility(View.GONE);



        LinearLayout root=new LinearLayout(ctx);

        root.setOrientation(LinearLayout.VERTICAL);

        root.setPadding(
            dp(7),
            dp(7),
            dp(7),
            dp(7)
        );


        root.setBackground(
            bg(BG,dp(14))
        );




        TextView title=new TextView(ctx);

        title.setText("ExampleMod");

        title.setTextColor(TEXT);

        title.setTextSize(12);

        title.setGravity(Gravity.CENTER);

        title.setTypeface(Typeface.DEFAULT_BOLD);


        title.setBackground(
            bg(ACCENT,dp(10))
        );


        title.setPadding(
            0,
            dp(7),
            0,
            dp(7)
        );


        root.addView(title,
                     new LinearLayout.LayoutParams(
                         -1,
                         dp(34)
                     )
                     );




        ScrollView scroll=new ScrollView(ctx);


        listContainer=new LinearLayout(ctx);

        listContainer.setOrientation(
            LinearLayout.VERTICAL
        );


        listContainer.setPadding(
            dp(3),
            dp(5),
            dp(3),
            dp(5)
        );


        scroll.addView(listContainer);



        root.addView(scroll,
                     new LinearLayout.LayoutParams(
                         -1,
                         0,
                         1
                     )
                     );




        TextView close=new TextView(ctx);

        close.setText("MINIMIZE");

        close.setTextColor(TEXT);

        close.setTextSize(11);

        close.setGravity(Gravity.CENTER);

        close.setTypeface(Typeface.DEFAULT_BOLD);


        close.setBackground(
            bg(CARD,dp(10))
        );


        close.setPadding(
            0,
            dp(7),
            0,
            dp(7)
        );


        close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                }
            });



        root.addView(close);



        buildItems();




        menuParams=new WindowManager.LayoutParams(

            dp(190),
            dp(285),

            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,

            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,

            PixelFormat.TRANSLUCENT
        );


        menuParams.gravity=Gravity.TOP|Gravity.LEFT;

        menuParams.x=floatParams.x;

        menuParams.y=floatParams.y;



        menuView=root;


        wm.addView(
            menuView,
            menuParams
        );



        title.setOnTouchListener(new View.OnTouchListener(){

                float sx,sy;


                public boolean onTouch(View v,MotionEvent e){

                    if(e.getAction()==0){

                        sx=e.getRawX();
                        sy=e.getRawY();

                    }


                    if(e.getAction()==2){

                        menuParams.x += e.getRawX()-sx;
                        menuParams.y += e.getRawY()-sy;


                        sx=e.getRawX();
                        sy=e.getRawY();


                        wm.updateViewLayout(
                            menuView,
                            menuParams
                        );
                    }


                    return true;
                }

            });

    }

    private void closeMenu(){

        if(menuView!=null)
            menuView.setVisibility(View.GONE);


        if(floatView!=null)
            floatView.setVisibility(View.VISIBLE);

    }

    private void killMenu() {
        if (menuView != null) {
            wm.removeView(menuView);
            menuView = null;
        }
        if (floatView != null) {
            wm.removeView(floatView);
            floatView = null;
        }
    }

    // ─── items ───

    private void buildItems() {
        addToggle("Nuker");
        addSeekbar("Nuker Range", 1, 8, 1);
        addToggle("Fast Break");
        /*addToggle("One Hit Kill");
        addToggle("Fly Hack");
        addSeekbar("Speed Multiplier", 1, 20, 1);
        addToggle("No Recoil");
        addToggle("No Spread");
        addToggle("Aimbot");
        addSeekbar("Aimbot FOV", 0, 360, 90);
        addToggle("Wallhack");
        addToggle("Unlimited Ammo");
        addSeekbar("Rapid Fire Rate", 0, 100, 0);
        addToggle("Anti Ban");
        addToggle("Bypass");*/
    }

    private void addToggle(final String name) {
        final TextView btn = new TextView(ctx);
        btn.setText(name);
        btn.setTextColor(Color.WHITE);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(0, dp(9), 0, dp(9));

        boolean saved = toggleStates.get(name) != null && toggleStates.get(name);
        final boolean[] on = {saved};
        btn.setBackground(
            bg(on[0] ? ACCENT : CARD,dp(10))
        );
        btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    on[0] = !on[0];
                    toggleStates.put(name, on[0]);
                    btn.setBackground(bg(on[0] ? 0xFF4CAF50 : 0xFF333333, dp(2)));
                    NativeClient.setEnableMod(name, on[0]);
                }
            });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(4);
        btn.setLayoutParams(lp);
        listContainer.addView(btn);
    }

    private void addSeekbar(final String name, int min, int max, int def) {
        LinearLayout box = new LinearLayout(ctx);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(8), dp(6), dp(8), dp(6));
        box.setBackground(bg(0xFF222222, dp(2)));
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(-1, -2);
        blp.bottomMargin = dp(4);
        box.setLayoutParams(blp);

        // label + value
        LinearLayout lr = new LinearLayout(ctx);
        lr.setOrientation(LinearLayout.HORIZONTAL);
        lr.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        TextView label = new TextView(ctx);
        label.setText(name);
        label.setTextColor(Color.WHITE);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        lr.addView(label);

        int savedVal = seekValues.get(name) != null ? seekValues.get(name) : def;
        final int finalMin = min;
        final TextView val = new TextView(ctx);
        val.setText(String.valueOf(savedVal));
        val.setTextColor(0xFF1E88E5);
        val.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        val.setTypeface(Typeface.DEFAULT_BOLD);
        lr.addView(val);

        box.addView(lr);

        SeekBar seek = new SeekBar(ctx);
        seek.setMax(max - min);
        seek.setProgress(savedVal - min);
        seek.setProgressTintList(ColorStateList.valueOf(0xFF1E88E5));
        seek.setThumbTintList(ColorStateList.valueOf(0xFF1E88E5));
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-1, dp(18));
        slp.topMargin = dp(4);
        seek.setLayoutParams(slp);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar s, int p, boolean u) {
                    int v = p + finalMin;
                    val.setText(String.valueOf(v));
                    seekValues.put(name, v);
                    NativeClient.setModValue(name, v);
                }
                public void onStartTrackingTouch(SeekBar s) {}
                public void onStopTrackingTouch(SeekBar s) {}
            });

        box.addView(seek);
        listContainer.addView(box);
    }
}
