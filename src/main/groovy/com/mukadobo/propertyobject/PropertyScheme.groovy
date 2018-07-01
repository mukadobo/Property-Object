package com.mukadobo.propertyobject

class PropertyScheme
{
    static private Map<String, PropertyScheme> registry = new LinkedHashMap<>()

    private final Map<String, Map<String, PropertyDefinition>> declarations = new LinkedHashMap<>()

    PropertyScheme(Map<String, PropertyDefinition> declarations)
    {
        this.declarations.putAll(declarations)
   }

    static void register(String kind, Map<String, PropertyDefinition> declarations)
    {
        if (registry.get(kind))
        {
            throw new IllegalArgumentException("kind already registered: $kind")
        }

        registry.put(kind, declarations)
    }

    // - - - -

    static class Metadata
    {
        static final String KIND    = "kind"
        static final String VERSION = "version"
    }

}
