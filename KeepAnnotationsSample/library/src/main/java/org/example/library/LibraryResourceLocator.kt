package org.example.library

import androidx.annotation.keep.UsesReflectionToConstruct

interface LibraryResourceLocator {
    companion object {
        @UsesReflectionToConstruct(classConstant = LibraryResourceLocatorImpl::class)
        fun load(): LibraryResourceLocator {
            val klass = Class.forName(LibraryResourceLocatorImpl::class.java.name)
            @Suppress("DEPRECATION")
            return klass.newInstance() as LibraryResourceLocator
        }
    }
}

private class LibraryResourceLocatorImpl : LibraryResourceLocator
