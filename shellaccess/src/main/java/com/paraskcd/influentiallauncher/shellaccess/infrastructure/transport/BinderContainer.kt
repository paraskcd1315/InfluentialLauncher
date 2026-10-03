// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport

import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable

class BinderContainer(val binder: IBinder?) : Parcelable {
    private constructor(parcel: Parcel) : this(parcel.readStrongBinder())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeStrongBinder(binder)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BinderContainer> {
        override fun createFromParcel(parcel: Parcel): BinderContainer = BinderContainer(parcel)
        override fun newArray(size: Int): Array<BinderContainer?> = arrayOfNulls(size)
    }
}
