package com.mukadobo.json.schema.poc.command

import com.mukadobo.propertyobject.KindAndVersion
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

@EqualsAndHashCode
class Subject extends KindAndVersion.Base
{
	final Map<String, String> selector
	
	Subject(JSONObject jsonDom)
	{
		super(jsonDom)
		
		selector = jsonDom.getJSONObject("selector").toMap() as Map<String, String>
	}
}
