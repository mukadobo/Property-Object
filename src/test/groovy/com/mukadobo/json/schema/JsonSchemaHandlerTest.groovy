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
		String jphpGiven = System.getProperty('java.protocol.handler.pkgs')
		String jphpPlus  = (jphpGiven ? "$jphpGiven|" : "") + 'com.mukadobo.json.schema'

		System.setProperty('java.protocol.handler.pkgs', jphpPlus)

		println "System.getProperty('java.protocol.handler.pkgs') = ${System.getProperty('java.protocol.handler.pkgs')}"

		String fstabEntrySchemaId = """jsonschema:jsonschema/FstabEntry-schema.json?src=aaa&src=foo://h:p/p/p?q=q#foobar""" ; """
			?tag=3.14!6.674.*
			&variant=2.71
			&src=@
			&src=http://google.com/json/jsonschema/foo.json?token=0xbab0face%26foo=bar
			&src=tiznut
			""".replaceAll(/\s/, "")

		String fstabRootSchemaJsonText = '''\
		{
			"$id": "fstab",
			"$jsonschema": "http://json-jsonschema.org/draft-07/jsonschema#",
			"type": "object",
			"required": [
				"/"
			],
			"properties": {
				"/": {
					"$ref": ''' + "\"${fstabEntrySchemaId}\"" \
					+ '''
				}
			},
			"patternProperties": {
				"^(/[^/]+)+$": {
					"$ref": ''' + "\"${fstabEntrySchemaId}\"" \
					+ '''
				}
			},
			"additionalProperties": false,
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


//		SchemaLoader.SchemaLoaderBuilder fstabRootSchemaLoaderBuilder = SchemaLoader
//				.builder()
//				.draftV7Support()
//
//
//				.pointerSchema
//				.schemaJson(fstabRootSchemaJsonDom)
//				.build()
//
//		Schema fstabRootSchema = fstabRootSchemaLoader.load().build()

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
