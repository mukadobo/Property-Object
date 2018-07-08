package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

@EqualsAndHashCode
class Predicate extends EntityObject.Base
{
	String verb
	
	Predicate(JSONObject jsonDom)
	{
		super(jsonDom)
		
		verb = jsonDom.verb
	}
}

