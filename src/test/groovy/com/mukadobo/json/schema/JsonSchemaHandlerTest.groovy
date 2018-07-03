package com.mukadobo.json.schema

import groovy.json.JsonBuilder
import org.everit.json.schema.ReferenceSchema
import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

/**
 *
 */
class JsonSchemaHandlerTest
{
	// c.f. http://json-schema.org/learn/file-system.html

	static void main (String[] args)
	{
		JsonSchemaUrl.registerHandlerPackage()

		String fstabRootSchemaJsonText = '''\
		{
			"$id": "AD-HOC",
			"$schema": "http://json-schema.org/draft-07/schema#",
			"$ref": "jsonschema:jsonschema/Fstab-schema.json"
		}
		'''.stripIndent()

		String sampleFstabJsonText = '''\
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

		JSONObject fstabRootSchemaJsonDom = new JSONObject(new JSONTokener(fstabRootSchemaJsonText))
		println "fstabRootSchemaJsonDom: ${new JsonBuilder(fstabRootSchemaJsonDom.toMap()).toPrettyString()}"
		println ""

		SchemaLoader fstabRootSchemaLoader = SchemaLoader
				.builder()
				.draftV6Support()
				.schemaJson(fstabRootSchemaJsonDom)
				.build()

		Schema fstabRootSchema = fstabRootSchemaLoader.load().build() as Schema

		println "fstabRootSchema: $fstabRootSchema"
		println ""

		try {
			fstabRootSchema.validate(new JSONObject(sampleFstabJsonText))
		}
		catch (ValidationException e)
		{
			System.err.println(e.getMessage())

			if (! e.causingExceptions.isEmpty()) {
				int limit = 3

				if (e.causingExceptions.size() > limit)
					System.err.println "  (only showing first 3 violations)"

				e.getCausingExceptions().stream().limit(limit).each {
					System.err.println "  " + it.getMessage()
				}
			}

			System.err.println ""
		}
	}
}
