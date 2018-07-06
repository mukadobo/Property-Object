package com.mukadobo.json.schema.poc.command

import com.mukadobo.propertyobject.KindAndVersion
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

@EqualsAndHashCode
class Predicate extends KindAndVersion.Base
{
	String verb
	
	Predicate(JSONObject jsonDom)
	{
		super(jsonDom)
		
		verb = jsonDom.verb
	}
}

