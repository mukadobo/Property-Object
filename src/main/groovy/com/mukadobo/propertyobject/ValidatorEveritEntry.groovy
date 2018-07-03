package com.mukadobo.propertyobject

import groovy.json.JsonBuilder
import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

class ValidatorEveritEntry
{
	static void main (String[] args)
	{
		String jsonText = '''\
		{
			"storage": {
			  "type": "disk",
			  "device": "/dev/sda1"
			}
		}
		'''.stripIndent()
		getClass().getResourceAsStream("/example-entry+jsonschema.json").withCloseable {
			JSONObject rawSchema = new JSONObject(new JSONTokener(it))
			Schema schema = SchemaLoader.load(rawSchema)

			println "productJsonText: $jsonText"
			println ""
			println "rawSchema: ${new JsonBuilder(rawSchema.toMap()).toPrettyString()})"
			println ""
			println "jsonschema: $schema"
			println ""

			try {
				schema.validate(new JSONObject(jsonText)) // throws a ValidationException if this object is invalid
			}
			catch (ValidationException e)
			{
				System.err.println(e.getMessage())
				if (e.causingExceptions.size() > 3)
					System.err.println "  (only showing first 3 violations)"

				if (e.causingExceptions.size() > 0)
					e.getCausingExceptions().stream().limit(3).each {
						System.err.println "  " + it.getMessage()
					}

				println ""
			}
		}
	}
}
