package com.mukadobo.version

import org.jetbrains.annotations.NotNull

class VersionChain implements Comparable<VersionChain>
{
    public static final VersionChain ZERO = new VersionChain([0])
    public static final VersionChain MAX  = new VersionChain([Integer.MAX_VALUE])

    private final Integer[] chain
    private final String    text

    VersionChain(String text)
    {
        this(text.tokenize(".")*.toInteger())
    }

    VersionChain(List<Integer> list)
    {
        this(list.toArray() as Integer[])
    }

    VersionChain(Integer[] array)
    {
        if (array.length == 0) throw new IllegalArgumentException("zero-length array")

        chain = array.clone()

        text  = chain*.toString().join(".")
    }

    int    length()    { chain.length }
    int    at(int i)   { chain[i] }
    String toString()  { text }

    @Override
    int compareTo(@NotNull VersionChain that)
    {
        if (this.is(that)) return 0

        int shortLength = Math.min(this.chain.length, that.chain.length)

        for (idx in (0 ..< shortLength))
        {
            int sense = this.chain[idx] - that.chain[idx]
            if (sense != 0) return sense
        }

        for (idx in (shortLength ..< this.chain.length))
        {
            int sense = this.chain[idx] - 0
            if (sense != 0) return sense
        }

        for (idx in (shortLength ..< that.chain.length))
        {
            int sense = 0 - that.chain[idx]
            if (sense != 0) return sense
        }

        0
    }
}
