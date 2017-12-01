package io.vamshedhar.searchwords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/22/17 1:57 PM.
 * vchinta1@uncc.edu
 */

public class KeyWordItemUI extends LinearLayout {

    public TextView keyWord;
    public ImageView removeKeywordBtn;

    public KeyWordItemUI(Context context) {
        super(context);
        inflateXML(context);
    }

    private void inflateXML(Context context) {
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View KeyWordItemView = inflater.inflate(R.layout.key_word_item, this);
        this.keyWord = (EditText) findViewById(R.id.keyWordET);
        this.removeKeywordBtn = (ImageView) findViewById(R.id.removeKeywordBtn);
    }


}
