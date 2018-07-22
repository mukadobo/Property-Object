package com.mukadobo.json.schema.poc.command.docker

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonSchema
import com.mukadobo.json.schema.poc.command.Command
import org.apache.log4j.Logger
import org.json.JSONObject
/**
 *  TODO:
 *  - !! More than just "pull"
 *  ---------------------------------------------------------------------------
 *
 */
class DockerTraject extends EntityObject.Base implements Command.Performer
{
	static private Logger     logger = Logger.getLogger(DockerTraject)
	static private JsonSchema schema = null
	
	final DockerVerb  verb
	final DockerActor actor
	
	DockerTraject(JSONObject jsonDom)
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
	
	static DockerTraject factory(JSONObject jsonDom)
	{
		null
	}
}
