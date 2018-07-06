package com.mukadobo.propertyobject

import com.mukadobo.version.VersionChain

interface KindAndVersion
{
	String       getKind()
	VersionChain getVersion()
}