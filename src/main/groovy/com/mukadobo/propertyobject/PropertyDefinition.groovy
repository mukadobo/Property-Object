package com.mukadobo.propertyobject

import com.mukadobo.version.VersionChain
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class PropertyDefinition
{
    final PropertyType type
    final PropertyNeed need
    final VersionChain since
    final VersionChain until

    PropertyDefinition(PropertyType type, PropertyNeed need, VersionChain since, VersionChain until)
    {
        if (!type) throw new NullPointerException("type")

        this.type  = type
        this.need  = need  ? need  : PropertyNeed.OPTIONAL
        this.since = since ? since : VersionChain.ZERO
        this.until = until ? until : VersionChain.MAX
    }
}
