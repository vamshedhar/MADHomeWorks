package io.vamshedhar.searchwords;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.math.BigInteger;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/24/17 4:41 PM.
 * vchinta1@uncc.edu
 */

public class SearchResult implements Serializable, Comparable<SearchResult>  {

    BigInteger index;
    String startString, keyWord, endString;

    public SearchResult(BigInteger index, String startString, String keyWord, String endString) {
        this.index = index;
        this.startString = startString;
        this.keyWord = keyWord;
        this.endString = endString;
    }

    @Override
    public int compareTo(@NonNull SearchResult searchResult) {
        return this.index.compareTo(searchResult.index);
    }
}
