## Format

Converter mappings are simple text files that are read by the converter and interpreted into 'find & replace' rules.  
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

### Example 1 - Explicit Blockstates

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

----

### Example 3 - Extended Blockstates

Extended BlockStates are BlockStates whose information is wholly or partially determined by information outside of the standard
blockstate properties. This information might include neighbour blocks or tile entity data. Extended states require additional
code added to the converter in order to gather the extra information for the blockstate.

An example of is the [BedExtender](https://github.com/Conquest-Reforged/WorldConverter/blob/master/ConverterCore/src/main/java/me/dags/converter/block/extender/BedExtender.java)  which pulls the bed's color from it's tile entity and adds it to the blockstate's property map - this color property can then be 
referenced in mappings using the '#color' property ('#' signifying the property is an extended one).

BlockStateExtenders are provided the game version class [as can be seen here](https://github.com/Conquest-Reforged/WorldConverter/blob/master/ConverterCore/src/main/java/me/dags/converter/version/versions/V1_12.java#L25).

**Rule:**
```
minecraft:bed[#color=white] -> minecraft:white_bed
```

**Conversions:**
```
minecraft:bed[#color=white,facing=north,occupied=true,part=head] -> minecraft:white_bed[facing=north,occupied=true,part=head]
minecraft:bed[#color=white,facing=north,occupied=true,part=foot] -> minecraft:white_bed[facing=north,occupied=true,part=foot]
minecraft:bed[#color=white,facing=north,occupied=false,part=head] -> minecraft:white_bed[facing=north,occupied=false,part=head]
minecraft:bed[#color=white,facing=north,occupied=false,part=foot] -> minecraft:white_bed[facing=north,occupied=false,part=foot]
minecraft:bed[#color=white,facing=south,occupied=true,part=head] -> minecraft:white_bed[facing=south,occupied=true,part=head]
minecraft:bed[#color=white,facing=south,occupied=true,part=foot] -> minecraft:white_bed[facing=south,occupied=true,part=foot]
minecraft:bed[#color=white,facing=south,occupied=false,part=head] -> minecraft:white_bed[facing=south,occupied=false,part=head]
minecraft:bed[#color=white,facing=south,occupied=false,part=foot] -> minecraft:white_bed[facing=south,occupied=false,part=foot]
minecraft:bed[#color=white,facing=west,occupied=true,part=head] -> minecraft:white_bed[facing=west,occupied=true,part=head]
minecraft:bed[#color=white,facing=west,occupied=true,part=foot] -> minecraft:white_bed[facing=west,occupied=true,part=foot]
minecraft:bed[#color=white,facing=west,occupied=false,part=head] -> minecraft:white_bed[facing=west,occupied=false,part=head]
minecraft:bed[#color=white,facing=west,occupied=false,part=foot] -> minecraft:white_bed[facing=west,occupied=false,part=foot]
minecraft:bed[#color=white,facing=east,occupied=true,part=head] -> minecraft:white_bed[facing=east,occupied=true,part=head]
minecraft:bed[#color=white,facing=east,occupied=true,part=foot] -> minecraft:white_bed[facing=east,occupied=true,part=foot]
minecraft:bed[#color=white,facing=east,occupied=false,part=head] -> minecraft:white_bed[facing=east,occupied=false,part=head]
minecraft:bed[#color=white,facing=east,occupied=false,part=foot] -> minecraft:white_bed[facing=east,occupied=false,part=foot]
```
