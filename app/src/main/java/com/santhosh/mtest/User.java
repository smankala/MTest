package com.santhosh.mtest;

import android.os.Parcel;
import android.os.Parcelable;

public final class User implements Parcelable {

    //TODO: Make private
    public String username;
    public int id;
    public String password;
    public int age;
    public int height;
    public boolean likes_javascript;
    public int magic_number;
    public String magic_hash;

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(){
    }

    private User(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(username);
        out.writeString(password);
        out.writeInt(age);
        out.writeInt(height);
        out.writeByte((byte) (likes_javascript ? 1 : 0));
        out.writeInt(magic_number);
        out.writeString(magic_hash);
    }
    public void readFromParcel(Parcel in) {
        username = in.readString();
        password = in.readString();
        age = in.readInt();
        height = in.readInt();
        likes_javascript = in.readByte() != 0;
        magic_number = in.readInt();
        magic_hash = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


}

