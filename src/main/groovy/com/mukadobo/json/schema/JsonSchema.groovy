package com.mukadobo.json.schema

import org.apache.commons.io.IOUtils
import org.everit.json.schema.ObjectSchema
import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

class JsonSchema // NOTE: extending ObjectSchema is a pain, so wrap instead...
{
	static final JSONObject EMPTY_JSON_DOM_MAP   = new JSONObject(new JSONTokener("{}"))
	static final JSONArray  EMPTY_JSON_DOM_ARRAY = new JSONArray (new JSONTokener("[]"))
	
	private final Schema schema
	
	private JsonSchema(Schema schema)
	{
		this.schema = schema
	}
	
	JsonSchema(
		Class   classRef,
		Boolean canonical = JsonSchemaUrl.DEFAULT_CLASS_CANONICAL,
		String  prefix    = JsonSchemaUrl.DEFAULT_PATH_PREFIX
	)
	{
		this(JsonSchemaUrl.toUrl(classRef, canonical, prefix))
	}
	
	JsonSchema(URL url)
	{
		this((InputStream) url.getContent())
	}
	
	JsonSchema(InputStream stream)
	{
		this(IOUtils.toString(stream, "UTF-8"))
	}
	
	JsonSchema(String jsonText)
	{
		this(jsonTextToDom(jsonText))
	}
	
	JsonSchema(JSONObject jsonDom)
	{
		Schema loadedSchema = SchemaLoader
			.builder()
			.draftV6Support()
			.schemaJson(jsonDom)
			.build()
			.load().build() as Schema
		
		schema = loadedSchema
	}
	
	static private JSONObject jsonTextToDom(String jsonText)
	{
		try
		{
			new JSONObject(new JSONTokener(jsonText))
		}
		catch (JSONException e)
		{
			throw new RuntimeException("Schema-Input is not even valid JSON text", e)
		}
	}
	
	NonconformanceException validate(Object subject, Boolean unifiedException = true, Integer msgLimit = 2000, Boolean rethrow = true)
	{
		NonconformanceException derived = null
		
		try
		{
			schema.validate(subject)
		}
		catch (ValidationException caught)
		{
			derived = new NonconformanceException(schema, caught, unifiedException, msgLimit)
			
			if (rethrow)
				throw derived
		}
		
		derived
	}
	
	static class NonconformanceException extends RuntimeException
	{
		NonconformanceException(Schema schema, ValidationException cause, Boolean unify, Integer limit)
		{
			super(deriveMessage(schema, cause, unify, limit), cause)
		}
		
		private static deriveMessage(Schema schema, ValidationException cause, Boolean unify, Integer limit)
		{
			String message = "schema validation error: schema.id='${schema.getId()}'"
			
			if (unify)
			{
				String causeMsg = cause.toJSON().toString(2)
				
				int causeMsgLen = causeMsg.length()
				int totalMsgLen = message.length() + causeMsgLen + 5
				
				if ((limit <= 0) || (limit > totalMsgLen))
				{
					message += ":\n" + causeMsg
				}
				else
				{
					message += ":\n" + causeMsg.substring(limit) + "\n# validation error cause truncated from ${causeMsgLen} to ${limit} chars"
				}
			}

			message
		}
	}
	
	// below are  just pass-thru to the delegate org.everit.json.schema.**
	// @formatter:off
	
	String                   getTitle()                        { schema.getTitle() }
	String                   getDescription()                  { schema.getDescription() }
	String                   getId()                           { schema.getId() }
	String                   getSchemaLocation()               { schema.getSchemaLocation() }
	String                   toString()                        { schema.toString() }
	
	Integer                  getMaxProperties()                { (schema as ObjectSchema).getMaxProperties() }
	Integer                  getMinProperties()                { (schema as ObjectSchema).getMinProperties() }
	Map<String, Set<String>> getPropertyDependencies()         { (schema as ObjectSchema).getPropertyDependencies() }
	Map<String, Schema>      getPropertySchemas()              { (schema as ObjectSchema).getPropertySchemas() }
	List<String>             getRequiredProperties()           { (schema as ObjectSchema).getRequiredProperties() }
	Map<String, Schema>      getSchemaDependencies()           { (schema as ObjectSchema).getSchemaDependencies() }
	Schema                   getSchemaOfAdditionalProperties() { (schema as ObjectSchema).getSchemaOfAdditionalProperties() }
	Schema                   getPropertyNameSchema()           { (schema as ObjectSchema).getPropertyNameSchema() }
	
	// @formatter:on
}
