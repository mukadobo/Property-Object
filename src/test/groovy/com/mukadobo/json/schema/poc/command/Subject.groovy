package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

@EqualsAndHashCode
class Subject extends EntityObject.Base
{
	final String name
	final String species
	
	final Map<String, String> selector
	final Map<String, String> qualifiers
	
	Subject(JSONObject jsonDom)
	{
		super(jsonDom)
		
		name    = jsonDom.getString("name")
		species = jsonDom.getString("species")
		
		selector = jsonDom.getJSONObject("selector").toMap() as Map<String, String>
		
		if (!jsonDom.has("qualifiers"))
			qualifiers = Collections.EMPTY_MAP
		else
			qualifiers = jsonDom.getJSONObject("qualifiers").toMap() as Map<String, String>
	}
}
