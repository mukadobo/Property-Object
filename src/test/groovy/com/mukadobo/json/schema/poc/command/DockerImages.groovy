package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonSchema
import org.apache.log4j.Logger
import org.json.JSONObject

class DockerImages extends DockerActor.Base
{
	static private Logger     logger = Logger.getLogger(DockerImages)
	static private JsonSchema schema = null
	
	final Boolean all
	final Boolean digests
	final String  filter
	final String  format
	final Boolean noTrunc
	final Boolean quiet
	
	/**
	 * Default optional values follow Docker standards.
	 * <BR><BR>
	 * See <A HREF="https://docs.docker.com/engine/reference/commandline/images/#options">
	 *     https://docs.docker.com/engine/reference/commandline/images/#options
	 *     </A>
	 *
	 * @param jsonDom
	 */
	DockerImages(JSONObject jsonDom)
	{
		super(jsonDom)

		validate(jsonDom)
		
		this.all     = jsonDom.optBoolean("--all"     , false)
		this.digests = jsonDom.optBoolean("--digests" , false)
		this.filter  = jsonDom.optString ("--filter"  , null )
		this.format  = jsonDom.optString ("--format"  , null )
		this.noTrunc = jsonDom.optBoolean("--no-trunc", false)
		this.quiet   = jsonDom.optBoolean("--quiet"   , false)
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
