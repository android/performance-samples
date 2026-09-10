package org.example.java.library

import androidx.annotation.keep.UsesReflectionToConstruct

interface PlainLocator {
    companion object {
        @UsesReflectionToConstruct(classConstant = PlainLocatorImpl::class)
        fun load(): PlainLocator {
            val klass = Class.forName(PlainLocatorImpl::class.java.name)
            @Suppress("DEPRECATION")
            return klass.newInstance() as PlainLocator
        }
    }
}

private class PlainLocatorImpl : PlainLocator
