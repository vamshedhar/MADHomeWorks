package io.vamshedhar.musicsearch;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/12/17 10:25 AM.
 * vchinta1@uncc.edu
 */

public class Track implements Serializable, Comparable<Track> {
    String id, name, artist, url, thumbnailUrl, imageUrl;
    Boolean isFav;

    public Track(String id, String name, String artist, String url) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.url = url;
        this.isFav = false;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", url='" + url + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public Boolean getisFav() {
        return isFav;
    }

    public void setisFav(Boolean isFav) {
        this.isFav = isFav;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        if (id != null ? !id.equals(track.id) : track.id != null) return false;
        if (name != null ? !name.equals(track.name) : track.name != null) return false;
        return artist != null ? artist.equals(track.artist) : track.artist == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        return result;
    }


    @Override
    public int compareTo(@NonNull Track track) {
        return this.name.compareTo(track.name);
    }
}
