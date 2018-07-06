package com.mukadobo.json.schema.poc.command

import com.mukadobo.propertyobject.KindAndVersion
import groovy.json.JsonOutput
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

@EqualsAndHashCode
class Command extends KindAndVersion.Base
{
	Boolean dryrun
	String  verbosity
	
	Map<String, KindAndVersion.Base> payload = new LinkedHashMap<>()
	
	Command(JSONObject jsonDom)
	{
		super(jsonDom)
		
		dryrun    = jsonDom.dryrun
		verbosity = jsonDom.verbosity
		
		JSONObject payloadDom = jsonDom.getJSONObject("payload")
		
		payloadDom.keySet().each {
			switch (it)
			{
				case "subject"  : payload.put(it, new Subject  (payloadDom.getJSONObject(it))); break
				case "predicate": payload.put(it, new Predicate(payloadDom.getJSONObject(it))); break
				
				default: throw new RuntimeException("unsupported payload key in jsonDom: '$it'")
			}
		}
	}
	
}
