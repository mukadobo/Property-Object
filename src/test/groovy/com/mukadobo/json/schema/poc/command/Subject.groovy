package com.mukadobo.json.schema.poc.command

import com.mukadobo.propertyobject.KindAndVersion
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

@EqualsAndHashCode
class Subject extends KindAndVersion.Base
{
	Map<String, String> selector
	
	Subject(JSONObject jsonDom)
	{
		super(jsonDom)
		
		selector = jsonDom.selector
	}
}
