package com.mukadobo.json.schema

import org.everit.json.schema.ReferenceSchema
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.everit.json.schema.loader.SchemaLoader.SchemaLoaderBuilder
import org.json.JSONObject
import org.json.JSONTokener

class SchemaRegistry
{
    private static Map<String, ReferenceSchema.Builder> refSchemaMap = new LinkedHashMap<>()

    private static class MyLoaderBuilder extends SchemaLoaderBuilder
    {
        MyLoaderBuilder setPointerSchemas(Map<String, ReferenceSchema.Builder> schemaMap)
        {
            this.pointerSchemas(schemaMap)
            return this
        }
    }

    SchemaRegistry addSchema(String id, String jsonText)
    {
        addSchema(id, JSONTokener(jsonText))
    }

    SchemaRegistry addSchema(String id, JSONObject jsonDom)
    {
        if (refSchemaMap.containsKey(id))
        {
            throw new IllegalArgumentException("duplicate id: $id")
        }

        Schema entrySchema = SchemaLoader
                .builder()
                .draftV7Support()
                .setPointerSchemas(pointerSchemas)
                .schemaJson(jsonDom)
                .build()
                .load()
                .build()

        ReferenceSchema.Builder refBuilder = ReferenceSchema
                .builder()
                .refValue(id)

        refBuilder.build().setReferredSchema(entrySchema)

        refSchemaMap.put(id, refBuilder)

        this
    }
}
