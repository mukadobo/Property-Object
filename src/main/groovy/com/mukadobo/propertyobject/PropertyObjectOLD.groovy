package com.mukadobo.propertyobject

import org.jetbrains.annotations.NotNull

import javax.naming.OperationNotSupportedException

interface PropertyObjectOLD extends PropertyNode, Map<String,PropertyNode>
{
    final PropertyScheme scheme;

    static class Base implements PropertyObjectOLD
    {
        private final Map<String,PropertyNode> props = new LinkedHashMap<>()

        protected Base(Map<String, Object> values)
        {
            values.each {
                props.put(it.key, it.value)
            }
        }

        @Override
        String getKind()
        {
            props.get(PropertyScheme.Metadata.KIND)
        }

        @Override
        Integer[] getVersion()
        {
            props.get(PropertyScheme.Metadata.VERSION)
        }

        @Override
        Object getValue()
        {
            return null
        }

        // pass-through to underlying properties map

        @Override
        int size()
        {
            return props.size()
        }

        @Override
        boolean isEmpty()
        {
            return props.isEmpty()
        }

        @Override
        boolean containsKey(Object o)
        {
            return props.containsKey(o)
        }

        @Override
        boolean containsValue(Object o)
        {
            return props.containsValue(o)
        }

        @Override
        PropertyNode get(Object o)
        {
            return null
        }

        // underlying properties map is immutable

        @Override
        PropertyNode put(String s, PropertyNode propertyNode)
        {
            throw new OperationNotSupportedException()
        }

        @Override
        PropertyNode remove(Object o)
        {
            throw new OperationNotSupportedException()
        }

        @Override
        void putAll(@NotNull Map<? extends String, ? extends PropertyNode> map)
        {
            throw new OperationNotSupportedException()
        }

        @Override
        void clear()
        {
            throw new OperationNotSupportedException()
        }

        @Override
        Set<String> keySet()
        {
            throw new OperationNotSupportedException()
        }

        @Override
        Collection<PropertyNode> values()
        {
            throw new OperationNotSupportedException()
        }

        @Override
        Set<Map.Entry<String, PropertyNode>> entrySet()
        {
            throw new OperationNotSupportedException()
        }
    }
}