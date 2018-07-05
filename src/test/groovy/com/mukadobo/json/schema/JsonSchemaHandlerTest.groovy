package com.mukadobo.json.schema

import org.apache.commons.io.IOUtils
import org.everit.json.schema.ObjectSchema
import org.everit.json.schema.Schema
import spock.lang.Specification

/**
 *
 */
class JsonSchemaHandlerTest extends Specification
{
	def "Class-based JSON-Schema URL ~ all args"()
	{
		expect:
			JsonSchemaUrl.toUrlText(refClass, canonical, prefix).toString() == urlText

		where:
			refClass    | canonical | prefix | urlText
			Outer.class | true      | null   | 'jsonschema:com/mukadobo/json/schema/Outer'
			Outer.class | false     | null   | 'jsonschema:Outer'
			Outer.class | false     | ""     | 'jsonschema:Outer'
			Outer.class | false     | 'pfx'  | 'jsonschema:pfx/Outer'
			Outer.class | false     | '/pfx' | 'jsonschema:pfx/Outer'
	}

	def "Class-based JSON-Schema URL ~ default: prefix"()
	{
		expect:
			JsonSchemaUrl.toUrlText(refClass, canonical).toString() == urlText

		where:
			refClass    | canonical | urlText
			Outer.class | true      | 'jsonschema:com/mukadobo/json/schema/Outer'
			Outer.class | false     | 'jsonschema:jsonschema/Outer'
	}

	def "Class-based JSON-Schema URL ~ default: canonical"()
	{
		expect:
			JsonSchemaUrl.toUrlText(refClass, canonical).toString() == urlText

		where:
			refClass                  | canonical | urlText
			Outer              .class | false     | 'jsonschema:jsonschema/Outer'
			Outer              .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer'
	}

	def "Class-based JSON-Schema URL ~ default: nested classes"()
	{
		expect:
			JsonSchemaUrl.toUrlText(refClass, canonical).toString() == urlText

		where:
			refClass                  | canonical | urlText
			Outer              .class | false     | 'jsonschema:jsonschema/Outer'
			Outer.Static       .class | false     | 'jsonschema:jsonschema/Outer.Static'
			Outer.Inner        .class | false     | 'jsonschema:jsonschema/Outer.Inner'
			Outer.Inner.Static .class | false     | 'jsonschema:jsonschema/Outer.Inner.Static'
			Outer              .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer'
			Outer.Static       .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer.Static'
			Outer.Inner        .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer.Inner'
			Outer.Inner.Static .class | true      | 'jsonschema:com/mukadobo/json/schema/Outer.Inner.Static'
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

			fstabRootSchema.getId() == 'jsonschema:jsonschema/Fstab'
			propertySchemas != null
			propertySchemas.size() == 1
			propertySchemas.entrySet().first().getValue().getReferredSchema().getId() == "http://example.com/entry-schema"
	}
}
