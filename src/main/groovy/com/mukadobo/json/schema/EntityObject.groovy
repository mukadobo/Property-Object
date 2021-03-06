package com.mukadobo.json.schema

import com.mukadobo.version.VersionChain
import groovy.json.JsonOutput
import groovy.transform.EqualsAndHashCode
import org.apache.log4j.Logger
import org.json.JSONObject

import java.lang.reflect.Constructor
import java.lang.reflect.Method

interface EntityObject
{
	String       getKind   ()
	VersionChain getVersion()
	String       getName   ()
	UUID         getUuid   ()
	
	@EqualsAndHashCode
	class Base implements EntityObject
	{
		static private Logger     logger = Logger.getLogger(Base)
		static private JsonSchema schema = null
		
		private String       kind     = null
		private VersionChain version  = null
		private String       name     = null
		private UUID         uuid     = null
		
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
			this.version = VersionChain.from(jsonDom.optString("version"))
			this.name    = jsonDom.optString("name", null)
			this.uuid    = jsonDom.optString("uuid") ? UUID.fromString(jsonDom.getString("uuid")) : null
		}
		
		protected Base(JSONObject jsonDom, Class kindClass)
		{
			String kind = jsonDom.opt("kind")
			
			if (kind)
			{
				if (kind != kindClass.getCanonicalName())
					throw new RuntimeException("init-data kind mismatch: expected '${kindClass.getCanonicalName()}' but given as '${kind}'")
			}
			else
			{
				jsonDom.put("kind", kindClass.getCanonicalName())
			}
			validate(jsonDom)
			
			this.kind    = jsonDom.getString("kind")
			this.version = VersionChain.from(jsonDom.optString("version"))
			this.name    = jsonDom.optString("name", null)
			this.uuid    = jsonDom.optString("uuid") ? UUID.fromString(jsonDom.getString("uuid")) : null
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
			logger.trace("---------- factory start ----------")
			validate(jsonDom)
			
			String kind  = jsonDom.getString("kind")
			logger.trace("kind=${kind}")
			
			Class  kindClass
			try
			{
				kindClass = Class.forName(kind)
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException("No such kind-class: $kind", e)
			}
			logger.trace("kindClass=${kindClass}")
			
			Constructor constructor
			try
			{
				constructor = kindClass.getConstructor(JSONObject.class)
			}
			catch (NoSuchMethodException ignored)
			{
				constructor = null
			}
			logger.trace("constructor=${constructor}")
			
			Object instance = null
			if (constructor)
			{
				try
				{
					instance = constructor.newInstance(jsonDom)
				}
				catch (GroovyRuntimeException e)
				{
					if (! e.getMessage() ==~ /^Could not find matching constructor for:.*/)
						throw new RuntimeException("kind-class constructor failure: $constructor", e)
				}
				catch (Throwable e)
				{
					throw new RuntimeException("kind-class constructor failure: $constructor", e)
				}
			}
			
			if (instance != null)
			{
				instance as EntityObject
			}
			else
			{
				Method thisFactory = EntityObject.Base.class.getMethod("factory", JSONObject.class)
				Method kindFactory
				try
				{
					kindFactory = kindClass.getMethod("factory", JSONObject.class)
				}
				catch (NoSuchMethodException ignored)
				{
					kindFactory = null
				}
				logger.trace("kindFactory=${kindFactory}")
				
				if (!kindFactory || kindFactory == thisFactory)
					throw new RuntimeException("kind-class has no suitable constructor or factory: $kind")
				
				instance = kindFactory.invoke(null, jsonDom)
				if (instance == null)
					throw new RuntimeException("kind-factory returned null: $kindFactory")
				
				if (! kindClass.isInstance(instance))
					throw new RuntimeException("kind-factory return type mismatch: $kindFactory")
				
				instance as EntityObject
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
				catch (ClassNotFoundException ignored)
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