package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.JsonSchema
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Specification

class CommandPOC extends Specification
{
	String simpleCommandJsonText =  '''\
	{
		"kind"      : "Command",
		"version"   : "0.1.0",
		
		"verbosity" : "HIGH",
		"dryrun"    : true,
		
		"payload"   : {
			"subject" : {
				"kind"    : "Subject",
				"version" : " 0 . 02 . 00 . 0",
				
				"selector" : {
					"scheme" : "https",
					"server" : "example.com",
					"port"   : "80"
				}
			},
			"predicate" : {
				"kind"    : "Predicate",
				"version" : "0",
				
				"verb"    : "report"
			}
		}
	}
	'''.stripIndent()
	
	JSONObject simpleCommandJsonDom = new JSONObject(new JSONTokener(simpleCommandJsonText))
	
	def "JSON validation ~ simple command"()
	{
		when:
			
			new JsonSchema(Command.class).validate(simpleCommandJsonDom)

		then:
			
			notThrown(JsonSchema.NonconformanceException)
	}
}
