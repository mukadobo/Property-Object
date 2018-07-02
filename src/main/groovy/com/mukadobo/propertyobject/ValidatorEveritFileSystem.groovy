package com.mukadobo.propertyobject

import groovy.json.JsonBuilder
import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

class ValidatorEveritFileSystem
{
	// c.f. http://json-schema.org/learn/file-system.html

	static void main (String[] args)
	{
		String jsonText = '''\
		{
		  "/": {
			"storage": {
			  "type": "disk",
			  "device": "/dev/sda1"
			},
			"fstype": "btrfs",
			"readonly": true
		  },
		  "/var": {
			"storage": {
			  "type": "disk",
			  "label": "8f3ba6f4-5c70-46ec-83af-0d5434953e5f"
			},
			"fstype": "ext4",
			"options": [ "nosuid" ]
		  },
		  "/tmp": {
			"storage": {
			  "type": "tmpfs",
			  "sizeInMB": 64
			}
		  },
		  "/var/www": {
			"storage": {
			  "type": "nfs",
			  "server": "my.nfs.server",
			  "remotePath": "/exports/my-path"
			}
		  }
		}
		'''.stripIndent()

		getClass().getResourceAsStream("/example-fstab+schema.json").withCloseable {
			println "productJsonText: $jsonText"
			println ""

			JSONObject rawSchema = new JSONObject(new JSONTokener(it))
			println "rawSchema: ${new JsonBuilder(rawSchema.toMap()).toPrettyString()})"
			println ""

			Schema schema = SchemaLoader.load(rawSchema)
			println "schema: $schema"
			println ""

			try {
				schema.validate(new JSONObject(jsonText)) // throws a ValidationException if this object is invalid
			}
			catch (ValidationException e)
			{
				println(e.getMessage())
				if (e.causingExceptions.size() > 3)
					println "  (only showing first 3 violations)"

				e.getCausingExceptions().stream().limit(3).each {
					println "  " + it.getMessage()
				}
				println ""
			}
		}
	}
}
