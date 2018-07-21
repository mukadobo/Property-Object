package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

@EqualsAndHashCode
class HttpPredicate extends EntityObject.Base
{
	final HttpVerb verb
	final String path
	final List<Map<String, Object>> params
	
	HttpPredicate(JSONObject jsonDom)
	{
		super(jsonDom)
		
		verb = HttpVerb.from(jsonDom.optString("verb"))
		path = jsonDom.optString("path")
		
		if (! jsonDom.has("params"))
		{
			params = Collections.EMPTY_LIST
		}
		else
		{
			params = jsonDom.optJSONArray("params").toList()
		}
	}
}

