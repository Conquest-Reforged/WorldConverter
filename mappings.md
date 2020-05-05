## Format

Converter mappings are simple text files that read by the converter and interpreted into 'find & replace' rules.  
Each line in the mapping file should define a rule in the following format:
```
namespace:input_block_type[input_properties] -> namespace:output_block_type[output_properties]
```

Where:
- `namespace` - the name of the owner/provider of the block (ie `minecraft` or `conquest` etc)
- `input_block_type` - the 1.12 block to find (ie `planks`)
- `output_block_type` - the 1.15 block that replaces the input type
- `[properties]` - the blockstate properties to match/replace

## Examples

### Example 1 - Single Block

The simplest way to write a converter mapping is to write out every permutation 
of a given blockstate to find in the world, and the specific blockstate it should be converted into.

**Rule:**
```
minecraft:planks[variant=oak] -> minecraft:oak_planks
minecraft:planks[variant=spruce] -> minecraft:spruce_planks

```

These 2 rules find all oak & spruce variants of planks in the 1.12 world and replace them with their
1.15 flattened counterparts in the converted world.

----

### Example 2 - Implicit Properties

You only need to provide the properties that you need to match/replace.  

For example `logs[variant=<species>,axis=<axis>]` got flattened into `<species>_logs[axis=<axis>]`.

We're not interested in changing the axis property so we can leave it out of our conversion rule.
When we do this it is implied that the property should not be altered.

**Rule:**
```
minecraft:log[variant=oak] -> minecraft:oak_log
minecraft:log[variant=spruce] -> minecraft:spruce_log
```

**Conversions:**
```
minecraft:log[axis=x,variant=oak] -> minecraft:oak_log[axis=x]
minecraft:log[axis=y,variant=oak] -> minecraft:oak_log[axis=y]
minecraft:log[axis=z,variant=oak] -> minecraft:oak_log[axis=z]
minecraft:log[axis=none,variant=oak] -> minecraft:oak_wood[axis=y]
minecraft:log[axis=x,variant=spruce] -> minecraft:spruce_log[axis=x]
minecraft:log[axis=y,variant=spruce] -> minecraft:spruce_log[axis=y]
minecraft:log[axis=z,variant=spruce] -> minecraft:spruce_log[axis=z]
minecraft:log[axis=none,variant=spruce] -> minecraft:spruce_wood[axis=y]
```

Not bad. For just 2 lines of mapping rules we can convert 8 blockstate permutations 👍

**Notes**  
- 1.15 does not have a value `none` for the `axis` property so the output falls back to the default value of `y`
- any properties that exist on 1.12 that do not exist in 1.15 are omitted from the output blockstate
- any properties that exist on 1.15 and not on 1.12 will be set to its default value if not otherwise specified in the conversion rule
