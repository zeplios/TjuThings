package edu.tju.ina.things.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;

import edu.tju.ina.things.R;
import edu.tju.ina.things.fragments.FragmentFactory;
import edu.tju.ina.things.fragments.MenuFragment;
import edu.tju.ina.things.util.Utils;

public class MenuMainActivity extends ActionBarActivity {

    private Fragment mContent;
    private SlidingMenu menu;
    private long lastBackPressed = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_content);
        setActioinBar();

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        //menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setBehindScrollScale(0.25f);
        menu.setFadeDegree(0.25f);
        menu.setBehindCanvasTransformer(mTransformer);
        //menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.menu_frame);

        MenuFragment menuFrag = new MenuFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, menuFrag)
                .commit();

        // set the Above View Fragment
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if (mContent == null)
            mContent = FragmentFactory.createInstance(menuFrag.getCurrentSelected());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mContent)
                .commit();
    }

    private void setActioinBar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        // the homeasup do the job of menu.toggle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.title_menu);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    public void switchContent(final Fragment fragment) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                menu.showContent();
            }
        }, 50);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fragment != null) {
                    mContent = fragment;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();
                }
            }
        }, 500);
    }

    private CanvasTransformer mTransformer = new CanvasTransformer() {
        @Override
        public void transformCanvas(Canvas canvas, float percentOpen) {
            float scale = (float) (percentOpen * 0.25 + 0.75);
            canvas.scale(scale, scale, canvas.getWidth() / 2, canvas.getHeight() / 2);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menu.toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        long thistime = System.currentTimeMillis();
        if (thistime - lastBackPressed > 2000) {
            Utils.toast(this, "连续按两下退出应用");
            lastBackPressed = thistime;
        } else {
            super.onBackPressed();
        }
    }
}
