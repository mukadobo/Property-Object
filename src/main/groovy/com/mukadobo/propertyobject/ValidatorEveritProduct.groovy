package com.mukadobo.propertyobject

import groovy.json.JsonBuilder
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

class ValidatorEveritProduct
{
	static void main (String[] args)
	{
		String productJsonText = '''\
		{
		  "productId": 1,
		  "productName": "A green door",
		  "price": 12.50,
		  "tags": [ "home", "green" ]
		}
		'''.stripIndent()
		getClass().getResourceAsStream("/example-product+jsonschema.json").withCloseable {
			JSONObject rawSchema = new JSONObject(new JSONTokener(it))
			Schema schema = SchemaLoader.load(rawSchema)

			println "productJsonText: $productJsonText"
			println ""
			println "rawSchema: ${new JsonBuilder(rawSchema.toMap()).toPrettyString()})"
			println ""
			println "jsonschema: $schema"
			println ""

			schema.validate(new JSONObject(productJsonText)) // throws a ValidationException if this object is invalid
		}
	}
}
