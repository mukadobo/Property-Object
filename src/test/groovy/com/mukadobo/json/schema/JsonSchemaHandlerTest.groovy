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


		String fstabEntrySchemaId = """jsonschema:jsonschema/fstab-entry+schema.json
			?tag=3.14!6.674.*
			&variant=2.71
			&src=@
			&src=http://google.com/json/schema/foo.json?token=0xbab0face%26foo=bar
			&src=tiznut
			""".replaceAll(/\s/, "")

		String fstabRootSchemaJsonText = '''\
		{
			"$id": "fstab",
			"$schema": "http://json-schema.org/draft-07/schema#",
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

		String fstabEntrySchemaJsonText = '''\
		{
			"$id": ''' + "\"${fstabEntrySchemaId}QQQ\"" \
			+ ''',
			"$schema": "http://json-schema.org/draft-07/schema#",
			"description": "JSON Schema for an fstab entry",
			"type": "object",
			"required": [
				"storage"
			],
			"properties": {
				"storage": {
					"type": "object",
					"oneOf": [
						{
							"$ref": "#/definitions/diskDevice"
						},
						{
							"$ref": "#/definitions/diskUUID"
						},
						{
							"$ref": "#/definitions/nfs"
						},
						{
							"$ref": "#/definitions/tmpfs"
						}
					]
				},
				"fstype": {
					"enum": [
						"ext3",
						"ext4",
						"btrfs"
					]
				},
				"options": {
					"type": "array",
					"minItems": 1,
					"items": {
						"type": "string"
					},
					"uniqueItems": true
				},
				"readonly": {
					"type": "boolean"
				}
			},
			"definitions": {
				"diskDevice": {
					"properties": {
						"type": {
							"enum": [
								"disk"
							]
						},
						"device": {
							"type": "string",
							"pattern": "^/dev/[^/]+(/[^/]+)*$"
						}
					},
					"required": [
						"type",
						"device"
					],
					"additionalProperties": false
				},
				"diskUUID": {
					"properties": {
						"type": {
							"enum": [
								"disk"
							]
						},
						"label": {
							"type": "string",
							"pattern": "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$"
						}
					},
					"required": [
						"type",
						"label"
					],
					"additionalProperties": false
				},
				"nfs": {
					"properties": {
						"type": {
							"enum": [
								"nfs"
							]
						},
						"remotePath": {
							"type": "string",
							"pattern": "^(/[^/]+)+$"
						},
						"server": {
							"type": "string",
							"oneOf": [
								{
									"format": "hostname"
								},
								{
									"format": "ipv4"
								},
								{
									"format": "ipv6"
								}
							]
						}
					},
					"required": [
						"type",
						"server",
						"remotePath"
					],
					"additionalProperties": false
				},
				"tmpfs": {
					"properties": {
						"type": {
							"enum": [
								"tmpfs"
							]
						},
						"sizeInMB": {
							"type": "integer",
							"minimum": 16,
							"maximum": 512
						}
					},
					"required": [
						"type",
						"sizeInMB"
					],
					"additionalProperties": false
				}
			}
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

//		getClass().getResourceAsStream("/example-fstab+schema.json").withCloseable {
//			JSONObject rawSchema = new JSONObject(new JSONTokener(it))
//		}

		JSONObject fstabEntrySchemaJsonDom = new JSONObject(new JSONTokener(fstabEntrySchemaJsonText))
		println "fstabEntrySchemaJsonDom: ${new JsonBuilder(fstabEntrySchemaJsonDom.toMap()).toPrettyString()}"
		println ""

		Schema fstabEntrySchema = SchemaLoader.load(fstabEntrySchemaJsonDom)

		println "fstabEntrySchema: $fstabEntrySchema"
		println ""

		ReferenceSchema.Builder fstabRefSchemaBuilder = ReferenceSchema
				.builder()
				.refValue(fstabEntrySchemaId)

		fstabRefSchemaBuilder.build().setReferredSchema(fstabEntrySchema)

		Map<String, ReferenceSchema.Builder> schemaPointerMap = [
				(fstabEntrySchema.getId()) : fstabRefSchemaBuilder,
		]

		// -----

		JSONObject fstabRootSchemaJsonDom = new JSONObject(new JSONTokener(fstabRootSchemaJsonText))
		println "fstabRootSchemaJsonDom: ${new JsonBuilder(fstabRootSchemaJsonDom.toMap()).toPrettyString()}"
		println ""

		SchemaLoader fstabRootSchemaLoader = SchemaLoader
				.builder()
				.draftV6Support()
				.pointerSchemas(schemaPointerMap)
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
