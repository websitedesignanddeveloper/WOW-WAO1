package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.R;
import com.application.wowwao1.Utils.IntroManager;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class IntroSliderActivity extends AppCompatActivity {

    ViewPager viewPager;
    int[] layouts;
    TextView[] dots;
    LinearLayout dotsLayout;
    Button skip,next;
    CardView btn_started;

    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        typeface = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Regular.ttf");

        final IntroManager introManager = new IntroManager(this);

        if (!introManager.getFirst()) {
            Intent i = new Intent(this, SplashScreenActivity.class);
            startActivity(i);
            finish();
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        setContentView(R.layout.activity_intro_slider);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots) ;
        skip = (Button) findViewById(R.id.btn_skip);
        next = (Button) findViewById(R.id.btn_next);
        btn_started = (CardView) findViewById(R.id.btn_started);

        skip.setTypeface(typeface);
        next.setTypeface(typeface);

        layouts = new int[]{R.layout.slide_1, R.layout.slide_2, R.layout.slide_3};
        addBottomDots(0);
        changeStatusColor();

        ViewPagerAdapter adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(viewListener);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                introManager.setFirst(false);
                Intent i = new Intent(IntroSliderActivity.this, SplashScreenActivity.class);
                startActivity(i);
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentItem = getItem(+1);
                if(currentItem<layouts.length)
                {
                    viewPager.setCurrentItem(currentItem);
                }
                else
                {
                    introManager.setFirst(false);
                    Intent i = new Intent(IntroSliderActivity.this, SplashScreenActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        btn_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                introManager.setFirst(false);
                Intent i = new Intent(IntroSliderActivity.this, SplashScreenActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i)
    {
        return viewPager.getCurrentItem()+1;
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addBottomDots(position);
            if(position == layouts.length-1)
            {
                next.setText("PROCEED");
                skip.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                btn_started.setVisibility(View.VISIBLE);
            }
            else
            {
                next.setText("NEXT");
                skip.setVisibility(View.VISIBLE);
                btn_started.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void changeStatusColor() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    public class ViewPagerAdapter extends PagerAdapter {

        LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = layoutInflater.inflate(layouts[position], container, false);

            TextView tv = (TextView) v.findViewById(R.id.desc);
            tv.setTypeface(typeface);

            container.addView(v);
            return v;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }
    }



}
