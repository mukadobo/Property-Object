package com.mukadobo.json.schema.poc.command

import com.mukadobo.propertyobject.KindAndVersion
import groovy.transform.EqualsAndHashCode
import org.json.JSONObject

@EqualsAndHashCode
class Command extends KindAndVersion.Base
{
	Boolean dryrun
	String  verbosity
	
	Subject   subject
	Predicate predicate
	
	Command(JSONObject jsonDom)
	{
		super(jsonDom)
		
		dryrun    = jsonDom.dryrun
		verbosity = jsonDom.verbosity
		
		subject   = new Subject  (jsonDom.subject  )
		predicate = new Predicate(jsonDom.predicate)
	}
}
