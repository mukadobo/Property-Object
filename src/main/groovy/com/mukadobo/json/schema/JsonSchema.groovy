package com.mukadobo.json.schema

import org.apache.commons.io.IOUtils
import org.everit.json.schema.ObjectSchema
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

class JsonSchema // extends ObjectSchema // NOTE: extending is a pain, so wrap instead...
{
	private final ObjectSchema schema
	
	private JsonSchema(ObjectSchema schema)
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
		this(new JSONObject(new JSONTokener(jsonText)))
	}
	
	JsonSchema(JSONObject jsonDom)
	{
		schema = SchemaLoader
			.builder()
			.draftV6Support()
			.schemaJson(jsonDom)
			.build()
			.load().build()
	}
	
	void validate(Object subject)
	{
		schema.validate(subject)
	}
	
	// below are  just pass-thru to the delegate org.everit.json.schema.**
	// @formatter:off
	
	String                   getTitle()                        { schema.getTitle() }
	String                   getDescription()                  { schema.getDescription() }
	String                   getId()                           { schema.getId() }
	String                   getSchemaLocation()               { schema.getSchemaLocation() }
	String                   toString()                        { schema.toString() }
	Integer                  getMaxProperties()                { schema.getMaxProperties() }
	Integer                  getMinProperties()                { schema.getMinProperties() }
	Map<String, Set<String>> getPropertyDependencies()         { schema.getPropertyDependencies() }
	Map<String, Schema>      getPropertySchemas()              { schema.getPropertySchemas() }
	List<String>             getRequiredProperties()           { schema.getRequiredProperties() }
	Map<String, Schema>      getSchemaDependencies()           { schema.getSchemaDependencies() }
	Schema                   getSchemaOfAdditionalProperties() { schema.getSchemaOfAdditionalProperties() }
	Schema                   getPropertyNameSchema()           { schema.getPropertyNameSchema() }
	
	// @formatter:on
}
