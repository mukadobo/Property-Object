package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonSchema
import org.apache.log4j.Logger
import org.json.JSONObject

class DockerPull extends DockerActor.Base
{
	static private Logger     logger = Logger.getLogger(DockerPull)
	static private JsonSchema schema = null
	
	final String  source
	final Boolean allTags
	final Boolean noTrust
	
	/**
	 * Default optional values follow Docker standards.
	 * <BR><BR>
	 * See <A HREF="https://docs.docker.com/engine/reference/commandline/pull/#description">
	 *     https://docs.docker.com/engine/reference/commandline/pull/#description
	 *     </A>
	 *
	 * @param jsonDom
	 */
	DockerPull(JSONObject jsonDom)
	{
		super(jsonDom)

		validate(jsonDom)
		
		this.source  = jsonDom.optString("source")
		this.allTags = jsonDom.optBoolean("--all-tags", false)
		this.noTrust = jsonDom.optBoolean("--disable-content-trust", true)
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
	
	
	@Override
	Command.Result perform(Map args)
	{
		return Command.Result.failure("WIP")
	}
}
