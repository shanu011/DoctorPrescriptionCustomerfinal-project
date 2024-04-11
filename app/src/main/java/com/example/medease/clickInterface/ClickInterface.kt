package com.example.medease.clickInterface

import android.widget.ImageView

interface ClickInterface {
    fun onClick(position: Int, clickType: ClickType ?= ClickType.ViewClick,imageView: ImageView) :Boolean
    fun view(position: Int,imageView: ImageView)
}

enum class ClickType{
   ViewClick,Details,Delete
}