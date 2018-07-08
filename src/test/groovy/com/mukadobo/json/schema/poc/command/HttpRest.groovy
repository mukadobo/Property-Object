package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonSchema
import com.mukadobo.version.VersionChain
import org.json.JSONObject

class HttpRest extends EntityObject.Base
{
	static private JsonSchema schema = null

	HttpRest(JSONObject jsonDom)
	{
		super(jsonDom)
		
		try
		{
			myJsonSchema().validate(jsonDom)
		}
		catch (Exception e)
		{
			throw new RuntimeException("JSON input not valid", e)
		}
		
		this.kind    = jsonDom.getString("kind")
		this.version = new VersionChain(jsonDom.getString("version"))
		this.name    = jsonDom.optString("name", null)
		this.uuid    = UUID.fromString(jsonDom.optString("uuid", '00000000-0000-0000-0000-000000000000'))
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
	
}
