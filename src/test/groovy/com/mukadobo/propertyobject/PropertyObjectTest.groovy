package com.mukadobo.propertyobject

import com.mukadobo.json.schema.Command
import com.mukadobo.json.schema.JsonSchemaUrl
import org.everit.json.schema.ObjectSchema
import org.everit.json.schema.ValidationException
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Specification

class PropertyObjectTest extends Specification
{
	def "YAML to JSON - all value primitives"()
	{
		expect:

			PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:

			yamlText  | jsonText
			'a: A'    | '{"a" : "A"}'
			'a: 0'    | '{"a" : 0}'
			'a: true' | '{"a" : true}'
			'a: 2.7'  | '{"a" : 2.7}'
			'a: null' | '{"a" : null}'
	}

	def "YAML to JSON - Simple Lists"()
	{
		expect:

			PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:

			yamlText     | jsonText
			'[]'         | '[]'
			'["a"]'      | '["a"]'
			'[1]'        | '[1]'
			'[null]'     | '[null]'
			'[k: null]'  | '[{"k" : null}]'
			'[0,1,2,3]'  | '[0,1,2,3]'
	}

	def "YAML to JSON - Nested Lists"()
	{
		expect:

			PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:

			yamlText         | jsonText
			'[[]]'           | '[[]]'
			'[[0]]'          | '[[0]]'
			'[[0,1]]'        | '[[0,1]]'
			'[[0,1],[],[2]]' | '[[0,1],[],[2]]'
	}

	def "YAML to JSON - Simple Maps"()
	{
		expect:

			PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:

			yamlText     | jsonText
			'{}'         | '{}'
			'{a}'        | '{"a" : null}'
			'{a: }'      | '{"a" : null}'
			'{a: null}'  | '{"a" : null}'
			'{a: "A"}'   | '{"a" : "A"}'
			'{a: 0}'     | '{"a" : 0}'
			'{a: 2.7}'   | '{"a" : 2.7}'
	}

	def "YAML to JSON - Nested Maps"()
	{
		expect:

			PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:

			yamlText                       | jsonText
			'{a0: {}}'                     | '{"a0" : {}}'
			'{a0: {}, b0:{}}'              | '{"a0" : {}, "b0" : {}}'
			'{a0: {a1: A1}, b0:{b1: B1}}'  | '{"a0" : {"a1" : "A1"}, "b0" : {"b1" : "B1"}}'
	}

	def "Base properties (eg. kind, version)"()
	{
		setup:
			
			String commandJsonText = '''\
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
		
		when:
			
			JSONObject commandJsonDom = new JSONObject(new JSONTokener(commandJsonText))
			
			ObjectSchema commandSchema = JsonSchemaUrl.loadSchema(Command.class, false)
		
		then:
			
			try {
				commandSchema.validate(commandJsonDom)
			}
			catch (ValidationException e)
			{
				throw new RuntimeException("schema validation error: schema.id='${commandSchema.getId()}':\n${e.toJSON().toString(2)}", e)
			}
	}
	
}