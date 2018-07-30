package com.mukadobo.json.schema.poc.command.ssh

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
class SshTraject extends EntityObject.Base implements Command.Performer
{
	static private Logger     logger = Logger.getLogger(SshTraject)
	static private JsonSchema schema = null
	
	final String  user
	final String  pswd
	final String  exec
	
	SshTraject(JSONObject jsonDom)
	{
		super(jsonDom)
		
		validate(jsonDom)
		
		user  = jsonDom.optJSONObject("credential").optString("user")
		pswd  = jsonDom.optJSONObject("credential").optString("pswd")
		exec  = jsonDom.optString("exec")
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
		Command.Result.failure("WIP/NYI")
	}
}
