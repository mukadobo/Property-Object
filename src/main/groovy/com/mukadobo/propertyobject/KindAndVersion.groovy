package com.mukadobo.propertyobject

import com.mukadobo.version.VersionChain
import groovy.json.JsonOutput
import org.json.JSONObject

interface KindAndVersion
{
	String       getKind()
	VersionChain getVersion()
	
	class Base implements KindAndVersion
	{
		private final String       kind
		private final VersionChain version
		
		protected Base(String kind, VersionChain version)
		{
			this.kind    = kind
			this.version = version
		}
		
		protected Base(JSONObject jsonDom)
		{
			this.kind    = jsonDom.getString("kind")
			this.version = new VersionChain(jsonDom.getString("version"))
		}
		
		@Override String       getKind()    { kind }
		@Override VersionChain getVersion() { version }
		
		@Override
		String toString()
		{
			JsonOutput.toJson(this)
		}
	}
}