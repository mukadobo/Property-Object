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
			validate(jsonDom)
			
			this.kind    = jsonDom.getString("kind")
			this.version = new VersionChain(jsonDom.optString("version", "0"))
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
		
		static private void validate(JSONObject jsonDom)
		{
			try {
				myJsonSchema().validate(jsonDom)
			}
			catch (Exception e)
			{
				throw new RuntimeException("JSON input not valid", e)
			}
		}
		
		static EntityObject factory(JSONObject jsonDom)
		{
			validate(jsonDom)
			
			String kind  = jsonDom.getString("kind")
			
			try
			{
				Class  kindClass = Class.forName(kind)
				kindClass.newInstance(jsonDom) as EntityObject
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException("No such class for kind: $kind")
			}
			catch (GroovyRuntimeException e)
			{
				if (e.getMessage() ==~ /^Could not find matching constructor for:.*/)
					throw new RuntimeException("kind class has no constructor: ${kind}(JSONObject)")
				else
					throw new RuntimeException("failed to instantiate kind: $kind", e)
			}
			catch (Throwable e)
			{
				throw new RuntimeException("failed to instantiate kind: $kind", e)
			}
		}
		
		static Boolean canTransformFrom(Object candidate)
		{
			Object kind = candidate.opt("kind")
			if (kind == null || !(kind instanceof String))
			{
				false
			}
			else
			{
				try
				{
					Class foundClass = Class.forName(kind)
					foundClass != null
				}
				catch (ClassNotFoundException e)
				{
					false
				}
			}
		}
		
		@Override
		String toString()
		{
			JsonOutput.toJson(this)
		}
	}
}