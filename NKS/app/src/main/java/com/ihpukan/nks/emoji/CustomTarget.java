package com.ihpukan.nks.emoji;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

import androidx.annotation.RequiresApi;

public class CustomTarget implements Target {
    private String url;
    private int start;
    private int end;
    private WeakReference<EditText> postText;
    private SpannableStringBuilder rawText;
    private Integer imgWidth;
    private Integer imgHeight;

    public CustomTarget(String url, int start, int end, EditText postText, SpannableStringBuilder rawText, Integer imgWidth, Integer imgHeight )
    {
        this.url = url;
        this.start = start;
        this.end = end;
        this.postText = new WeakReference<>(postText);
        this.rawText = rawText;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
    {
        EditText textView = postText.get();

        BitmapDrawable d = new BitmapDrawable(textView.getResources(), bitmap);

        d.setBounds(0,0,this.imgWidth,this.imgHeight); //d.getIntrinsicWidth()+5,d.getIntrinsicHeight()+5

        ImageSpan imageSpan = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);


        rawText.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        ImageSpan[] spans = textView.getText().getSpans(0,textView.length(),ImageSpan.class);

        if(spans.length>=1)
        {
            for(ImageSpan image: spans)
            {
                int end = textView.getText().getSpanEnd(image);
                int start_ = textView.getText().getSpanStart(image);
                if(rawText.length()<end) //Attempt to fix: ends beyond on scrolling too fast...
                {
                    end = rawText.length();
                }
                if(end<start_)
                {
                    if(end-1>=0){start_ = end - 1;}
                    else{start_ = end;}
                }
                rawText.setSpan(image, start_,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        textView.setText(rawText,TextView.BufferType.SPANNABLE);

        //targets.remove(this);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) //Exception exception,
    {

    }
    /*public void onBitmapFailed(Drawable errorDrawable)
    {

    }*/

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable)
    {

    }

}
