package com.mukadobo.json.schema

import groovy.json.JsonBuilder
import org.apache.commons.io.IOUtils
import org.everit.json.schema.ObjectSchema
import org.everit.json.schema.ReferenceSchema
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Specification

/**
 *
 */
class JsonSchemaHandlerTest extends Specification
{
	def setupSpec()
	{
		JsonSchemaUrl.registerHandlerPackage()
	}

	def "Class-based JSON-Schema URL ~ all args"()
	{
		expect:
			JsonSchemaUrl.toUrlText(refClass, canonical, prefix).toString() == urlText

		where:
			refClass    | canonical | prefix | urlText
			Outer.class | true      | null   | 'jsonschema:com/mukadobo/json/schema/Outer-schema.json'
			Outer.class | false     | null   | 'jsonschema:Outer-schema.json'
			Outer.class | false     | ""     | 'jsonschema:Outer-schema.json'
			Outer.class | false     | 'pfx'  | 'jsonschema:pfx/Outer-schema.json'
			Outer.class | false     | '/pfx' | 'jsonschema:pfx/Outer-schema.json'
	}

	def "Class-based JSON-Schema URL ~ default: prefix"()
	{
		expect:
			JsonSchemaUrl.toUrlText(refClass, canonical).toString() == urlText

		where:
			refClass    | canonical | urlText
			Outer.class | true      | 'jsonschema:com/mukadobo/json/schema/Outer-schema.json'
			Outer.class | false     | 'jsonschema:jsonschema/Outer-schema.json'
	}

	def "Class-based JSON-Schema URL ~ default: canonical"()
	{
		expect:
			JsonSchemaUrl.toUrlText(refClass, canonical).toString() == urlText

		where:
			refClass                  | canonical | urlText
			Outer              .class | false     | 'jsonschema:jsonschema/Outer-schema.json'
			Outer              .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer-schema.json'
	}

	def "Class-based JSON-Schema URL ~ default: nested classes"()
	{
		expect:
			JsonSchemaUrl.toUrlText(refClass, canonical).toString() == urlText

		where:
			refClass                  | canonical | urlText
			Outer              .class | false     | 'jsonschema:jsonschema/Outer-schema.json'
			Outer.Static       .class | false     | 'jsonschema:jsonschema/Outer.Static-schema.json'
			Outer.Inner        .class | false     | 'jsonschema:jsonschema/Outer.Inner-schema.json'
			Outer.Inner.Static .class | false     | 'jsonschema:jsonschema/Outer.Inner.Static-schema.json'
			Outer              .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer-schema.json'
			Outer.Static       .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer.Static-schema.json'
			Outer.Inner        .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer.Inner-schema.json'
			Outer.Inner.Static .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer.Inner.Static-schema.json'
	}

	def "Handler ~ getContent()"()
	{
		when:
			InputStream urlStream = (InputStream) JsonSchemaUrl.toUrl(refClass).getContent()

		then:
			urlStream != null

		when:
			String urlData = IOUtils.toString(urlStream, "UTF-8")

		then:
			urlData == expectedData

		where:
			refClass                  | expectedData
			EmptyResource      .class | ''
			SimpleResource     .class | '{"test":"SimpleResource"}'
	}

	def "Load schema from classpath - URL variant 0"()
	{
		setup:

			ObjectSchema        fstabRootSchema = JsonSchemaUrl.loadSchema(Fstab.class)
			Map<String, Schema> propertySchemas = fstabRootSchema.getPropertySchemas()


		expect:

			fstabRootSchema.getId() == 'jsonschema:jsonschema/Fstab-schema.json'
			propertySchemas != null
			propertySchemas.size() == 1
			propertySchemas.entrySet().first().getValue().getReferredSchema().getId() == "http://example.com/entry-schema"
	}
}
