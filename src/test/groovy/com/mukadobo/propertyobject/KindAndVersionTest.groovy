package com.mukadobo.propertyobject

import com.mukadobo.json.schema.JsonSchema
import com.mukadobo.json.schema.JsonSchemaUrl
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Specification

class KVAndId {}
class Voucher {}

class KindAndVersionTest extends Specification
{
	def "JSON Schema is available"()
	{
		when:
			
			new JsonSchema(KindAndVersion.class, false, JsonSchemaUrl.DEFAULT_PATH_PREFIX)
		
		then:
			
			notThrown(JsonSchema.NonconformanceException)
	}
	
	def "missing [kind]"()
	{
		String jsonText = '''\
		{
			"version" : "3.2.1.0"
		}
		'''.stripIndent()
		
		when:
			
			JSONObject jsonDom = new JSONObject(new JSONTokener(jsonText))
			JsonSchema schema  = new JsonSchema(KindAndVersion.class, false, JsonSchemaUrl.DEFAULT_PATH_PREFIX)
			
			schema.validate(jsonDom) == null
		
		then:
			
			thrown(JsonSchema.NonconformanceException)
	}
	
	def "missing [version]"()
	{
		String jsonText = '''\
		{
			"kind" : "com.mukadobo.propertyobject.KindAndVersion"
		}
		'''.stripIndent()
		
		when:
			
			JSONObject jsonDom = new JSONObject(new JSONTokener(jsonText))
			JsonSchema schema  = new JsonSchema(KindAndVersion.class, false, JsonSchemaUrl.DEFAULT_PATH_PREFIX)
			
			schema.validate(jsonDom) == null
		
		then:
			
			thrown(JsonSchema.NonconformanceException)
	}
	
	def "just [kind, version]"()
	{
		String jsonText = '''\
		{
			"kind" : "com.mukadobo.propertyobject.KindAndVersion",
			"version" : "3.2.1.0"
		}
		'''.stripIndent()
		
		when:
			
			JSONObject jsonDom = new JSONObject(new JSONTokener(jsonText))
			JsonSchema schema  = new JsonSchema(KindAndVersion.class, false, JsonSchemaUrl.DEFAULT_PATH_PREFIX)
			
			schema.validate(jsonDom) == null
		
		then:
			
			notThrown(JsonSchema.NonconformanceException)
	}
	
	def "JSON text with more than [kind, version]"()
	{
		String jsonText = '''\
		{
			"kind" : "com.mukadobo.propertyobject.KindAndVersion",
			"version" : "3.2.1.0",
			"id" : "abc123"
		}
		'''.stripIndent()
		
		when:
			
			JSONObject jsonDom = new JSONObject(new JSONTokener(jsonText))
			JsonSchema schema  = new JsonSchema(KindAndVersion.class, false, JsonSchemaUrl.DEFAULT_PATH_PREFIX)
			
			schema.validate(jsonDom) == null
		
		then:
			
			notThrown(JsonSchema.NonconformanceException)
	}
	
	def "Derived KVAndId"()
	{
		String jsonText = '''\
		{
			"kind" : "com.mukadobo.propertyobject.KVAndId",
			"version" : "3.2.1.0",
			"id" : "abc123"
		}
		'''.stripIndent()
		
		when:
			
			JSONObject jsonDom = new JSONObject(new JSONTokener(jsonText))
			JsonSchema schema  = new JsonSchema(KVAndId.class, false, JsonSchemaUrl.DEFAULT_PATH_PREFIX)
			
			schema.validate(jsonDom) == null
		
		then:
			
			notThrown(JsonSchema.NonconformanceException)
	}
	
	def "Derived KVAndId - missing [id]"()
	{
		String jsonText = '''\
		{
			"kind" : "com.mukadobo.propertyobject.KVAndId",
			"version" : "3.2.1.0"
		}
		'''.stripIndent()
		
		when:
			
			JSONObject jsonDom = new JSONObject(new JSONTokener(jsonText))
			JsonSchema schema  = new JsonSchema(KVAndId.class, false, JsonSchemaUrl.DEFAULT_PATH_PREFIX)
			
			schema.validate(jsonDom) == null
		
		then:
			
			thrown(JsonSchema.NonconformanceException)
	}
	
	def "Voucher - all as required"()
	{
		String jsonText = '''\
		{
			"kind"     : "com.mukadobo.propertyobject.KVAndId",
			"version"  : "3.2.1.0",
			"id"       : "3-14159",
			"designee" : "pi"
		}
		'''.stripIndent()
		
		when:
			
			JSONObject jsonDom = new JSONObject(new JSONTokener(jsonText))
			JsonSchema schema  = new JsonSchema(Voucher.class, false, JsonSchemaUrl.DEFAULT_PATH_PREFIX)
			
			schema.validate(jsonDom, true) == null
		
		then:
			
			notThrown(JsonSchema.NonconformanceException)
	}
}