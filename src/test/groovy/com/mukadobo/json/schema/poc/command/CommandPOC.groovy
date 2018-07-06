package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.JsonSchema
import com.mukadobo.propertyobject.KindAndVersion
import com.mukadobo.propertyobject.KindAndVersion.Base
import groovy.json.JsonOutput
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Specification

class CommandPOC extends Specification
{
	static String simpleSubjectJsonText =  '''\
	{
		"kind"    : "Subject",
		"version" : " 0 . 02 . 00 . 0",
		
		"selector" : {
			"scheme" : "https",
			"server" : "example.com",
			"port"   : "80"
		}
	}
	'''.stripIndent()
	
	static String simplePredicateJsonText =  '''\
	{
		"kind"    : "Predicate",
		"version" : "0",
		
		"verb"    : "report"
	}
	'''.stripIndent()
	
	static String simpleCommandJsonText =  '''\
	{
		"kind"      : "Command",
		"version"   : "0.1.0",
		
		"verbosity" : "HIGH",
		"dryrun"    : true,
		
		"payload"   : {
			"subject"   : ''' + simpleSubjectJsonText   + ''',
			"predicate" : ''' + simplePredicateJsonText + ''',
		}
	}
	'''.stripIndent()
	
	static JSONObject simpleSubjectJsonDom   = new JSONObject(new JSONTokener(simpleSubjectJsonText))
	static JSONObject simplePredicateJsonDom = new JSONObject(new JSONTokener(simplePredicateJsonText))
	static JSONObject simpleCommandJsonDom   = new JSONObject(new JSONTokener(simpleCommandJsonText))
	
	def "JSON validation"()
	{
		expect:
			
			def schema = new JsonSchema(pogoClass)
			schema.validate(jsonText) == null

		where:
			
			pogoClass       | jsonText
			Subject  .class | simpleSubjectJsonDom
			Predicate.class | simplePredicateJsonDom
			Command  .class | simpleCommandJsonDom
	}
	
	def "POGO from JSON ~ Subject"()
	{
		when:
			
			Subject probject = new Subject(simpleSubjectJsonDom)
		
		then:
			
			probject.kind == "Subject"
	}
	
	def "POGO from JSON ~ Predicate"()
	{
		when:
			
			Predicate probject = new Predicate(simplePredicateJsonDom)
		
		then:
			
			probject.kind == "Predicate"
	}
	
	def "POGO from JSON ~ Command"()
	{
		when:
			
			Command probject = new Command(simpleCommandJsonDom)
		
		then:
			
			probject.kind == "Command"
	}
	
	def "JSON round-trip"()
	{
		when:
		
			JSONObject firstDom  = new JSONObject(new JSONTokener(refText))
			Base       firstObj  = refClass.newInstance(firstDom)
		
		then:
			
			refClass.getSimpleName() == firstObj.getKind()
			
		when:
			
			String     dupeText  = JsonOutput.toJson(firstObj)
			JSONObject dupeDom   = new JSONObject(new JSONTokener(dupeText))
			
		then:
		
			def schema = new JsonSchema(refClass)
			schema.validate(dupeDom) == null
		
		when:
			
			Base dupeObj = refClass.newInstance(dupeDom)
		
		then:
			
			firstObj == dupeObj
			JsonOutput.toJson(firstObj) == JsonOutput.toJson(dupeObj)
			
		where:
			
			refClass        | refText
			Predicate.class | simplePredicateJsonText
			Subject  .class | simpleSubjectJsonText
			Command  .class | simpleCommandJsonText
	}
	
}
