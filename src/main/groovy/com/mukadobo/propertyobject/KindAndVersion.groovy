package com.mukadobo.propertyobject

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.version.VersionChain
import org.json.JSONObject

interface KindAndVersion extends EntityObject
{
	String       getKind()
	VersionChain getVersion()
	
	class Base extends EntityObject.Base
	{
		protected Base(
			String kind,
			VersionChain version,
			String name,
			UUID uuid
		)
		{
			super(kind, version, name, uuid)
		}
		
		protected Base(JSONObject jsonDom)
		{
			super(jsonDom)
		}
	}
}