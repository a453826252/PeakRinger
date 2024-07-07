package com.zaz.support

import kotlin.Cloneable

interface Clone:Cloneable {
    public override fun clone(): Any
}