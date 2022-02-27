package com.example.instagram.models;


import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + getObjectId().hashCode();
        hash = 31 * hash + getDescription().hashCode();
        hash = 31 * hash + getUser().getObjectId().hashCode();
        return hash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())
            return false;
        Post otherPost = (Post) obj;
        return getObjectId().equals(otherPost.getObjectId()) &&
                getDescription().equals(otherPost.getDescription()) &&
                getUser().getObjectId().equals(otherPost.getUser().getObjectId());
    }
}
