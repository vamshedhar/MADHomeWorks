package io.vamshedhar.searchwords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/24/17 5:46 PM.
 * vchinta1@uncc.edu
 */

public class SearchResultItemUI extends RelativeLayout {

    public TextView startText, keyWord, endText;

    public SearchResultItemUI(Context context) {
        super(context);
        inflateXML(context);
    }

    private void inflateXML(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View KeyWordItemView = inflater.inflate(R.layout.search_result_item, this);
        this.keyWord = (TextView) findViewById(R.id.keyWordTV);
        this.startText = (TextView) findViewById(R.id.startTextTV);
        this.endText = (TextView) findViewById(R.id.endTextTV);
    }
}
