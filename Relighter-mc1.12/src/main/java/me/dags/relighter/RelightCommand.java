package me.dags.relighter;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.WorldWorkerManager;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Command Input:               |   Command Behaviour:                  |   Notes:
 * ---------------------------  |---------------------------------------|-------------------
 * /relight                     |   single chunk at player pos          |   (player-only)
 * /relight <radius>            |   radius of chunks around player pos  |   (player-only)
 * /relight <x> <z>             |   single chunk at given pos           |
 * /relight <x> <z> <radius>    |   radius of chunks around given pos   |
 */
public class RelightCommand implements ICommand {

    @Override
    public String getName() {
        return "relight";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/relight <x> <z> <chunk_radius>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("relight");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 3) {
            throw new CommandException(getUsage(sender));
        }

        // Minimum of 2 args means x, z co-ord pair is present, else use relative pos ('~')
        // Exactly 1 arg means only radius is present, exactly 3 means x, z, & radius are present, else use a
        //  radius of 0 (ie single chunk)
        String xArg = args.length >= 2 ? args[0] : "~";
        String zArg = args.length >= 2 ? args[1] : "~";
        String rArg = args.length == 1 ? args[0] : args.length == 3 ? args[2] : "0";

        int x = parseIntOrPos(sender, xArg, BlockPos::getX) >> 4;
        int z = parseIntOrPos(sender, zArg, BlockPos::getZ) >> 4;
        int radius = parseInt(rArg);

        PosIterator iterator = new PosIterator(x, z, radius);
        WorldServer world = server.getWorld(DimensionType.OVERWORLD.getId());
        RelightWorker worker = new RelightWorker(world, iterator, sender);
        WorldWorkerManager.addWorker(worker);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, this.getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }

    private static int parseIntOrPos(ICommandSender sender, String in, Function<BlockPos, Integer> func) throws CommandException {
        if (in.equals("~")) {
            return parseCoord(sender, func);
        }
        return parseInt(in);
    }

    private static int parseCoord(ICommandSender sender, Function<BlockPos, Integer> func) throws CommandException {
        if (sender.getCommandSenderEntity() == null) {
            throw new CommandException("Only players can use the '~' arg");
        }
        return func.apply(sender.getCommandSenderEntity().getPosition());
    }

    private static int parseInt(String in) throws CommandException {
        try {
            return Integer.parseInt(in);
        } catch (Throwable t) {
            throw new CommandException(t.getMessage());
        }
    }
}