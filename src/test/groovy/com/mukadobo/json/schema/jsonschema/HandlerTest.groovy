package com.mukadobo.json.schema.jsonschema

import com.mukadobo.json.schema.EmptyResource
import com.mukadobo.json.schema.Fstab
import com.mukadobo.json.schema.JsonSchema
import com.mukadobo.json.schema.JsonSchemaUrl
import com.mukadobo.json.schema.SimpleResource
import org.apache.commons.io.IOUtils
import org.everit.json.schema.Schema
import spock.lang.Specification

/**
 *
 */
class HandlerTest extends Specification
{
	def "getContent"()
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
			refClass             | expectedData
			EmptyResource.class  | ''
			SimpleResource.class | '{"test":"SimpleResource"}'
	}

	def "Indirect Use via JsonSchema loading"()
	{
		setup:

			JsonSchema          topSchema  = new JsonSchema(Fstab.class)
			Map<String, Schema> subSchemas = topSchema.getPropertySchemas()

		expect:
			
			topSchema.getId() == 'jsonschema:jsonschema/Fstab'
		
			subSchemas != null
			subSchemas.size() == 1
			subSchemas.entrySet().first().getValue().getReferredSchema().getId() == "http://example.com/entry-schema"
	}
}
