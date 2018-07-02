package com.argos.android.opencv.model

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.util.Log
import org.opencv.core.Mat

open class Feature(open var featureName: String?, @field:DrawableRes open var featureThumbnail: Int): Parcelable {

    companion object CREATOR : Parcelable.Creator<Feature> {
        override fun createFromParcel(parcel: Parcel): Feature {
            return Feature(parcel)
        }

        override fun newArray(size: Int): Array<Feature?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(featureName)
        parcel.writeInt(featureThumbnail)
    }

    override fun describeContents(): Int {
        return 0
    }

    open fun getFrameInfoAndDebugImage(currentFrame: Mat): Pair<Mat, Mat?> {
        Log.d(Feature::class.java.simpleName, "FEATURE")
        return Pair(currentFrame, null)
    }
}
