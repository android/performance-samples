package org.example

import androidx.annotation.keep.UsesReflectionToConstruct

interface ResourceLocator {
    companion object {
        @UsesReflectionToConstruct(classConstant = ResourceLocatorImpl::class)
        fun load(): ResourceLocator {
            val klass = Class.forName(ResourceLocatorImpl::class.java.name)
            @Suppress("DEPRECATION")
            return klass.newInstance() as ResourceLocator
        }
    }
}

private class ResourceLocatorImpl : ResourceLocator
