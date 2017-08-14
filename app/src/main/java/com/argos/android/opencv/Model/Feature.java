package com.argos.android.opencv.Model;

import android.support.annotation.DrawableRes;

/**
 * Model for Feature used in each card
 */

public class Feature
{
    private String featureName;
    private @DrawableRes int featureThumbnail;

    public Feature(String featureName, int featureThumbnail)
    {
        this.featureName = featureName;
        this.featureThumbnail = featureThumbnail;
    }

    public String getFeatureName()
    {
        return featureName;
    }

    public void setFeatureName(String featureName)
    {
        this.featureName = featureName;
    }

    public int getFeatureThumbnail()
    {
        return featureThumbnail;
    }

    public void setFeatureThumbnail(int featureThumbnail)
    {
        this.featureThumbnail = featureThumbnail;
    }
}
