package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

@EqualsAndHashCode
class Predicate extends EntityObject.Base
{
	final String verb
	final String path
	
	Predicate(JSONObject jsonDom)
	{
		super(jsonDom)
		
		verb = jsonDom.getString("verb")
		path = jsonDom.getString("path")
		params = jsonDom.getJSONArray("params").toList()
	}
}

