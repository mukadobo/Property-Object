package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonSchema
import org.apache.log4j.Logger
import org.json.JSONObject
/**
 *  TODO:
 *  - !! More than just "pull"
 *  ---------------------------------------------------------------------------
 *
 */
class DockerClient extends EntityObject.Base implements Command.Performer
{
	static private Logger     logger = Logger.getLogger(DockerClient)
	static private JsonSchema schema = null
	
	final DockerVerb  verb
	final DockerActor actor
	
	DockerClient(JSONObject jsonDom)
	{
		super(jsonDom)
		
		validate(jsonDom)
		
		verb  = DockerVerb.from(jsonDom.optString("action"))
		actor = DockerActor.Base.newActor(verb, jsonDom)
	}
	
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
		try
		{
			myJsonSchema().validate(jsonDom)
		}
		catch (Exception e)
		{
			throw new RuntimeException("JSON input not valid", e)
		}
	}
	
	Command.Result perform(Map args)
	{
		actor.perform(args)
	}
	
	static DockerClient factory(JSONObject jsonDom)
	{
		null
	}
}
