package com.example.xuzhi.easykitchen;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuzhi on 2016/1/29.
 */
public class RecipeStruct implements Parcelable {
    int mId;
    String mName;
    String mMaterial;
    String mSeasoning;
    String mSteps;
    int mImage;
    /*
    String mSource;
    String mFavorite;
    String mWeight;*/
    String mMealType;
    String mTimeConsuming;
    String mDifficulty;/*
    String mPopularity;*/
    String mTaste;
    public RecipeStruct (int id,
                        String name,
                        String material,
                        String seasoning,
                        String steps,
                        int image,
                        /*
                        String source,
                        String favorite,
                        String weight,*/
                        String mealType,
                        String timeConsuming,
                        String difficulty,/*
                        String popularity,*/
                        String taste)
    {
        mId = id;
        mName = name;
        mMaterial = material;
        mSeasoning = seasoning;
        mSteps = steps;
        mImage = image;
        /*
        mSource = source;
        mFavorite = favorite;
        mWeight = weight;*/
        mMealType = mealType;
        mTimeConsuming = timeConsuming;
        mDifficulty = difficulty;/*
        mPopularity = popularity;*/
        mTaste = taste;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 序列化过程：必须按成员变量声明的顺序进行封装
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mMaterial);
        dest.writeString(mSeasoning);
        dest.writeString(mSteps);
        dest.writeInt(mImage);
        dest.writeString(mMealType);
        dest.writeString(mTimeConsuming);
        dest.writeString(mDifficulty);
        dest.writeString(mTaste);
    }

    // 反序列过程：必须实现Parcelable.Creator接口，并且对象名必须为CREATOR
    // 读取Parcel里面数据时必须按照成员变量声明的顺序，Parcel数据来源上面writeToParcel方法，读出来的数据供逻辑层使用
    public static final Parcelable.Creator<RecipeStruct> CREATOR = new Creator<RecipeStruct>() {

        @Override
        public RecipeStruct createFromParcel(Parcel source) {
            return new RecipeStruct(source.readInt(), source.readString(), source.readString(), source.readString(), source.readString(),source.readInt(),
                    source.readString(), source.readString(), source.readString(), source.readString());
        }

        @Override
        public RecipeStruct[] newArray(int size) {
            return new RecipeStruct[size];
        }
    };
}
