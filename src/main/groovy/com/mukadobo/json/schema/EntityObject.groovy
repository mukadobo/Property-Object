package com.mukadobo.json.schema

import com.mukadobo.version.VersionChain
import groovy.json.JsonOutput
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

interface EntityObject
{
	String       getKind   ()
	VersionChain getVersion()
	String       getName   ()
	UUID         getUuid   ()
	
	@EqualsAndHashCode
	class Base implements EntityObject
	{
		static private JsonSchema schema = null
		
		private final String       kind
		private final VersionChain version
		private final String       name
		private final UUID         uuid
		
		protected Base(String kind, VersionChain version, String name, UUID uuid)
		{
			this.kind     = kind
			this.version  = version
			this.name     = name
			this.uuid     = uuid
		}
		
		protected Base(JSONObject jsonDom)
		{
			try {
				myJsonSchema().validate(jsonDom)
			}
			catch (Exception e)
			{
				throw new RuntimeException("JSON input not valid", e)
			}
			
			this.kind    = jsonDom.getString("kind")
			this.version = new VersionChain(jsonDom.getString("version"))
			this.name    = jsonDom.optString("name", null)
			this.uuid    = UUID.fromString(jsonDom.optString("uuid", '00000000-0000-0000-0000-000000000000'))
		}
		
		@Override String       getKind   () { kind    }
		@Override VersionChain getVersion() { version }
		@Override String       getName   () { name    }
		@Override UUID         getUuid   () { uuid    }
		
		static private JsonSchema myJsonSchema()
		{
			if (!schema)
			{
				schema = new JsonSchema(EntityObject.class)
			}
			
			schema
		}
		
		@Override
		String toString()
		{
			JsonOutput.toJson(this)
		}
	}
}