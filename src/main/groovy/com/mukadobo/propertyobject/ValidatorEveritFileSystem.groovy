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
		String fstabCompleteSchema = '''\
		{
		  "$id": "http://example.com/fstab",
		  "$schema": "http://json-schema.org/draft-07/schema#",
		  "type": "object",
		  "required": [ "/" ],
		  "properties": {
			"/": { "$ref": "http://example.com/entry-schema-qqq" }
		  },
		  "patternProperties": {
			"^(/[^/]+)+$":  { "$ref": "http://example.com/entry-schema-qqq" }
		  },
		  "additionalProperties": false,
		  "definitions": {
			"fstab-entry": {
			  "$id": "http://example.com/entry-schema-qqq",
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
					  "$ref": "#/definitions/fstab-entry/definitions/diskDevice"
					},
					{
					  "$ref": "#/definitions/fstab-entry/definitions/diskUUID"
					},
					{
					  "$ref": "#/definitions/fstab-entry/definitions/nfs"
					},
					{
					  "$ref": "#/definitions/fstab-entry/definitions/tmpfs"
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
		  }
		}
		'''.stripIndent()
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

//			JSONObject rawSchema = new JSONObject(new JSONTokener(it))
			JSONObject rawSchema = new JSONObject(new JSONTokener(fstabCompleteSchema))

			println "rawSchema: ${new JsonBuilder(rawSchema.toMap()).toPrettyString()})"
			println ""

//			Schema schema = SchemaLoader.load(rawSchema)
			SchemaLoader loader = SchemaLoader.builder()
				.draftV7Support()
				.schemaJson(rawSchema)
				.build()

			Schema schema = loader.load().build()

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
