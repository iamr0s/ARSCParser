# ARSCParser

Language: [中文](./README.md)

ARSCParser can parse an Android app's resources.arsc file and extract useful, actionable information about its contents.

ARSCParser can parse Sparse Entry,
[ResourceTypes.h#1384](http://androidxref.com/8.0.0_r4/xref/frameworks/base/libs/androidfw/include/androidfw/ResourceTypes.h#1384), [ResourceTypes.h#1411](http://androidxref.com/8.0.0_r4/xref/frameworks/base/libs/androidfw/include/androidfw/ResourceTypes.h#1411)

```c
struct ResTable_type
{
    struct ResChunk_header header;

    enum {
        NO_ENTRY = 0xFFFFFFFF
    };

    // The type identifier this chunk is holding.  Type IDs start
    // at 1 (corresponding to the value of the type bits in a
    // resource identifier).  0 is invalid.
    uint8_t id;

    enum {
        // If set, the entry is sparse, and encodes both the entry ID and offset into each entry,
        // and a binary search is used to find the key. Only available on platforms >= O.
        // Mark any types that use this with a v26 qualifier to prevent runtime issues on older
        // platforms.
        FLAG_SPARSE = 0x01,
    };
    uint8_t flags;

    // Must be 0.
    uint16_t reserved;

    // Number of uint32_t entry indices that follow.
    uint32_t entryCount;

    // Offset from header where ResTable_entry data starts.
    uint32_t entriesStart;

    // Configuration this collection of entries is designed for. This must always be last.
    ResTable_config config;
};

```

```c
/**
 * An entry in a ResTable_type with the flag `FLAG_SPARSE` set.
 */
union ResTable_sparseTypeEntry {
    // Holds the raw uint32_t encoded value. Do not read this.
    uint32_t entry;
    struct {
        // The index of the entry.
        uint16_t idx;

        // The offset from ResTable_type::entriesStart, divided by 4.
        uint16_t offset;
    };
};
```
