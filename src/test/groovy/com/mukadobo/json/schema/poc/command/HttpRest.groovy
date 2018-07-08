package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonSchema
import org.json.JSONObject

class HttpRest extends EntityObject.Base
{
	static private JsonSchema schema = null
	
	final String  server
	final Integer port
	final Boolean tls
	final Map<String,String> headers

	HttpRest(JSONObject jsonDom)
	{
		super(jsonDom)
		
		validate(jsonDom)
		
		this.tls = jsonDom.optBoolean("tls", false)
		int defaultPort = tls ? 443 : 80
		
		this.server  = jsonDom.optString("server", "localhost")
		this.port    = jsonDom.optInt   ("port"  , defaultPort)
		this.headers = jsonDom.has("headers") ? jsonDom.getJSONObject("headers").toMap() : []
		
//		this.server  = jsonDom.getString("server")
//		this.port    = new VersionChain(jsonDom.getString("version"))
//		this.name    = jsonDom.optString("name", null)
//		this.uuid    = UUID.fromString(jsonDom.optString("uuid", '00000000-0000-0000-0000-000000000000'))
	}

	Command.Result perform(Predicate predicate)
	{
		String summary = "${this.getClass().getSimpleName()}.perform(): NYI"
		
		new Command.Result(Command.Result.Code.SUCCESS, summary, summary, null)
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
	
}